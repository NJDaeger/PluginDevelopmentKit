package com.njdaeger.pdk.command.brigadier.arguments.types;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.impl.ExecutionHelpers;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GreedyStringArgument extends PdkArgumentType<String, String> {

    private final Message defaultTooltipMessage;
    private final Function<ICommandContext, Map<String, Message>> suggestions;

    public GreedyStringArgument(Message defaultTooltipMessage) {
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = null;
    }

    public GreedyStringArgument(Function<ICommandContext, Map<String, Message>> suggestions) {
        this.defaultTooltipMessage = () -> "A string of text.";
        this.suggestions = suggestions;
    }

    public GreedyStringArgument(Function<ICommandContext, List<String>> suggestions, Message defaultTooltipMessage) {
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
    public String convertToCustom(String nativeType) throws CommandSyntaxException {
        return nativeType;
    }

    @Override
    public String convertToNative(String s) {
        return s;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.greedyString();
    }
}
