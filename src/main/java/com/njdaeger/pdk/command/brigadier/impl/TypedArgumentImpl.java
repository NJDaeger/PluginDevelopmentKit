package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandArgument;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;
import com.njdaeger.pdk.command.brigadier.ITypedArgument;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.bukkit.plugin.Plugin;

import static com.njdaeger.pdk.command.brigadier.impl.ExecutionHelpers.rootCommandExecution;

@SuppressWarnings("UnstableApiUsage")
public class TypedArgumentImpl<T> extends AbstractCommandArgument implements ITypedArgument<T> {

    private final String name;
    private final ArgumentType<T> type;

    public TypedArgumentImpl(String name, ArgumentType<T> type) {
        super(null);
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArgumentType<T> getType() {
        return type;
    }

    @Override
    public ArgumentBuilder<CommandSourceStack, ?> build() {
        return Commands.argument(name, type);
    }

    @Override
    public ICommandArgument deepCopy() {
        TypedArgumentImpl<T> copy = new TypedArgumentImpl<>(name, type);
        copy.defaultExecutor = defaultExecutor;
        copy.executor = getExecutor();
        copy.arguments.addAll(getArguments().stream().map(ICommandArgument::deepCopy).toList());
        return copy;
    }

    @Override
    public String toString() {
        return "TypedArgumentImpl{name='" + name + "', type=" + type.toString() + "}";
    }
}
