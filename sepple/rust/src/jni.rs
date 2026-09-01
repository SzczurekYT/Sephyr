use std::{
    fmt::Display,
    sync::{
        Condvar, Mutex,
        atomic::{AtomicBool, Ordering},
    },
    thread::{self, Thread},
    time::Instant,
};

use j4rs::{InvocationArg, errors::J4RsError, prelude::*};
use j4rs_derive::*;

use crate::sepple::Sepple;

static LOG_CALLBACK: Mutex<Option<WordConsumer>> = Mutex::new(None);
static SEPPLE: (Mutex<Option<Sepple>>, Condvar) = (Mutex::new(None), Condvar::new());
static IS_RUNNING: AtomicBool = AtomicBool::new(false);
pub(crate) static SHOULD_STOP: AtomicBool = AtomicBool::new(false);

pub fn print_error(error: &J4RsError, context: &str) {
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
fn init(path: Instance, dictionary: Instance) {
    let jvm = Jvm::attach_thread().unwrap();

    let path: String = jvm.to_rust(path).unwrap();
    let dictionary: Vec<String> = jvm.to_rust(dictionary).unwrap();

    log("Initializing sepple");
    let thread = thread::current();
    let thread_name = thread
        .name()
        .map(str::to_string)
        .unwrap_or_else(|| format!("unnamed thread of id: {:?}", thread.id()));
    log(&format!("On {thread_name}",));
    let load_start = Instant::now();
    let sepple = Sepple::init(&path, dictionary);

    *SEPPLE.0.lock().unwrap() = Some(sepple);

    SEPPLE.1.notify_all();

    log(&format!("Init done (took: {:.2?})", load_start.elapsed()));
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.run")]
fn run(callback: Instance) {
    let thread = thread::current();
    let thread_name = thread
        .name()
        .map(str::to_string)
        .unwrap_or_else(|| format!("unnamed thread of id: {:?}", thread.id()));
    log(&format!("Starting sepple. {thread_name}",));
    let mut sepple_guard = SEPPLE.0.lock().unwrap();
    while sepple_guard.is_none() {
        sepple_guard = SEPPLE.1.wait(sepple_guard).unwrap();
    }

    let sepple = sepple_guard.take().expect("Sepple to be initialized");

    drop(sepple_guard);

    IS_RUNNING.store(true, Ordering::SeqCst);
    log("Sepple is listening.");

    sepple.run(&callback.into());

    IS_RUNNING.store(false, Ordering::SeqCst);
    log("Sepple stopped.");
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

pub fn log(text: &str) {
    LOG_CALLBACK
        .lock()
        .unwrap()
        .as_ref()
        .expect("called log before logging was setup")
        .accept(text);
}
