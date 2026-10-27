package yt.szczurek.sephyr;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.blay09.mods.balm.Balm;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import yt.szczurek.sephyr.registry.SephyrRegistries;
import yt.szczurek.sephyr.registry.SephyrSpells;
import yt.szczurek.sephyr.spell.SpellCtx;
import yt.szczurek.sephyr.spell.SpellDefinition;

import java.util.Arrays;
import java.util.List;

public class SephyrCommand {
    public static void register() {
        Balm.commands().register(SephyrCommand::register);
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> castNode = Commands.literal("cast");

        for (final SpellDefinition<?> spellDefinition : SephyrRegistries.SPELL) {
            visitNode(spellDefinition.node().build(), castNode);
        }
        dispatcher.register(
                Commands.literal("sephyr")
                            .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)
                        )
                        .then(castNode)
        );
    }

    // Quite primitive tree walk, that only works for literal arguments
    private static void visitNode(LiteralCommandNode<SpellCtx> spellNode, LiteralArgumentBuilder<CommandSourceStack> cmdNode) {
        LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(spellNode.getLiteral());
        Command<SpellCtx> command = spellNode.getCommand();
        if (command != null) {
            base.executes(context -> {
                // Skips two words - the "sephyr cast" prefix
                List<String> spell = Arrays.stream(context.getInput().split(" "))
                        .dropWhile(s -> !s.equals("sephyr"))
                        .skip(2)
                        .toList();
                SpellCastAttemptResult result = SephyrSpells.tryExecute(spell, context.getSource().getEntity(), true);
                return result == SpellCastAttemptResult.EXECUTED ? Command.SINGLE_SUCCESS : 0;
            });
        }

        for (final CommandNode<SpellCtx> subNode : spellNode.getChildren()) {
            if (subNode instanceof LiteralCommandNode<SpellCtx> subNodeCasted) {
                visitNode(subNodeCasted, base);
            }
        }
        cmdNode.then(base);
    }
}
