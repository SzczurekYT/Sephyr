use std::{path::PathBuf, sync::atomic::Ordering, time::Duration};

use sepple::{
    dictionary::Dictionary,
    error::SeppleError,
    model_provider,
    pipeline::{
        Pipeline,
        processor::{
            chunker::AudioChunker,
            ipa_processor::IpaProcessor,
            silero_vad_scorer::SileroVadScorer,
            sliding_window::{SlidingWindowChunker, SlidingWindowConfig},
            vad_filter::VadFilter,
            word_detector::WordDetector,
        },
        producer::capture::AudioCapture,
    },
    vad,
};
use tokio::{
    runtime::{self},
    time::timeout,
};

use crate::{consumer::Consumer, jni::SHOULD_STOP};

pub struct Sepple {
    pipeline: Pipeline<String>,
}

impl Sepple {
    pub fn init(
        model_path: &str,
        dictionary: Vec<String>,
        progress_consumer: &Consumer<(u64, Option<u64>)>,
        on_model_loading: &Consumer<()>,
    ) -> Result<Self, SeppleError> {
        let model_path: PathBuf = if model_path.is_empty() {
            let progress_callback = |downloaded: u64, total: Option<u64>| {
                progress_consumer.accept((downloaded, total));
            };
            model_provider::ensure_downloaded_and_get_path(&progress_callback)?
        } else {
            PathBuf::from(model_path)
        };

        on_model_loading.accept(());

        let sliding_window_config = SlidingWindowConfig {
            window_size: Duration::from_millis(1000),
            cut_left: Duration::from_millis(150),
            cut_right: Duration::from_millis(150),
        };
        let vad_scorer = SileroVadScorer::init();

        let ipa_processor = IpaProcessor::init(model_path, &sliding_window_config)?;
        let word_detector = WordDetector::init(Dictionary::from_vec(dictionary));

        let pipeline = Pipeline::new(AudioCapture::new()?)
            .then(AudioChunker::new(vad::CHUNK_SIZE))
            .then(vad_scorer)
            .then(VadFilter::new(0.35, 0.35, 10))
            .then(SlidingWindowChunker::new(
                &sliding_window_config,
                &Duration::from_millis(40),
            ))
            .then(ipa_processor)
            .then(word_detector);

        Ok(Sepple { pipeline })
    }

    pub fn run(self, callback: &Consumer<String>) {
        let (mut receiver, handle) = self.pipeline.build_no_consumer();

        let rt = runtime::Builder::new_current_thread()
            .enable_time()
            .build()
            .unwrap();

        let future = async {
            loop {
                match timeout(Duration::from_millis(250), receiver.recv()).await {
                    Ok(Some(word)) => callback.accept(word),
                    Ok(None) => break,
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
