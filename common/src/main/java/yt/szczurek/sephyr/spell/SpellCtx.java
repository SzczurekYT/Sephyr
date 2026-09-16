package yt.szczurek.sephyr.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class SpellCtx {
    public @NotNull SpellElement element;
    public @NotNull Entity castingEntity;
    public @NotNull Vec3 castPos;
    public @NotNull Vec3 castDirection;
    public @Nullable Entity targetEntity;
    public @Nullable BlockPos targetPos;

    public SpellCtx(@NotNull SpellElement element, @NotNull Entity castingEntity, @NotNull Vec3 castPos, @NotNull Vec3 castDirection, @Nullable Entity targetEntity) {
        this.element = element;
        this.castingEntity = castingEntity;
        this.castPos = castPos;
        this.castDirection = castDirection;
        this.targetEntity = targetEntity;
    }

    public SpellCtx(@NotNull SpellElement element, @NotNull Entity castingEntity, @NotNull Vec3 castPos, @NotNull Vec3 castDirection, @Nullable BlockPos targetPos) {
        this.element = element;
        this.castingEntity = castingEntity;
        this.castPos = castPos;
        this.castDirection = castDirection;
        this.targetPos = targetPos;
    }

    public SpellCtx(@NotNull SpellElement element, @NotNull Entity castingEntity, @NotNull Vec3 castPos, @NotNull Vec3 castDirection) {
        this.element = element;
        this.castingEntity = castingEntity;
        this.castPos = castPos;
        this.castDirection = castDirection;
    }

    public static SpellCtx fromEntity(Entity entity, SpellElement element) {
        return new SpellCtx(element, entity, entity.getEyePosition(), entity.getLookAngle());
    }
}
