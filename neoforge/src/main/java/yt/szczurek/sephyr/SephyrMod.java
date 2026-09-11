package yt.szczurek.sephyr;


import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import oshi.util.tuples.Pair;
import yt.szczurek.sephyr.platform.NeoForgePlatformHelper;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.SpellDefinition;
import yt.szczurek.sephyr.spells.SpellElement;
import yt.szczurek.sephyr.spells.SpellImpls;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public static final Registry<CommandNode<SpellCtx>> SPELL_IMPL_REGISTRY = new RegistryBuilder<>(SephyrRegistries.SPELL_IMPL).create();

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();

        NeoForge.EVENT_BUS.addListener(SephyrMod::serverReloadListenersRegistration);
        eventBus.addListener(SephyrMod::registerDatapackRegistries);
        eventBus.addListener(SephyrMod::registerRegistries);
        eventBus.addListener(SephyrMod::register);
    }

    public static void serverReloadListenersRegistration(AddServerReloadListenersEvent event) {
        for (Pair<Identifier, PreparableReloadListener> pair : NeoForgePlatformHelper.LISTENERS) {
            event.addListener(pair.getA(), pair.getB());
        }
    }

    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                SephyrRegistries.SPELL_ELEMENT,
                SpellElement.DIRECT_CODEC,
                SpellElement.DIRECT_CODEC,
                builder -> builder.sync(true)
        );
        event.dataPackRegistry(
                SephyrRegistries.SPELL,
                SpellDefinition.DIRECT_CODEC,
                SpellDefinition.DIRECT_CODEC,
                builder -> builder.sync(true)
        );
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SPELL_IMPL_REGISTRY);
    }

    public static void register(RegisterEvent event) {
        event.register(SephyrRegistries.SPELL_IMPL, registry -> {
            SpellImpls.register((name, spell) -> {
                registry.register(Sephyr.identifier(name), spell.buildNode());
            });
        });
    }
}
