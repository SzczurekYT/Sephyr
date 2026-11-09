package yt.szczurek.sephyr;

import yt.szczurek.sepple.Microphone;

public record SephyrStateSummary(
        SeppleManager.SeppleLoadState loadState,
        boolean running,
        boolean capturing,
        String savedMicrophoneId,
        Microphone activeMicrophone,
        long millisSinceLastSpeech
) {
    public boolean hasSpeech() {
        return millisSinceLastSpeech >= 0;
    }
}
