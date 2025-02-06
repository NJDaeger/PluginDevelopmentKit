package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.nodes.IPdkLiteralNode;

public interface IPdkLiteralNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?>> extends IPdkCommandNodeBuilder<IPdkLiteralNodeBuilder<PARENT_NODE>, PARENT_NODE> {

    /**
     * Builds the literal node.
     * @return The literal node.
     */
    @Override
    IPdkLiteralNode build();
}
