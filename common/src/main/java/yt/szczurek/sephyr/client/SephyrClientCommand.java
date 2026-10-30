package yt.szczurek.sephyr.client;

import com.google.common.base.CaseFormat;
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
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import yt.szczurek.sephyr.Sephyr;
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
        List<Microphone> mics = Sepple.enumerateMicrophones();
        Optional<Microphone> mic = mics.stream().filter(microphone -> microphone.name().equals(micName)).findFirst();
        if (mic.isEmpty()) {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("Invalid microphone selected"));
            return Command.SINGLE_SUCCESS;
        }
        String newMicId = mic.get().id();
        boolean success = SeppleManager.setMicrophone(newMicId);
        Microphone current = Sepple.getCurrentMicrophone();
        if (success && current != null) {
            if (current.id().equals(newMicId)) {
                Minecraft.getInstance().gui.getChat().addClientSystemMessage(Component.literal("Set the microphone to " + micName));
            } else {
                Minecraft.getInstance().gui.getChat().addClientSystemMessage(
                        Component.literal("Failed to start capturing this microphone" +
                                "\nMicrophone \"" + current.name() + "\" was used instead"
                        ).withStyle(ChatFormatting.RED));
            }
        } else {
            Minecraft.getInstance().gui.getChat().addClientSystemMessage(
                    Component.literal("Failed to start capturing this microphone" +
                            "\nLogs might have additional details"
                    ).withStyle(ChatFormatting.RED));
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

        Component isRunning = Component.literal("\nIs running: ").withStyle(ChatFormatting.WHITE)
                .append(Sepple.isRunning() ?
                Component.literal("Yes").withStyle(ChatFormatting.GREEN) :
                Component.literal("No").withStyle(ChatFormatting.RED));
        String state = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, SeppleManager.loadingState().name());
        Component loadingState = Component.literal("\nLoading state: ").withStyle(ChatFormatting.WHITE)
                .append(Component.literal(state).withStyle(ChatFormatting.GRAY));
        Component microphone = Component.literal("\nMicrophone: ").withStyle(ChatFormatting.WHITE)
                .append(currentMic == null ?
                        Component.literal("None").withStyle(ChatFormatting.RED) :
                        Component.literal(String.format("%s (%s)", currentMic.name(), currentMic.id())).withStyle(ChatFormatting.GRAY));
        Component message = Component.literal("Sephyr status").withColor(Sephyr.BLUE_COLOR)
                .append(isRunning)
                .append(loadingState)
                .append(microphone);
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
