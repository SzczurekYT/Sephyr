package yt.szczurek.sepple;

public final class SpeechActivityCallback {
    private final Runnable callback;

    public SpeechActivityCallback(Runnable callback) {
        this.callback = callback;
    }

    public void accept() {
        callback.run();
    }
}
