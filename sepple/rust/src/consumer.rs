use std::fmt::Display;

use j4rs::{Instance, InvocationArg, Jvm};

use crate::jni::print_error;

pub struct Consumer<T> {
    instance: Instance,
    name: &'static str,
    args_builder: Box<ConsumerArgBuilder<T>>,
}

type ConsumerArgBuilder<T> = dyn Fn(&Consumer<T>, T) -> Option<Vec<InvocationArg>> + Send;

impl<T> Consumer<T> {
    pub fn new(
        instance: Instance,
        name: &'static str,
        args_builder: impl Fn(&Consumer<T>, T) -> Option<Vec<InvocationArg>> + Send + 'static,
    ) -> Self {
        Self {
            instance,
            name,
            args_builder: Box::new(args_builder),
        }
    }

    pub fn accept(&self, value: T) {
        let jvm = Jvm::attach_thread().unwrap();

        let Some(args) = (self.args_builder)(self, value) else {
            return;
        };

        let result = jvm.invoke(&self.instance, "accept", &args);
        if let Err(err) = result {
            print_error(&err, &format!("calling {} consumer callback", self.name));
        }
    }

    pub fn build_arg<A>(&self, value: A, arg_name: &str) -> Option<InvocationArg>
    where
        InvocationArg: TryFrom<A>,
        <InvocationArg as TryFrom<A>>::Error: Display,
    {
        let arg = match InvocationArg::try_from(value) {
            Ok(arg) => arg,
            Err(err) => {
                print_error(
                    &err,
                    &format!("creating {} consumer {arg_name} arg", self.name),
                );
                return None;
            }
        };
        Some(arg)
    }

    pub fn build_primitive_arg<A>(&self, value: A, arg_name: &str) -> Option<InvocationArg>
    where
        InvocationArg: TryFrom<A>,
        <InvocationArg as TryFrom<A>>::Error: Display,
    {
        let arg = self.build_arg(value, arg_name)?;
        let arg = match arg.into_primitive() {
            Ok(arg) => arg,
            Err(err) => {
                print_error(
                    &err,
                    &format!("casting {} consumer {arg_name} arg to primitive", self.name),
                );
                return None;
            }
        };
        Some(arg)
    }
}
