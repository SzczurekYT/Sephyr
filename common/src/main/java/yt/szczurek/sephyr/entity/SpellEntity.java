package yt.szczurek.sephyr.entity;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.codec.ByteBufCodecs;
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
import yt.szczurek.sephyr.spell.SpellDefinition;

import java.util.Optional;

public class SpellEntity extends Entity {
    public static Codec<Pair<Spell, RefSpellCtx>> SPELL_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Spell.DISPATCH_CODEC.fieldOf("Spell").forGetter(Pair::getFirst),
            RefSpellCtx.CODEC.fieldOf("Ctx").forGetter(Pair::getSecond)
    ).apply(instance, Pair::new));
    public static final EntityDataSerializer<Optional<Pair<Spell, RefSpellCtx>>> SPELL_SERIALIZER = EntityDataSerializer.forValueType(
            ByteBufCodecs.fromCodec(SPELL_CODEC).apply(ByteBufCodecs::optional));
    public static final EntityDataAccessor<Optional<Pair<Spell, RefSpellCtx>>> SPELL =
            SynchedEntityData.defineId(SpellEntity.class, SPELL_SERIALIZER);

    private boolean hasStarted = false;

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
        if (hasStarted || pair.isEmpty()) {
            return;
        }
        hasStarted = true;
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
        Optional<Pair<Spell, RefSpellCtx>> pair = entityData.get(SPELL);
        if (pair.isEmpty()) {
            return;
        }
        Spell spell = pair.get().getFirst();
        SpellDefinition<?> definition = spell.getDefinition();
        if (this.tickCount > definition.lifetimeTicks()) {
            remove(RemovalReason.DISCARDED);
            return;
        }
        RefSpellCtx ctx = pair.get().getSecond();
        if (!definition.ticking()) {
            return;
        }
        if (level().isClientSide()) {
            spell.clientTick(this, ctx);
        } else {
            spell.tick(this, ctx);
        }
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel serverLevel, @NonNull DamageSource damageSource, float v) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput valueInput) {
        hasStarted = valueInput.getBooleanOr("HasStarted", false);
        entityData.set(SPELL, valueInput.read("Spell", SPELL_CODEC));
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput valueOutput) {
        valueOutput.putBoolean("HasStarted", hasStarted);
        Optional<Pair<Spell, RefSpellCtx>> spellPair = entityData.get(SPELL);
        spellPair.ifPresent(pair -> valueOutput.store("Spell", SPELL_CODEC, pair));
    }
}
