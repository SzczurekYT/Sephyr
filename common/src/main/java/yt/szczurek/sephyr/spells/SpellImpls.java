package yt.szczurek.sephyr.spells;

import yt.szczurek.sephyr.spells.wind.WindLeapEffect;

import java.util.function.BiConsumer;

public class SpellImpls {
    public static void register(BiConsumer<String, Spell> consumer) {
        consumer.accept("wind_leap", new WindLeapEffect());
    }
}
