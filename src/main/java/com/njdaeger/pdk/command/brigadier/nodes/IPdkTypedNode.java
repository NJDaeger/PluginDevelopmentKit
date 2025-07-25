package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;

public interface IPdkTypedNode<T, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends IPdkCommandNode<EXECUTOR, CTX> {

    /**
     * Gets the name of the argument.
     * @return The name of the argument.
     */
    String getArgumentName();

    /**
     * Gets the type of the argument.
     * @return The type of the argument.
     */
    ArgumentType<T> getArgumentType();

}
