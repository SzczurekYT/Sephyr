package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.sephyr.registry.SephyrRegistries;

import java.util.function.Supplier;

public record SpellDefinition<S extends Spell>(
        SpellElement element,
        int lifetimeTicks,
        boolean ticking,
        MapCodec<S> codec,
        LiteralArgumentBuilder<SpellCtx> node
) {
    public static final Codec<SpellDefinition<?>> CODEC = Codec.lazyInitialized(() -> SephyrRegistries.SPELL.byNameCodec());

    public static class Builder<S extends Spell> {
        SpellElement element;
        int lifetimeTicks = -1;
        boolean ticking = false;
        MapCodec<S> codec;
        LiteralArgumentBuilder<SpellCtx> node;

        public Builder(
                @NotNull SpellElement element,
                @NotNull MapCodec<S> codec,
                @NotNull Supplier<CommandNode<SpellCtx>> nodeFactory
        ) {
            this.element = element;
            this.codec = codec;
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
            return new SpellDefinition<>(element, lifetimeTicks, ticking, codec, node);
        }
    }
}
