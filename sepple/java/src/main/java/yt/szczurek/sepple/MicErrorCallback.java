package yt.szczurek.sepple;

import java.util.function.Consumer;

public final class MicErrorCallback {
    private final Consumer<MicError> callback;

    public MicErrorCallback(Consumer<MicError> callback) {
        this.callback = callback;
    }

    public void accept(MicError error) {
        callback.accept(error);
    }
}
