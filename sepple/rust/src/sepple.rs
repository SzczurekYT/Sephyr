use std::{thread::JoinHandle, time::Duration};

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
use tokio::sync::mpsc::Receiver;

pub struct Sepple {
    handles: Vec<JoinHandle<()>>,
    receiver: Receiver<String>,
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

        let (receiver, handles) = Pipeline::new(AudioCapture)
            .then(AudioChunker::new(vad::CHUNK_SIZE))
            .then(vad_scorer)
            .then(VadFilter::new(0.35, 0.35, 10))
            .then(SlidingWindowChunker::new(
                &sliding_window_config,
                &Duration::from_millis(40),
            ))
            .then(ipa_processor)
            .then(word_detector)
            .build_no_consumer();

        Sepple { handles, receiver }
    }

    pub fn run_word_transfer(mut self, callback: &Instance) {
        let jvm = Jvm::attach_thread().unwrap();

        while let Some(word) = self.receiver.blocking_recv() {
            let arg = InvocationArg::try_from(word)
                .map_err(|error| format!("{}", error))
                .unwrap();

            jvm.invoke(callback, "accept", &[&arg]).unwrap();
        }

        self.handles
            .into_iter()
            .for_each(|join| join.join().unwrap());
    }

    pub fn is_running(&self) -> bool {
        false
    }
}
