package yt.szczurek.sephyr;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.spell.Words;
import yt.szczurek.sephyr.spell.casting.ClientCastingManager;
import yt.szczurek.sepple.Sepple;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {}

    public static void clientInit() {
        Sepple.setupLogging(message -> LOG.info("(Sepple/Rust) {}", message));
        // We do the init off thread, so we don't slow down game start
        Thread initThread = new Thread(() -> Sepple.init("/disks/wizard/dev/Python/Projekty/AI/Sepple/model/multipa_sim.bpk", Words.getList()));
        initThread.start();
    }

    public static void clientTick() {
        ClientCastingManager.INSTANCE.tick();
    }

    public static void startSepple() {
        if (Sepple.isRunning()) {
            return;
        }

        Thread seppleThread = new Thread(() -> Sepple.run(ClientCastingManager.INSTANCE::handleNewWord));
        seppleThread.start();
    }

    public static void stopSepple() {
        if (Sepple.isRunning()) {
            Sepple.stop();
        }
    }
}
