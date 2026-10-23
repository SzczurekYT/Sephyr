package yt.szczurek.sephyr.spell.wind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.sephyr.entity.SpellEntity;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.*;

public class WindBeamSpell extends Spell {
    public static StreamCodec<ByteBuf, WindBeamSpell> STREAM_CODEC = supplierCodec(WindBeamSpell::new);

    @Override
    public SpellDefinition<?> getDefinition() {
        return SephyrSpells.WIND_BEAM.value();
    }

    @Override
    public void run(SpellEntity spellEntity, RefSpellCtx ctx) {

    }

    public static @NotNull CommandNode<SpellCtx> buildNode() {
        return literal(Words.CHARGE).executes(ctx -> {
            ctx.getSource().runSpell(new WindBeamSpell());
            return Command.SINGLE_SUCCESS;
        }).build();
    }
}
