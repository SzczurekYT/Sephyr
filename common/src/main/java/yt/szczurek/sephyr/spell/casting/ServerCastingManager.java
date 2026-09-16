package yt.szczurek.sephyr.spell.casting;

import net.minecraft.world.entity.Entity;
import yt.szczurek.sephyr.SpellCastAttemptResult;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.spell.Spells;
import yt.szczurek.sephyr.spell.Words;

import java.util.List;

public class ServerCastingManager {
    public static void addWord(Entity entity, String word) {
        if (word.equals(Words.DEBUG)) {
            CastWords.clear(entity);
            return;
        }

        CastWords.add(entity, word);
    }

    public static void onChanged(Entity entity, List<String> words) {
        if (words.isEmpty()) {
            return;
        }

        SpellCastAttemptResult result = Spells.tryExecute(words, entity, false);

        if (result == SpellCastAttemptResult.EXECUTED) {
            CastWords.clear(entity);
        }
    }

    public static void onTimeout(Entity entity) {
        List<String> words = CastWords.get(entity);

        if (words.isEmpty()) {
            return;
        }

        SpellCastAttemptResult result = Spells.tryExecute(words, entity, true);

        CastWords.clear(entity);
    }
}
