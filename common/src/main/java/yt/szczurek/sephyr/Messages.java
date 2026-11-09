package yt.szczurek.sephyr;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.SeppleManager.SeppleLoadState;
import yt.szczurek.sephyr.client.SephyrClientConfig;
import yt.szczurek.sepple.MicError;
import yt.szczurek.sepple.Microphone;

public final class Messages {

    public static final String CMD_STATUS_HEADER = "commands.sephyr.status.header";
    public static final String CMD_STATUS_RUNNING_LABEL = "commands.sephyr.status.running_label";
    public static final String CMD_STATUS_CAPTURING_LABEL = "commands.sephyr.status.capturing_label";
    public static final String CMD_STATUS_YES = "commands.sephyr.status.yes";
    public static final String CMD_STATUS_NO = "commands.sephyr.status.no";
    public static final String CMD_STATUS_LOADING_LABEL = "commands.sephyr.status.loading_label";
    public static final String CMD_STATUS_SELECTED_MICROPHONE_LABEL = "commands.sephyr.status.selected_microphone_label";
    public static final String CMD_STATUS_ACTIVE_MICROPHONE_LABEL = "commands.sephyr.status.active_microphone_label";
    public static final String CMD_STATUS_NONE = "commands.sephyr.status.none";
    public static final String CMD_STATUS_DEFAULT = "commands.sephyr.status.default";
    public static final String CMD_STATUS_MICROPHONE_NAME = "commands.sephyr.status.microphone_name";
    public static final String CMD_STATUS_STATE_LOADING = "commands.sephyr.status.state.loading";
    public static final String CMD_STATUS_STATE_DOWNLOADING = "commands.sephyr.status.state.downloading";
    public static final String CMD_STATUS_STATE_FAILED = "commands.sephyr.status.state.failed";
    public static final String CMD_STATUS_STATE_FINISHED = "commands.sephyr.status.state.finished";

    public static final String MIC_ERROR_NO_DEVICE = "sephyr.sepple.mic.error.no_device";
    public static final String MIC_ERROR_PERMISSION = "sephyr.sepple.mic.error.permission";
    public static final String MIC_ERROR_BUSY = "sephyr.sepple.mic.error.busy";
    public static final String MIC_ERROR_BUSY_UNKNOWN = "sephyr.sepple.mic.error.busy_unknown";
    public static final String MIC_ERROR_DEVICE_GONE = "sephyr.sepple.mic.error.device_gone";
    public static final String MIC_ERROR_DEVICE_GONE_UNKNOWN = "sephyr.sepple.mic.error.device_gone_unknown";
    public static final String MIC_ERROR_UNKNOWN = "sephyr.sepple.mic.error.unknown";
    public static final String MIC_ERROR_INTERNAL = "sephyr.sepple.mic.error.internal";
    public static final String MIC_ERROR_NOT_RUNNING = "sephyr.sepple.mic.error.not_running";
    public static final String MIC_ERROR_STILL_STARTING = "sephyr.sepple.mic.error.still_starting";
    public static final String MIC_ERROR_STARTUP_FAILED = "sephyr.sepple.mic.error.startup_failed";
    public static final String MIC_ERROR_INVALID_SELECTION = "sephyr.sepple.mic.error.invalid_selection";
    public static final String MIC_FIRST_TIME_PREFIX = "sephyr.sepple.mic.first_time_prefix";

    public static final String MIC_ACTION_SELECT = "sephyr.sepple.mic.action.select";
    public static final String MIC_ACTION_PERMISSION = "sephyr.sepple.mic.action.permission";
    public static final String MIC_ACTION_CLOSE_APP = "sephyr.sepple.mic.action.close_app";
    public static final String MIC_ACTION_PLUG_IN = "sephyr.sepple.mic.action.plug_in";
    public static final String MIC_ACTION_REPORT = "sephyr.sepple.mic.action.report";
    public static final String MIC_ACTION_WAIT = "sephyr.sepple.mic.action.wait";
    public static final String MIC_ACTION_CHECK_LOG = "sephyr.sepple.mic.action.check_log";

