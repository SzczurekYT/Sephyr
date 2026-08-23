package yt.szczurek.sephyr.spells;

import java.util.*;

public class SpellsRegistry {
    private static final SpellsRegistry instance = new SpellsRegistry();
    private final HashMap<SpellSequence, String> spells = new HashMap<>();

    public void register(SpellSequence spell, String name) {
        spells.put(spell, name);
    }

    public static SpellsRegistry get() {
        return instance;
    }

    public static void registerSpells() {
        SpellsRegistry registry = get();
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ ɛvi"), "leap");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ wˈanga"), "wind projectile");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ ɛkɐsuɾɯ"), "wind charge");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ ãntikɔ vəˈluɡoː"), "wind gravity sphere");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ fɒksɑm vəˈluɡoː"), "wind lift column");
    }

    public Set<SpellSequence> getCandidates(String word) {
        return filterCandidates(word, 0, new HashSet<>(this.spells.keySet()));
    }

    public Set<SpellSequence> filterCandidates(String word,  int offset, Set<SpellSequence> currentCandidates) {
        Set<SpellSequence> result = new HashSet<>();

        for (SpellSequence spell : currentCandidates) {
            try {
                String spellWord = spell.words().get(offset);
                if (spellWord.equals(word)) {
                    result.add(spell);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return result;
    }
}
