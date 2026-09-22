package yt.szczurek.sephyr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class SephyrClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((_, _, _) -> {
            SeppleManager.startSepple();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> {
            SeppleManager.stopSepple();
        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> {
            Sephyr.clientTick();
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(Sephyr::onClientInitFinished);

        Sephyr.clientInit();
    }
}
