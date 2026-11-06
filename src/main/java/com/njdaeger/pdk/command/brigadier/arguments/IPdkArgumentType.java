package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.IContextGenerator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public interface IPdkArgumentType<CUSTOM, NATIVE> extends CustomArgumentType<CUSTOM, NATIVE> {

    /**
     * Returns the plugin that registered the command being used
     * @return The plugin that registered the command.
     */
    Plugin getPlugin();

    /**
     * Sets the context generator supplier
     * @param plugin The plugin that registered the command.
     * @param generator The context generator to use for this argument type.
     */
    void setContextGenerator(Plugin plugin, IContextGenerator<?> generator);

    /**
     * Returns a list of basic suggestions. All tooltips will be the default tooltip message provided by {@link #getDefaultTooltipMessage()}.
     * @param commandContext The command context.
     * @return A list of basic suggestions.
     */
    List<CUSTOM> listBasicSuggestions(ICommandContext commandContext);

    /**
     * Returns a map of a suggestion and a tooltip message.
     * @param commandContext The command context.
     * @return A map of suggestions and their tooltip messages.
     */
    Map<CUSTOM, Message> listSuggestions(ICommandContext commandContext);

    /**
     * Returns a future of a list of basic suggestions. All tooltips will be the default tooltip message provided by
     * {@link #getDefaultTooltipMessage()}. Override this instead of {@link #listBasicSuggestions(ICommandContext)} when
     * the suggestions come from something asynchronous, such as a web request.
     * <p>
     * This is only used when {@link #listSuggestions(ICommandContext)} returns no suggestions.
     * @param commandContext The command context.
     * @return A future of a list of basic suggestions.
     */
    default CompletableFuture<List<CUSTOM>> listBasicSuggestionsAsync(ICommandContext commandContext) {
        return CompletableFuture.completedFuture(List.of());
    }

    /**
     * Returns a future of a map of a suggestion and a tooltip message. This is what the command completion calls. By
     * default it uses {@link #listSuggestions(ICommandContext)}, falling back to
     * {@link #listBasicSuggestionsAsync(ICommandContext)} when that has no suggestions. Override this instead when the
     * suggestions and their tooltips come from something asynchronous, such as a web request.
     * <p>
     * Note: whatever thread completes the future is the thread the suggestions are built on, so avoid touching the
     * Bukkit API in anything chained after an asynchronous lookup.
     * @param commandContext The command context.
     * @return A future of a map of suggestions and their tooltip messages.
     */
    default CompletableFuture<Map<CUSTOM, Message>> listSuggestionsAsync(ICommandContext commandContext) {
        var suggestions = listSuggestions(commandContext);
        if (!suggestions.isEmpty()) return CompletableFuture.completedFuture(suggestions);
        var defaultMessage = getDefaultTooltipMessage();
        return listBasicSuggestionsAsync(commandContext)
                .thenApply(basic -> basic.stream().collect(Collectors.toMap(suggestion -> suggestion, unused -> defaultMessage)));
    }

    /**
     * Returns the default tooltip message for this argument type.
     * @return The default tooltip message.
     */
    Message getDefaultTooltipMessage();

    /**
     * Converts the custom argument type to the native argument type. This is used when creating completions, it will convert an argument on the fly to the native type.
     * @param custom The custom argument type.
     * @return The native argument type.
     */
    NATIVE convertToNative(CUSTOM custom);

    /**
     * Converts the native argument type to the custom argument type for command execution.
     *
     * @param source The command sender. Note: This may be null in some instances.
     * @param nativeType The native argument type.
     * @param reader The stringreader used to parse the command.
     * @return The custom argument type.
     * @throws CommandSyntaxException If the conversion from the native to the custom fails.
     */
    CUSTOM convertToCustom(@Nullable CommandSender source, NATIVE nativeType, StringReader reader) throws CommandSyntaxException;

    @Override
    default <S> CUSTOM parse(StringReader reader, S source) throws CommandSyntaxException {
        return this.convertToCustom(((CommandSourceStack)source).getSender(), getNativeType().parse(reader), reader);
    }

    @Override
    default @NotNull CUSTOM parse(@NotNull StringReader reader) throws CommandSyntaxException {
        return convertToCustom(null, getNativeType().parse(reader), reader);
    }
}
