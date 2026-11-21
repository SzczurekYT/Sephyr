package yt.szczurek.sephyr.spell.casting;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import yt.szczurek.sephyr.registry.SephyrSounds;
import yt.szczurek.sephyr.SpellCastAttemptResult;
import yt.szczurek.sephyr.attachment.CastWords;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.SpellElement;
import yt.szczurek.sephyr.spell.Words;

import java.util.List;

public class ServerCastingManager {
    private static final Component STAR_COMPONENT = Component.literal("✨").withColor(16773205); // 0xfff055
    public static void addWord(Entity entity, String word) {
        if (word.equals(Words.DEBUG)) {
            CastWords.clear(entity);
            return;
        }
        if (word.equals(Words.WIND)) {
            entity.level().playSound(null, entity.blockPosition(), SephyrSounds.SPELL_START_WIND.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        CastWords.add(entity, word);
    }

    public static void onChanged(Entity entity, List<String> words) {
        if (words.isEmpty()) {
            return;
        }

        if (entity instanceof Player player) {
            var spellComponent = Component.literal(" " + String.join(" ", words) + " ");

            var element = SpellElement.byWord(words.getFirst());
            spellComponent.withColor(element != null ? element.color() : 0xFFFFFF);

            var message = STAR_COMPONENT.copy().append(spellComponent).append(STAR_COMPONENT);
            player.sendOverlayMessage(message);
        }

        SpellCastAttemptResult result = SephyrSpells.tryExecute(words, entity, false);

        if (result == SpellCastAttemptResult.EXECUTED) {
            CastWords.clear(entity);
        }
    }

    public static void onTimeout(Entity entity) {
        List<String> words = CastWords.get(entity);

        if (words.isEmpty()) {
            return;
        }

        SpellCastAttemptResult result = SephyrSpells.tryExecute(words, entity, true);

        CastWords.clear(entity);
    }
}
