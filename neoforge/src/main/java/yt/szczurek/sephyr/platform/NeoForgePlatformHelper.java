package yt.szczurek.sephyr.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import yt.szczurek.sephyr.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public void sendCustomPacketToServer(CustomPacketPayload packet) {
        ClientPacketDistributor.sendToServer(packet);
    }
}
