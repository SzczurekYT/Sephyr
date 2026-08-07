package yt.szczurek.sephyr;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Sephyr.init();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            Sephyr.startSepple();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            Sephyr.stopSepple();
        });
    }
}
