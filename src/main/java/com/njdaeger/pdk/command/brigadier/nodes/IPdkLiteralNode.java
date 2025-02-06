package com.njdaeger.pdk.command.brigadier.nodes;

public interface IPdkLiteralNode extends IPdkCommandNode {

    /**
     * Gets the literal string this node represents.
     * @return The literal string this node represents.
     */
    String getLiteral();

}
