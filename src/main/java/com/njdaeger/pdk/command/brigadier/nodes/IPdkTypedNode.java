package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.arguments.ArgumentType;

public interface IPdkTypedNode<T> extends IPdkCommandNode {

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
