package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;

public class PdkTypedNode<T, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends PdkCommandNode<EXECUTOR, CTX> implements IPdkTypedNode<T, EXECUTOR, CTX>{

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    public PdkTypedNode(EXECUTOR executor, List<IPdkCommandNode<EXECUTOR, CTX>> arguments, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, String argumentName, ArgumentType<T> argumentType) {
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
