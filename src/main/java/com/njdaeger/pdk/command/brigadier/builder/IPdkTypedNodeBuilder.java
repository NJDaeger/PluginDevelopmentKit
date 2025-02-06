package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.nodes.IPdkTypedNode;

public interface IPdkTypedNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?>, T> extends IPdkCommandNodeBuilder<IPdkTypedNodeBuilder<PARENT_NODE, T>, PARENT_NODE> {

    /**
     * Builds the typed node.
     * @return The typed node.
     */
    @Override
    IPdkTypedNode<T> build();
}
