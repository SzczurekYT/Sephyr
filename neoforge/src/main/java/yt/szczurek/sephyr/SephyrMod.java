package yt.szczurek.sephyr;


import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.network.AddCastWordPacket;
import yt.szczurek.sephyr.network.CastWordTimeoutPacket;
import yt.szczurek.sephyr.network.ServerNetworkHandler;
import yt.szczurek.sephyr.spell.Spell;
import yt.szczurek.sephyr.spell.Spells;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public static final Registry<Spell> SPELL_REGISTRY = new RegistryBuilder<>(SephyrRegistries.SPELL).create();

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Sephyr.MOD_ID);
    public static final Supplier<AttachmentType<List<String>>> CAST_WORDS = ATTACHMENT_TYPES.register(
            "cast_words",
            () -> AttachmentType.<List<String>>builder(() -> new ArrayList<>())
                    .sync(CastWords.STREAM_CODEC)
                    .build()
    );

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();

        ATTACHMENT_TYPES.register(eventBus);
        NeoForgeSephyrSounds.SOUND_EVENTS.register(eventBus);

        eventBus.addListener(SephyrMod::registerRegistries);
        eventBus.addListener(SephyrMod::register);
        eventBus.addListener(SephyrMod::registerNetworkHandler);
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SPELL_REGISTRY);
    }

    public static void register(RegisterEvent event) {
        event.register(SephyrRegistries.SPELL, registry -> {
            Spells.register((name, spell) -> {
                registry.register(Sephyr.identifier(name), spell);
            });
        });
    }

    public static void registerNetworkHandler(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                AddCastWordPacket.TYPE,
                AddCastWordPacket.CODEC,
                (packet, context) -> {
                    ServerNetworkHandler.handleAddCastWord(packet, (ServerPlayer) context.player());
                }
        );
        registrar.playToServer(
                CastWordTimeoutPacket.TYPE,
                CastWordTimeoutPacket.CODEC,
                (packet, context) -> {
                    ServerNetworkHandler.handleCastWordTimeout(packet, (ServerPlayer) context.player());
                }
        );
    }
}
