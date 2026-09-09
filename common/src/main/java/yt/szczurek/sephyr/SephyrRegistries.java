package yt.szczurek.sephyr;

import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.SpellDefinition;
import yt.szczurek.sephyr.spells.SpellElement;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrRegistries {
    public static final ResourceKey<Registry<SpellDefinition>> SPELL = ResourceKey.createRegistryKey(identifier("spell"));
    public static final ResourceKey<Registry<SpellElement>> SPELL_ELEMENT = ResourceKey.createRegistryKey(identifier("spell_element"));
    public static final ResourceKey<Registry<CommandNode<SpellCtx>>> SPELL_IMPL = ResourceKey.createRegistryKey(identifier("spell_impl"));
}
