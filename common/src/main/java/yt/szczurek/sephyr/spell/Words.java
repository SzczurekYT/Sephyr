package yt.szczurek.sephyr.spell;

import org.jetbrains.annotations.UnmodifiableView;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Words {
    public static final String SELF = "ɛvi";
    public static final String SHOT = "wˈanga";
    public static final String CHARGE = "ɛkɐsuɾɯ";
    public static final String CONJURE = "jɑːkɾidɔ";
    public static final String BLOW = "vɛɾtɛɾ";
    public static final String FORWARD = "ˈɔnːdˌɔː";
    public static final String REVERSE = "ãntikɔ";
    public static final String UP = "fɒksɑm";
    public static final String DOWN = "ˈunvaksɒm";
    public static final String AND = "lirɔ";
    public static final String FORCE = "vəˈluɡoː";
    public static final String SPHERE = "plɒka";
    public static final String PLANE = "toŋk";
    public static final String WIND = "ʔalɪˈvɑn";
    public static final String FIRE = "ibaŋk";
    public static final String LIGHT = "prizim";
    public static final String DEBUG = "kɛjfida";

    private static final List<String> WORDS = collectWordList();

    public static @UnmodifiableView List<String> getList() {
        return Collections.unmodifiableList(WORDS);
    }

    private static ArrayList<String> collectWordList() {
        ArrayList<String> result = new ArrayList<>();

        for (Field field : Words.class.getFields()) {
            if (field.getType() != String.class || !Modifier.isStatic(field.getModifiers())) {
                continue;
            }

            try {
                result.add((String) field.get(null));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        return result;
    }
}
