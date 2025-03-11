package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class QuotedStringArgument extends AbstractQuotedTypedArgument<String> {

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

}
