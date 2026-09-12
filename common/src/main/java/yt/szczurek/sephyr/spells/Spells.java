package yt.szczurek.sephyr.spells;

import com.mojang.brigadier.CommandDispatcher;
import yt.szczurek.sephyr.spells.wind.WindBeamEffect;
import yt.szczurek.sephyr.spells.wind.WindLeapEffect;

import java.util.function.BiConsumer;

public class Spells {
    private static final CommandDispatcher<SpellCtx> dispatcher = new CommandDispatcher<>();

    public static void register(BiConsumer<String, Spell> consumer) {
        register(consumer, "wind_leap", new WindLeapEffect("Wind leap"));
        register(consumer, "wind_beam", new WindBeamEffect("Wind beam"));
    }

    private static void register(BiConsumer<String, Spell> consumer, String name, Spell spell) {
        consumer.accept(name, spell);
        dispatcher.register(spell.getNode());
    }
}
