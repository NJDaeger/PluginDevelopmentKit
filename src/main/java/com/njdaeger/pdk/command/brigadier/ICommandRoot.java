package com.njdaeger.pdk.command.brigadier;

import com.njdaeger.pdk.command.brigadier.impl.CommandRootBuilderImpl;
import org.bukkit.plugin.Plugin;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public interface ICommandRoot {

    static ICommandRootBuilder of(String... alias) {
        if (alias.length == 0) throw new IllegalArgumentException("Alias cannot be empty.");

        return new CommandRootBuilderImpl(alias);
    }

    ICommandExecutor getDefaultExecutor();

    List<String> getAliases();

    String getDescription();

    String getPermission();

    List<ICommandFlag<?>> getFlags();

    List<ICommandArgument> getArguments();

    Plugin getPlugin();

}
