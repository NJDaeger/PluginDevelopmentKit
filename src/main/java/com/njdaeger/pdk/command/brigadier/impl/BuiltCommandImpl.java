package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.njdaeger.pdk.command.brigadier.IBuiltCommand;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class BuiltCommandImpl implements IBuiltCommand {

    private final ICommandRoot commandRoot;
    private final LiteralCommandNode<CommandSourceStack> baseCommand;

    public BuiltCommandImpl(ICommandRoot commandRoot, LiteralCommandNode<CommandSourceStack> baseCommand) {
        this.commandRoot = commandRoot;
        this.baseCommand = baseCommand;
    }

    @Override
    public ICommandRoot getCommandRoot() {
        return commandRoot;
    }

    @Override
    public LiteralCommandNode<CommandSourceStack> getBaseCommand() {
        return baseCommand;
    }

}