    public static final String MIC_OUTCOME_SWITCHED_DEFAULT = "sephyr.sepple.mic.outcome.switched_default";
    public static final String MIC_OUTCOME_STILL_USING = "sephyr.sepple.mic.outcome.still_using";
    public static final String MIC_SUCCESS_SELECTED = "sephyr.sepple.mic.success.selected";
    public static final String MIC_SUCCESS_ALREADY_SELECTED = "sephyr.sepple.mic.success.already_selected";
    public static final String MIC_DEFAULT_NAME = "sephyr.sepple.mic.default_name";
    public static final String MIC_STATUS_LAST_SPEECH = "sephyr.sepple.mic.status.last_speech";
    public static final String MIC_STATUS_NO_SPEECH = "sephyr.sepple.mic.status.no_speech";

    public static final String TOAST_LOADING_TITLE = "sephyr.sepple.toast.loading.title";
    public static final String TOAST_LOADING_MESSAGE = "sephyr.sepple.toast.loading.message";
    public static final String TOAST_READY_TITLE = "sephyr.sepple.toast.ready.title";
    public static final String TOAST_READY_MESSAGE = "sephyr.sepple.toast.ready.message";
    public static final String TOAST_FAILED_TITLE = "sephyr.sepple.toast.failed.title";
    public static final String TOAST_FAILED_MESSAGE = "sephyr.sepple.toast.failed.message";
    public static final String TOAST_DOWNLOAD_TITLE = "sephyr.sepple.toast.download.title";
    public static final String TOAST_DOWNLOAD_PROGRESS = "sephyr.sepple.toast.download.progress";
    public static final String TOAST_DOWNLOAD_PROGRESS_UNKNOWN = "sephyr.sepple.toast.download.progress_unknown";

    public static final String SOUND_SPELL_START_WIND = "sound.sephyr.spell.start.wind";
    public static final String SOUND_SPELL_RUNNING_WIND = "sound.sephyr.spell.running.wind";
    public static final String ENTITY_SPELL = "entity.sephyr.spell";

    public static final MutableComponent LOADING_TOAST_TITLE = Component.translatable(TOAST_LOADING_TITLE);
    public static final MutableComponent LOADING_TOAST_MESSAGE = Component.translatable(TOAST_LOADING_MESSAGE);
    public static final MutableComponent READY_TOAST_TITLE = Component.translatable(TOAST_READY_TITLE);
    public static final MutableComponent READY_TOAST_MESSAGE = Component.translatable(TOAST_READY_MESSAGE);
    public static final MutableComponent FAILED_TOAST_TITLE = Component.translatable(TOAST_FAILED_TITLE);
    public static final MutableComponent FAILED_TOAST_MESSAGE = Component.translatable(TOAST_FAILED_MESSAGE);
    public static final MutableComponent DOWNLOAD_TOAST_TITLE = Component.translatable(TOAST_DOWNLOAD_TITLE);

    private Messages() {
    }

    public static void sendChat(Component message) {
        Minecraft client = Minecraft.getInstance();
        client.execute(() -> {
            if (client.gui != null) {
                client.gui.getChat().addClientSystemMessage(
                        Component.literal("[Sephyr] ").withStyle(ChatFormatting.BLUE).append(message)
                );
            }
        });
    }

    public static Component micError(MicError error) {
        MutableComponent message = errorText(error);
        for (MicError.Action action : error.actions()) {
            message.append(Component.literal("\n"))
                    .append(actionText(action).withStyle(ChatFormatting.GRAY));
        }
        return message;
    }

    public static Component micErrorStillUsing(MicError error, String microphoneName) {
        return errorText(error)
                .append(Component.literal("\n"))
                .append(Component.translatable(MIC_OUTCOME_STILL_USING, microphoneName)
                        .withStyle(ChatFormatting.GRAY));
    }

    public static Component micErrorRestoreFailed(MicError error) {
        return micError(error);
    }

    public static Component micSwitchedToDefault(String microphoneName) {
        return Component.translatable(MIC_OUTCOME_SWITCHED_DEFAULT, microphoneName)
                .withStyle(ChatFormatting.GREEN);
    }

