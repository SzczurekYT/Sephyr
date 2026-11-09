package yt.szczurek.sepple;

import java.util.List;
import java.util.Locale;

public record MicError(Origin origin, Cause cause, String deviceName, String detail) {

    public enum Origin {
        STARTUP,
        STREAM,
        SELECTION
    }

    public enum Cause {
        NO_DEVICE,
        PERMISSION,
        BUSY,
        DEVICE_GONE,
        UNKNOWN,
        INTERNAL
    }

    public enum Action {
        ALLOW_PERMISSION,
        SELECT_MIC,
        CLOSE_APP,
        PLUG_IN,
        REPORT
    }

    public static MicError fromStrings(String origin, String kind, String deviceName, String detail) {
        String normalizedDevice = deviceName == null || deviceName.isBlank() ? null : deviceName;
        return new MicError(
                parseOrigin(origin),
                mapCause(kind, normalizedDevice),
                normalizedDevice,
                detail == null ? "" : detail
        );
    }

    public boolean canAutoRecover() {
        return switch (cause) {
            case NO_DEVICE, DEVICE_GONE, BUSY -> true;
            default -> false;
        };
    }

    public List<Action> actions() {
        return switch (cause) {
            case NO_DEVICE -> List.of(Action.PLUG_IN, Action.SELECT_MIC);
            case PERMISSION -> List.of(Action.ALLOW_PERMISSION, Action.REPORT);
            case BUSY -> List.of(Action.CLOSE_APP, Action.SELECT_MIC);
            case DEVICE_GONE -> List.of(Action.SELECT_MIC);
            case UNKNOWN, INTERNAL -> List.of(Action.SELECT_MIC, Action.REPORT);
        };
    }

    private static Origin parseOrigin(String origin) {
        if (origin == null) {
            return Origin.STARTUP;
        }
        return switch (origin.toUpperCase(Locale.ROOT)) {
            case "STREAM" -> Origin.STREAM;
            case "SELECTION" -> Origin.SELECTION;
            default -> Origin.STARTUP;
        };
    }

    private static Cause mapCause(String kind, String deviceName) {
        if (kind == null) {
            return Cause.UNKNOWN;
        }
        return switch (kind) {
            case "DeviceUnavailable" -> deviceName == null ? Cause.NO_DEVICE : Cause.DEVICE_GONE;
            case "DeviceLost" -> Cause.DEVICE_GONE;
            case "PermissionDenied" -> Cause.PERMISSION;
            case "DeviceBusy" -> Cause.BUSY;
            case "UnsupportedFormat", "InvalidChannel" -> Cause.UNKNOWN;
            case "Backend", "NoAudio", "StreamInvalidated", "Stalled", "SinkStalled", "Processing",
                 "AlreadyRecording", "NotRecording", "StopFromSink", "OpenTimedOut", "CloseTimedOut" ->
                    Cause.INTERNAL;
            default -> Cause.UNKNOWN;
        };
    }
}
