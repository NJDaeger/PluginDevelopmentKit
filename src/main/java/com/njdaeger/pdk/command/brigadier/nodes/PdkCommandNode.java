package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class PdkCommandNode implements IPdkCommandNode {

    private final ArgumentBuilder<CommandSourceStack, ?> baseNode;
    private final ICommandExecutor executor;
    private final List<IPdkCommandNode> arguments;
    private final String permission;

    public PdkCommandNode(ICommandExecutor executor, List<IPdkCommandNode> arguments, String permission, ArgumentBuilder<CommandSourceStack, ?> baseNode) {
        this.executor = executor;
        this.baseNode = baseNode;
        this.arguments = arguments;
        this.permission = permission;
    }

    @Override
    public ICommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public List<IPdkCommandNode> getArguments() {
        return arguments;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> getBaseNode() {
        return baseNode;
    }
}
