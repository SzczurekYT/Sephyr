package yt.szczurek.sephyr.registry;

import net.blay09.mods.balm.core.BalmRegistrar;
import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class SephyrSounds {
    public static Holder<SoundEvent> SPELL_START_WIND;
    public static Holder<SoundEvent> SPELL_RUNNING_WIND;

    public static void register(BalmRegistrar.Scoped<SoundEvent> sounds) {
        SPELL_START_WIND = sounds.register("spell_start_wind", SoundEvent::createVariableRangeEvent);
        SPELL_RUNNING_WIND = sounds.register("spell_running_wind", SoundEvent::createVariableRangeEvent);
    }
}
