package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.stream.LongStream;

public abstract class AbstractFixedTypedArgument<TYPE, FIXED_NUMERIC> extends BasePdkArgumentType<TYPE, FIXED_NUMERIC > {

    protected final long min;
    protected final long max;

    public AbstractFixedTypedArgument(long min, long max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(generateContext((CommandContext<CommandSourceStack>) context));
        var current = builder.getRemaining();
        var completingAt = builder.getStart() + ((current.contains("-") && min < 0) ? current.lastIndexOf("-") + 1 : 0);
        var newBuilder = builder.createOffset(completingAt);

        if (suggestions.isEmpty()) {
            long parsed;
            try {
                parsed = Long.parseLong(current);
            } catch (NumberFormatException e) {
                parsed = 0;
            }
            if (parsed < 0) parsed *= -1;
            LongStream.rangeClosed(parsed * 10L, parsed * 10L + 10).mapToObj(String::valueOf).forEach(newBuilder::suggest);
            return newBuilder.buildFuture();
        }
        suggestions.forEach((suggestion, message) -> builder.suggest(convertToNative(suggestion).toString(), message));
        return builder.buildFuture();
    }

}