    public static Component micSelectSuccess(String microphoneName) {
        return Component.translatable(MIC_SUCCESS_SELECTED, microphoneName)
                .withStyle(ChatFormatting.GREEN);
    }

    public static Component micAlreadySelected(String microphoneName) {
        return Component.translatable(MIC_SUCCESS_ALREADY_SELECTED, microphoneName)
                .withStyle(ChatFormatting.YELLOW);
    }

    public static Component micNoDevices() {
        return micError(new MicError(MicError.Origin.SELECTION, MicError.Cause.NO_DEVICE, null, ""));
    }

    public static Component micNotRunning(SeppleLoadState state) {
        return switch (state) {
            case LOADING, DOWNLOADING -> withAction(
                    Component.translatable(MIC_ERROR_STILL_STARTING).withStyle(ChatFormatting.RED),
                    MIC_ACTION_WAIT
            );
            case FAILED -> withAction(
                    Component.translatable(MIC_ERROR_STARTUP_FAILED).withStyle(ChatFormatting.RED),
                    MIC_ACTION_CHECK_LOG
            );
            case FINISHED -> withAction(
                    Component.translatable(MIC_ERROR_NOT_RUNNING).withStyle(ChatFormatting.RED),
                    MIC_ACTION_CHECK_LOG
            );
        };
    }

    public static Component micInvalidSelection() {
        return Component.translatable(MIC_ERROR_INVALID_SELECTION)
                .withStyle(ChatFormatting.RED)
                .append(Component.literal("\n"))
                .append(Component.translatable(MIC_ACTION_SELECT).withStyle(ChatFormatting.GRAY));
    }

    public static String defaultMicrophoneName() {
        return Component.translatable(MIC_DEFAULT_NAME).getString();
    }

    public static Component statusSummary(SephyrStateSummary summary) {
        Component runningLine = line(
                Component.translatable(CMD_STATUS_RUNNING_LABEL).withStyle(ChatFormatting.WHITE),
                summary.running()
                        ? Component.translatable(CMD_STATUS_YES).withStyle(ChatFormatting.GREEN)
                        : Component.translatable(CMD_STATUS_NO).withStyle(ChatFormatting.RED)
        );
        Component capturingLine = line(
                Component.translatable(CMD_STATUS_CAPTURING_LABEL).withStyle(ChatFormatting.WHITE),
                summary.capturing()
                        ? Component.translatable(CMD_STATUS_YES).withStyle(ChatFormatting.GREEN)
                        : Component.translatable(CMD_STATUS_NO).withStyle(ChatFormatting.RED)
        );
        Component loadingStateLine = line(
                Component.translatable(CMD_STATUS_LOADING_LABEL).withStyle(ChatFormatting.WHITE),
                loadingStateNameColored(summary.loadState())
        );
        Component selectedMicrophoneLine = line(
                Component.translatable(CMD_STATUS_SELECTED_MICROPHONE_LABEL).withStyle(ChatFormatting.WHITE),
                summary.savedMicrophoneId().isEmpty()
                        ? Component.translatable(CMD_STATUS_DEFAULT).withStyle(ChatFormatting.GRAY)
                        : Component.literal(summary.savedMicrophoneId()).withStyle(ChatFormatting.GRAY)
        );
        Component activeMicrophoneLine = line(
                Component.translatable(CMD_STATUS_ACTIVE_MICROPHONE_LABEL).withStyle(ChatFormatting.WHITE),
                microphoneName(summary.activeMicrophone())
        );
        Component speechLine = Component.literal("\n")
                .append(summary.hasSpeech()
                        ? Component.translatable(MIC_STATUS_LAST_SPEECH, formatDuration(summary.millisSinceLastSpeech()))
                        .withStyle(ChatFormatting.GRAY)
                        : Component.translatable(MIC_STATUS_NO_SPEECH).withStyle(ChatFormatting.GRAY));

        return Component.translatable(CMD_STATUS_HEADER).withColor(Sephyr.BLUE_COLOR)
                .append(runningLine)
                .append(capturingLine)
                .append(loadingStateLine)
                .append(selectedMicrophoneLine)
                .append(activeMicrophoneLine)
                .append(speechLine);
    }

