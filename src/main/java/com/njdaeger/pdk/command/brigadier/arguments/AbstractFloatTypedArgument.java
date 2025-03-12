package com.njdaeger.pdk.command.brigadier.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractFloatTypedArgument<TYPE> extends AbstractFloatingTypedArgument<TYPE, Float> {

    public AbstractFloatTypedArgument(float min, float max) {
        super(min, max);
    }

    @Override
    public @NotNull ArgumentType<Float> getNativeType() {
        return FloatArgumentType.floatArg((float) min, (float) max);
    }
}
