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
    private Set<SpellDefinition> wipSpellCache = new HashSet<>();

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

    public Set<SpellDefinition> getCandidates(String word) {
        HashSet<SpellDefinition> candidates = new HashSet<>();
        if (Minecraft.getInstance().level == null) {
            return candidates;
        }
        Registry<SpellDefinition> registry = Minecraft.getInstance().level.registryAccess().lookupOrThrow(SephyrRegistries.SPELL);
        for (SpellDefinition spellDefinition : registry) {
            if (spellDefinition.element().value().word().equals(word)) {
                candidates.add(spellDefinition);
            }
        }
        return candidates;
    }

    public Set<SpellDefinition> filterCandidates(String word, int offset, Set<SpellDefinition> currentCandidates) {
        Set<SpellSequence> result = new HashSet<>();

        for (SpellDefinition spellDefinition : currentCandidates) {
            try {
                String spellWord = spellDefinition.text().words().get(offset);
                if (spellWord.equals(word)) {
                    result.add(spellDefinition);
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
        }

        return result;
    }
}
