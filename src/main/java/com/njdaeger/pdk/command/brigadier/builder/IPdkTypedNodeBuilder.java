package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkTypedNode;

public interface IPdkTypedNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?, EXECUTOR, CTX>, T, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends IPdkCommandNodeBuilder<IPdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX>, PARENT_NODE, EXECUTOR, CTX> {

    /**
     * Builds the typed node.
     * @return The typed node.
     */
    @Override
    IPdkTypedNode<T, EXECUTOR, CTX> build();
}
