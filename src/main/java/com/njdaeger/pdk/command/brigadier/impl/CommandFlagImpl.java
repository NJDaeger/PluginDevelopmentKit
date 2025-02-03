package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.njdaeger.pdk.command.brigadier.ICommandFlag;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;

import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class CommandFlagImpl<T> implements ICommandFlag<T> {

    private final String name;
    private final String tooltipMessage;
    private final ArgumentType<T> type;

    public CommandFlagImpl(String name, String tooltipMessage, ArgumentType<T> type) {
        this.name = name;
        this.type = type;
        this.tooltipMessage = tooltipMessage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTooltip() {
        return tooltipMessage;
    }

    @Override
    public boolean isBooleanFlag() {
        return type == null;
    }

    @Override
    public ArgumentType<T> getType() {
        return type;
    }
}
