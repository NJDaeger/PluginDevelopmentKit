package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

@SuppressWarnings("UnstableApiUsage")
public interface IBuiltCommand {

    ICommandRoot getCommandRoot();

    LiteralCommandNode<CommandSourceStack> getBaseCommand();

    default void register(Plugin plugin) {
        var lcem = plugin.getLifecycleManager();
        lcem.registerEventHandler(LifecycleEvents.COMMANDS, e -> {
            e.registrar().register(getBaseCommand(), getCommandRoot().getDescription(), getCommandRoot().getAliases());
        });
    }

}
