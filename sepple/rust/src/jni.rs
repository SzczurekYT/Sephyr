use std::{
    collections::HashMap,
    fmt::Display,
    panic,
    sync::{
        Arc, Condvar, Mutex,
        atomic::{AtomicBool, Ordering},
    },
    time::Instant,
};

use j4rs::{InvocationArg, Null, prelude::*};
use j4rs_derive::*;
use sepple::{
    error::{SeppleAudioError, SeppleError},
    pipeline::producer::capture::{AudioCaptureController, CaptureDevice},
};

use crate::{consumer::Consumer, sepple::Sepple};

const MICROPHONE_CLASS: &str = "yt.szczurek.sepple.Microphone";
const MIC_ERROR_CLASS: &str = "yt.szczurek.sepple.MicError";

static LOG_CALLBACK: Mutex<Option<Consumer<(String, bool)>>> = Mutex::new(None);
static MIC_ERROR_CALLBACK: Mutex<Option<Consumer<Instance>>> = Mutex::new(None);
static SPEECH_ACTIVITY_CALLBACK: Mutex<Option<Consumer<()>>> = Mutex::new(None);
static SEPPLE: (Mutex<Option<Sepple>>, Condvar) = (Mutex::new(None), Condvar::new());
static AUDIO_CONTROLLER: Mutex<Option<Arc<Mutex<AudioCaptureController>>>> = Mutex::new(None);
static IS_RUNNING: AtomicBool = AtomicBool::new(false);
pub(crate) static SHOULD_STOP: AtomicBool = AtomicBool::new(false);

pub fn print_error(error: &dyn Display, context: &str) {
    let string = "[Sephyr/Sepple]: An error occured.\n".to_owned()
        + "If you see this, please open an issue in the Sephyr mod.\n"
        + "The error happened while "
        + context
        + "\nError:\n"
        + &error.to_string();
    println!("{string}");
}

fn log_consumer_arg_builder(
    consumer: &Consumer<(String, bool)>,
    (message, is_error): (String, bool),
) -> Option<Vec<InvocationArg>> {
    Some(vec![
        consumer.build_arg(message, "message")?,
        consumer.build_primitive_arg(is_error, "isError")?,
    ])
}

fn mic_error_consumer_arg_builder(
    consumer: &Consumer<Instance>,
    error: Instance,
) -> Option<Vec<InvocationArg>> {
    Some(vec![consumer.build_arg(error, "error")?])
}

fn progress_consumer_arg_builder(
    consumer: &Consumer<(u64, Option<u64>)>,
    (downloaded, total): (u64, Option<u64>),
) -> Option<Vec<InvocationArg>> {
    Some(vec![
        consumer.build_primitive_arg(downloaded as i64, "downloaded")?,
        consumer.build_primitive_arg(total.map(|t| t as i64).unwrap_or(-1), "total")?,
    ])
}

fn to_java<T>(value: T) -> Result<Instance, String>
where
    InvocationArg: TryFrom<T>,
    <InvocationArg as TryFrom<T>>::Error: Display,
{
    let ia = InvocationArg::try_from(value)
        .map_err(|error| format!("{}", error))
        .unwrap();
    Instance::try_from(ia).map_err(|error| format!("{}", error))
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.init")]
fn init_java(
    path: Instance,
    microphone_id: Instance,
    dictionary: Instance,
    progress_callback: Instance,
    model_loading_callback: Instance,
) -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();

    let path: String = jvm.to_rust(path).unwrap();
    let microphone_id: String = jvm.to_rust(microphone_id).unwrap();
    let microphone_id = if microphone_id.is_empty() {
        None
    } else {
        Some(microphone_id)
    };
    let dictionary: Vec<String> = jvm.to_rust(dictionary).unwrap();

    let init_result = init(
        path,
        microphone_id,
        dictionary,
        &Consumer::new(progress_callback, "progress", progress_consumer_arg_builder),
        &Consumer::new(model_loading_callback, "modelLoading", |_, _| {
            Some(Vec::new())
        }),
    );

    if let Err(err) = &init_result {
        log(format!("Sepple init failed! Error:\n{err}"), true);
    }

    to_java(init_result.is_ok())
}

