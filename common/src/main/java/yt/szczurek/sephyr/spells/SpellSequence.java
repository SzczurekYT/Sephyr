package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.List;

public record SpellSequence(List<String> words) {
    public static final Codec<SpellSequence> CODEC = Codec.STRING.xmap(SpellSequence::new, SpellSequence::toString);

    public SpellSequence(String spell) {
        this(Arrays.asList(spell.split(" ")));
    }

    @Override
    public @NonNull String toString() {
        return String.join(" ", this.words);
    }

    public boolean spellMatches(List<String> otherWords) {
        return otherWords.equals(words);
    }
}
