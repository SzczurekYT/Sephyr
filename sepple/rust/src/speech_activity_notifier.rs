use std::time::{Duration, Instant};

use sepple::pipeline::{
    PipelineConsumer, PipelineProcessor, PipelineProducer, processor::vad_filter::VadValue,
};
use tokio::sync::mpsc::{Sender, error::SendError};

use crate::sepple::CallbackValue;

const CALLBACK_COOLDOWN: Duration = Duration::from_millis(250);

pub struct SpeechActivityNotifier {
    last_callback: Option<Instant>,
    callback_sender: Sender<CallbackValue>,
}

impl SpeechActivityNotifier {
    pub fn new(callback_sender: Sender<CallbackValue>) -> Self {
        Self {
            last_callback: None,
            callback_sender,
        }
    }
}

impl PipelineConsumer for SpeechActivityNotifier {
    type Input = VadValue;

    fn input_size(&self) -> Option<usize> {
        None
    }
}

impl PipelineProducer for SpeechActivityNotifier {
    type Output = VadValue;

    fn output_size(&self) -> Option<usize> {
        None
    }
}

impl PipelineProcessor for SpeechActivityNotifier {
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
                .is_none_or(|last| now.duration_since(last) >= CALLBACK_COOLDOWN);
            if should_notify {
                self.last_callback = Some(now);
                let _ = self.callback_sender.try_send(CallbackValue::Activity);
            }
        }

        sender.send(value).await
    }
}
