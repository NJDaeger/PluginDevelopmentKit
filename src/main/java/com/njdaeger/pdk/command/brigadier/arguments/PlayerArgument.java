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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerArgument extends BasePdkArgumentType<Player, String> {

    private static final DynamicCommandExceptionType PLAYER_NOT_FOUND = new DynamicCommandExceptionType(o -> () -> "Player " + o.toString() + " not found");

    private final Predicate<Player> playerPredicate;
    private final Function<ICommandContext, Map<Player, Message>> suggestions;
    private final Message defaultTooltipMessage;

    public PlayerArgument(Message defaultTooltipMessage) {
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.playerPredicate = p -> true;
        this.suggestions = null;
    }

    public PlayerArgument(Predicate<Player> playerPredicate, Message defaultTooltipMessage) {
        this.defaultTooltipMessage = defaultTooltipMessage;
        this.playerPredicate = playerPredicate;
        this.suggestions = null;
    }

    public PlayerArgument(Function<ICommandContext, Map<Player, Message>> suggestions) {
        this.playerPredicate = p -> true;
        this.suggestions = suggestions;
        this.defaultTooltipMessage = () -> "A player.";
    }

    public PlayerArgument(Function<ICommandContext, Collection<Player>> suggestions, Message defaultTooltipMessage) {
        this.playerPredicate = p -> true;
        this.suggestions = context -> suggestions.apply(context).stream().collect(Collectors.toMap(s -> s, unused -> defaultTooltipMessage));
        this.defaultTooltipMessage = defaultTooltipMessage;
    }

    @Override
    public Message getDefaultTooltipMessage() {
        return defaultTooltipMessage;
    }

    @Override
    public String convertToNative(Player player) {
        return player.getName();
    }

    @Override
    public Player convertToCustom(String nativeType, StringReader reader) throws CommandSyntaxException {
        System.out.println("Converting to custom: " + nativeType);
        var player = Bukkit.getPlayer(nativeType);
        if (player == null) {
            var length = nativeType.length();
            reader.setCursor(reader.getCursor() - length);
            throw PLAYER_NOT_FOUND.createWithContext(reader, nativeType);
        }
        return player;
    }

    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }

    @Override
    public Map<Player, Message> listSuggestions(ICommandContext commandContext) {
        if (suggestions != null) return suggestions.apply(commandContext);
        else return super.listSuggestions(commandContext);
    }

    @Override
    public @NotNull <S> CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        var suggestions = listSuggestions(new CommandContextImpl((CommandContext<CommandSourceStack>) context));

        if (suggestions.isEmpty()) {
            var onlinePlayers = Bukkit.getOnlinePlayers();
            onlinePlayers.stream().filter(playerPredicate).forEach((player) -> builder.suggest(player.getName(), () -> player.getDisplayName()));
            return builder.buildFuture();
        }
        suggestions.forEach((suggestion, message) -> builder.suggest(convertToNative(suggestion), message));
        return builder.buildFuture();
    }
}
