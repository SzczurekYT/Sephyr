package yt.szczurek.sephyr;


import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public SephyrMod(ModContainer modContainer, IEventBus eventBus) {
        final var loadContext = new NeoForgeLoadContext(modContainer, eventBus);
        Balm.initializeMod(Sephyr.MOD_ID, loadContext, Sephyr::init);
    }
}
