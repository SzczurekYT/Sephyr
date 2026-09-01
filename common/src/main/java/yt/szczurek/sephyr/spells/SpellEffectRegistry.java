package yt.szczurek.sephyr.spells;

import net.minecraft.resources.Identifier;
import yt.szczurek.sephyr.Sephyr;
import yt.szczurek.sephyr.spells.wind.WindLeapEffect;

import java.util.HashMap;

public class SpellEffectRegistry {
    private static final SpellEffectRegistry instance = new SpellEffectRegistry();
    private final HashMap<Identifier, SpellEffect> effects = new HashMap<>();

    public void register(Identifier id, SpellEffect effect) {
        effects.put(id, effect);
    }

    public SpellEffect getEffect(Identifier id) {
        return effects.get(id);
    }

    public static SpellEffectRegistry get() {
        return instance;
    }

    public static void registerEffects() {
        SpellEffectRegistry registry = get();
        registry.register(Sephyr.identifier("wind_leap"), new WindLeapEffect(Sephyr.identifier("wind_leap")));
    }
}
