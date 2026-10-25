package yt.szczurek.sephyr.spell.wind;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.MapCodec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.entity.SpellEntity;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.*;

public class WindLeapSpell extends Spell {
    public static MapCodec<WindLeapSpell> CODEC = MapCodec.unit(WindLeapSpell::new);

    @Override
    public SpellDefinition<?> getDefinition() {
        return SephyrSpells.WIND_LEAP.value();
    }

    @Override
    public void run(SpellEntity spellEntity, RefSpellCtx ctx) {
        Entity caster = ctx.castingEntity().getEntity(spellEntity.level(), Entity.class);
        if (caster == null) {
            return;
        }

        caster.setDeltaMovement(ctx.castDirection().scale(3.0));

        if (caster instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    @Override
    public void runClient(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.runClient(spellEntity, ctx);
        Level level = spellEntity.level();
        FX fx = FXHelper.getFX(Sephyr.identifier("leap"));
        if (fx != null) {
            EntityEffectExecutor executor = new EntityEffectExecutor(fx, level, spellEntity, EntityEffectExecutor.AutoRotate.NONE);
            executor.setRotation(new Quaternionf().rotationTo(new Vector3f(0.0f, 0.0f, -1.0f), ctx.castDirection().reverse().toVector3f()));
            executor.start();
        }
    }

    public enum WindDirection {
        FORWARD,
        BACK,
        UP
    }

    public static @NotNull CommandNode<SpellCtx> buildNode() {
        var forward = literal(Words.FORWARD).executes(ctx -> exec(ctx, WindDirection.FORWARD));
        var reverse = literal(Words.REVERSE).executes(ctx -> exec(ctx, WindDirection.BACK));
        var up = literal(Words.UP).executes(ctx -> exec(ctx, WindDirection.UP));
        return literal(Words.BLOW).then(literal(Words.SELF)
                .then(forward)
                .then(reverse)
                .then(up).
                executes(ctx -> exec(ctx, WindDirection.FORWARD))
        ).build();
    }

    static int exec(CommandContext<SpellCtx> ctx, @NotNull WindDirection direction) {
        SpellCtx spellCtx = ctx.getSource();
        spellCtx.castPos = spellCtx.castingEntity.position();
        spellCtx.castDirection = calculateLeapDirection(spellCtx, direction);
        spellCtx.runSpell(new WindLeapSpell());
        return Command.SINGLE_SUCCESS;
    }

    private static Vec3 calculateLeapDirection(SpellCtx ctx, @NonNull WindDirection direction) {
        Vec3 leapDirection;
        switch (direction) {
            case FORWARD -> leapDirection = ctx.castDirection;
            case BACK -> leapDirection = ctx.castDirection.reverse();
            case UP -> leapDirection = Vec3.Y_AXIS.lerp(ctx.castDirection, 0.25);
            case null -> throw new IllegalArgumentException("what idiot set the direction to null?");
        }
        return leapDirection;
    }
}
