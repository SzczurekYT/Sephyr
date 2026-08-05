package yt.szczurek.sepple;

import java.util.function.Consumer;

public class WordCallback {
    private final Consumer<String> callback;

    public WordCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    public void accept(String word) {
        callback.accept(word);
    }
}
