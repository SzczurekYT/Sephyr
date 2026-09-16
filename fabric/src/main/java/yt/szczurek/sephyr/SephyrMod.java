package yt.szczurek.sephyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.network.AddCastWordPacket;
import yt.szczurek.sephyr.spell.Spell;
import yt.szczurek.sephyr.spell.Spells;
import yt.szczurek.sephyr.network.ServerNetworkHandler;

import java.util.ArrayList;
import java.util.List;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SephyrMod implements ModInitializer {

    public static final AttachmentType<List<String>> CAST_WORDS = AttachmentRegistry.create(
            identifier("cast_words"),
            builder -> builder
                    .initializer(ArrayList::new)
                    .persistent(CastWords.CODEC)
                    .syncWith(CastWords.STREAM_CODEC, AttachmentSyncPredicate.all())
    );

    @Override
    public void onInitialize() {
        WritableRegistry<Spell> spellImplRegistry = FabricRegistryBuilder.create(SephyrRegistries.SPELL).buildAndRegister();

        Spells.register((name, spell) -> {
            ResourceKey<Spell> key = ResourceKey.create(SephyrRegistries.SPELL, identifier(name));
            spellImplRegistry.register(key, spell, RegistrationInfo.BUILT_IN);
        });

        PayloadTypeRegistry.clientboundPlay().register(AddCastWordPacket.TYPE, AddCastWordPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(AddCastWordPacket.TYPE, (packet, context) -> {
            ServerNetworkHandler.handleAddCastWord(packet, context.player());
        });

        Sephyr.init();
    }
}
