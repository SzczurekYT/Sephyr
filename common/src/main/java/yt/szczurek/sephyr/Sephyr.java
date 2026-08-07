package yt.szczurek.sephyr;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static void init() {
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
        Sephyr.LOG.debug("Received word from Sepple: {}", word);
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Casting: " + word));
    }
}
