package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class PdkLiteralNode extends PdkCommandNode implements IPdkLiteralNode {

    private final String literal;

    public PdkLiteralNode(ICommandExecutor executor, List<IPdkCommandNode> arguments, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, String literal) {
        super(executor, arguments, permissionMode, permissions, baseNode);
        this.literal = literal;
    }

    @Override
    public String getLiteral() {
        return literal;
    }
}
