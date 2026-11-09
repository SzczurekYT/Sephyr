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
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Messages;
import yt.szczurek.sephyr.SeppleManager;
import yt.szczurek.sepple.Microphone;
import yt.szczurek.sepple.Sepple;

import java.util.List;
import java.util.Optional;
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
        Microphone previousMic = Sepple.getCurrentMicrophone();
        List<Microphone> mics = Sepple.enumerateMicrophones();
        Optional<Microphone> mic = mics.stream().filter(microphone -> microphone.name().equals(micName)).findFirst();
        if (mic.isEmpty()) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Messages.invalidMicrophone());
            return Command.SINGLE_SUCCESS;
        }
        String newMicId = mic.get().id();
        if (previousMic != null && newMicId.equals(previousMic.id())) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Messages.microphoneAlreadySelected());
            return Command.SINGLE_SUCCESS;
        }
        boolean success = SeppleManager.setMicrophone(newMicId);
        Microphone current = Sepple.getCurrentMicrophone();
        if (success && current != null) {
            if (current.id().equals(newMicId)) {
                Minecraft.getInstance().gui.getChat().addClientSystemMessage(Messages.microphoneSet(micName));
            } else {
                Minecraft.getInstance().gui.getChat().addClientSystemMessage(
                        Messages.microphoneFailedWithFallback(current.name()));
            }
        } else {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Messages.microphoneFailed());
        }

        return Command.SINGLE_SUCCESS;
    }

    private static CompletableFuture<Suggestions> getMicrophoneSuggestions(CommandContext<SharedSuggestionProvider> context, SuggestionsBuilder builder) {
        for (Microphone mic: Sepple.enumerateMicrophones()) {
            builder.suggest(mic.name());
        }
        return CompletableFuture.completedFuture(builder.build());
    }

    private static int statusExec(CommandContext<SharedSuggestionProvider> ctx) {
        Microphone currentMic = Sepple.getCurrentMicrophone();

        Component message = Messages.statusSummary(Sepple.isRunning(), SeppleManager.loadingState(), currentMic);
        Minecraft.getInstance().gui.getChat().addClientSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }

    public static LiteralArgumentBuilder<SharedSuggestionProvider> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static <T> RequiredArgumentBuilder<SharedSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
