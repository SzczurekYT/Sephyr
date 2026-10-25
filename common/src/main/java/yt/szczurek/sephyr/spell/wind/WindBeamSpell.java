package yt.szczurek.sephyr.spell.wind;

import com.lowdragmc.photon.client.fx.EntityEffectExecutor;
import com.lowdragmc.photon.client.fx.FX;
import com.lowdragmc.photon.client.fx.FXHelper;
import com.lowdragmc.photon.client.fx.FXRuntime;
import com.lowdragmc.photon.client.gameobject.EmptyFXObject;
import com.lowdragmc.photon.client.gameobject.IFXObject;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.Constant;
import com.lowdragmc.photon.client.gameobject.emitter.data.number.NumberFunction3;
import com.lowdragmc.photon.client.gameobject.emitter.particle.ParticleEmitter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.entity.SpellEntity;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.*;

import java.util.function.Predicate;

public class WindBeamSpell extends Spell {
    public static MapCodec<WindBeamSpell> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            RotatedBox.CODEC.fieldOf("Box").forGetter(spell -> spell.box),
            Codec.BOOL.fieldOf("Reverse").forGetter(spell -> spell.reverse)
    ).apply(instance, WindBeamSpell::new));
    public static final float BOX_DEPTH = 24.0f;
    public static final float BOX_DEPTH_EXPANDED = 12.0f;
    public static final float BOX_DIAMETER = 1.0f;
    public static final float BOX_DIAMETER_EXPANDED = 3.0f;
    public static final float FORCE_STRENGTH = 0.4f;
    public static final float FORCE_STRENGTH_EXPANDED = 0.2f;
    public static final int DURATION_TICKS = 7 * 20 + 10; // 7.5s
    private static final Predicate<Entity> EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE);

    private RotatedBox box;
    private final boolean reverse;

    public WindBeamSpell(RotatedBox box, boolean reverse) {
        this.box = box;
        this.reverse = reverse;
    }

    @Override
    public SpellDefinition<?> getDefinition() {
        return SephyrSpells.WIND_BEAM.value();
    }

    @Override
    public void tick(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.tick(spellEntity, ctx);

        Level level = spellEntity.level();

        Vec3 forceDirection = spellEntity.getLookAngle();
        if (reverse) {
            forceDirection = forceDirection.scale(-1.0);
        }

        boolean expanded = box.diameter() > BOX_DIAMETER;

        forceDirection = forceDirection.scale(expanded ? FORCE_STRENGTH_EXPANDED : FORCE_STRENGTH);

        Entity caster = ctx.castingEntity().getEntity(spellEntity.level(), Entity.class);
        if (caster != null) {
            box = boxFromCaster(caster, box.diameter(), box.depth());
            spellEntity.setXRot((float) Math.toDegrees(box.angleX()));
            spellEntity.setYRot((float) Math.toDegrees(box.angleY()));
            spellEntity.setPos(new Vec3(box.center()));
        }

        for (Entity entityToBeLaunched : level.getEntitiesOfClass(Entity.class, box.aabb(), EFFECT_PREDICATE)) {
            entityToBeLaunched.checkFallDistanceAccumulation();
            if (entityToBeLaunched.canSimulateMovement()
                    && box.intersectAABB(entityToBeLaunched.getBoundingBox())
                    && !(entityToBeLaunched instanceof Player player && player.getAbilities().flying)
                    && !entityToBeLaunched.isPassenger()) {
                entityToBeLaunched.addDeltaMovement(forceDirection);

                if (entityToBeLaunched instanceof ServerPlayer player) {
                    player.connection.send(new ClientboundSetEntityMotionPacket(player));
                }
            }
        }
    }

    @Override
    public void runClient(SpellEntity spellEntity, RefSpellCtx ctx) {
        super.runClient(spellEntity, ctx);
        Level level = spellEntity.level();
        Entity caster = ctx.castingEntity().getEntity(level, Entity.class);
        if (caster == null) {
            return;
        }
        FX fx = FXHelper.getFX(Sephyr.identifier("wind_beam"));
        if (fx == null) {
            return;
        }

        EntityEffectExecutor executor = new EntityEffectExecutor(fx, level, caster, EntityEffectExecutor.AutoRotate.LOOK);
        executor.start();
        FXRuntime runtime = executor.getRuntime();
        if (runtime != null) {
            setEmitterShape(runtime);
        } else {
            Sephyr.LOG.error("FXRuntime was null");
        }
    }

    public void setEmitterShape(FXRuntime runtime) {
        IFXObject emitterObject = runtime.findObject("particle_emitter");
        if (!(emitterObject instanceof ParticleEmitter emitter)) {
            return;
        }

        var values = emitter.runtime();
        values.duration.set(DURATION_TICKS);

        Constant diameter = new Constant(box.diameter());
        Constant depth = new Constant(box.depth());
        emitter.updateRotation(new Vector3f(0f, reverse ? (float) Math.PI : 0f, 0f));
        values.shape.scale.set(new NumberFunction3(
                diameter,
                depth,
                diameter
        ));

        IFXObject offsetArm = runtime.findObject("offset_arm");
        if (!(offsetArm instanceof EmptyFXObject)) {
            return;
        }
        offsetArm.transform().localPosition(new Vector3f(box.depth() / 2.0f, 0.0f, 0.0f));
    }

    public static @NotNull CommandNode<SpellCtx> buildNode() {
        var reverseNode = literal(Words.REVERSE).executes(exec(false, true));
        var expandedReverseNode = literal(Words.REVERSE).executes(exec(true, true));
        var expandedNode = literal(Words.EXPAND).executes(exec(true, false))
                .then(expandedReverseNode);
        return literal(Words.CHARGE)
                .executes(exec(false, false))
                .then(expandedNode)
                .then(reverseNode)
                .build();
    }

    private static @NonNull Command<SpellCtx> exec(boolean expanded, boolean reverse) {
        return ctx -> {
            SpellCtx spellCtx = ctx.getSource();
            float diameter = expanded ? BOX_DIAMETER_EXPANDED : BOX_DIAMETER;
            float depth = expanded ? BOX_DEPTH_EXPANDED : BOX_DEPTH;
            RotatedBox box = boxFromCaster(spellCtx.castingEntity, diameter, depth);
            spellCtx.castPos = new Vec3(box.center());
            spellCtx.runSpell(new WindBeamSpell(box, reverse));
            return Command.SINGLE_SUCCESS;
        };
    }

    private static RotatedBox boxFromCaster(Entity caster, float diameter, float depth) {
        Vector3f center = calculateBoxCenter(caster, depth);
        Vec3 castDirection = caster.getLookAngle();
        float pitch = (float)Math.asin(-castDirection.y / Math.sqrt(castDirection.x * castDirection.x + castDirection.y * castDirection.y + castDirection.z * castDirection.z));
        float yaw = (float) Math.atan2(-castDirection.x, castDirection.z);
        return new RotatedBox(center, diameter, depth, pitch, yaw);
    }

    public static Vector3f calculateBoxCenter(Entity caster, float depth) {
        Vec3 origin = caster.getEyePosition();
        Vec3 castDirection = caster.getLookAngle();
        // We don't want to get caught in our own spell
        float casterHeight = caster.getBbHeight();
        float casterEyeHeight = caster.getEyeHeight();
        double offset = Math.max(casterEyeHeight, casterHeight - casterEyeHeight);
        offset = Math.max(caster.getBbWidth() / 2.0, offset);
        offset = Math.max(offset, 1.0);
        return origin.add(castDirection.scale(depth / 2.0 + offset)).toVector3f();
    }
}
