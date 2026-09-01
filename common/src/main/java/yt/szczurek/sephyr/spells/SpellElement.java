package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import yt.szczurek.sephyr.SephyrRegistries;

public record SpellElement(String word, int color) {
    public static final Codec<SpellElement> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("word").forGetter(SpellElement::word),
                    Codec.INT.fieldOf("color").forGetter(SpellElement::color)
            )
            .apply(instance, SpellElement::new));

    public static final Codec<Holder<SpellElement>> CODEC = RegistryFileCodec.create(SephyrRegistries.SPELL_ELEMENT, DIRECT_CODEC);
}
