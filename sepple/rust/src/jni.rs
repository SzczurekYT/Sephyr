use std::{fmt::Display, time::Instant};

use j4rs::{InvocationArg, prelude::*};
use j4rs_derive::*;

use crate::sepple::Sepple;

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

#[call_from_java("yt.szczurek.sepple.SeppleBinding.run")]
fn run(path: Instance, dictionary: Instance, callback: Instance) {
    let jvm = Jvm::attach_thread().unwrap();

    let path: String = jvm.to_rust(path).unwrap();
    let dictionary: Vec<String> = jvm.to_rust(dictionary).unwrap();

    println!("Initializing sepple");
    let load_start = Instant::now();
    let sepple = Sepple::init(&path, dictionary);

    println!(
        "Init done (took: {:.2?}), Sepple is listening:",
        load_start.elapsed()
    );

    sepple.run_word_transfer(&callback);
}

#[call_from_java("yt.szczurek.sepple.SeppleBinding.isRunning")]
fn is_running() -> Result<Instance, String> {
    to_java(false)
}
