package com.njdaeger.pdk.command.brigadier.arguments.defaults;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.arguments.AbstractStringTypedArgument;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringArgument extends AbstractStringTypedArgument<String> {

    private final Message defaultTooltipMessage;
    private final Function<ICommandContext, Map<String, Message>> suggestions;

    public StringArgument(Message defaultTooltipMessage) {
        super();
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = null;
    }

    public StringArgument(Function<ICommandContext, Map<String, Message>> suggestions) {
        super();
        this.defaultTooltipMessage = () -> "A string of text.";
        this.suggestions = suggestions;
    }

    public StringArgument(Function<ICommandContext, Collection<String>> suggestions, Message defaultTooltipMessage) {
        super();
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public String convertToNative(String s) {
        return s;
    }

    @Override
    public String convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
        return nativeType;
    }

    @Override
    public Map<String, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        return super.listSuggestions(commandContext);
    }
}
