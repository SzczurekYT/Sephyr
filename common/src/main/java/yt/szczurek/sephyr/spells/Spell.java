package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import yt.szczurek.sephyr.SephyrRegistries;

import java.util.List;

public record Spell(String name, String description, SpellSequence text, Holder<SpellElement> element, List<SpellEffect> effects) {

    public static final Codec<Spell> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(Spell::name),
                    Codec.STRING.fieldOf("description").forGetter(Spell::description),
                    SpellSequence.CODEC.fieldOf("text").forGetter(Spell::text),
                    SpellElement.CODEC.fieldOf("element").forGetter(Spell::element),
                    SpellEffect.CODEC.listOf().fieldOf("effects").forGetter(Spell::effects)
            )
            .apply(instance, Spell::new));

    public static final Codec<Holder<Spell>> CODEC = RegistryFileCodec.create(SephyrRegistries.SPELL, DIRECT_CODEC);
}
