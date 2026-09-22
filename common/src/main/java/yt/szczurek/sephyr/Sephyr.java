package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.spell.casting.ClientCastingManager;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {}

    public static void clientInit() {
        SeppleManager.clientInit();
    }

    public static void clientTick() {
        ClientCastingManager.INSTANCE.tick();
    }

    public static void onClientInitFinished(Minecraft client) {
        SeppleManager.onClientInitFinished(client);
    }
}