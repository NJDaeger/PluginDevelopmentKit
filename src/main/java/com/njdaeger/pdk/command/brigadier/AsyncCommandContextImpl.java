package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class AsyncCommandContextImpl extends CommandContextImpl implements IAsyncCommandContext {

    public AsyncCommandContextImpl(Plugin plugin, CommandContext<CommandSourceStack> baseContext) {
        super(plugin, baseContext);
    }

//    public void schedule(Runnable runnable) {
//        Bukkit.getScheduler().runTask()
//    }

}
