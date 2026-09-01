package yt.szczurek.sephyr;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yt.szczurek.sephyr.spells.*;
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
    private static final int STAR_COLOR = 16773205; // 0xfff055
//    public static SimpleRegistry<SpellElement> SPELL_ELEMENTS = new SimpleRegistry<>("spell_element", SpellElement.DIRECT_CODEC);



    public static Identifier identifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        //  SPELL_ELEMENTS.register();
        SpellEffectRegistry.registerEffects();
        SpellRegistry.registerSpells();
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
        var registry = Minecraft.getInstance().level.registryAccess().lookup(SephyrRegistries.SPELL_ELEMENT).get();
        try {
            var fire = registry.getValue(identifier("fire"));
            var air = registry.getValue(identifier("wind"));

            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("fire color").withColor(fire.color()));
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("wind color").withColor(air.color()));
        } catch (Exception e) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("no worky\n" + e));
        }

        Sephyr.LOG.debug("Received word from Sepple: {}", word);
        var spellComponent = Component.literal(word);
        var element = registry.stream().filter(e -> e.word().equals(word)).findFirst();
        spellComponent.withColor(element.map(SpellElement::color).orElse(0xFFFFFF));
        var component = Component.literal("✨ ").withColor(STAR_COLOR)
                .append(spellComponent).append(Component.literal(" ✨").withColor(STAR_COLOR));
        Minecraft.getInstance().gui.setOverlayMessage(component, false);
        SPELL_CASTING_MANAGER.addWord(word);
    }
}
