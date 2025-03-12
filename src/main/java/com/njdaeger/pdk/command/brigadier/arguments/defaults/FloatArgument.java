package com.njdaeger.pdk.command.brigadier.arguments.defaults;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.AbstractFloatTypedArgument;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FloatArgument extends AbstractFloatTypedArgument<Float> {

    private static final DynamicCommandExceptionType FLOAT_OUT_OF_BOUNDS = new DynamicCommandExceptionType(o -> (Message) o);

    private final Function<ICommandContext, Map<Float, Message>> suggestions;

    private final Message defaultTooltipMessage;
    private Function<Float, Message> outOfBoundsMessage;

    public FloatArgument(Message defaultTooltipMessage) {
        super(Float.MIN_VALUE, Float.MAX_VALUE);
        this.suggestions = null;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public FloatArgument(float min, float max, Function<Float, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        super(min, max);
        this.suggestions = null;
        this.outOfBoundsMessage = outOfBoundsMessage;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public FloatArgument(Function<ICommandContext, Map<Float, Message>> suggestions) {
        super(Float.MIN_VALUE, Float.MAX_VALUE);
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A number.";
    }

    public FloatArgument(Function<ICommandContext, Collection<Float>> suggestions, Message defaultTooltipMessage) {
        super(Float.MIN_VALUE, Float.MAX_VALUE);
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
    public Map<Float, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }
}
