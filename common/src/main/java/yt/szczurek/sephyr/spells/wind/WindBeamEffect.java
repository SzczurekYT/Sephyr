package yt.szczurek.sephyr.spells.wind;

import com.mojang.brigadier.tree.CommandNode;
import yt.szczurek.sephyr.spells.Spell;
import yt.szczurek.sephyr.spells.SpellCtx;
import yt.szczurek.sephyr.spells.SpellElement;

public class WindBeamEffect extends Spell {
    public WindBeamEffect(String name) {
        super(name, SpellElement.WIND);
    }

    @Override
    public CommandNode<SpellCtx> buildNode() {
        return null;
    }
}
