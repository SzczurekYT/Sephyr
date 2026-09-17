package yt.szczurek.sephyr;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

public class NeoForgeSephyrSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Sephyr.MOD_ID);

    static {
        SephyrSounds.SPELL_START_WIND = SOUND_EVENTS.register(
                "spell_start_wind",
                SoundEvent::createVariableRangeEvent
        );
        SephyrSounds.SPELL_RUNNING_WIND = SOUND_EVENTS.register(
                "spell_running_wind",
                SoundEvent::createVariableRangeEvent
        );
    }

}
