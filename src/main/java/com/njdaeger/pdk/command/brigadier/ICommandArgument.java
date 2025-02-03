package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.njdaeger.pdk.command.CommandExecutor;
import com.njdaeger.pdk.command.brigadier.impl.LiteralArgumentImpl;
import com.njdaeger.pdk.command.brigadier.impl.TypedArgumentImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("UnstableApiUsage")
public interface ICommandArgument {

    static ILiteralArgument of(String literal) {
        return new LiteralArgumentImpl(literal);
    }

    static <T> ITypedArgument<T>  of(String name, ArgumentType<T> type) {
        return new TypedArgumentImpl<>(name, type);
    }

    ICommandArgument canExecute();

    ICommandArgument canExecute(ICommandExecutor executor);

    ICommandArgument then(ICommandArgument argument);

    ICommandExecutor getExecutor();

    List<ICommandArgument> getArguments();

    ArgumentBuilder<CommandSourceStack, ?> build();

    ICommandArgument deepCopy();

    void setDefaultExecutor(Supplier<ICommandExecutor> executor);

}
