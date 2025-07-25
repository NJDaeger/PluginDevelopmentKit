package com.njdaeger.pdk.command.brigadier;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

public class AsyncCommandContextImpl extends CommandContextImpl implements IAsyncCommandContext {

    public AsyncCommandContextImpl(CommandContext<CommandSourceStack> baseContext) {
        super(baseContext);
    }
}
