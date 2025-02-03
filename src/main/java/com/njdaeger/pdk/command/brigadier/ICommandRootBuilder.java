package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.arguments.ArgumentType;
import org.bukkit.plugin.Plugin;

import java.util.function.Function;

public interface ICommandRootBuilder {

    ICommandRootBuilder description(String description);

    ICommandRootBuilder permission(String permission);

    ICommandRootBuilder flag(String flagName, String tooltipMessage);

    <T> ICommandRootBuilder flag(String flagName, String tooltipMessage, ArgumentType<T> flagType);

    ICommandRootBuilder defaultExecutor(ICommandExecutor executor);

    ICommandRootBuilder canExecute();

    ICommandRootBuilder canExecute(ICommandExecutor executor);

    <T extends ICommandArgument> ICommandRootBuilder then(T argument);

    IBuiltCommand build(Plugin plugin);

}
