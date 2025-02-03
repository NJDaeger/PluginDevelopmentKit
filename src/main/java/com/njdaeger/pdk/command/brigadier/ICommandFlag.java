package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;

@SuppressWarnings("UnstableApiUsage")
public interface ICommandFlag<T> {

    String getName();

    boolean isBooleanFlag();

    ArgumentType<T> getType();

    default String getTooltip() {
        return "";
    }

    default Message getTooltipAsMessage() {
        return new LiteralMessage(getTooltip());
    }

}
