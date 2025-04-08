package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import com.njdaeger.pdk.command.brigadier.nodes.IPdkLiteralNode;
import com.njdaeger.pdk.command.brigadier.nodes.PdkLiteralNode;
import io.papermc.paper.command.brigadier.Commands;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PdkLiteralNodeBuilder<PARENT_NODE extends IPdkCommandNodeBuilder<?, ?>> extends BasePdkCommandNodeBuilder<IPdkLiteralNodeBuilder<PARENT_NODE>, PARENT_NODE> implements IPdkLiteralNodeBuilder<PARENT_NODE> {

    private final String literal;

    public PdkLiteralNodeBuilder(ICommandExecutor defaultExecutor, PARENT_NODE parentNode, String literal) {
        super(defaultExecutor, parentNode);
        this.literal = literal;
        this.permissions = parentNode.getPermissions();
        this.permissionMode = parentNode.getPermissionMode();
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE> permission(PermissionMode permissionMode, String... permissions) {
        this.permissionMode = permissionMode;
        this.permissions = permissions;
        return this;
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE> canExecute() {
        this.commandExecutor = defaultExecutor;
        return this;
    }

    @Override
    public PdkLiteralNodeBuilder<PARENT_NODE> canExecute(ICommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return this;
    }

    @Override
    public IPdkLiteralNode build() {
        var children = childrenNodes.stream().map(IPdkCommandNodeBuilder::build).collect(Collectors.toCollection(ArrayList::new));
        return new PdkLiteralNode(commandExecutor, children, permissionMode, permissions, Commands.literal(literal), literal);
    }

}
