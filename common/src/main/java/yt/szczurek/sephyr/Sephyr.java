package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.client.SpellCastingManager;
import yt.szczurek.sephyr.spells.SpellElement;
import yt.szczurek.sephyr.spells.Words;
import yt.szczurek.sepple.Sepple;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final SpellCastingManager SPELL_CASTING_MANAGER = new SpellCastingManager();
    private static final Component STAR_COMPONENT = Component.literal("✨").withColor(16773205); // 0xfff055


    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {

    }

    public static void clientInit() {
        Sepple.setupLogging(message -> LOG.info("(Sepple/Rust) {}", message));
        // We do the init off thread, so we don't slow down game start
        Thread initThread = new Thread(() -> Sepple.init("/disks/wizard/dev/Python/Projekty/AI/Sepple/model/multipa_sim.bpk", Words.getList()));
        initThread.start();
    }

    public static void startSepple() {
        if (Sepple.isRunning()) {
            return;
        }

        Thread seppleThread = new Thread(() -> Sepple.run(Sephyr::magicWordReceiver));
        seppleThread.start();
    }

    public static void stopSepple() {
        if (Sepple.isRunning()) {
            Sepple.stop();
        }
    }

    public static void magicWordReceiver(String word) {
        Sephyr.LOG.debug("Received word from Sepple: {}", word);
        var spellComponent = Component.literal(" " + word + " ");

        var element = SpellElement.byWord(word);
        int color = element != null ? element.color() : 0xFFFFFF;
        spellComponent.withColor(color);

        var component = STAR_COMPONENT.copy().append(spellComponent).append(STAR_COMPONENT);
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
        SPELL_CASTING_MANAGER.addWord(word);
    }
}
