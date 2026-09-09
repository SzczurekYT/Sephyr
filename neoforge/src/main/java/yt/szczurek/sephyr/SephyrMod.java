package yt.szczurek.sephyr;


import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import oshi.util.tuples.Pair;
import yt.szczurek.sephyr.platform.NeoForgePlatformHelper;
import yt.szczurek.sephyr.spells.SpellDefinition;
import yt.szczurek.sephyr.spells.SpellElement;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();

        NeoForge.EVENT_BUS.addListener(this::serverReloadListenersRegistration);
        eventBus.addListener(this::registerDatapackRegistries);
    }

    private void serverReloadListenersRegistration(AddServerReloadListenersEvent event) {
        for (Pair<Identifier, PreparableReloadListener> pair : NeoForgePlatformHelper.LISTENERS) {
            event.addListener(pair.getA(), pair.getB());
        }
    }

    public void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
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
}
