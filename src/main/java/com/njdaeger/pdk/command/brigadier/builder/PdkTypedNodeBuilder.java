package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.IContextGenerator;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.arguments.IPdkArgumentType;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkTypedNode;
import com.njdaeger.pdk.command.brigadier.nodes.PdkTypedNode;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PdkTypedNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?, EXECUTOR, CTX>, T, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends BasePdkCommandNodeBuilder<IPdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX>, PARENT_NODE, EXECUTOR, CTX> implements IPdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX> {

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    public PdkTypedNodeBuilder(EXECUTOR defaultExecutor, PARENT_NODE parentNode, String argumentName, ArgumentType<T> argumentType, IContextGenerator<CTX> contextGenerator) {
        super(defaultExecutor, parentNode, contextGenerator);
        this.argumentName = argumentName;
        this.argumentType = argumentType;
        this.permissions = parentNode.getPermissions();
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX> permission(PermissionMode permissionMode, String... permissions) {
        this.permissionMode = permissionMode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX> canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T, EXECUTOR, CTX> canExecute(EXECUTOR commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    public IPdkTypedNode<T, EXECUTOR, CTX> build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkTypedNode<>(commandExecutor, children, permissionMode, permissions, Commands.argument(argumentName, argumentType), argumentName, argumentType);
    }
}
