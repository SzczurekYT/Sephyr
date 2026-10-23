package yt.szczurek.sephyr;


import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.neoforge.platform.runtime.NeoForgeLoadContext;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import yt.szczurek.sephyr.entity.SpellEntity;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(
            NeoForgeRegistries.ENTITY_DATA_SERIALIZERS,
            Sephyr.MOD_ID
    );
    static {
        ENTITY_DATA_SERIALIZERS.register(
                "spell_entity_spell_data",
                () -> SpellEntity.SPELL_SERIALIZER
        );
    }

    public SephyrMod(ModContainer modContainer, IEventBus eventBus) {
        final var loadContext = new NeoForgeLoadContext(modContainer, eventBus);
        Balm.initializeMod(Sephyr.MOD_ID, loadContext, Sephyr::init);

        ENTITY_DATA_SERIALIZERS.register(eventBus);
    }
}
