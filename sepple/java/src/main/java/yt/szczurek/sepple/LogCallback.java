package yt.szczurek.sepple;

import java.util.function.BiConsumer;

public class LogCallback {
    private final BiConsumer<String, Boolean> callback;

    public LogCallback(BiConsumer<String, Boolean> callback) {
        this.callback = callback;
    }

    public void accept(String message, boolean isError) {
        callback.accept(message, isError);
    }
}