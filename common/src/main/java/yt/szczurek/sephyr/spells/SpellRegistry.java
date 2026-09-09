package yt.szczurek.sephyr.spells;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import yt.szczurek.sephyr.SephyrRegistries;
import yt.szczurek.sephyr.spells.wind.WindLeapEffect;

import java.util.*;

import static yt.szczurek.sephyr.Sephyr.identifier;

public class SpellRegistry {
    private static final SpellRegistry instance = new SpellRegistry();
    private final HashMap<SpellSequence, String> spells = new HashMap<>();
    private final CommandDispatcher<SpellCtx> dispatcher = new CommandDispatcher<>();

    public void register(SpellSequence spell, String name) {
        spells.put(spell, name);
    }

    public void register(SpellDefinition definition) {
        dispatcher.register(definition.getNode());
    }

    public static SpellRegistry get() {
        return instance;
    }

    public static void registerSpells(WritableRegistry<CommandNode<SpellCtx>> registry) {
        SpellRegistry self = get();
        self.register(registry, "wind_leap", new WindLeapEffect());
    }

    private void register(WritableRegistry<CommandNode<SpellCtx>> registry, String name, Spell impl) {
        ResourceKey<CommandNode<SpellCtx>> key = ResourceKey.create(SephyrRegistries.SPELL_IMPL, identifier(name));
        CommandNode<SpellCtx> node = impl.buildNode();
        registry.register(key, node, RegistrationInfo.BUILT_IN);
    }

    public static void registerSpells() {
        SpellRegistry registry = get();
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ ɛvi"), "leap");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ wˈanga"), "wind projectile");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ ɛkɐsuɾɯ"), "wind charge");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ ãntikɔ vəˈluɡoː"), "wind gravity sphere");
        registry.register(new SpellSequence("ʔalɪˈvɑn vɛɾtɛɾ lirɔ jɑːkɾidɔ fɒksɑm vəˈluɡoː"), "wind lift column");
    }


}
