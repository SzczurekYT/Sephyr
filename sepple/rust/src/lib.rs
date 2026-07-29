use j4rs::{prelude::*, InvocationArg};
use j4rs_derive::*;

#[call_from_java("yt.szczurek.sepple.SeppleBinding.fnnoargs")]
fn my_function_with_no_args() -> Result<Instance, String> {
    println!("Hello from the Rust world!");
    let ia = InvocationArg::try_from("Ferris says Hi!`")
        .map_err(|error| format!("{}", error))
        .unwrap();
    Instance::try_from(ia).map_err(|error| format!("{}", error))
}
