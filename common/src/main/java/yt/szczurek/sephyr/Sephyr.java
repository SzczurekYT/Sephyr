package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import yt.szczurek.sephyr.spell.Words;
import yt.szczurek.sephyr.spell.casting.ClientCastingManager;
import yt.szczurek.sepple.Sepple;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static boolean mcClientLoaded = false;
    public static boolean seppleLoadFailed = false;

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {}

    public static void clientInit() {
        Sepple.setupLogging((message, isError) -> {
            Level level = isError ? Level.ERROR : Level.INFO;
            LOG.atLevel(level).log("(Sepple/Rust) {}", message);
        });
        // We do the init off thread, so we don't slow down game start
        Thread initThread = new Thread(Sephyr::initSepple);
        initThread.start();
    }

    public static void clientTick() {
        ClientCastingManager.INSTANCE.tick();
    }

    public static void onClientInitFinished(Minecraft client) {
        mcClientLoaded = true;
        if (seppleLoadFailed) {
            sendSeppleFailToast(client);
        }
    }

    private static void initSepple() {
        // Model path can be overridden here for development if needed
        seppleLoadFailed = !Sepple.init(null, Words.getList());
        if (seppleLoadFailed && mcClientLoaded) {
            sendSeppleFailToast(Minecraft.getInstance());
        }
    }

    private static void sendSeppleFailToast(Minecraft client) {
        MutableComponent title = Component.literal("Sephyr mod init failed");
        MutableComponent message = Component.literal("Casting will not work! Check logs for details!");
        client.getToastManager().addToast(new SystemToast(new SystemToast.SystemToastId(10000L), title, message));
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
