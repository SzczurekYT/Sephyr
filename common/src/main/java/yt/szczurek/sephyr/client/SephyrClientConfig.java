package yt.szczurek.sephyr.client;

import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;

@Config(value = Sephyr.MOD_ID, type = "client")
public class SephyrClientConfig {
    @Comment("Microphone device id. DO NOT CHANGE MANUALLY, use /sephyr microphone")
    public String microphoneId = "";
}

