package yt.szczurek.sephyr.platform;

import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import oshi.util.tuples.Pair;
import yt.szczurek.sephyr.platform.services.IPlatformHelper;

import java.util.ArrayList;
import java.util.List;

public class NeoForgePlatformHelper implements IPlatformHelper {
    public static final List<Pair<Identifier, PreparableReloadListener>> LISTENERS = new ArrayList<>();

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

    public void registerServerReloadListener(Identifier id, PreparableReloadListener listener) {
        LISTENERS.add(new Pair<>(id, listener));
    }
}
