package yt.szczurek.sepple;


import org.astonbitecode.j4rs.api.Instance;
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils;

import java.util.List;
import java.util.function.Consumer;

public class Sepple {

    public static void run(String modelPath, List<String> dictionary, Consumer<String> callback) {
        SeppleBinding.run(Java2RustUtils.createInstance(modelPath), Java2RustUtils.createInstance(dictionary), Java2RustUtils.createInstance(new WordCallback(callback)));
    }

    public static boolean isRunning() {
        Instance<Boolean> result = SeppleBinding.isRunning();
        return Java2RustUtils.getObjectCasted(result);
    }
}
