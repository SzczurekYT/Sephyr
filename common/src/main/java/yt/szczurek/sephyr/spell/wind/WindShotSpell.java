package yt.szczurek.sephyr.spell.wind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.Spell;
import yt.szczurek.sephyr.spell.SpellCtx;
import yt.szczurek.sephyr.spell.SpellDefinition;
import yt.szczurek.sephyr.spell.Words;

public class WindShotSpell extends Spell {
    public static StreamCodec<ByteBuf, WindShotSpell> STREAM_CODEC = supplierCodec(WindShotSpell::new);

    @Override
    public SpellDefinition<?> getDefinition() {
        return SephyrSpells.WIND_SHOT.value();
    }

    public static @NotNull CommandNode<SpellCtx> buildNode() {
        return literal(Words.SHOT).executes(ctx -> {

            SpellCtx spellCtx = ctx.getSource();
            Entity caster = spellCtx.castingEntity;
            if (!(caster instanceof LivingEntity livingCaster)) {
                return Command.SINGLE_SUCCESS;
            }
            Level level = caster.level();
            if (!(level instanceof ServerLevel serverLevel)) {
                return Command.SINGLE_SUCCESS;
            }

            Projectile.spawnProjectileFromRotation(
                    (_, _, _) -> new WindCharge(level, caster.position().x(), caster.getEyePosition().y(), caster.position().z(), Vec3.ZERO),
                    serverLevel,
                    ItemStack.EMPTY,
                    livingCaster,
                    0.0F,
                    1.5F,
                    0.0F
            );
            level.playSound(
                    null,
                    caster.getX(),
                    caster.getY(),
                    caster.getZ(),
                    SoundEvents.WIND_CHARGE_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
            );

            return Command.SINGLE_SUCCESS;
        }).build();
    }
}