fn init(
    path: String,
    microphone_id: Option<String>,
    dictionary: Vec<String>,
    progress_consumer: &Consumer<(u64, Option<u64>)>,
    on_model_loading: &Consumer<()>,
) -> Result<(), SeppleError> {
    panic::set_hook(Box::new(|info| {
        let Some(msg) = info.payload_as_str() else {
            return;
        };
        let message = format!("Rust panicked! Error:\n{msg}",);
        log(&message, true);
        print_error(&message, "panic handling");
    }));

    log("Initializing sepple", false);
    let load_start = Instant::now();
    let (sepple, audio_controller) = Sepple::init(
        &path,
        microphone_id,
        dictionary,
        progress_consumer,
        on_model_loading,
    )?;

    *SEPPLE.0.lock().unwrap() = Some(sepple);
    *AUDIO_CONTROLLER.lock().unwrap() = Some(audio_controller);

    SEPPLE.1.notify_all();

    log(
        format!("Init done (took: {:.2?})", load_start.elapsed()),
        false,
    );
    Ok(())
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.run")]
fn run(callback: Instance) {
    log("Starting sepple.", false);
    let mut sepple_guard = SEPPLE.0.lock().unwrap();
    while sepple_guard.is_none() {
        sepple_guard = SEPPLE.1.wait(sepple_guard).unwrap();
    }

    let sepple = sepple_guard.take().expect("Sepple to be initialized");

    drop(sepple_guard);

    IS_RUNNING.store(true, Ordering::SeqCst);
    log("Sepple is listening.", false);

    let word_consumer = &Consumer::new(callback, "word", |consumer, arg| {
        Some(vec![consumer.build_arg(arg, "word")?])
    });
    sepple.run(word_consumer);

    IS_RUNNING.store(false, Ordering::SeqCst);
    *AUDIO_CONTROLLER.lock().unwrap() = None;
    log("Sepple stopped.", false);
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.stop")]
fn stop() {
    SHOULD_STOP.store(true, Ordering::SeqCst);
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.isRunning")]
fn is_running() -> Result<Instance, String> {
    to_java(IS_RUNNING.load(Ordering::SeqCst))
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.setupLogging")]
fn setup_logging(log_consumer: Instance, mic_error_consumer: Instance) {
    *LOG_CALLBACK.lock().unwrap() =
        Some(Consumer::new(log_consumer, "log", log_consumer_arg_builder));
    *MIC_ERROR_CALLBACK.lock().unwrap() = Some(Consumer::new(
        mic_error_consumer,
        "mic_error",
        mic_error_consumer_arg_builder,
    ));
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.setupActivityCallback")]
fn setup_activity_callback(callback: Instance) {
    *SPEECH_ACTIVITY_CALLBACK.lock().unwrap() =
        Some(Consumer::new(callback, "speech_activity", |_, _| {
            Some(Vec::new())
        }));
}

pub fn log<T>(text: T, is_error: bool)
where
    T: ToString,
{
    LOG_CALLBACK
        .lock()
        .unwrap()
        .as_ref()
        .expect("called log before logging was setup")
        .accept((text.to_string(), is_error));
}

pub fn notify_speech_activity() {
    let guard = SPEECH_ACTIVITY_CALLBACK.lock().unwrap();
    if let Some(callback) = guard.as_ref() {
        callback.accept(());
    }
}

fn create_mic_error(jvm: &Jvm, origin: &str, error: &SeppleAudioError) -> Result<Instance, String> {
    let kind = format!("{:?}", error.error.kind());
    let device_name = error
        .error
        .device()
        .map(|device| device.name.clone())
        .unwrap_or_default();
    let detail = format!("{error:#}");
    let args = [
        InvocationArg::try_from(origin).map_err(|error| format!("{error}"))?,
        InvocationArg::try_from(kind).map_err(|error| format!("{error}"))?,
        InvocationArg::try_from(device_name).map_err(|error| format!("{error}"))?,
        InvocationArg::try_from(detail).map_err(|error| format!("{error}"))?,
    ];
    jvm.invoke_static(MIC_ERROR_CLASS, "fromStrings", &args)
        .map_err(|error| format!("{error}"))
}

pub fn report_mic_error(origin: &str, error: &SeppleAudioError) {
    log(
        format!("Microphone capture error ({origin}):\n{error:#}"),
        true,
    );

    let jvm = Jvm::attach_thread().unwrap();
    let instance = match create_mic_error(&jvm, origin, error) {
        Ok(instance) => instance,
        Err(error) => {
            print_error(&error, "creating MicError");
            return;
        }
    };

    let guard = MIC_ERROR_CALLBACK.lock().unwrap();
    if let Some(callback) = guard.as_ref() {
        callback.accept(instance);
    } else {
        print_error(
            &"microphone error callback was not registered",
            "reporting a microphone error",
        );
    }
}

fn capture_device_to_java_microphone(device: CaptureDevice) -> InvocationArg {
    let map = HashMap::from([("name", device.name), ("id", device.id)]);
    InvocationArg::new(&map, MICROPHONE_CLASS)
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.enumerateMicrophones")]
fn enumerate_microphnes() -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();
    let microphones = AudioCaptureController::enumerate_microphones();
    let name_id_vec: Vec<_> = microphones
        .into_iter()
        .map(capture_device_to_java_microphone)
        .map(Ok)
        .collect();
    jvm.java_list(MICROPHONE_CLASS, name_id_vec)
        .map_err(|error| format!("{}", error))
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.setMicrophone")]
fn set_microphone(id: Instance) -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();

    let id: String = jvm.to_rust(id).unwrap();
    let id = if id.is_empty() { None } else { Some(id) };
    let result = AUDIO_CONTROLLER
        .lock()
        .unwrap()
        .as_ref()
        .map(|ctrl| ctrl.lock().unwrap().set_microphone(id));
    if let Some(Err(err)) = &result {
        log(format!("Setting microphone failed!\n{err:#}"), true);
        create_mic_error(&jvm, "SELECTION", err)
    } else {
        to_java(Null::Of(MIC_ERROR_CLASS))
    }
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.getCurrentMicrophone")]
fn get_current_microphone() -> Result<Instance, String> {
    AUDIO_CONTROLLER
        .lock()
        .unwrap()
        .as_ref()
        .and_then(|ctrl| ctrl.lock().unwrap().capture_device())
        .map(capture_device_to_java_microphone)
        .map(Ok)
        .map(to_java)
        .unwrap_or_else(|| to_java(Null::Of(MICROPHONE_CLASS)))
}
