package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkLiteralNode;

public interface IPdkLiteralNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?, EXECUTOR, CTX>, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends IPdkCommandNodeBuilder<IPdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX>, PARENT_NODE, EXECUTOR, CTX> {

    /**
     * Builds the literal node.
     * @return The literal node.
     */
    @Override
    IPdkLiteralNode<EXECUTOR, CTX> build();
}
