package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.IContextGenerator;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class BasePdkArgumentType<CUSTOM, NATIVE> implements IPdkArgumentType<CUSTOM, NATIVE> {

    private Plugin plugin;
    protected IContextGenerator<?> contextGenerator;

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public void setContextGenerator(Plugin plugin, IContextGenerator<?> generator) {
        this.plugin = plugin;
        this.contextGenerator = generator;
    }

    protected ICommandContext generateContext(CommandContext<CommandSourceStack> context) {
        return contextGenerator.generateContext(getPlugin(), context);
    }

    @Override
    public List<CUSTOM> listBasicSuggestions(ICommandContext commandContext) {
        return List.of();
    }

    @Override
    public Map<CUSTOM, Message> listSuggestions(ICommandContext commandContext) {
        var suggestions = listBasicSuggestions(commandContext);
        var defaultMessage = getDefaultTooltipMessage();
        if (!suggestions.isEmpty()) {
            return suggestions.stream().collect(Collectors.toMap(suggestion -> suggestion, unused -> defaultMessage));
        }
        return Map.of();
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(generateContext((CommandContext<CommandSourceStack>) context));

        var splitWords = builder.getRemaining().split(" ");
        var currentWord = splitWords.length == 0 ? builder.getRemaining() : splitWords[splitWords.length - 1];

        var newBuilder = builder.createOffset(builder.getStart() + builder.getRemaining().length());

        suggestions.entrySet().stream()
                .filter(entry -> currentWord.isBlank() || builder.getRemaining().endsWith(" ") || convertToNative(entry.getKey()).toString().toLowerCase().startsWith(currentWord.toLowerCase()))
                .forEach(entry -> newBuilder.suggest(convertToNative(entry.getKey()).toString().substring(builder.getRemaining().endsWith(" ") ? 0 : currentWord.length()), entry.getValue()));
        return newBuilder.buildFuture();
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return new LiteralMessage("");
    }
}
