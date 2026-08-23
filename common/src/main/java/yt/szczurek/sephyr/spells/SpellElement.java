package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SpellElement(String word, int color) {
    public static final Codec<SpellElement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("word").forGetter(SpellElement::word),
                    Codec.INT.fieldOf("color").forGetter(SpellElement::color)
            )
            .apply(instance, SpellElement::new));
}
