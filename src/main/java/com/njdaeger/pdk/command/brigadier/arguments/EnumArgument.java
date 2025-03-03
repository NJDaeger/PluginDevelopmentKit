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
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumArgument<T extends Enum<T>> extends BasePdkArgumentType<T, String>{

    private static final DynamicCommandExceptionType ENUM_ENTRY_NOT_FOUND = new DynamicCommandExceptionType(o -> () -> "Enum entry " + o.toString() + " not found");

    private final Class<T> enumClass;
    private final Message defaultTooltipMessage;

    private final Function<ICommandContext, Map<T, Message>> suggestions;

    public EnumArgument(Class<T> enumClass, Message defaultTooltipMessage) {
        this.enumClass = enumClass;
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.suggestions = null;
    }

    public EnumArgument(Class<T> enumClass, Function<ICommandContext, Map<T, Message>> suggestions) {
        this.enumClass = enumClass;
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "Any constant of the enum " + enumClass.getSimpleName() + ".";
    }

    public EnumArgument(Class<T> enumClass, Function<ICommandContext, Collection<T>> suggestions, Message defaultTooltipMessage) {
        this.enumClass = enumClass;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public String convertToNative(T t) {
        return t.name();
    }

    @Override
    public T convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
        try {
            return Enum.valueOf(enumClass, nativeType);
        } catch (IllegalArgumentException e) {
            var entryLength = nativeType.length();
            reader.setCursor(reader.getCursor() - entryLength);
            throw ENUM_ENTRY_NOT_FOUND.createWithContext(reader, nativeType);
        }
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public Map<T, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        else return super.listSuggestions(commandContext);
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));

        var currentInput = builder.getRemaining().trim();

        if (suggestions.isEmpty()) {
            Stream.of(enumClass.getEnumConstants()).filter(e -> e.name().toLowerCase().contains(currentInput.toLowerCase()) || currentInput.isBlank()).forEach(e -> builder.suggest(e.name().toLowerCase()));
            return builder.buildFuture();
        }

        suggestions.forEach((k, v) -> {
            var suggestionContainsCurrentInput = k.name().toLowerCase().contains(currentInput.toLowerCase()) || v.getString().toLowerCase().contains(currentInput.toLowerCase());
            if (suggestionContainsCurrentInput || currentInput.isBlank()) builder.suggest(k.name(), v);
        });
        return builder.buildFuture();
    }
}
