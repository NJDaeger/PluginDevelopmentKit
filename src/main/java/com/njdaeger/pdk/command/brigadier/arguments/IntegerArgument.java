package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import java.util.stream.IntStream;

public class IntegerArgument extends BasePdkArgumentType<Integer, Integer> {

    private static final DynamicCommandExceptionType INTEGER_OUT_OF_BOUNDS = new DynamicCommandExceptionType(o -> (Message) o);

    private final int min;
    private final int max;
    private final Function<ICommandContext, Map<Integer, Message>> suggestions;

    private final Message defaultTooltipMessage;
    private Function<Integer, Message> outOfBoundsMessage;

    public IntegerArgument(Message defaultTooltipMessage) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.suggestions = null;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public IntegerArgument(int min, int max, Function<Integer, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        this.min = min;
        this.max = max;
        this.suggestions = null;
        this.outOfBoundsMessage = outOfBoundsMessage;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public IntegerArgument(Function<ICommandContext, Map<Integer, Message>> suggestions) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A number.";
    }

    public IntegerArgument(Function<ICommandContext, Collection<Integer>> suggestions, Message defaultTooltipMessage) {
        this.min = Integer.MIN_VALUE;
        this.max = Integer.MAX_VALUE;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public Integer convertToCustom(Integer nativeType, StringReader reader) throws CommandSyntaxException {
        if (nativeType < min || nativeType > max) {
            throw INTEGER_OUT_OF_BOUNDS.createWithContext(reader, outOfBoundsMessage.apply(nativeType));
        }
        return nativeType;
    }

    @Override
    public Integer convertToNative(Integer integer) {
        return integer;
    }

    @Override
    public @NotNull ArgumentType<Integer> getNativeType() {
        return IntegerArgumentType.integer(min, max);
    }

    @Override
    public Map<Integer, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));
        var current = builder.getRemaining();
        var completingAt = builder.getStart() + ((current.contains("-") && min < 0) ? current.lastIndexOf("-") + 1 : 0);
        var newBuilder = builder.createOffset(completingAt);

        if (suggestions.isEmpty()) {
            int parsed;
            try {
                parsed = Integer.parseInt(current);
            } catch (NumberFormatException e) {
                parsed = 0;
            }
            if (parsed < 0) parsed *= -1;
            IntStream.rangeClosed(parsed * 10, parsed * 10 + 10).mapToObj(String::valueOf).forEach(newBuilder::suggest);
            return newBuilder.buildFuture();
        }
        suggestions.forEach((suggestion, message) -> builder.suggest(suggestion.toString(), message));
        return builder.buildFuture();
    }
}
