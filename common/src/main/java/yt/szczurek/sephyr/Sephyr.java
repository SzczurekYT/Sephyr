package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.spells.SpellCastingManager;
import yt.szczurek.sephyr.spells.SpellElement;
import yt.szczurek.sephyr.spells.SpellsRegistry;
import yt.szczurek.sephyr.spells.SimpleRegistry;
import yt.szczurek.sepple.Sepple;

import java.util.List;

public class Sephyr {

    public static final String MOD_ID = "sephyr";
    public static final String MOD_NAME = "Sephyr";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);
    public static final List<String> DICTIONARY = List.of(
            "fɛra", "kɛjfida", "ɛvi", "wˈanga", "ɛkɐsuɾɯ", "jɑːkɾidɔ", "vɛɾtɛɾ", "ˈɔnːdˌɔː", "ãntikɔ",
            "fɒksɑm", "ˈunvaksɒm", "lirɔ", "vəˈluɡoː", "plɒka", "toŋk", "ʔalɪˈvɑn", "ibaŋk", "prizim"
    );
    public static final SpellCastingManager SPELL_CASTING_MANAGER = new SpellCastingManager();
    public static SimpleRegistry<SpellElement> SPELL_ELEMENTS = new SimpleRegistry<>("spell_element", SpellElement.CODEC);

    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        SPELL_ELEMENTS.register();
        SpellsRegistry.registerSpells();
    }

    public static void clientInit() {
        Sepple.setupLogging(message -> {
            LOG.info("(Sepple/Rust) {}", message);
        });
        // We do the init off thread, so we don't slow down game start
        Thread initThread = new Thread(() -> Sepple.init("/disks/wizard/dev/Python/Projekty/AI/Sepple/model/multipa_sim.bpk", DICTIONARY));
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
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("fire").withColor(SPELL_ELEMENTS.get(Sephyr.identifier("fire")).get().color()));
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("wind").withColor(SPELL_ELEMENTS.get(Sephyr.identifier("wind")).get().color()));
        Sephyr.LOG.debug("Received word from Sepple: {}", word);
        Minecraft.getInstance().gui.setOverlayMessage(Component.literal("✨ " + word + " ✨"), false);
        SPELL_CASTING_MANAGER.addWord(word);
    }
}
