package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractTokenizedQuotedTypedArgument<TYPE> extends AbstractQuotedTypedArgument<TYPE> {

    @Override
    protected CompletableFuture<Suggestions> buildSuggestions(Map<TYPE, Message> suggestions, SuggestionsBuilder builder) {
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
