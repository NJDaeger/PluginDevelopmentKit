package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractIntegerTypedArgument<TYPE> extends AbstractFixedTypedArgument<TYPE, Integer> {

    public AbstractIntegerTypedArgument(int min, int max) {
        super(min, max);
    }

    @Override
    public @NotNull ArgumentType<Integer> getNativeType() {
        return IntegerArgumentType.integer((int) min, (int) max);
    }
}
