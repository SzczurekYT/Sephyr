package yt.szczurek.sephyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import yt.szczurek.sephyr.spells.Spell;
import yt.szczurek.sephyr.spells.SpellElement;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL_ELEMENT, SpellElement.DIRECT_CODEC, SpellElement.DIRECT_CODEC);
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL, Spell.DIRECT_CODEC, Spell.DIRECT_CODEC);
        Sephyr.init();
    }
}
