package yt.szczurek.sephyr.spells;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Sephyr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellCastingManager {
    private final List<String> wipSpell = new ArrayList<>();
    private Set<SpellSequence> wipSpellCache = new HashSet<>();

    public void addWord(String word) {
        if (wipSpell.isEmpty()) {
            wipSpellCache = SpellsRegistry.get().getCandidates(word);
        } else {
            wipSpellCache = SpellsRegistry.get().filterCandidates(word, wipSpell.size(), wipSpellCache);
        }

        if (wipSpellCache.isEmpty()) {
            wipSpell.clear();
        } else {
            wipSpell.add(word);
        }

        if (wipSpellCache.size() == 1) {
            SpellSequence candidate = wipSpellCache.iterator().next();
            if (candidate.spellMatches(wipSpell)) {
                castSpell(candidate);
                wipSpell.clear();
                wipSpellCache.clear();
            }
        }

        Sephyr.LOG.warn("Word buffer after adding {} is: {}", word, wipSpell);
    }

    public void castSpell(SpellSequence spell) {
        String message = "Casting spell: " + String.join(" ", spell.words());
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal(message));
    }
}
