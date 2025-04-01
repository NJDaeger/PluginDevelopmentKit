package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class PdkTypedNode<T> extends PdkCommandNode implements IPdkTypedNode<T>{

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    public PdkTypedNode(ICommandExecutor executor, List<IPdkCommandNode> arguments, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, String argumentName, ArgumentType<T> argumentType) {
        super(executor, arguments, permissionMode, permissions, baseNode);
        this.argumentName = argumentName;
        this.argumentType = argumentType;
    }

    @Override
    public String getArgumentName() {
        return argumentName;
    }

    @Override
    public ArgumentType<T> getArgumentType() {
        return argumentType;
    }
}
