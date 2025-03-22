package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkRootNode;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface IPdkRootNodeBuilder extends IPdkCommandNodeBuilder<IPdkRootNodeBuilder, IPdkRootNodeBuilder> {

    /**
     * Sets the description of the command.
     * @param description The description of the command.
     * @return The current builder instance.
     */
    IPdkRootNodeBuilder description(String description);

    /**
     * Adds a flag to the command.
     * @param flagName The name of the flag.
     * @param tooltipMessage The message to display when hovering over the flag.
     * @return The current builder instance.
     */
    IPdkRootNodeBuilder flag(String flagName, String tooltipMessage);

    /**
     * Adds a hidden flag to the command. Hidden flags are not displayed in the command's tab completion.
     * @param flagName The name of the flag.
     * @param tooltipMessage The message to display when hovering over the flag.
     * @return The current builder instance.
     */
    IPdkRootNodeBuilder hiddenFlag(String flagName, String tooltipMessage);

    /**
     * Adds a flag to the command.
     * @param flagName The name of the flag.
     * @param tooltipMessage The message to display when hovering over the flag.
     * @param flagType The type of the flag.
     * @param <T> The type of the flag.
     * @return The current builder instance.
     */
    <T> IPdkRootNodeBuilder flag(String flagName, String tooltipMessage, ArgumentType<T> flagType);

    /**
     * Adds a hidden flag to the command. Hidden flags are not displayed in the command's tab completion.
     * @param flagName The name of the flag.
     * @param tooltipMessage The message to display when hovering over the flag.
     * @param flagType The type of the flag.
     * @param <T> The type of the flag.
     * @return The current builder instance.
     */
    <T> IPdkRootNodeBuilder hiddenFlag(String flagName, String tooltipMessage, ArgumentType<T> flagType);

    /**
     * Sets the default executor for this command for execution paths that do not explicitly have an executor.
     * @param executor The executor to use as the default executor.
     * @return The current builder instance.
     */
    IPdkRootNodeBuilder defaultExecutor(ICommandExecutor executor);

    /**
     * Sets the custom help text for this command for /help command.
     * @param componentGenerator The custom help text to display to the sender.
     * @return The current builder instance.
     */
    IPdkRootNodeBuilder helpText(BiFunction<IPdkRootNode, CommandSender, TextComponent> componentGenerator);

    /**
     * Registers the command
     * @param plugin The plugin to register the command to
     */
    default void register(Plugin plugin) {
        build().register(plugin);
    }

    /**
     * Builds the root node.
     * @return The root node.
     */
    @Override
    IPdkRootNode build();

    /**
     * This will throw an exception as a root node has no parent node to default back onto.
     * @deprecated A root node has no parent node to default back onto. This method will throw an exception. Please use the canExecute() method to make this path execute the default executor.
     */
    @Override
    @Contract(" -> fail")
    @Deprecated
    default IPdkRootNodeBuilder executes() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute() method to make this path execute the default executor.");
    }

    /**
     * This will throw an exception as a root node has no parent node to default back onto.
     * @deprecated A root node has no parent node to default back onto. This method will throw an exception. Please use the canExecute(ICommandExecutor) method to make this path execute the default executor.
     */
    @Override
    @Contract("_ -> fail")
    @Deprecated
    default IPdkRootNodeBuilder executes(ICommandExecutor commandExecutor) throws UnsupportedOperationException  {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. Please use the canExecute(ICommandExecutor) method to make this path execute the default executor.");
    }

    /**
     * This will throw an exception as a root node has no parent node to default back onto.
     * @deprecated A root node has no parent node to default back onto. This method will throw an exception. To finish building the command, use the build() or register(Plugin) methods.
     */
    @Override
    @Contract(" -> fail")
    @Deprecated
    default IPdkRootNodeBuilder end() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("A root node has no parent node to default back onto. To finish building the command, use the build() or register(Plugin) methods.");
    }

}
