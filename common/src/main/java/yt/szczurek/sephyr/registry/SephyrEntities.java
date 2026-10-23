package yt.szczurek.sephyr.registry;

import net.blay09.mods.balm.world.entity.BalmEntityTypeRegistrar;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import yt.szczurek.sephyr.entity.SpellEntity;

import java.util.function.Supplier;

public class SephyrEntities {
    public static Holder<EntityType<SpellEntity>> SPELL;
    private static final Supplier<EntityType.Builder<SpellEntity>> SPELL_BUILDER =
            () -> EntityType.Builder.of(
                           (EntityType<SpellEntity> entityType, Level level) -> new SpellEntity(entityType, level),
                            MobCategory.MISC
                    )
                    .noLootTable()
                    .sized(0.0F, 0.0F)
                    .clientTrackingRange(10)
                    .updateInterval(1);

    public static void register(BalmEntityTypeRegistrar entities) {
        SPELL = entities.register("spell", SPELL_BUILDER).asHolder();
    }
}
