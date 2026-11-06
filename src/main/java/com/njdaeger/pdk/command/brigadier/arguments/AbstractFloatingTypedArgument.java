package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public abstract class AbstractFloatingTypedArgument<TYPE, FLOATING_NUMERIC> extends BasePdkArgumentType<TYPE, FLOATING_NUMERIC> {

    protected final double min;
    protected final double max;

    public AbstractFloatingTypedArgument(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    protected CompletableFuture<Suggestions> buildSuggestions(Map<TYPE, Message> suggestions, SuggestionsBuilder builder) {
        var current = builder.getRemaining();
        var hasDecimal = current.contains(".");
        var hasNegative = current.contains("-");
        var completingAt = builder.getStart();// + (hasDecimal ? 1 : 0) + (hasNegative ? 1 : 0);
        var newBuilder = builder.createOffset(completingAt);

        if (suggestions.isEmpty()) {
            IntStream.rangeClosed(0, 9).mapToObj(String::valueOf).forEach(val -> newBuilder.suggest(current + val));
            if (!hasDecimal) newBuilder.suggest(current + ".", () -> "Decimal");
            if (!hasNegative && min < 0) newBuilder.suggest("-" + current, () -> "Negative");
            return newBuilder.buildFuture();
        }
        suggestions.forEach((s, message) -> builder.suggest(convertToNative(s).toString(), message));
        return builder.buildFuture();
    }

}
