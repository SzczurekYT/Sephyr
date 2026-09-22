package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.slf4j.event.Level;
import yt.szczurek.sephyr.spell.Words;
import yt.szczurek.sephyr.spell.casting.ClientCastingManager;
import yt.szczurek.sephyr.toast.ModelDownloadToast;
import yt.szczurek.sepple.ModelLoadingCallback;
import yt.szczurek.sepple.ProgressCallback;
import yt.szczurek.sepple.Sepple;

public class SeppleManager {

    private static final ModelDownloadToast DOWNLOAD_TOAST = new ModelDownloadToast();
    private static final SystemToast.SystemToastId LOADING_TOAST_ID = new SystemToast.SystemToastId(Long.MAX_VALUE);
    private static final MutableComponent LOADING_TITLE = Component.literal("Sephyr is still loading");
    private static final MutableComponent LOADING_MESSAGE = Component.literal("Spellcasting will not work yet.");
    private static final MutableComponent READY_TITLE = Component.literal("Sephyr is ready");
    private static final MutableComponent READY_MESSAGE = Component.literal("Spellcasting is now available.");

    public static volatile boolean mcClientLoaded = false;
    private static volatile SeppleLoadState seppleLoadState = SeppleLoadState.LOADING;

    private SeppleManager() {}

    public static void clientInit() {
        Sepple.setupLogging((message, isError) -> {
            Level level = isError ? Level.ERROR : Level.INFO;
            Sephyr.LOG.atLevel(level).log("(Sepple/Rust) {}", message);
        });
        // We do the init off thread, so we don't slow down game start
        // We also allocate bigger stack, because Rust can sometime require more than Java allocates by default
        Thread initThread = new Thread(null, SeppleManager::initSepple, "SeppleNativeThread", 2 * 1024 * 1024L);
        initThread.start();
    }

    public static void onClientInitFinished(Minecraft client) {
        mcClientLoaded = true;

        switch (seppleLoadState) {
            case DOWNLOADING -> {
                ToastManager manager = client.getToastManager();
                if (manager.getToast(ModelDownloadToast.class, Toast.NO_TOKEN) == null) {
                    manager.addToast(DOWNLOAD_TOAST);
                }
            }
            case FAILED -> sendSeppleFailToast(client);
        }
    }

    private static void initSepple() {
        // Model path can be overridden here for development if needed
        boolean success = Sepple.init(
                null,
                Words.getList(),
                new ProgressCallback(SeppleManager::onDownloadProgress),
                new ModelLoadingCallback(SeppleManager::onModelLoadingNotification)
        );
        seppleLoadState = success ? SeppleLoadState.FINISHED : SeppleLoadState.FAILED;

        if (mcClientLoaded) {
            Minecraft client = Minecraft.getInstance();
            ToastManager manager = client.getToastManager();
            if (success) {
                if (manager.getToast(SystemToast.class, LOADING_TOAST_ID) != null) {
                    SystemToast.forceHide(manager, LOADING_TOAST_ID);
                    manager.addToast(new SystemToast(new SystemToast.SystemToastId(4000L), READY_TITLE, READY_MESSAGE));
                }
            } else {
                SystemToast.forceHide(manager, LOADING_TOAST_ID);
                sendSeppleFailToast(client);
            }
        }
    }

    private static void onModelLoadingNotification() {
        seppleLoadState = SeppleLoadState.LOADING;
        if (!mcClientLoaded) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        var inWorld = client.level != null;
        if (inWorld) {
            SystemToast.addOrUpdate(client.getToastManager(), LOADING_TOAST_ID, LOADING_TITLE, LOADING_MESSAGE);
        }
    }

    private static void onDownloadProgress(long downloaded, long total) {
        DOWNLOAD_TOAST.setProgress(downloaded, total);

        if (seppleLoadState != SeppleLoadState.DOWNLOADING) {
            seppleLoadState = SeppleLoadState.DOWNLOADING;
            if (mcClientLoaded) {
                Minecraft.getInstance().getToastManager().addToast(DOWNLOAD_TOAST);
            }

        }
    }

    private static void sendSeppleFailToast(Minecraft client) {
        MutableComponent title = Component.literal("Sephyr mod init failed");
        MutableComponent message = Component.literal("Casting will not work! Check logs for details!");
        client.getToastManager().addToast(new SystemToast(new SystemToast.SystemToastId(10000L), title, message));
    }

    public static void startSepple() {
        if (seppleLoadState == SeppleLoadState.FAILED || Sepple.isRunning()) {
            return;
        }

        if (seppleLoadState == SeppleLoadState.LOADING) {
            Minecraft client = Minecraft.getInstance();
            SystemToast.addOrUpdate(client.getToastManager(), LOADING_TOAST_ID, LOADING_TITLE, LOADING_MESSAGE);
        }

        Thread seppleThread = new Thread(() -> Sepple.run(ClientCastingManager.INSTANCE::handleNewWord));
        seppleThread.start();
    }

    public static void stopSepple() {
        if (seppleLoadState == SeppleLoadState.FAILED) {
            return;
        }

        if (Sepple.isRunning()) {
            Sepple.stop();
        }

        SystemToast.forceHide(Minecraft.getInstance().getToastManager(), LOADING_TOAST_ID);
    }

    enum SeppleLoadState {
        LOADING,
        DOWNLOADING,
        FAILED,
        FINISHED
    }
}