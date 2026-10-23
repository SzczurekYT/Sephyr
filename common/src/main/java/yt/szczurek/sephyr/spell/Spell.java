package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import yt.szczurek.sephyr.entity.SpellEntity;

import java.util.function.Supplier;

public abstract class Spell {
    public static StreamCodec<RegistryFriendlyByteBuf, Spell> DISPATCH_CODEC =
            SpellDefinition.STREAM_CODEC.dispatch(Spell::getDefinition, SpellDefinition::streamCodec);

    public static LiteralArgumentBuilder<SpellCtx> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> StreamCodec<ByteBuf, T> supplierCodec(Supplier<T> supplier) {
        return StreamCodec.of((_, _) -> {}, _ -> supplier.get());
    }

    public abstract SpellDefinition<?> getDefinition();

    public abstract void run(SpellEntity spellEntity, RefSpellCtx ctx);

    public void runClient(SpellEntity spellEntity, RefSpellCtx ctx) {}
}
