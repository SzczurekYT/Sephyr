package yt.szczurek.sephyr.spells;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.SephyrRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpellCastingManager {
    private final List<String> wipSpell = new ArrayList<>();
    private Set<Spell> wipSpellCache = new HashSet<>();

    public void addWord(String word) {
        if (wipSpell.isEmpty()) {
            wipSpellCache = getCandidates(word);
        } else {
            wipSpellCache = filterCandidates(word, wipSpell.size(), wipSpellCache);
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

    public Set<Spell> getCandidates(String word) {
        HashSet<Spell> candidates = new HashSet<>();
        if (Minecraft.getInstance().level == null) {
            return candidates;
        }
        Registry<Spell> registry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(SephyrRegistries.SPELL);
        for (Spell spell : registry) {
            if (spell.element().value().word().equals(word)) {
                candidates.add(spell);
            }
        }
        return candidates;
    }

    public Set<Spell> filterCandidates(String word, int offset, Set<Spell> currentCandidates) {
        Set<SpellSequence> result = new HashSet<>();

        for (Spell spell : currentCandidates) {
            try {
                String spellWord = spell.text().words().get(offset);
                if (spellWord.equals(word)) {
                    result.add(spell);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return result;
    }
}
