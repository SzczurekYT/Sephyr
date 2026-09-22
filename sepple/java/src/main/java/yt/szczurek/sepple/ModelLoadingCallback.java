package yt.szczurek.sepple;

public class ModelLoadingCallback {
    private final Runnable callback;

    public ModelLoadingCallback(Runnable callback) {
        this.callback = callback;
    }

    public void accept() {
        callback.run();
    }
}