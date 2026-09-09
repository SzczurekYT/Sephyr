package yt.szczurek.sephyr;

import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.SpellDefinition;
import yt.szczurek.sephyr.spells.SpellElement;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL_ELEMENT, SpellElement.DIRECT_CODEC, SpellElement.DIRECT_CODEC);
        DynamicRegistries.registerSynced(SephyrRegistries.SPELL, SpellDefinition.DIRECT_CODEC, SpellDefinition.DIRECT_CODEC);
        WritableRegistry<CommandNode<SpellCtx>> registry = FabricRegistryBuilder.create(SephyrRegistries.SPELL_IMPL).attribute(RegistryAttribute.SYNCED).buildAndRegister();

        Sephyr.init();
    }
}
