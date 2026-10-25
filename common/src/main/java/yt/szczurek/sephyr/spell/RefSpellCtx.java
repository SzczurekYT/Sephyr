package yt.szczurek.sephyr.spell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;


public record RefSpellCtx(
        @NotNull EntityReference<Entity> castingEntity,
        @NotNull Vec3 castPos,
        @NotNull Vec3 castDirection,
        @Nullable EntityReference<Entity> targetEntity,
        @Nullable BlockPos targetPos
) {
    public static Codec<RefSpellCtx> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    EntityReference.<Entity>codec().fieldOf("Caster").forGetter(RefSpellCtx::castingEntity),
                    Vec3.CODEC.fieldOf("Pos").forGetter(RefSpellCtx::castPos),
                    Vec3.CODEC.fieldOf("Direction").forGetter(RefSpellCtx::castDirection),
                    EntityReference.<Entity>codec().optionalFieldOf("TargetEntity").forGetter(o -> Optional.ofNullable(o.targetEntity)),
                    BlockPos.CODEC.optionalFieldOf("TargetPos").forGetter(refSpellCtx -> Optional.ofNullable(refSpellCtx.targetPos))
            ).apply(instance, RefSpellCtx::fromOptionals));
    // We want them as params, so it matches StreamCodec.composite signature
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static RefSpellCtx fromOptionals(
            @NotNull EntityReference<Entity> castingEntity,
            @NotNull Vec3 castPos,
            @NotNull Vec3 castDirection,
            Optional<EntityReference<Entity>> targetEntity,
            Optional<BlockPos> targetPos
    ) {
        return new RefSpellCtx(
                castingEntity,
                castPos,
                castDirection,
                targetEntity.orElse(null),
                targetPos.orElse(null)
        );
    }

    public static RefSpellCtx fromSpellCtx(SpellCtx ctx) {
        return new RefSpellCtx(
                EntityReference.of(ctx.castingEntity),
                ctx.castPos,
                ctx.castDirection,
                EntityReference.of(ctx.targetEntity),
                ctx.targetPos
        );
    }

}
