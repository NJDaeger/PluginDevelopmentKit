package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDelimitedTypedArgument<TYPE> extends AbstractStringTypedArgument<List<TYPE>> {

    private final char delimiter;

    public AbstractDelimitedTypedArgument() {
        this.delimiter = '+';
    }

    public AbstractDelimitedTypedArgument(char delimiter) {
        if (!StringReader.isAllowedInUnquotedString(delimiter)) {
            throw new IllegalArgumentException("Delimiter must be a character that is allowed in an unquoted string");
        }
        this.delimiter = delimiter;
    }

    /**
     * Convert the custom argument to a string representation
     * @param thing The custom argument to convert
     * @return The string representation of the custom argument
     */
    public abstract String convertToNativeSingle(TYPE thing);

    /**
     * Convert a string representation of the custom argument back to the custom argument. This is used when parsing individual segments of the comma separated argument
     * @param source The command sender that is parsing the argument. This can be null if the argument is being parsed outside a command context.
     * @param nativeType The string representation of the custom argument to convert back to the custom argument
     * @return The custom argument
     * @throws CommandSyntaxException If the string cannot be converted to the custom argument
     */
    public abstract TYPE convertSingleToCustom(@Nullable CommandSender source, String nativeType, StringReader reader) throws CommandSyntaxException;

    public List<TYPE> listBasicDelimitedSuggestions(ICommandContext commandContext) {
        return List.of();
    }

    public Map<TYPE, Message> listDelimitedSuggestions(ICommandContext commandContext) {
        var suggestions = listBasicDelimitedSuggestions(commandContext);
        var defaultMessage = getDefaultTooltipMessage();
        if (!suggestions.isEmpty()) {
            return suggestions.stream().collect(java.util.stream.Collectors.toMap(suggestion -> suggestion, unused -> defaultMessage));
        }
        return Map.of();
    }

    @Override
    public final List<List<TYPE>> listBasicSuggestions(ICommandContext commandContext) {
        throw new UnsupportedOperationException("listBasicSuggestions is not supported for AbstractDelimitedTypedArgument. Use listBasicDelimitedSuggestions instead.");
    }

    @Override
    public final Map<List<TYPE>, Message> listSuggestions(ICommandContext commandContext) {
        throw new UnsupportedOperationException("listSuggestions is not supported for AbstractDelimitedTypedArgument. Use listDelimitedSuggestions instead.");
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var ctx = generateContext((CommandContext<CommandSourceStack>) context);
        var typedSuggestions = listDelimitedSuggestions(ctx);

        if (typedSuggestions.isEmpty()) return builder.buildFuture();

        var suggestions = typedSuggestions.entrySet().stream()
                .filter(entry -> !entry.getKey().toString().isBlank())
                .collect(java.util.stream.Collectors.toMap(entry -> convertToNativeSingle(entry.getKey()), Map.Entry::getValue));

        var fullCurrent = builder.getRemaining();
        var currentSelections = Arrays.stream(fullCurrent.replace(delimiter, ',').split(",")).map(String::toLowerCase).toList();
        var lastDelimiterIndex = fullCurrent.lastIndexOf(delimiter);
        var currentToken = lastDelimiterIndex == -1 ? fullCurrent : fullCurrent.substring(lastDelimiterIndex + 1);
        var isCurrentTokenValid = suggestions.keySet().stream().anyMatch(suggestion -> suggestion.equalsIgnoreCase(currentToken));

        var remainingPossibleSuggestions = suggestions.entrySet().stream()
                .filter(entry -> {
                    //if the current token is a valid option and the current suggestion we are looking at is not the current token, only suggest it if it starts with the current token.
                    if (isCurrentTokenValid) {
                        if (currentToken.equalsIgnoreCase(entry.getKey())) return false;
                        return entry.getKey().toLowerCase().startsWith(currentToken.toLowerCase());
                    }
                    return (currentToken.isBlank() || entry.getKey().toLowerCase().startsWith(currentToken.toLowerCase())) && currentSelections.stream().noneMatch(selection -> selection.equalsIgnoreCase(entry.getKey()));
                }).toList();


        var newBuilder = builder.createOffset(builder.getStart() + (lastDelimiterIndex == -1 ? 0 : lastDelimiterIndex + 1));

        if (isCurrentTokenValid) {
            //suggest all non suggested selections with the current token + delimiter in front.
            suggestions.entrySet().stream()
                    .filter(entry -> !entry.getKey().equalsIgnoreCase(currentToken) && currentSelections.stream().noneMatch(selection -> selection.equalsIgnoreCase(entry.getKey())))
                    .forEach(entry -> newBuilder.suggest(currentToken + delimiter + entry.getKey(), entry.getValue()));
        }
        remainingPossibleSuggestions.forEach(entry -> newBuilder.suggest(entry.getKey(), entry.getValue()));
        return newBuilder.buildFuture();
    }

    @Override
    public String convertToNative(List<TYPE> types) {
        return types.stream().map(this::convertToNativeSingle).reduce((a, b) -> a + delimiter + b).orElse("");
    }

    @Override
    public List<TYPE> convertToCustom(@Nullable CommandSender source, String nativeType, StringReader reader) throws CommandSyntaxException {
        var splitData = nativeType.split(String.valueOf(delimiter));
        var result = new ArrayList<TYPE>();
        for (String str : splitData) {
            result.add(convertSingleToCustom(source, str, reader));
        }
        return result;
    }
}
