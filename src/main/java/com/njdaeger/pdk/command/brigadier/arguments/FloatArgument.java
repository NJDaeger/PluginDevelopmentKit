package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FloatArgument extends BasePdkArgumentType<Float, Float> {

    private static final DynamicCommandExceptionType FLOAT_OUT_OF_BOUNDS = new DynamicCommandExceptionType(o -> (Message) o);

    private final float min;
    private final float max;
    private final Function<ICommandContext, Map<Float, Message>> suggestions;

    private final Message defaultTooltipMessage;
    private Function<Float, Message> outOfBoundsMessage;

    public FloatArgument(Message defaultTooltipMessage) {
        this.min = Float.MIN_VALUE;
        this.max = Float.MAX_VALUE;
        this.suggestions = null;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public FloatArgument(float min, float max, Function<Float, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        this.min = min;
        this.max = max;
        this.suggestions = null;
        this.outOfBoundsMessage = outOfBoundsMessage;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public FloatArgument(Function<ICommandContext, Map<Float, Message>> suggestions) {
        this.min = Float.MIN_VALUE;
        this.max = Float.MAX_VALUE;
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A number.";
    }

    public FloatArgument(Function<ICommandContext, Collection<Float>> suggestions, Message defaultTooltipMessage) {
        this.min = Float.MIN_VALUE;
        this.max = Float.MAX_VALUE;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public Float convertToNative(Float aFloat) {
        return aFloat;
    }

    @Override
    public Float convertToCustom(Float nativeType, StringReader reader) throws CommandSyntaxException {
        if (nativeType < min || nativeType > max) {
            throw FLOAT_OUT_OF_BOUNDS.createWithContext(reader, outOfBoundsMessage.apply(nativeType));
        }
        return nativeType;
    }

    @Override
    public @NotNull ArgumentType<Float> getNativeType() {
        return FloatArgumentType.floatArg(min, max);
    }

    @Override
    public Map<Float, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));
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
        suggestions.forEach((s, message) -> builder.suggest(s.toString(), message));
        return builder.buildFuture();
    }
}
