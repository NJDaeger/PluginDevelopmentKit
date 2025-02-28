package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class BasePdkArgumentType<CUSTOM, NATIVE> implements IPdkArgumentType<CUSTOM, NATIVE> {

    @Override
    public List<CUSTOM> listBasicSuggestions(ICommandContext commandContext) {
        return List.of();
    }

    @Override
    public Map<CUSTOM, Message> listSuggestions(ICommandContext commandContext) {
        var suggestions = listBasicSuggestions(commandContext);
        var defaultMessage = getDefaultTooltipMessage();
        if (!suggestions.isEmpty()) {
            return suggestions.stream().collect(Collectors.toMap(suggestion -> suggestion, unused -> defaultMessage));
        }
        return Map.of();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));
        suggestions.forEach((suggestion, message) -> builder.suggest(convertToNative(suggestion).toString(), message));
        return builder.buildFuture();
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return new LiteralMessage("");
    }
}
