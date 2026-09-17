package yt.szczurek.sephyr.spell.casting;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import yt.szczurek.sephyr.SephyrSounds;
import yt.szczurek.sephyr.SpellCastAttemptResult;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.spell.Spells;
import yt.szczurek.sephyr.spell.Words;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ServerCastingManager {
    public static void addWord(Entity entity, String word) {
        if (word.equals(Words.DEBUG)) {
            CastWords.clear(entity);
            return;
        }
        if (word.equals(Words.WIND)) {
            entity.level().playSound(null, entity.blockPosition(), SephyrSounds.SPELL_START_WIND.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
            entity.level().playSound(null, entity.blockPosition(), SephyrSounds.SPELL_RUNNING_WIND.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
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
