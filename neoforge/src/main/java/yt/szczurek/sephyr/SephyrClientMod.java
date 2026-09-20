package yt.szczurek.sephyr;


import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = Sephyr.MOD_ID, dist = Dist.CLIENT)
public class SephyrClientMod {

    public SephyrClientMod(IEventBus eventBus) {
        Sephyr.clientInit();

        NeoForge.EVENT_BUS.addListener(this::clientPlayerJoinWorld);
        NeoForge.EVENT_BUS.addListener(this::clientPlayerLeaveWorld);
        NeoForge.EVENT_BUS.addListener(this::clientTick);
        NeoForge.EVENT_BUS.addListener(this::clientLoad);
    }

    private void clientPlayerJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        Sephyr.startSepple();
    }

    private void clientPlayerLeaveWorld(ClientPlayerNetworkEvent.LoggingOut event) {
        Sephyr.stopSepple();
    }

    private void clientTick(ClientTickEvent.Post event) {
        Sephyr.clientTick();
    }

    private void clientLoad(ClientStartedEvent event) {
        Sephyr.onClientInitFinished(event.getClient());
    }
}
