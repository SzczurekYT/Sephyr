package yt.szczurek.sephyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.network.AddCastWordPacket;
import yt.szczurek.sephyr.spells.Spell;
import yt.szczurek.sephyr.spells.Spells;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        WritableRegistry<Spell> spellImplRegistry = FabricRegistryBuilder.create(SephyrRegistries.SPELL).buildAndRegister();

        Spells.register((name, spell) -> {
            ResourceKey<Spell> key = ResourceKey.create(SephyrRegistries.SPELL, identifier(name));
            spellImplRegistry.register(key, spell, RegistrationInfo.BUILT_IN);
        });

        PayloadTypeRegistry.clientboundPlay().register(AddCastWordPacket.TYPE, AddCastWordPacket.CODEC);

        Sephyr.init();
    }
}
