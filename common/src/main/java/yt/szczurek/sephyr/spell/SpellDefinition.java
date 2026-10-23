package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.sephyr.registry.SephyrRegistries;

import java.util.function.Supplier;

public record SpellDefinition<S extends Spell>(
        SpellElement element,
        int lifetimeTicks,
        boolean ticking,
        StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec,
        LiteralArgumentBuilder<SpellCtx> node
) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SpellDefinition<?>> STREAM_CODEC = ByteBufCodecs.registry(SephyrRegistries.SPELL_REGISTRY_KEY);

    public static class Builder<S extends Spell> {
        SpellElement element;
        int lifetimeTicks = -1;
        boolean ticking = false;
        StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec;
        LiteralArgumentBuilder<SpellCtx> node;

        public Builder(
                @NotNull SpellElement element,
                @NotNull StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec,
                @NotNull Supplier<CommandNode<SpellCtx>> nodeFactory
        ) {
            this.element = element;
            this.streamCodec = streamCodec;
            this.node = Spell.literal(element.word()).then(nodeFactory.get());
        }

        public Builder<S> ticking() {
            this.ticking = true;
            return this;
        }

        public Builder<S> lifetime(int ticks) {
            this.lifetimeTicks = ticks;
            return this;
        }

        public SpellDefinition<S> build() {
            return new SpellDefinition<>(element, lifetimeTicks, ticking, streamCodec, node);
        }
    }
}
