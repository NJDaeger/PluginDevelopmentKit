package com.njdaeger.pdk.command.brigadier.nodes;

import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.flags.IPdkCommandFlag;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;

public interface IPdkRootNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends IPdkCommandNode<EXECUTOR, CTX> {

    /**
     * Gets the names of the command. [0] is the primary name.
     *
     * @return The names of the command.
     */
    @NotNull
    List<String> getAliases();

    /**
     * Gets the primary alias of the command. This is the first alias in the list of aliases.
     *
     * @return The primary alias of the command.
     * @throws IllegalArgumentException if there are no aliases defined for this command (should not happen).
     */
    @NotNull
    default String getPrimaryAlias() {
        List<String> aliases = getAliases();
        if (aliases.isEmpty()) throw new IllegalArgumentException("primary alias is null");
        return aliases.getFirst();
    }

    /**
     * Gets the flags of the command.
     * @return The flags of the command.
     */
    @NotNull
    List<IPdkCommandFlag<?>> getFlags();

    /**
     * Gets the description of the command.
     * @return The description of the command.
     */
    @Nullable
    String getDescription();

    /**
     * Gets the custom help text for the command.
     *
     * @return The custom help text for the command.
     */
    BiFunction<IPdkRootNode<EXECUTOR, CTX>, CommandSender, TextComponent> getCustomHelpTextGenerator();

    /**
     * Registers the command with the plugin.
     * @param plugin The plugin to register the command with.
     */
    void register(Plugin plugin);
}
