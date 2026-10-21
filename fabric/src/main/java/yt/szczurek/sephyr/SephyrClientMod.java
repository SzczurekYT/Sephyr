package yt.szczurek.sephyr;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ClientModInitializer;
import yt.szczurek.sephyr.client.SephyrClient;

public class SephyrClientMod implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BalmClient.initializeMod(Sephyr.MOD_ID, FabricLoadContext.INSTANCE, SephyrClient::clientInit);
    }
}
