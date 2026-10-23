package yt.szczurek.sephyr.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yt.szczurek.sephyr.entity.SpellEntity;


public class SpellCtx {
    public @NotNull Entity castingEntity;
    public @NotNull Vec3 castPos;
    public @NotNull Vec3 castDirection;
    public @Nullable Entity targetEntity;
    public @Nullable BlockPos targetPos;

    public SpellCtx(@NotNull Entity castingEntity, @NotNull Vec3 castPos, @NotNull Vec3 castDirection, @Nullable Entity targetEntity, @Nullable BlockPos targetPos) {
        this.castingEntity = castingEntity;
        this.castPos = castPos;
        this.castDirection = castDirection;
        this.targetEntity = targetEntity;
        this.targetPos = targetPos;
    }

    public static SpellCtx fromEntity(Entity entity) {
        return new SpellCtx(entity, entity.getEyePosition(), entity.getLookAngle(), null, null);
    }

    public void runSpell(Spell spell) {
        Level level = castingEntity.level();
        SpellEntity spellEntity = new SpellEntity(level, RefSpellCtx.fromSpellCtx(this), spell);
        castingEntity.level().addFreshEntity(spellEntity);
    }
}
