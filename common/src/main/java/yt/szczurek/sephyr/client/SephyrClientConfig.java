package yt.szczurek.sephyr.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import yt.szczurek.sephyr.Sephyr;

import java.util.Objects;

@Config(value = Sephyr.MOD_ID, type = "client")
public class SephyrClientConfig {
    @Comment("Microphone device id. DO NOT CHANGE MANUALLY, use /sephyr microphone")
    String microphoneId = "";

    @Comment("Internal flag used to tailor microphone setup messages. DO NOT CHANGE MANUALLY.")
    boolean micSetupDone = false;

    public static String microphoneId() {
        String microphoneId = get().microphoneId;
        return microphoneId == null ? "" : microphoneId;
    }

    public static boolean micSetupDone() {
        return get().micSetupDone;
    }

    private static SephyrClientConfig get() {
        return Objects.requireNonNull(
                Balm.config().getActiveConfig(SephyrClientConfig.class),
                "SephyrClientConfig was accessed before it was loaded"
        );
    }

    public static void setMicrophoneId(String microphoneId) {
        String normalizedId = microphoneId == null ? "" : microphoneId;
        Balm.config().updateLocalConfig(SephyrClientConfig.class, config -> config.microphoneId = normalizedId);
    }

    public static void setMicSetupDone(boolean micSetupDone) {
        Balm.config().updateLocalConfig(SephyrClientConfig.class, config -> config.micSetupDone = micSetupDone);
    }
}

