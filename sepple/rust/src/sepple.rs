use std::{sync::atomic::Ordering, time::Duration};

use j4rs::{Instance, InvocationArg, Jvm};
use sepple::{
    dictionary::Dictionary,
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

use crate::jni::SHOULD_STOP;

pub struct Sepple {
    pipeline: Pipeline<String>,
}

impl Sepple {
    pub fn init(model_path: &str, dictionary: Vec<String>) -> Self {
        let sliding_window_config = SlidingWindowConfig {
            window_size: Duration::from_secs(2),
            cut_left: Duration::from_millis(500),
            cut_right: Duration::from_millis(500),
        };
        let vad_scorer = SileroVadScorer::init();
        let ipa_processor = IpaProcessor::init(model_path, &sliding_window_config);
        let word_detector = WordDetector::init(Dictionary::from_vec(dictionary));

        let pipeline = Pipeline::new(AudioCapture)
            .then(AudioChunker::new(vad::CHUNK_SIZE))
            .then(vad_scorer)
            .then(VadFilter::new(0.35, 0.35, 10))
            .then(SlidingWindowChunker::new(
                &sliding_window_config,
                &Duration::from_millis(40),
            ))
            .then(ipa_processor)
            .then(word_detector);

        Sepple { pipeline }
    }

    pub fn run(self, jvm: &Jvm, callback: &Instance) {
        let (mut receiver, handle) = self.pipeline.build_no_consumer();

        let rt = runtime::Builder::new_current_thread()
            .enable_time()
            .build()
            .unwrap();

        let future = async {
            loop {
                match timeout(Duration::from_millis(250), receiver.recv()).await {
                    Ok(Some(word)) => {
                        let arg = InvocationArg::try_from(word)
                            .map_err(|error| format!("{}", error))
                            .unwrap();

                        jvm.invoke(callback, "accept", &[&arg]).unwrap();
                    }
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
