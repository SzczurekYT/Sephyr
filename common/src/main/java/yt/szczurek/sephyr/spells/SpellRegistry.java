package yt.szczurek.sephyr.spells;

import java.util.*;

public class SpellRegistry {
    private static final SpellRegistry instance = new SpellRegistry();
    private final HashMap<SpellSequence, String> spells = new HashMap<>();

    public void register(SpellSequence spell, String name) {
        spells.put(spell, name);
    }

    public static SpellRegistry get() {
        return instance;
    }

    public static void registerSpells() {
        SpellRegistry registry = get();
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ ɛvi"), "leap");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ wˈanga"), "wind projectile");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ ɛkɐsuɾɯ"), "wind charge");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ ãntikɔ vəˈluɡoː"), "wind gravity sphere");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ fɒksɑm vəˈluɡoː"), "wind lift column");
    }


}
