package yt.szczurek.sephyr.spell.wind;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.tree.CommandNode;
import yt.szczurek.sephyr.spell.Spell;
import yt.szczurek.sephyr.spell.SpellCtx;
import yt.szczurek.sephyr.spell.SpellElement;
import yt.szczurek.sephyr.spell.Words;

public class WindBeamEffect extends Spell {
    public WindBeamEffect(String name) {
        super(name, SpellElement.WIND);
    }

    @Override
    public CommandNode<SpellCtx> buildNode() {
        return literal(Words.CHARGE).executes(context -> {
            return Command.SINGLE_SUCCESS;
        }).build();
    }
}
