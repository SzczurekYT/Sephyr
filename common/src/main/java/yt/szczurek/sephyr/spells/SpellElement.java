package yt.szczurek.sephyr.spells;

import java.util.HashMap;

public record SpellElement(String word, int color) {
    private static final HashMap<String, SpellElement> byWord = new HashMap<>();
    public static SpellElement FIRE = new SpellElement(Words.FIRE, 15426306);
    public static SpellElement WIND = new SpellElement(Words.WIND, 12573383);

    public SpellElement {
        byWord.put(word, this);
    }

    public static SpellElement byWord(String word) {
        return byWord.get(word);
    }
}
