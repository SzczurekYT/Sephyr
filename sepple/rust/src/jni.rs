use std::{
    fmt::Display,
    sync::{
        Condvar, Mutex,
        atomic::{AtomicBool, Ordering},
    },
    time::Instant,
};

use j4rs::{InvocationArg, prelude::*};
use j4rs_derive::*;

use crate::sepple::Sepple;

static SEPPLE: (Mutex<Option<Sepple>>, Condvar) = (Mutex::new(None), Condvar::new());
static IS_RUNNING: AtomicBool = AtomicBool::new(false);
pub(crate) static SHOULD_STOP: AtomicBool = AtomicBool::new(false);

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

    println!("Initializing sepple");
    let load_start = Instant::now();
    let sepple = Sepple::init(&path, dictionary);

    *SEPPLE.0.lock().unwrap() = Some(sepple);

    SEPPLE.1.notify_all();

    println!("Init done (took: {:.2?})", load_start.elapsed());
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.run")]
fn run(callback: Instance) {
    let jvm = Jvm::attach_thread().unwrap();

    let mut sepple_guard = SEPPLE.0.lock().unwrap();
    while sepple_guard.is_none() {
        sepple_guard = SEPPLE.1.wait(sepple_guard).unwrap();
    }

    let sepple = sepple_guard.take().expect("Sepple to be initialized");

    drop(sepple_guard);

    IS_RUNNING.store(true, Ordering::SeqCst);
    println!("Sepple is listening.",);

    sepple.run(&jvm, &callback);

    IS_RUNNING.store(false, Ordering::SeqCst);
    println!("Sepple stopped.",);
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.stop")]
fn stop() {
    SHOULD_STOP.store(true, Ordering::SeqCst);
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.isRunning")]
fn is_running() -> Result<Instance, String> {
    to_java(IS_RUNNING.load(Ordering::SeqCst))
}
