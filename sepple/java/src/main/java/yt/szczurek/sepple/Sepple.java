package yt.szczurek.sepple;


import org.astonbitecode.j4rs.api.Instance;
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils;

import java.util.List;
import java.util.function.Consumer;

public class Sepple {

    public static void init(String modelPath, List<String> dictionary) {
        SeppleBinding.init(Java2RustUtils.createInstance(modelPath), Java2RustUtils.createInstance(dictionary));
    }

    public static void run(Consumer<String> callback) {
        SeppleBinding.run(Java2RustUtils.createInstance(new WordCallback(callback)));
    }

    public static void stop() {
        SeppleBinding.stop();
    }

    public static boolean isRunning() {
        Instance<Boolean> result = SeppleBinding.isRunning();
        return Java2RustUtils.getObjectCasted(result);
    }
}
