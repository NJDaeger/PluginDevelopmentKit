package com.njdaeger.pdk.command.brigadier.nodes;

import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;

public interface IPdkLiteralNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends IPdkCommandNode<EXECUTOR, CTX> {

    /**
     * Gets the literal string this node represents.
     * @return The literal string this node represents.
     */
    String getLiteral();

}
