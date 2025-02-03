package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandArgument;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;
import com.njdaeger.pdk.command.brigadier.ILiteralArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public class LiteralArgumentImpl extends AbstractCommandArgument implements ILiteralArgument {

    private final String literal;

    public LiteralArgumentImpl(String literal) {
        super(null);
        this.literal = literal;
    }

    @Override
    public String getLiteral() {
        return literal;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.literal(literal);
    }

    @Override
    public ICommandArgument deepCopy() {
        LiteralArgumentImpl copy = new LiteralArgumentImpl(literal);
        copy.defaultExecutor = defaultExecutor;
        copy.executor = getExecutor();
        copy.arguments.addAll(getArguments().stream().map(ICommandArgument::deepCopy).toList());
        return copy;
    }

    @Override
    public String toString() {
        return "LiteralArgumentImpl{literal='" + literal + "'}";
    }
}
