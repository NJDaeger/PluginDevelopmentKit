package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

@SuppressWarnings("UnstableApiUsage")
public interface ITypedArgument<T> extends ICommandArgument {

    String getName();

    ArgumentType<T> getType();

}
