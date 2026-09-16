package yt.szczurek.sephyr.spell;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;

public abstract class Spell {
    String name;
    SpellElement element;

    public Spell(String name, SpellElement element) {
        this.name = name;
        this.element = element;
    }

    public static LiteralArgumentBuilder<SpellCtx> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public LiteralArgumentBuilder<SpellCtx> getNode() {
        return literal(element.word()).then(buildNode());
    }

    public abstract CommandNode<SpellCtx> buildNode();
}
