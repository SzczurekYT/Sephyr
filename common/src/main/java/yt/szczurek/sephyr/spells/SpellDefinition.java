package yt.szczurek.sephyr.spells;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import yt.szczurek.sephyr.SephyrRegistries;
import yt.szczurek.sephyr.spells.wind.WindLeapEffect;

import java.util.List;
import java.util.function.Supplier;

public record SpellDefinition(String name, String description, Holder<SpellElement> element, Holder<CommandNode<SpellCtx>> impl) {

    public LiteralArgumentBuilder<SpellCtx> getNode() {
        return Spell.literal(element.value().word()).then(impl.value());
    }

    public static final Codec<SpellDefinition> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(SpellDefinition::name),
                    Codec.STRING.fieldOf("description").forGetter(SpellDefinition::description),
                    SpellElement.CODEC.fieldOf("element").forGetter(SpellDefinition::element),
                    Spell.CODEC.fieldOf("impl").forGetter(SpellDefinition::impl)
            )
            .apply(instance, SpellDefinition::new));

    public static final Codec<Holder<SpellDefinition>> CODEC = RegistryFileCodec.create(SephyrRegistries.SPELL, DIRECT_CODEC);
}
