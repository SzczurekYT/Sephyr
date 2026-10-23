package yt.szczurek.sephyr;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import yt.szczurek.sephyr.entity.SpellEntity;

public class SephyrMod implements ModInitializer {

    static {
        FabricEntityDataRegistry.register(Sephyr.identifier("spell_entity_spell_data"), SpellEntity.SPELL_SERIALIZER);
    }

    @Override
    public void onInitialize() {
        Balm.initializeMod(Sephyr.MOD_ID, FabricLoadContext.INSTANCE, Sephyr::init);
    }
}
