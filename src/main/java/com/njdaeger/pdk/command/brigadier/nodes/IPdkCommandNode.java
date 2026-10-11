package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public interface IPdkCommandNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> {

    /**
     * Gets the executor for this command node.
     * @return The executor for this command node.
     */
    EXECUTOR getExecutor();

    /**
     * Checks if this command node can be executed.
     * @return True if this command node can be executed, false otherwise.
     */
    default boolean canExecute() {
        return getExecutor() != null;
    }

    /**
     * Gets the arguments for this command node.
     * @return The arguments for this command node.
     */
    List<IPdkCommandNode<EXECUTOR, CTX>> getArguments();

    /**
     * Gets the permission for this command node.
     * @return The permission for this command node.
     */
    String[] getPermissions();

    /**
     * Gets the permission mode for this command node.
     * @return The permission mode for this command node.
     */
    PermissionMode getPermissionMode();

    /**
     * Gets the base mojang-based node for this command node.
     * @return The base mojang-based node for this command node.
     */
    ArgumentBuilder<CommandSourceStack, ?>  getBaseNode();
}
