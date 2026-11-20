use std::{ops::ControlFlow, path::PathBuf, sync::atomic::Ordering, time::Duration};

use sepple::{
    configs::PreconfiguredPipelineParts,
    dictionary::Dictionary,
    error::{SeppleAudioError, SeppleError},
    model_provider::{self, SILERO_MODEL_META, ZIPA_MODEL_META},
    pipeline::{
        Pipeline,
        processor::{
            ipa_processor::IpaProcessor,
            silero_vad_scorer::SileroVadScorer,
            word_detector::{DetectedWord, WordDetector},
        },
        producer::capture::{AudioCapture, AudioCaptureController, CallbackErrorOrigin},
    },
};
use tokio::{
    runtime::{self},
    select,
    sync::mpsc::{self, Receiver},
    time::timeout,
};

use crate::{
    consumer::Consumer,
    jni::{SHOULD_STOP, notify_speech_activity, report_mic_error},
    speech_activity_notifier::SpeechActivityNotifier,
};

pub struct Sepple {
    pipeline: Pipeline<DetectedWord>,
    callback_receiver: Receiver<CallbackValue>,
}

impl Sepple {
    pub fn init(
        model_path: &str,
        microphone_id: Option<String>,
        dictionary: Vec<String>,
        progress_consumer: &Consumer<(u64, Option<u64>)>,
        on_model_loading: &Consumer<()>,
    ) -> Result<(Self, AudioCaptureController), SeppleError> {
        let zipa_model_path: PathBuf = if model_path.is_empty() {
            let progress_callback = |downloaded: u64, total: Option<u64>| {
                progress_consumer.accept((downloaded, total));
            };
            model_provider::ensure_downloaded_and_get_path(&ZIPA_MODEL_META, &progress_callback)?
        } else {
            PathBuf::from(model_path)
        };

        let vad_model_path =
            model_provider::ensure_downloaded_and_get_path(&SILERO_MODEL_META, &|_, _| {})?;

        on_model_loading.accept(());

        let PreconfiguredPipelineParts {
            audio_chunker,
            sliding_window_config,
            vad_filter,
            sliding_window_chunker,
        } = PreconfiguredPipelineParts::default();

        let vad_scorer = SileroVadScorer::init(vad_model_path);

        let ipa_processor = IpaProcessor::init(zipa_model_path, &sliding_window_config)?;
        let word_detector = WordDetector::init(Dictionary::from_vec(dictionary));

        let (callback_sender, callback_receiver) = mpsc::channel(10);
        let callback_sender_clone = callback_sender.clone();

        let audio_capture =
            AudioCapture::with_error_handler(microphone_id, move |error, origin| {
                let _ = callback_sender_clone.try_send(CallbackValue::AudioError { error, origin });
            });
        let audio_controller = audio_capture.controller();

        let pipeline = Pipeline::new(audio_capture)
            .then(audio_chunker)
            .then(vad_scorer)
            .then(vad_filter)
            .then(SpeechActivityNotifier::new(callback_sender))
            .then(sliding_window_chunker)
            .then(ipa_processor)
            .then(word_detector);

        Ok((
            Sepple {
                pipeline,
                callback_receiver,
            },
            audio_controller,
        ))
    }

    pub fn run(self, callback: &Consumer<String>) {
        let (mut receiver, handle) = self.pipeline.build_no_consumer();

        let mut callback_receiver = self.callback_receiver;

        let rt = runtime::Builder::new_current_thread()
            .enable_time()
            .build()
            .unwrap();

        let future = async {
            loop {
                let receiver_future =
                    receiver_future(callback, &mut receiver, &mut callback_receiver);
                match timeout(Duration::from_millis(250), receiver_future).await {
                    Ok(ControlFlow::Continue(_)) => continue,
                    Ok(ControlFlow::Break(_)) => break,
                    Err(_) => {
                        if SHOULD_STOP
                            .compare_exchange(true, false, Ordering::SeqCst, Ordering::SeqCst)
                            .is_ok()
                        {
                            break;
                        }
                    }
                }
            }
        };

        rt.block_on(future);

        handle.cancel();
    }
}

async fn receiver_future(
    callback: &Consumer<String>,
    receiver: &mut Receiver<DetectedWord>,
    callback_receiver: &mut Receiver<CallbackValue>,
) -> ControlFlow<()> {
    select! {
        val = receiver.recv() => {
            match val {
                Some(word) => { callback.accept(word.word); ControlFlow::Continue(()) },
                None => ControlFlow::Break(()),
            }
        }
        callback = callback_receiver.recv() => {
            match callback {
                Some(callback) => {
                    callback.handle();
                    ControlFlow::Continue(())
                },
                None => ControlFlow::Break(()),
            }
        }
    }
}

pub(crate) enum CallbackValue {
    Activity,
    AudioError {
        error: SeppleAudioError,
        origin: CallbackErrorOrigin,
    },
}

impl CallbackValue {
    fn handle(&self) {
        match self {
            CallbackValue::Activity => notify_speech_activity(),
            CallbackValue::AudioError { error, origin } => {
                let origin = match origin {
                    CallbackErrorOrigin::FirstRun => "STARTUP",
                    CallbackErrorOrigin::Stream => "STREAM",
                };
                report_mic_error(origin, error);
            }
        };
    }
}
