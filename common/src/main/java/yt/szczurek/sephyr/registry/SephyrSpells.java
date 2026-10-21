package yt.szczurek.sephyr.registry;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.core.BalmRegistrar;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import yt.szczurek.sephyr.SpellCastAttemptResult;
import yt.szczurek.sephyr.spell.Spell;
import yt.szczurek.sephyr.spell.SpellCtx;
import yt.szczurek.sephyr.spell.SpellElement;
import yt.szczurek.sephyr.spell.wind.WindBeamEffect;
import yt.szczurek.sephyr.spell.wind.WindLeapEffect;

import java.util.List;

public class SephyrSpells {
    public static final CommandDispatcher<SpellCtx> dispatcher = new CommandDispatcher<>();

    public static void register(BalmRegistrar.Scoped<Spell> spells) {
        register(spells, "wind_leap", new WindLeapEffect("Wind leap"));
        register(spells, "wind_beam", new WindBeamEffect("Wind beam"));
    }

    private static void register(BalmRegistrar.Scoped<Spell> spells, String name, Spell spell) {
        spells.register(name, _ -> spell);
        dispatcher.register(spell.getNode());
    }

    public static SpellCastAttemptResult tryExecute(List<String> spell, Entity entity, boolean force) {
        String spellString = String.join(" ", spell);
        SpellCtx ctx = SpellCtx.fromEntity(entity, SpellElement.byWord(spell.getFirst()));
        ParseResults<SpellCtx> result = dispatcher.parse(spellString, ctx);


        if (Balm.platform().isDevelopmentEnvironment()) {
            String msg;
            if (force) {
                msg = "DEBUG: force trying: " + spellString;
            } else {
                msg = "DEBUG: trying: " + spellString;
            }
            entity.level().getServer().getPlayerList().broadcastSystemMessage(Component.literal(msg), false);
        }

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
