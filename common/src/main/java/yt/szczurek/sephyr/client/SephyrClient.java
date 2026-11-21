package yt.szczurek.sephyr.client;

import com.lowdragmc.photon.client.fx.FXEffectExecutor;
import com.lowdragmc.photon.client.fx.IEffectExecutor;
import com.lowdragmc.photon.client.fx.timeline.PhotonSignals;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.minecraft.nbt.CompoundTag;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.SeppleManager;

public class SephyrClient {
    public static void clientInit(BalmClientRegistrars registrars) {
        SephyrClientRegistries.register(registrars);
        Balm.config().registerConfig(SephyrClientConfig.class);

        Balm.config().onConfigAvailable(SephyrClientConfig.class, _ -> SeppleManager.clientInit());
        SephyrClientCommand.register();

        ClientLifecycleCallback.ConnectedToServer.EVENT.register(_ -> SeppleManager.startSepple());
        ClientLifecycleCallback.DisconnectedFromServer.EVENT.register(_ -> SeppleManager.stopSepple());
        ClientTickCallback.AFTER.register(_ -> Sephyr.clientTick());
        ClientLifecycleCallback.Started.EVENT.register(Sephyr::onClientInitFinished);

        PhotonSignals.register(SephyrClient::onPhotonSignal);
    }

    private static void onPhotonSignal(IEffectExecutor effect, String channel, String name, CompoundTag _data, double _time) {
        if (!(effect instanceof FXEffectExecutor executor &&
                executor.getRuntime() != null &&
                channel.equals("lifecycle") &&
                name.equals("spell_finished"))
        ) {
            return;
        }
        executor.getRuntime().destroy(false);
    }
}
