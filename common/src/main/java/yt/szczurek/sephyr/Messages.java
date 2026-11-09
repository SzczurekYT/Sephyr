package yt.szczurek.sephyr;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.SeppleManager.SeppleLoadState;
import yt.szczurek.sepple.Microphone;

public final class Messages {

    public static final String CMD_MIC_INVALID = "commands.sephyr.microphone.invalid";
    public static final String CMD_MIC_ALREADY_SELECTED = "commands.sephyr.microphone.already_selected";
    public static final String CMD_MIC_SET = "commands.sephyr.microphone.set";
    public static final String CMD_MIC_FAILED_FALLBACK = "commands.sephyr.microphone.failed_fallback";
    public static final String CMD_MIC_FAILED = "commands.sephyr.microphone.failed";

    public static final String CMD_STATUS_HEADER = "commands.sephyr.status.header";
    public static final String CMD_STATUS_RUNNING_LABEL = "commands.sephyr.status.running_label";
    public static final String CMD_STATUS_YES = "commands.sephyr.status.yes";
    public static final String CMD_STATUS_NO = "commands.sephyr.status.no";
    public static final String CMD_STATUS_LOADING_LABEL = "commands.sephyr.status.loading_label";
    public static final String CMD_STATUS_MICROPHONE_LABEL = "commands.sephyr.status.microphone_label";
    public static final String CMD_STATUS_NONE = "commands.sephyr.status.none";
    public static final String CMD_STATUS_MICROPHONE_NAME = "commands.sephyr.status.microphone_name";
    public static final String CMD_STATUS_STATE_LOADING = "commands.sephyr.status.state.loading";
    public static final String CMD_STATUS_STATE_DOWNLOADING = "commands.sephyr.status.state.downloading";
    public static final String CMD_STATUS_STATE_FAILED = "commands.sephyr.status.state.failed";
    public static final String CMD_STATUS_STATE_FINISHED = "commands.sephyr.status.state.finished";

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

    public static @NonNull Component invalidMicrophone() {
        return Component.translatable(CMD_MIC_INVALID);
    }

    public static Component microphoneAlreadySelected() {
        return Component.translatable(CMD_MIC_ALREADY_SELECTED);
    }

    public static Component microphoneSet(String micName) {
        return Component.translatable(CMD_MIC_SET, micName);
    }

    public static Component microphoneFailedWithFallback(String usedMicName) {
        return Component.translatable(CMD_MIC_FAILED_FALLBACK, usedMicName).withStyle(ChatFormatting.RED);
    }

    public static Component microphoneFailed() {
        return Component.translatable(CMD_MIC_FAILED).withStyle(ChatFormatting.RED);
    }

    public static Component statusSummary(boolean isRunning, SeppleLoadState state, Microphone microphone) {
        Component runningLine = Component.literal("\n")
                .append(Component.translatable(CMD_STATUS_RUNNING_LABEL).withStyle(ChatFormatting.WHITE))
                .append(isRunning
                        ? Component.translatable(CMD_STATUS_YES).withStyle(ChatFormatting.GREEN)
                        : Component.translatable(CMD_STATUS_NO).withStyle(ChatFormatting.RED));
        Component loadingStateLine = Component.literal("\n")
                .append(Component.translatable(CMD_STATUS_LOADING_LABEL).withStyle(ChatFormatting.WHITE))
                .append(loadingStateNameColored(state));
        Component microphoneLine = Component.literal("\n")
                .append(Component.translatable(CMD_STATUS_MICROPHONE_LABEL).withStyle(ChatFormatting.WHITE))
                .append(microphone == null
                        ? Component.translatable(CMD_STATUS_NONE).withStyle(ChatFormatting.RED)
                        : Component.translatable(CMD_STATUS_MICROPHONE_NAME, microphone.name(), microphone.id())
                                .withStyle(ChatFormatting.GRAY));
        return Component.translatable(CMD_STATUS_HEADER).withColor(Sephyr.BLUE_COLOR)
                .append(runningLine)
                .append(loadingStateLine)
                .append(microphoneLine);
    }

    public static MutableComponent loadingStateNameColored(SeppleLoadState state) {
        return switch (state) {
            case LOADING -> Component.translatable(CMD_STATUS_STATE_LOADING).withStyle(ChatFormatting.GOLD);
            case DOWNLOADING -> Component.translatable(CMD_STATUS_STATE_DOWNLOADING).withStyle(ChatFormatting.GOLD);
            case FAILED -> Component.translatable(CMD_STATUS_STATE_FAILED).withStyle(ChatFormatting.RED);
            case FINISHED -> Component.translatable(CMD_STATUS_STATE_FINISHED).withStyle(ChatFormatting.GREEN);
        };
    }

    public static Component downloadProgress(int percent, long downloadedMb, long totalMb) {
        return Component.translatable(TOAST_DOWNLOAD_PROGRESS, percent, downloadedMb, totalMb);
    }

    public static Component downloadProgressUnknown(long downloadedMb) {
        return Component.translatable(TOAST_DOWNLOAD_PROGRESS_UNKNOWN, downloadedMb);
    }
}
