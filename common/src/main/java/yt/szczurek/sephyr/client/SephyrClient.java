package yt.szczurek.sephyr.client;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
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
    }
}
