package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import yt.szczurek.sephyr.entity.SpellEntity;

import java.util.function.Supplier;

public abstract class Spell {
    public static Codec<Spell> DISPATCH_CODEC = Codec.lazyInitialized(() ->
            SpellDefinition.CODEC.dispatch("Id", Spell::getDefinition, SpellDefinition::codec));

    public static LiteralArgumentBuilder<SpellCtx> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public abstract SpellDefinition<?> getDefinition();

    public void run(SpellEntity spellEntity, RefSpellCtx ctx) {}

    public void runClient(SpellEntity spellEntity, RefSpellCtx ctx) {}

    public void tick(SpellEntity spellEntity, RefSpellCtx ctx) {}
}