    private static MutableComponent errorText(MicError error) {
        MutableComponent text = switch (error.cause()) {
            case NO_DEVICE -> Component.translatable(MIC_ERROR_NO_DEVICE);
            case PERMISSION -> Component.translatable(MIC_ERROR_PERMISSION);
            case BUSY -> error.deviceName() == null
                    ? Component.translatable(MIC_ERROR_BUSY_UNKNOWN)
                    : Component.translatable(MIC_ERROR_BUSY, error.deviceName());
            case DEVICE_GONE -> error.deviceName() == null
                    ? Component.translatable(MIC_ERROR_DEVICE_GONE_UNKNOWN)
                    : Component.translatable(MIC_ERROR_DEVICE_GONE, error.deviceName());
            case UNKNOWN -> Component.translatable(MIC_ERROR_UNKNOWN);
            case INTERNAL -> Component.translatable(MIC_ERROR_INTERNAL);
        };

        boolean firstTime = !SephyrClientConfig.micSetupDone()
                && (error.cause() == MicError.Cause.NO_DEVICE || error.cause() == MicError.Cause.PERMISSION);
        if (firstTime) {
            return Component.translatable(MIC_FIRST_TIME_PREFIX).withStyle(ChatFormatting.GOLD)
                    .append(text.withStyle(ChatFormatting.RED));
        }
        return text.withStyle(ChatFormatting.RED);
    }

    private static MutableComponent withAction(MutableComponent text, String actionKey) {
        return text.append(Component.literal("\n"))
                .append(Component.translatable(actionKey).withStyle(ChatFormatting.GRAY));
    }

    private static MutableComponent actionText(MicError.Action action) {
        return switch (action) {
            case ALLOW_PERMISSION -> Component.translatable(MIC_ACTION_PERMISSION);
            case SELECT_MIC -> Component.translatable(MIC_ACTION_SELECT);
            case CLOSE_APP -> Component.translatable(MIC_ACTION_CLOSE_APP);
            case PLUG_IN -> Component.translatable(MIC_ACTION_PLUG_IN);
            case REPORT -> Component.translatable(MIC_ACTION_REPORT);
        };
    }

    private static Component line(Component label, Component value) {
        return Component.literal("\n").append(label).append(value);
    }

    private static Component microphoneName(Microphone microphone) {
        return microphone == null
                ? Component.translatable(CMD_STATUS_NONE).withStyle(ChatFormatting.RED)
                : Component.translatable(CMD_STATUS_MICROPHONE_NAME, microphone.name(), microphone.id())
                        .withStyle(ChatFormatting.GRAY);
    }

    private static String formatDuration(long millis) {
        if (millis < 1000) {
            return "less than a second";
        }
        long seconds = millis / 1000;
        if (seconds < 60) {
            return seconds + "s";
        }
        long minutes = seconds / 60;
        long remainingSeconds = seconds % 60;
        return minutes + "m " + remainingSeconds + "s";
    }

    public static MutableComponent loadingStateNameColored(SeppleLoadState state) {
        return switch (state) {
            case LOADING -> Component.translatable(CMD_STATUS_STATE_LOADING).withStyle(ChatFormatting.GOLD);
            case DOWNLOADING -> Component.translatable(CMD_STATUS_STATE_DOWNLOADING).withStyle(ChatFormatting.GOLD);
            case FAILED -> Component.translatable(CMD_STATUS_STATE_FAILED).withStyle(ChatFormatting.RED);
            case FINISHED -> Component.translatable(CMD_STATUS_STATE_FINISHED).withStyle(ChatFormatting.GREEN);
        };
    }

    public static @NonNull Component downloadProgress(int percent, long downloadedMb, long totalMb) {
        return Component.translatable(TOAST_DOWNLOAD_PROGRESS, percent, downloadedMb, totalMb);
    }

    public static Component downloadProgressUnknown(long downloadedMb) {
        return Component.translatable(TOAST_DOWNLOAD_PROGRESS_UNKNOWN, downloadedMb);
    }
}
