package com.njdaeger.pdk.command.brigadier.builder;

import com.mojang.brigadier.arguments.ArgumentType;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePdkCommandNodeBuilder<CURRENT_NODE extends IPdkCommandNodeBuilder<CURRENT_NODE, PARENT_NODE>, PARENT_NODE extends IPdkCommandNodeBuilder<?, ?>> implements IPdkCommandNodeBuilder<CURRENT_NODE, PARENT_NODE> {

    protected String[] permissions;
    protected PermissionMode permissionMode;
    protected ICommandExecutor commandExecutor;
    protected ICommandExecutor defaultExecutor;
    protected final PARENT_NODE parentNode;
    protected final List<IPdkCommandNodeBuilder<?, CURRENT_NODE>> childrenNodes;

    public BasePdkCommandNodeBuilder(ICommandExecutor defaultExecutor, PARENT_NODE parentNode) {
        this.childrenNodes = new ArrayList<>();
        this.defaultExecutor = defaultExecutor;
        this.parentNode = parentNode;
    }

    @Override
    public PARENT_NODE executes() {
        this.commandExecutor = defaultExecutor;
        return parentNode;
    }

    @Override
    public PARENT_NODE executes(ICommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
        return parentNode;
    }

    @Override
    public PARENT_NODE end() {
        return parentNode;
    }

    @Override
    public String[] getPermissions() {
        return permissions;
    }

    @Override
    public PermissionMode getPermissionMode() {
        return permissionMode;
    }

    @Override
    public IPdkLiteralNodeBuilder<CURRENT_NODE> then(String literal) {
        var literalNode = new PdkLiteralNodeBuilder<>(defaultExecutor, this, literal);
        childrenNodes.add((IPdkLiteralNodeBuilder<CURRENT_NODE>) literalNode);
        return (IPdkLiteralNodeBuilder<CURRENT_NODE>) literalNode;
    }

    @Override
    public <T> IPdkTypedNodeBuilder<CURRENT_NODE, T> then(String argument, ArgumentType<T> argumentType) {
        var argumentNode = new PdkTypedNodeBuilder<>(defaultExecutor, this, argument, argumentType);
        childrenNodes.add((IPdkTypedNodeBuilder<CURRENT_NODE, T>) argumentNode);
        return (IPdkTypedNodeBuilder<CURRENT_NODE, T>) argumentNode;
    }
}
