package yt.szczurek.sephyr;


import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();
    }
}
