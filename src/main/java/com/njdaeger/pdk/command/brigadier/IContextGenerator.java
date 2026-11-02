package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.plugin.Plugin;

public interface IContextGenerator<CTX extends ICommandContext> {

    CTX generateContext(Plugin plugin, CommandContext<CommandSourceStack> baseContext);

}
