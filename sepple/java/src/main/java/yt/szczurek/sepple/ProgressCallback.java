package yt.szczurek.sepple;

import java.util.function.BiConsumer;

public class ProgressCallback {
    private final BiConsumer<Long, Long> callback;

    public ProgressCallback(BiConsumer<Long, Long> callback) {
        this.callback = callback;
    }

    public void accept(long downloaded, long total) {
        callback.accept(downloaded, total);
    }
}