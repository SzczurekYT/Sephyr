package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import yt.szczurek.sephyr.SpellCastAttemptResult;
import yt.szczurek.sephyr.spell.wind.WindBeamEffect;
import yt.szczurek.sephyr.spell.wind.WindLeapEffect;

import java.util.List;
import java.util.function.BiConsumer;

public class Spells {
    public static final CommandDispatcher<SpellCtx> dispatcher = new CommandDispatcher<>();

    public static void register(BiConsumer<String, Spell> consumer) {
        register(consumer, "wind_leap", new WindLeapEffect("Wind leap"));
        register(consumer, "wind_beam", new WindBeamEffect("Wind beam"));
    }

    private static void register(BiConsumer<String, Spell> consumer, String name, Spell spell) {
        consumer.accept(name, spell);
        dispatcher.register(spell.getNode());
    }

    public static SpellCastAttemptResult tryExecute(List<String> spell, Entity entity, boolean force) {
        String spellString = String.join(" ", spell);
        SpellCtx ctx = SpellCtx.fromEntity(entity, SpellElement.byWord(spell.getFirst()));
        ParseResults<SpellCtx> result = dispatcher.parse(spellString, ctx);


        if (!result.getExceptions().isEmpty()) {
            return SpellCastAttemptResult.INVALID;
        }

        if (result.getContext().getNodes().isEmpty()) {
            return SpellCastAttemptResult.INVALID;
        }

        boolean isFinished = result.getContext().getNodes().getLast().getNode().getChildren().isEmpty();

        if (!isFinished && !force) {
            return SpellCastAttemptResult.NOT_FINISHED;
        }

        try {
            dispatcher.execute(result);
        } catch (CommandSyntaxException _) {
            // TODO: More robust catching
            return SpellCastAttemptResult.INVALID;
        }

        return SpellCastAttemptResult.EXECUTED;
    }
}
