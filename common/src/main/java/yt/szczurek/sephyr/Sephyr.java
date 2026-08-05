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
    public static final List<String> DICTIONARY = List.of("prizim", "fɛra", "kɛjfida", "ɛvi", "jɑːkɾidɔ", "vɛɾtɛɾ", "toŋk", "ʔalɪˈvɑn", "ibaŋk");

    public static void init() {
        Thread seppleThread = new Thread(() -> Sepple.run("/disks/wizard/dev/Python/Projekty/AI/Sepple/model/multipa_sim.bpk", DICTIONARY, Sephyr::magicWordReceiver));
        seppleThread.start();
    }

    public static void magicWordReceiver(String word) {
        Sephyr.LOG.debug("Received word from Sepple: {}", word);
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal("Casting: " + word));
    }
}
