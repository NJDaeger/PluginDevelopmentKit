package com.njdaeger.pdk.command.brigadier.arguments.defaults;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.AbstractLongTypedArgument;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LongArgument extends AbstractLongTypedArgument<Long> {

    private static final DynamicCommandExceptionType LONG_OUT_OF_BOUNDS = new DynamicCommandExceptionType(o -> (Message) o);

    private final Function<ICommandContext, Map<Long, Message>> suggestions;

    private final Message defaultTooltipMessage;
    private Function<Long, Message> outOfBoundsMessage;

    public LongArgument(Message defaultTooltipMessage) {
        super(Long.MIN_VALUE, Long.MAX_VALUE);
        this.suggestions = null;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public LongArgument(long min, long max, Function<Long, Message> outOfBoundsMessage, Message defaultTooltipMessage) {
        super(min, max);
        this.suggestions = null;
        this.outOfBoundsMessage = outOfBoundsMessage;
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    public LongArgument(Function<ICommandContext, Map<Long, Message>> suggestions) {
        super(Long.MIN_VALUE, Long.MAX_VALUE);
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A number.";
    }

    public LongArgument(Function<ICommandContext, Collection<Long>> suggestions, Message defaultTooltipMessage) {
        super(Long.MIN_VALUE, Long.MAX_VALUE);
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public Long convertToCustom(CommandSender sender, Long nativeType, StringReader reader) throws CommandSyntaxException {
        if (nativeType < min || nativeType > max) {
            throw LONG_OUT_OF_BOUNDS.createWithContext(reader, outOfBoundsMessage.apply(nativeType));
        }
        return nativeType;
    }

    @Override
    public Long convertToNative(Long longType) {
        return longType;
    }

    @Override
    public Map<Long, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }
}
