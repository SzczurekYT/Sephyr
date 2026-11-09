package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import yt.szczurek.sephyr.client.SephyrClientConfig;
import yt.szczurek.sepple.MicError;
import yt.szczurek.sepple.Microphone;
import yt.szczurek.sepple.Sepple;

import java.util.List;
import java.util.Optional;

public final class MicManager {

    private static volatile long lastSpeechNanos = 0L;

    private MicManager() {
    }

    public static void onMicError(MicError error) {
        logError("Microphone capture failed", error);

        Minecraft.getInstance().execute(() -> {
            if (error.origin() == MicError.Origin.SELECTION) {
                Messages.sendChat(Messages.micError(error));
                return;
            }

            handleCaptureError(error);
        });
    }

    public static void onSpeechDetected() {
        lastSpeechNanos = System.nanoTime();
        if (!SephyrClientConfig.micSetupDone()) {
            Minecraft.getInstance().execute(() -> {
                if (!SephyrClientConfig.micSetupDone()) {
                    SephyrClientConfig.setMicSetupDone(true);
                }
            });
        }
    }

    public static void onCaptureStopped() {
        lastSpeechNanos = 0L;
    }

    public static void select(String microphoneName) {
        if (!Sepple.isRunning()) {
            Messages.sendChat(Messages.micNotRunning(SeppleManager.loadingState()));
            return;
        }

        List<Microphone> microphones = Sepple.enumerateMicrophones();
        if (microphones.isEmpty()) {
            Messages.sendChat(Messages.micNoDevices());
            return;
        }

        Optional<Microphone> selected = microphones.stream()
                .filter(microphone -> microphone.name().equals(microphoneName))
                .findFirst();
        if (selected.isEmpty()) {
            Messages.sendChat(Messages.micInvalidSelection());
            return;
        }

        Microphone target = selected.get();
        Microphone previous = Sepple.getCurrentMicrophone();
        boolean sameAsCurrent = previous != null && target.id().equals(previous.id());

        MicError error = Sepple.setMicrophone(target.id());
        if (error == null) {
            SephyrClientConfig.setMicrophoneId(target.id());
            SephyrClientConfig.setMicSetupDone(true);
            Sephyr.LOG.info("Selected microphone {} ({})", target.name(), target.id());
            Messages.sendChat(sameAsCurrent
                    ? Messages.micAlreadySelected(target.name())
                    : Messages.micSelectSuccess(target.name()));
            return;
        }

        logError("Selecting microphone " + target.name() + " (" + target.id() + ") failed", error);

        if (sameAsCurrent) {
            Messages.sendChat(Messages.micError(error));
            return;
        }

        String previousId = previous == null ? null : previous.id();
        MicError restoreError = Sepple.setMicrophone(previousId);
        if (restoreError == null) {
            Microphone restored = Sepple.getCurrentMicrophone();
            String restoredName = restored == null
                    ? Messages.defaultMicrophoneName()
                    : restored.name();
            Sephyr.LOG.info("Restored microphone {}", restoredName);
            Messages.sendChat(Messages.micErrorStillUsing(error, restoredName));
        } else {
            logError("Restoring the previous microphone failed", restoreError);
            Messages.sendChat(Messages.micErrorRestoreFailed(error));
        }
    }

    public static SephyrStateSummary summary() {
        Microphone active = Sepple.getCurrentMicrophone();
        long lastSpeech = lastSpeechNanos;
        long millisSinceLastSpeech = lastSpeech == 0L
                ? -1L
                : (System.nanoTime() - lastSpeech) / 1_000_000L;

        return new SephyrStateSummary(
                SeppleManager.loadingState(),
                Sepple.isRunning(),
                active != null,
                SephyrClientConfig.microphoneId(),
                active,
                millisSinceLastSpeech
        );
    }

    private static void handleCaptureError(MicError error) {
        if (!Sepple.isRunning()) {
            return;
        }

        String savedId = SephyrClientConfig.microphoneId();
        if (savedId.isEmpty() || !error.canAutoRecover()) {
            Messages.sendChat(Messages.micError(error));
            return;
        }

        Sephyr.LOG.warn("Trying the default microphone after the saved microphone failed");
        MicError retryError = Sepple.setMicrophone(null);
        if (retryError != null) {
            logError("The default microphone also failed", retryError);
            Messages.sendChat(Messages.micError(error));
            return;
        }

        SephyrClientConfig.setMicrophoneId("");
        SephyrClientConfig.setMicSetupDone(true);

        Microphone current = Sepple.getCurrentMicrophone();
        String name = current == null ? Messages.defaultMicrophoneName() : current.name();
        Sephyr.LOG.info("Switched to the default microphone {}", name);
        Messages.sendChat(Messages.micSwitchedToDefault(name));
    }

    private static void logError(String context, MicError error) {
        Sephyr.LOG.error(
                "{}: origin={}, cause={}, device={}, detail={}",
                context,
                error.origin(),
                error.cause(),
                error.deviceName(),
                error.detail()
        );
    }
}
