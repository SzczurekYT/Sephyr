package yt.szczurek.sephyr;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();

        NeoForge.EVENT_BUS.addListener(this::clientPlayerJoinWorld);
        NeoForge.EVENT_BUS.addListener(this::clientPlayerLeaveWorld);
    }

    private void clientPlayerJoinWorld(ClientPlayerNetworkEvent.LoggingIn event) {
        Sephyr.startSepple();
    }

    private void clientPlayerLeaveWorld(ClientPlayerNetworkEvent.LoggingOut event) {
        Sephyr.stopSepple();
    }
}
