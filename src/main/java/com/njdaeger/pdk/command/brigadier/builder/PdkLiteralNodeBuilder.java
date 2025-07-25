package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkLiteralNode;
import com.njdaeger.pdk.command.brigadier.nodes.PdkLiteralNode;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PdkLiteralNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?, EXECUTOR, CTX>, EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends BasePdkCommandNodeBuilder<IPdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX>, PARENT_NODE, EXECUTOR, CTX> implements IPdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX> {

    private final String literal;

    public PdkLiteralNodeBuilder(EXECUTOR defaultExecutor, PARENT_NODE parentNode, String literal) {
        super(defaultExecutor, parentNode);
        this.literal = literal;
        this.permissions = parentNode.getPermissions();
        this.permissionMode = parentNode.getPermissionMode();
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX> permission(PermissionMode permissionMode, String... permissions) {
        this.permissionMode = permissionMode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX> canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE, EXECUTOR, CTX> canExecute(EXECUTOR commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    public IPdkLiteralNode<EXECUTOR, CTX> build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkLiteralNode<EXECUTOR, CTX>(commandExecutor, children, permissionMode, permissions, Commands.literal(literal), literal);
    }

}
