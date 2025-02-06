package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkCommandNode;

public interface IPdkCommandNodeBuilder<CURRENT_NODE extends IPdkCommandNodeBuilder<?, ?>, PARENT_NODE> {

    /**
     * Sets the permission for this node.
     * @param permission The permission to set.
     * @return The current node.
     */
    CURRENT_NODE permission(String permission);

    /**
     * Allows this node to be an execution path and will execute the default executor.
     * @return The current node.
     */
    CURRENT_NODE canExecute();

    /**
     * Allows this node to be an execution path and will execute the specified executor.
     * @param commandExecutor The executor to execute.
     * @return The current node.
     */
    CURRENT_NODE canExecute(ICommandExecutor commandExecutor);

    /**
     * Allows this node to be an execution path and will execute the default executor. This will also return the parent node and end the current node.
     * @return The parent node.
     */
    PARENT_NODE executes();

    /**
     * Allows this node to be an execution path and will execute the specified executor. This will also return the parent node and end the current node.
     * @param commandExecutor The executor to execute.
     * @return The parent node.
     */
    PARENT_NODE executes(ICommandExecutor commandExecutor);

    /**
     * Ends the current node and returns the parent node.
     * @return The parent node.
     */
    PARENT_NODE end();

    /**
     * Adds a literal node child node to the current node.
     * @param literal The literal to add.
     * @return The literal node builder.
     */
    IPdkLiteralNodeBuilder<CURRENT_NODE> then(String literal);

    /**
     * Adds a typed node child node to the current node.
     * @param argument The argument to add.
     * @param argumentType The type of the argument.
     * @param <T> The type of the argument.
     * @return The typed node builder.
     */
    <T> IPdkTypedNodeBuilder<CURRENT_NODE, T> then(String argument, ArgumentType<T> argumentType);

    /**
     * Builds the current node.
     * @return The built node.
     */
    IPdkCommandNode build();

    /**
     * Gets the permission of this node.
     * @return The permission of this node.
     */
    String getPermission();

}
