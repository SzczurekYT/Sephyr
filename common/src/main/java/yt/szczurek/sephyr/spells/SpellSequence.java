package yt.szczurek.sephyr.spells;

import java.util.Arrays;
import java.util.List;

public record SpellSequence(List<String> words) {
    public SpellSequence(String spell) {
        this(Arrays.asList(spell.split(" ")));
    }

    public boolean spellMatches(List<String> otherWords) {
        return otherWords.equals(words);
    }
}
