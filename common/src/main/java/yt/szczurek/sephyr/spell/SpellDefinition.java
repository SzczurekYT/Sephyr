package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import yt.szczurek.sephyr.registry.SephyrRegistries;

import java.util.function.Supplier;

public record SpellDefinition<S extends Spell>(
        SpellElement element,
        StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec,
        LiteralArgumentBuilder<SpellCtx> node
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellDefinition<?>> STREAM_CODEC = ByteBufCodecs.registry(SephyrRegistries.SPELL_REGISTRY_KEY);

    public SpellDefinition(SpellElement element, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec, Supplier<CommandNode<SpellCtx>> nodeFactory) {
        this(element, streamCodec, Spell.literal(element.word()).then(nodeFactory.get()));
    }
}
