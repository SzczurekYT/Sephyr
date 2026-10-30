package yt.szczurek.sephyr;

import net.blay09.mods.balm.core.BalmRegistrars;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.network.ServerNetworkHandler;
import yt.szczurek.sephyr.registry.SephyrRegistries;
import yt.szczurek.sephyr.spell.casting.ClientCastingManager;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final int BLUE_COLOR = 0xff768fff;

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init(BalmRegistrars registrars) {
        SephyrRegistries.register(registrars);
        ServerNetworkHandler.register();
        SephyrCommand.register();
    }

    public static void clientTick() {
        ClientCastingManager.INSTANCE.tick();
    }

    public static void onClientInitFinished(Minecraft client) {
        SeppleManager.onClientInitFinished(client);
    }
}