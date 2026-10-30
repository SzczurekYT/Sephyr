package yt.szczurek.sepple;


import org.astonbitecode.j4rs.api.Instance;
import org.astonbitecode.j4rs.api.java2rust.Java2RustUtils;
import org.astonbitecode.j4rs.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Sepple {

    public static boolean init(@Nullable String modelPath, @Nullable String microphoneId, List<String> dictionary, ProgressCallback progressCallback, ModelLoadingCallback modelLoadingCallback) {
        try {
            Field isAndroidField = Utils.class.getDeclaredField("IsAndroid");
            isAndroidField.setAccessible(true);
            isAndroidField.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if (modelPath == null) {
            modelPath = "";
        }
        if (microphoneId == null) {
            microphoneId = "";
        }
        Instance<Boolean> result = SeppleBinding.init(Java2RustUtils.createInstance(modelPath), Java2RustUtils.createInstance(microphoneId), Java2RustUtils.createInstance(dictionary), Java2RustUtils.createInstance(progressCallback), Java2RustUtils.createInstance(modelLoadingCallback));
        return Java2RustUtils.getObjectCasted(result);
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

    public static void setupLogging(BiConsumer<String, Boolean> callback) {
        SeppleBinding.setupLogging(Java2RustUtils.createInstance(new LogCallback(callback)));
    }

    public static List<Microphone> enumerateMicrophones() {
        return Java2RustUtils.getObjectCasted(SeppleBinding.enumerateMicrophones());
    }

    public static boolean setMicrophone(String id) {
        Instance<Boolean> result = SeppleBinding.setMicrophone(Java2RustUtils.createInstance(id));
        return Java2RustUtils.getObjectCasted(result);
    }

    public static @Nullable Microphone getCurrentMicrophone() {
        return Java2RustUtils.getObjectCasted(SeppleBinding.getCurrentMicrophone());
    }
}
