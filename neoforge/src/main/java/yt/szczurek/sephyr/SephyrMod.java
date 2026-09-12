package yt.szczurek.sephyr;


import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.Spells;

@Mod(Sephyr.MOD_ID)
public class SephyrMod {

    public static final Registry<CommandNode<SpellCtx>> SPELL_IMPL_REGISTRY = new RegistryBuilder<>(SephyrRegistries.SPELL_IMPL).create();

    public SephyrMod(IEventBus eventBus) {
        Sephyr.init();
        
        eventBus.addListener(SephyrMod::registerRegistries);
        eventBus.addListener(SephyrMod::register);
    }

    public static void registerRegistries(NewRegistryEvent event) {
        event.register(SPELL_IMPL_REGISTRY);
    }

    public static void register(RegisterEvent event) {
        event.register(SephyrRegistries.SPELL, registry -> {
            Spells.register((name, spell) -> {
                registry.register(Sephyr.identifier(name), spell);
            });
        });
    }
}
