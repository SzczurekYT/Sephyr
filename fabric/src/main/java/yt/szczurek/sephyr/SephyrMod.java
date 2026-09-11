package yt.szczurek.sephyr;

import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.SpellDefinition;
import yt.szczurek.sephyr.spells.SpellElement;
import yt.szczurek.sephyr.spells.SpellImpls;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL_ELEMENT, SpellElement.DIRECT_CODEC, SpellElement.DIRECT_CODEC);
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL, SpellDefinition.DIRECT_CODEC, SpellDefinition.DIRECT_CODEC);

        WritableRegistry<CommandNode<SpellCtx>> spellImplRegistry = FabricRegistryBuilder.create(SephyrRegistries.SPELL_IMPL).attribute(RegistryAttribute.SYNCED).buildAndRegister();

        SpellImpls.register((name, spell) -> {
            ResourceKey<CommandNode<SpellCtx>> key = ResourceKey.create(SephyrRegistries.SPELL_IMPL, identifier(name));
            spellImplRegistry.register(key, spell.buildNode(), RegistrationInfo.BUILT_IN);
        });

        RegistryEntryAddedCallback.allEntries(SephyrRegistries.SPELL, entry -> {

        });

        Sephyr.init();
    }
}
