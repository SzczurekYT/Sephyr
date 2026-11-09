use std::time::{Duration, Instant};

use sepple::pipeline::{
    PipelineConsumer, PipelineProcessor, PipelineProducer, processor::vad_filter::VadValue,
};
use tokio::sync::mpsc::{Sender, error::SendError};

use crate::jni::notify_speech_activity;

pub struct SpeechActivityTap {
    last_callback: Option<Instant>,
    callback_interval: Duration,
}

impl SpeechActivityTap {
    pub fn new() -> Self {
        Self {
            last_callback: None,
            callback_interval: Duration::from_millis(250),
        }
    }
}

impl PipelineConsumer for SpeechActivityTap {
    type Input = VadValue;

    fn input_size(&self) -> Option<usize> {
        None
    }
}

impl PipelineProducer for SpeechActivityTap {
    type Output = VadValue;

    fn output_size(&self) -> Option<usize> {
        None
    }
}

impl PipelineProcessor for SpeechActivityTap {
    fn name() -> &'static str {
        "SpeechActivityTap"
    }

    async fn process_value(
        &mut self,
        value: Self::Input,
        sender: &Sender<Self::Output>,
    ) -> Result<(), SendError<Self::Output>> {
        if let VadValue::Data(_) = &value {
            let now = Instant::now();
            let should_notify = self
                .last_callback
                .is_none_or(|last| now.duration_since(last) >= self.callback_interval);
            if should_notify {
                self.last_callback = Some(now);
                notify_speech_activity();
            }
        }

        sender.send(value).await
    }
}
