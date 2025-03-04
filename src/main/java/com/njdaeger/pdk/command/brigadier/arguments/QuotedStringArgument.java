package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuotedStringArgument extends BasePdkArgumentType<String, String> {

    private static final DynamicCommandExceptionType EMPTY_STRING = new DynamicCommandExceptionType(o -> () -> "Empty strings are not allowed.");

    private final boolean allowEmpty;
    private final Message defaultTooltipMessage;
    private final Function<ICommandContext, Map<String, Message>> suggestions;

    public QuotedStringArgument(boolean allowEmpty, Message defaultTooltipMessage) {
        this.allowEmpty = allowEmpty;
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = null;
    }

    public QuotedStringArgument(boolean allowEmpty, Function<ICommandContext, Map<String, Message>> suggestions) {
        this.allowEmpty = allowEmpty;
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "Something surrounded by quotes.";
    }

    public QuotedStringArgument(boolean allowEmpty, Function<ICommandContext, Collection<String>> suggestions, Message defaultTooltipMessage) {
        this.allowEmpty = allowEmpty;
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
    }

    @Override
    public Map<String, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

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

    @Override
    public String convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
        if (!allowEmpty && nativeType.isEmpty()) {
            reader.setCursor(reader.getCursor() - 2);
            throw EMPTY_STRING.createWithContext(reader, nativeType);
        }
        return nativeType;
    }

    @Override
    public String convertToNative(String s) {
        return s;
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
