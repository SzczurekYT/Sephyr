package yt.szczurek.sephyr.entity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;
import yt.szczurek.sephyr.registry.SephyrEntities;
import yt.szczurek.sephyr.spell.RefSpellCtx;
import yt.szczurek.sephyr.spell.Spell;

import java.util.Optional;

public class SpellEntity extends Entity {
    public static final EntityDataSerializer<Optional<Pair<Spell, RefSpellCtx>>> SPELL_SERIALIZER = EntityDataSerializer.forValueType(
            StreamCodec.composite(
                            Spell.DISPATCH_CODEC, Pair::getFirst,
                            RefSpellCtx.STREAM_CODEC, (Pair<Spell, RefSpellCtx> p) -> p.getSecond(),
                            Pair::new)
                    .apply(ByteBufCodecs::optional)
    );
    public static final EntityDataAccessor<Optional<Pair<Spell, RefSpellCtx>>> SPELL =
            SynchedEntityData.defineId(SpellEntity.class, SPELL_SERIALIZER);

    public SpellEntity(EntityType<?> type, Level level) {
        super(type, level);
    }

    public SpellEntity(Level level, RefSpellCtx ctx, Spell spell) {
        super(SephyrEntities.SPELL.value(), level);
        setPos(ctx.castPos());
        lookAt(EntityAnchorArgument.Anchor.EYES, ctx.castPos().add(ctx.castDirection()));
        entityData.set(SPELL, Optional.of(new Pair<>(spell, ctx)));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder builder) {
        builder.define(SPELL, Optional.empty());
    }

    @Override
    public void onSyncedDataUpdated(@NonNull EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!SPELL.equals(accessor)) {
            return;
        }

        Optional<Pair<Spell, RefSpellCtx>> pair = entityData.get(SPELL);
        if (pair.isEmpty()) {
            return;
        }
        Spell spell = pair.get().getFirst();
        RefSpellCtx ctx = pair.get().getSecond();
        if (this.level().isClientSide()) {
            spell.runClient(this, ctx);
        } else {
            spell.run(this, ctx);
        }
    }

    @Override
    public void tick() {
        Spell spell = entityData.get(SPELL).map(Pair::getFirst).orElse(null);
        if (spell == null) {
            return;
        }
        if (this.tickCount > spell.getDefinition().lifetimeTicks()) {
            remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {

    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {

    }
}
