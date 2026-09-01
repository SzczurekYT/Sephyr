package yt.szczurek.sephyr.spells;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

public abstract class SpellEffect {
    public static final Codec<SpellEffect> CODEC = Codec.stringResolver(
            effect -> effect.id.toString(),
            s -> SpellEffectRegistry.get().getEffect(Identifier.parse(s))
    );

    final Identifier id;

    public SpellEffect(Identifier id) {
        this.id = id;
    }

    public Identifier getId() {
        return id;
    }

    public abstract void execute(SpellSequence sequence, SpellParameters params);
}
