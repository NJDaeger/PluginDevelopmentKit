package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractLongTypedArgument<TYPE> extends AbstractFixedTypedArgument<TYPE, Long>{

    public AbstractLongTypedArgument(long min, long max) {
        super(min, max);
    }

    @Override
    public @NotNull ArgumentType<Long> getNativeType() {
        return LongArgumentType.longArg(min, max);
    }

}
