package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkTypedNode;
import com.njdaeger.pdk.command.brigadier.nodes.PdkTypedNode;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PdkTypedNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?>, T> extends BasePdkCommandNodeBuilder<IPdkTypedNodeBuilder<PARENT_NODE, T>, PARENT_NODE> implements IPdkTypedNodeBuilder<PARENT_NODE, T> {

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    public PdkTypedNodeBuilder(ICommandExecutor defaultExecutor, PARENT_NODE parentNode, String argumentName, ArgumentType<T> argumentType) {
        super(defaultExecutor, parentNode);
        this.argumentName = argumentName;
        this.argumentType = argumentType;
        this.permissions = parentNode.getPermissions();
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T> permission(PermissionMode permissionMode, String... permissions) {
        this.permissionMode = permissionMode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T> canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkTypedNodeBuilder<PARENT_NODE, T> canExecute(ICommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    public IPdkTypedNode<T> build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkTypedNode<>(commandExecutor, children, permissionMode, permissions, Commands.argument(argumentName, argumentType), argumentName, argumentType);
    }
}
