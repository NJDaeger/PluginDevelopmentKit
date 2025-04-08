package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractQuotedTypedArgument<TYPE> extends BasePdkArgumentType<TYPE, String> {

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        var quotedSuggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));
        var current = builder.getRemaining();
        var startsWithQuote = current.startsWith("\"");
        var currentWithoutQuotes = startsWithQuote ? current.substring(1) : current;
        var newBuilder2 = builder.createOffset(builder.getStart() + current.length());

        if (areQuotesBalancedIgnoringEscaped(current) && !current.isBlank()) return newBuilder2.buildFuture();

        if (quotedSuggestions.isEmpty()) {
            if (!currentWithoutQuotes.isBlank()) {
                builder.suggest(current + "\"", getDefaultTooltipMessage());
                return builder.buildFuture();
            }
            newBuilder2.suggest((startsWithQuote ? "\"" : "\"\""));
            return newBuilder2.buildFuture();
        }

        quotedSuggestions.entrySet().stream()
                .filter(entry -> currentWithoutQuotes.isBlank() || convertToNative(entry.getKey()).toLowerCase().startsWith(currentWithoutQuotes.toLowerCase()))
                .forEach(entry -> newBuilder2.suggest(("\"" + convertToNative(entry.getKey()) + "\"").substring(current.length()), entry.getValue()));
        return newBuilder2.buildFuture();
    }

    protected static boolean areQuotesBalancedIgnoringEscaped(String str) {
        int count = 0;
        boolean escaped = false;
        for (char c : str.toCharArray()) {
            if (c == '\\') {
                escaped = !escaped;
            } else if (c == '"' && !escaped) {
                count++;
            } else {
                escaped = false;
            }
        }
        return count % 2 == 0;
    }

}
