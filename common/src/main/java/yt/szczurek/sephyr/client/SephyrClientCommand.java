package yt.szczurek.sephyr.client;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.blay09.mods.balm.client.BalmClient;
import net.minecraft.commands.SharedSuggestionProvider;
import yt.szczurek.sephyr.Messages;
import yt.szczurek.sephyr.MicManager;
import yt.szczurek.sepple.Microphone;
import yt.szczurek.sepple.Sepple;

import java.util.concurrent.CompletableFuture;

public class SephyrClientCommand {
    public static void register() {
        BalmClient.clientCommands().register(SephyrClientCommand::register);
    }

    private static void register(CommandDispatcher<SharedSuggestionProvider> dispatcher) {
        var micNode = literal("microphone").then(argument("microphone", StringArgumentType.greedyString())
                .suggests(SephyrClientCommand::getMicrophoneSuggestions)
                .executes(SephyrClientCommand::setMicExec));
        var statusNode = literal("status")
                .executes(SephyrClientCommand::statusExec);
        dispatcher.register(
                literal("sephyr")
                        .then(micNode)
                        .then(statusNode)
        );
    }

    private static int setMicExec(CommandContext<SharedSuggestionProvider> ctx) {
        String micName = ctx.getArgument("microphone", String.class);
        MicManager.select(micName);
        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getMicrophoneSuggestions(CommandContext<SharedSuggestionProvider> context, SuggestionsBuilder builder) {
        for (Microphone mic : Sepple.enumerateMicrophones()) {
            builder.suggest(mic.name());
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    private static int statusExec(CommandContext<SharedSuggestionProvider> ctx) {
        Messages.sendChat(Messages.statusSummary(MicManager.summary()));
        return Command.SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<SharedSuggestionProvider> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
