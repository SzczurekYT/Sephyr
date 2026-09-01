package yt.szczurek.sephyr;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.spells.Spell;
import yt.szczurek.sephyr.spells.SpellElement;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrRegistries {
    public static final ResourceKey<Registry<Spell>> SPELL = ResourceKey.createRegistryKey(identifier("spell"));
    public static final ResourceKey<Registry<SpellElement>> SPELL_ELEMENT = ResourceKey.createRegistryKey(identifier("spell_element"));
}
