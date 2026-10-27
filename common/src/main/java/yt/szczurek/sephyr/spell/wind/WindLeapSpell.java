package yt.szczurek.sephyr.spell.wind;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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
    public static MapCodec<WindLeapSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.fieldOf("HasAdditionalLift").forGetter(spell -> spell.hasAdditionalLift),
            Codec.BOOL.fieldOf("IsReverse").forGetter(spell -> spell.isReverse)
    ).apply(instance, WindLeapSpell::new));
    public static final double LEAP_FORCE = 0.15;
    public static final double ADDITIONAL_LIFT_FORCE = 0.3;
    public static final double LOOK_EFFECT_ON_DIRECTION_FRACTION = 0.15;
    public static final int DURATION_TICKS = 30; // 1.5s

    private final boolean hasAdditionalLift;
    private final boolean isReverse;
    private EntityEffectExecutor executor = null;

    public WindLeapSpell(boolean hasAdditionalLift, boolean isReverse) {
        this.hasAdditionalLift = hasAdditionalLift;
        this.isReverse = isReverse;
    }

    @Override
    public SpellDefinition<?> getDefinition() {
        return SephyrSpells.WIND_LEAP.value();
    }

    @Override
    public void tick(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.tick(spellEntity, ctx);

        Entity caster = ctx.castingEntity().getEntity(spellEntity.level(), Entity.class);
        if (caster == null) {
            return;
        }

        Vec3 leapVector = calculateLeapVector(ctx, spellEntity, caster);
        caster.addDeltaMovement(leapVector);

        if (caster instanceof ServerPlayer player) {
            player.connection.send(new ClientboundSetEntityMotionPacket(player));
        }
    }

    public Vec3 calculateLeapVector(RefSpellCtx ctx, SpellEntity spellEntity, Entity caster) {
        double reverseMultiplier = isReverse ? -1 : 1;
        Vec3 castDir = ctx.castDirection();
        Vec3 lookDir = caster.getLookAngle().scale(reverseMultiplier);
        double timeFraction = (double) spellEntity.tickCount / DURATION_TICKS;
        double leapForceTimeMultiplier = calculateForceTimeMultiplier(timeFraction);
        Vec3 force = castDir.lerp(lookDir, LOOK_EFFECT_ON_DIRECTION_FRACTION).normalize().scale(LEAP_FORCE * leapForceTimeMultiplier);
        if (hasAdditionalLift) {
            double liftTimeMultiplier = calculateLiftTimeMultiplier(timeFraction);
            double liftNeedY = ctx.castDirection().y * reverseMultiplier;
            double liftNeedMultiplier = calculateLiftNeedMultiplier(liftNeedY);
            double liftMultiplier = liftTimeMultiplier * liftNeedMultiplier;
            force = force.add(0.0, ADDITIONAL_LIFT_FORCE * liftMultiplier, 0.0);
        }
        return force;
    }

    public double calculateLiftTimeMultiplier(double timeFraction) {
        return Math.pow(1 - timeFraction, 5.9) * 0.9;
    }

    public double calculateLiftNeedMultiplier(double y) {
        y = Math.max(y, -0.15);
        return Math.min(Math.pow(1 - y + 0.46, 4.3) * 0.27, 1.0);
    }

    public double calculateForceTimeMultiplier(double timeFraction) {
        return Math.pow(1 - timeFraction + 0.1, 3.8) * 0.58 + 0.8;
    }



    @Override
    public void runClient(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.runClient(spellEntity, ctx);
        Level level = spellEntity.level();
        Entity caster = ctx.castingEntity().getEntity(level, Entity.class);
        if (caster == null) {
            return;
        }
        FX fx = FXHelper.getFX(Sephyr.identifier("leap"));
        if (fx != null) {
            executor = new EntityEffectExecutor(fx, level, caster, EntityEffectExecutor.AutoRotate.NONE);
            executor.setOffset(0.0, -caster.getEyeHeight(), 0.0);
            updateVfxRotation(ctx, spellEntity, caster);
            executor.setAllowMulti(true);
            executor.start();
        }
    }

    @Override
    public void clientTick(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.clientTick(spellEntity, ctx);
        Entity caster = ctx.castingEntity().getEntity(spellEntity.level(), Entity.class);
        if (caster == null) {
            return;
        }
        updateVfxRotation(ctx, spellEntity, caster);
    }

    private void updateVfxRotation(RefSpellCtx ctx, SpellEntity spellEntity, Entity caster) {
        if (executor == null) {
            return;
        }
        Vec3 leapVector = calculateLeapVector(ctx, spellEntity, caster);
        executor.setRotation(new Quaternionf().rotationTo(
                new Vector3f(0.0f, 0.0f, -1.0f),
                leapVector.reverse().toVector3f()
        ));
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
        Pair<Vec3, Boolean> directionAdditionaLiftPair = calculateCastDirection(spellCtx, direction);
        spellCtx.castDirection = directionAdditionaLiftPair.getFirst();
        spellCtx.runSpell(new WindLeapSpell(directionAdditionaLiftPair.getSecond(), direction == WindDirection.BACK));
        return Command.SINGLE_SUCCESS;
    }

    private static Pair<Vec3, Boolean> calculateCastDirection(SpellCtx ctx, @NonNull WindDirection direction) {
        Vec3 leapDirection;

        switch (direction) {
            case FORWARD -> leapDirection = ctx.castDirection;
            case BACK -> leapDirection = ctx.castDirection.reverse();
            case UP -> leapDirection = Vec3.Y_AXIS;
            case null -> throw new IllegalArgumentException("what idiot set the direction to null?");
        }
        boolean additionalLift = direction == WindDirection.FORWARD && leapDirection.y > -0.15
                || direction == WindDirection.BACK && leapDirection.y < 0.15;
        return new Pair<>(leapDirection, additionalLift);
    }
}
