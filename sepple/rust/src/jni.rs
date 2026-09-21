use std::{
    fmt::Display,
    sync::{
        Condvar, Mutex,
        atomic::{AtomicBool, Ordering},
    },
    thread::{self},
    time::Instant,
};

use j4rs::{InvocationArg, prelude::*};
use j4rs_derive::*;
use sepple::error::SeppleError;

use crate::sepple::Sepple;

static LOG_CALLBACK: Mutex<Option<LogConsumer>> = Mutex::new(None);
static SEPPLE: (Mutex<Option<Sepple>>, Condvar) = (Mutex::new(None), Condvar::new());
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

pub struct WordConsumer {
    instance: Instance,
}

impl WordConsumer {
    pub fn accept(&self, string: &str) {
        let jvm = Jvm::attach_thread().unwrap();

        let arg = match InvocationArg::try_from(string) {
            Ok(arg) => arg,
            Err(err) => {
                print_error(&err, "creating word consumer callback arg");
                return;
            }
        };

        let result = jvm.invoke(&self.instance, "accept", &[&arg]);
        if let Err(err) = result {
            print_error(&err, "calling word consumer callback");
        }
    }
}

impl From<Instance> for WordConsumer {
    fn from(value: Instance) -> Self {
        WordConsumer { instance: value }
    }
}

pub struct LogConsumer {
    instance: Instance,
}

impl LogConsumer {
    pub fn accept(&self, string: &str, is_error: bool) {
        let jvm = Jvm::attach_thread().unwrap();

        let text_arg = match InvocationArg::try_from(string) {
            Ok(arg) => arg,
            Err(err) => {
                print_error(&err, "creating log consumer callback arg");
                return;
            }
        };
        let error_arg = match InvocationArg::try_from(is_error) {
            Ok(arg) => arg,
            Err(err) => {
                print_error(&err, "creating log consumer isError arg");
                return;
            }
        };

        let error_arg = match error_arg.into_primitive() {
            Ok(arg) => arg,
            Err(err) => {
                print_error(&err, "casting log consumer isError arg to primitive");
                return;
            }
        };

        let result = jvm.invoke(&self.instance, "accept", &[text_arg, error_arg]);
        if let Err(err) = result {
            print_error(&err, "calling log consumer callback");
        }
    }
}

impl From<Instance> for LogConsumer {
    fn from(value: Instance) -> Self {
        LogConsumer { instance: value }
    }
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
fn init_java(path: Instance, dictionary: Instance) -> Result<Instance, String> {
    let jvm = Jvm::attach_thread().unwrap();

    let path: String = jvm.to_rust(path).unwrap();
    let dictionary: Vec<String> = jvm.to_rust(dictionary).unwrap();

    let init_result = init(path, dictionary);

    if let Err(err) = &init_result {
        log(&format!("Sepple init failed! Error:\n{err}"), true);
    }

    to_java(init_result.is_ok())
}

fn init(path: String, dictionary: Vec<String>) -> Result<(), SeppleError> {
    log("Initializing sepple", false);
    let thread = thread::current();
    let thread_name = thread
        .name()
        .map(str::to_string)
        .unwrap_or_else(|| format!("unnamed thread of id: {:?}", thread.id()));
    log(&format!("On {thread_name}",), false);
    let load_start = Instant::now();
    let sepple = Sepple::init(&path, dictionary)?;

    *SEPPLE.0.lock().unwrap() = Some(sepple);

    SEPPLE.1.notify_all();

    log(
        &format!("Init done (took: {:.2?})", load_start.elapsed()),
        false,
    );
    Ok(())
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.run")]
fn run(callback: Instance) {
    let thread = thread::current();
    let thread_name = thread
        .name()
        .map(str::to_string)
        .unwrap_or_else(|| format!("unnamed thread of id: {:?}", thread.id()));
    log(&format!("Starting sepple. {thread_name}",), false);
    let mut sepple_guard = SEPPLE.0.lock().unwrap();
    while sepple_guard.is_none() {
        sepple_guard = SEPPLE.1.wait(sepple_guard).unwrap();
    }

    let sepple = sepple_guard.take().expect("Sepple to be initialized");

    drop(sepple_guard);

    IS_RUNNING.store(true, Ordering::SeqCst);
    log("Sepple is listening.", false);

    sepple.run(&callback.into());

    IS_RUNNING.store(false, Ordering::SeqCst);
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
fn setup_logging(log_consumer: Instance) {
    *LOG_CALLBACK.lock().unwrap() = Some(log_consumer.into());
}

pub fn log(text: &str, is_error: bool) {
    LOG_CALLBACK
        .lock()
        .unwrap()
        .as_ref()
        .expect("called log before logging was setup")
        .accept(text, is_error);
}
