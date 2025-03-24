package com.njdaeger.pdk.command.brigadier.arguments.defaults;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.AbstractIntegerTypedArgument;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class IntegerArgument extends AbstractIntegerTypedArgument<Integer> {

    private static final DynamicCommandExceptionType INTEGER_OUT_OF_BOUNDS = new DynamicCommandExceptionType(o -> (Message) o);

    private final Function<ICommandContext, Map<Integer, Message>> suggestions;

    private final Message defaultTooltipMessage;
    private Function<Integer, Message> outOfBoundsMessage;

    public IntegerArgument(Message defaultTooltipMessage) {
        super(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.suggestions = null;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public IntegerArgument(int min, int max, Function<Integer, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        super(min, max);
        this.suggestions = null;
        this.outOfBoundsMessage = outOfBoundsMessage;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public IntegerArgument(Function<ICommandContext, Map<Integer, Message>> suggestions) {
        super(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A number.";
    }

    public IntegerArgument(Function<ICommandContext, Collection<Integer>> suggestions, Message defaultTooltipMessage) {
        super(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public Integer convertToCustom(CommandSender sender, Integer nativeType, StringReader reader) throws CommandSyntaxException {
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
    public Map<Integer, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }
}
