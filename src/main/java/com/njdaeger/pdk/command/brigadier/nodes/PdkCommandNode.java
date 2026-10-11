package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;
import java.util.stream.Stream;

public class PdkCommandNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> implements IPdkCommandNode<EXECUTOR, CTX> {

    private final ArgumentBuilder<CommandSourceStack, ?> baseNode;
    private final EXECUTOR executor;
    private final List<IPdkCommandNode<EXECUTOR, CTX>> arguments;
    private final PermissionMode permissionMode;
    private final String[] permissions;

    public PdkCommandNode(EXECUTOR executor, List<IPdkCommandNode<EXECUTOR, CTX>> arguments, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode) {
        this.executor = executor;
        this.arguments = arguments;
        this.permissions = permissions;
        this.permissionMode = permissionMode;
        if (permissions != null && permissions.length > 0 && permissionMode != null)
            this.baseNode = baseNode.requires(cs -> {
                var sender = cs.getSender();
                if (permissionMode == PermissionMode.ANY) return Stream.of(permissions).anyMatch(sender::hasPermission);
                else if (permissionMode == PermissionMode.ALL) return Stream.of(permissions).allMatch(sender::hasPermission);
                return false;
            });
        else this.baseNode = baseNode;
    }

    @Override
    public EXECUTOR getExecutor() {
        return executor;
    }

    @Override
    public List<IPdkCommandNode<EXECUTOR, CTX>> getArguments() {
        return arguments;
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
    public ArgumentBuilder<CommandSourceStack, ?> getBaseNode() {
        return baseNode;
    }
}
