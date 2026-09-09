package yt.szczurek.sephyr.spells;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import yt.szczurek.sephyr.SephyrRegistries;

import java.util.List;

public abstract class Spell {
    public static final Codec<Holder<CommandNode<SpellCtx>>> CODEC = RegistryFixedCodec.create(SephyrRegistries.SPELL_IMPL);

    private SpellSequence sequence;
    private List<CommandNode<SpellCtx>> nodes;


    public static LiteralArgumentBuilder<SpellCtx> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public abstract CommandNode<SpellCtx> buildNode();

    public abstract void execute(SpellSequence sequence, SpellCtx ctx);
}
