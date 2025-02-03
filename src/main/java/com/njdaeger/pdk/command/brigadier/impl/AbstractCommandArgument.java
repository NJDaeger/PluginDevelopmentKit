package com.njdaeger.pdk.command.brigadier.impl;

import com.njdaeger.pdk.command.brigadier.ICommandArgument;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractCommandArgument implements ICommandArgument {

    Supplier<ICommandExecutor> defaultExecutor;
    ICommandExecutor executor;
    final List<ICommandArgument> arguments;

    public AbstractCommandArgument(Supplier<ICommandExecutor> defaultExecutor) {
        this.executor = null;
        this.defaultExecutor = defaultExecutor;
        this.arguments = new ArrayList<>();
    }

    @Override
    public ICommandArgument canExecute() {
        this.executor = ctx -> defaultExecutor.get().execute(ctx);
        return this;
    }

    @Override
    public ICommandArgument canExecute(ICommandExecutor executor) {
        this.executor = executor;
        return this;
    }

    @Override
    public ICommandArgument then(ICommandArgument argument) {
        argument.setDefaultExecutor(defaultExecutor);
        arguments.add(argument);
        return this;
    }

    @Override
    public ICommandExecutor getExecutor() {
        return executor;
    }

    @Override
    public List<ICommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public void setDefaultExecutor(Supplier<ICommandExecutor> executor) {
        this.defaultExecutor = executor;
    }
}
