package yt.szczurek.sephyr;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class SephyrClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((_, _, _) -> {
            Sephyr.startSepple();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((_, _) -> {
            Sephyr.stopSepple();
        });

        ClientTickEvents.END_CLIENT_TICK.register(_ -> {
            Sephyr.clientTick();
        });

        Sephyr.clientInit();
    }
}
