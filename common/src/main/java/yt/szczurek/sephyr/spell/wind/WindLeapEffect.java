package yt.szczurek.sephyr.spell.wind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.sephyr.spell.*;

public class WindLeapEffect extends Spell {

    public WindLeapEffect(String name) {
        super(name, SpellElement.WIND);
    }

    @Override
    public CommandNode<SpellCtx> buildNode() {
        var forward = literal(Words.FORWARD).executes(ctx -> exec(ctx.getSource(), WindDirection.FORWARD));
        var reverse = literal(Words.REVERSE).executes(ctx -> exec(ctx.getSource(), WindDirection.BACK));
        var up = literal(Words.UP).executes(ctx -> exec(ctx.getSource(), WindDirection.UP));
        return literal(Words.BLOW).then(literal(Words.SELF)
                .then(forward)
                .then(reverse)
                .then(up).
                executes(ctx -> exec(ctx.getSource(), WindDirection.FORWARD))
        ).build();
    }


    int exec(SpellCtx ctx, @NotNull WindDirection direction) {
        Entity caster = ctx.castingEntity;
        Vec3 leapDirection;
        switch (direction) {
            case FORWARD -> leapDirection = ctx.castDirection;
            case BACK -> leapDirection = ctx.castDirection.reverse();
            case UP -> leapDirection = Vec3.Y_AXIS.lerp(ctx.castDirection, 0.25);
            case null -> throw new IllegalArgumentException("what idiot set the direction to null?");
        }
        caster.setDeltaMovement(leapDirection.scale(3.0));

        if (caster instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
        return Command.SINGLE_SUCCESS;
    }

    enum WindDirection {
        FORWARD,
        BACK,
        UP
    }
}
