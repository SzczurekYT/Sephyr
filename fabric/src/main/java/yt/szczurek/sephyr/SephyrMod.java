package yt.szczurek.sephyr;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;

public class SephyrMod implements ModInitializer {

    @Override
    public void onInitialize() {
        Balm.initializeMod(Sephyr.MOD_ID, FabricLoadContext.INSTANCE, Sephyr::init);
    }
}
