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
        var completingAt = builder.getStart() + (current.contains("\"") ? current.indexOf('"') + 1 : 0);
        var newBuilder = builder.createOffset(completingAt);

        if (areQuotesBalancedIgnoringEscaped(current) && !current.isBlank()) return newBuilder.buildFuture();

        if (quotedSuggestions.isEmpty()) {
            if (current.isBlank()) {
                newBuilder.suggest("\"\"", getDefaultTooltipMessage());
                return newBuilder.buildFuture();
            }

            newBuilder.suggest((current.startsWith("\"") ? current.substring(1) : current) + "\"", getDefaultTooltipMessage());

            return newBuilder.buildFuture();
        }

        quotedSuggestions.forEach((suggestion, tooltip) -> newBuilder.suggest("\"" + suggestion + "\"", tooltip));

        return newBuilder.buildFuture();
    }

    private static boolean areQuotesBalancedIgnoringEscaped(String str) {
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
