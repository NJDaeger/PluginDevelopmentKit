package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDoubleTypedArgument<TYPE> extends AbstractFixedTypedArgument<TYPE, Double> {

    public AbstractDoubleTypedArgument(long min, long max) {
        super(min, max);
    }

    @Override
    public @NotNull ArgumentType<Double> getNativeType() {
        return DoubleArgumentType.doubleArg(min, max);
    }
}
