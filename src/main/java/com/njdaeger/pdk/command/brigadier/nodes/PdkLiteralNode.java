package com.njdaeger.pdk.command.brigadier.nodes;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.PermissionMode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.checkerframework.checker.units.qual.C;

import java.util.List;

public class PdkLiteralNode<EXECUTOR extends ICommandExecutor<CTX>, CTX extends ICommandContext> extends PdkCommandNode<EXECUTOR, CTX> implements IPdkLiteralNode<EXECUTOR, CTX> {

    private final String literal;

    public PdkLiteralNode(EXECUTOR executor, List<IPdkCommandNode<EXECUTOR, CTX>> arguments, PermissionMode permissionMode, String[] permissions, ArgumentBuilder<CommandSourceStack, ?> baseNode, String literal) {
        super(executor, arguments, permissionMode, permissions, baseNode);
        this.literal = literal;
    }

    @Override
    public String getLiteral() {
        return literal;
    }
}
