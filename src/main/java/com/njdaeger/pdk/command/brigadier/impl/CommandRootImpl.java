package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.tree.LiteralCommandNode;
import com.njdaeger.pdk.command.brigadier.ICommandArgument;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandFlag;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class CommandRootImpl implements ICommandRoot {

    private final Plugin plugin;
    private final String[] aliases;
    private final String description;
    private final String permission;
    private final List<ICommandFlag<?>> flags;
    private final ICommandExecutor defaultExecutor;
    private final List<ICommandArgument> arguments;

    public CommandRootImpl(Plugin plugin, String[] aliases, String description, String permission, List<ICommandFlag<?>> flags, ICommandExecutor defaultExecutor, List<ICommandArgument> arguments) {
        this.plugin = plugin;
        this.aliases = aliases;
        this.description = description;
        this.permission = permission;
        this.flags = flags;
        this.defaultExecutor = defaultExecutor;
        this.arguments = arguments;
    }


    @Override
    public ICommandExecutor getDefaultExecutor() {
        return defaultExecutor;
    }

    @Override
    public List<String> getAliases() {
        return List.of(aliases);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getPermission() {
        return permission;
    }

    @Override
    public List<ICommandFlag<?>> getFlags() {
        return flags;
    }

    @Override
    public List<ICommandArgument> getArguments() {
        return arguments;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

}
