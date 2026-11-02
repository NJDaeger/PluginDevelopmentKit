package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractTokenizedQuotedTypedArgument<TYPE> extends AbstractQuotedTypedArgument<TYPE> {

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(generateContext((CommandContext<CommandSourceStack>) context));
        var current = builder.getRemaining();
        var startsWithQuote = current.startsWith("\"");
        var currentWithoutQuotes = startsWithQuote ? current.substring(1) : current;
        var newBuilder = builder.createOffset(builder.getStart() + current.length());

        if (areQuotesBalancedIgnoringEscaped(current) && !current.isBlank()) return newBuilder.buildFuture();

        if (suggestions.isEmpty() || (currentWithoutQuotes.isBlank() && !startsWithQuote && !currentWithoutQuotes.endsWith(" "))) {
            if (!currentWithoutQuotes.isBlank()) {
                builder.suggest(current + "\"", getDefaultTooltipMessage());
                return builder.buildFuture();
            }
            newBuilder.suggest("\"");
            return newBuilder.buildFuture();
        }

        List<String> tokenizedCurrent = currentWithoutQuotes.isBlank() ? List.of() : List.of(currentWithoutQuotes.trim().split(" "));

        //we should suggest the current + each token
        suggestions.entrySet().stream()
                .filter(entry -> currentWithoutQuotes.isBlank() || convertToNative(entry.getKey()).toLowerCase().startsWith(tokenizedCurrent.getLast().toLowerCase()) || (currentWithoutQuotes.endsWith(" ") && !tokenizedCurrent.contains(convertToNative(entry.getKey()))))
                .forEach(entry -> {
                    if (currentWithoutQuotes.isBlank() || tokenizedCurrent.isEmpty() || currentWithoutQuotes.endsWith(" ")) newBuilder.suggest(convertToNative(entry.getKey()), entry.getValue());
                    else newBuilder.suggest(convertToNative(entry.getKey()).substring(tokenizedCurrent.getLast().length()), entry.getValue());
                });
        newBuilder.suggest("\"");
        return newBuilder.buildFuture();
    }
}
