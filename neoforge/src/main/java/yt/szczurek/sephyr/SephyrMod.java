package yt.szczurek.sephyr;


import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import oshi.util.tuples.Pair;
import yt.szczurek.sephyr.platform.NeoForgePlatformHelper;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();

        NeoForge.EVENT_BUS.addListener(this::clientPlayerJoinWorld);
        NeoForge.EVENT_BUS.addListener(this::clientPlayerLeaveWorld);
        NeoForge.EVENT_BUS.addListener(this::serverReloadListenersRegistration);
    }

    private void clientPlayerJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        Sephyr.startSepple();
    }

    private void clientPlayerLeaveWorld(ClientPlayerNetworkEvent.LoggingOut event) {
        Sephyr.stopSepple();
    }

    private void serverReloadListenersRegistration(AddServerReloadListenersEvent event) {
        for (Pair<Identifier, PreparableReloadListener> pair : NeoForgePlatformHelper.LISTENERS) {
            event.addListener(pair.getA(), pair.getB());
        }
    }
}
