package com.njdaeger.pdk.command.brigadier.impl;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ICommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandRoot;
import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.exception.PermissionDeniedException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@SuppressWarnings("UnstableApiUsage")
public interface ExecutionHelpers {

    static Command<CommandSourceStack> rootCommandExecution(String permission, ICommandExecutor executor) {
        return (ctx) -> {
            try {
                if (permission != null && !ctx.getSource().getSender().hasPermission(permission)) throw new PermissionDeniedException();
                executor.execute(new CommandContextImpl(ctx));
            } catch (PDKCommandException e) {
                ctx.getSource().getSender().sendMessage(e.getMessage());

            }
            return Command.SINGLE_SUCCESS;
        };
    }

    static ICommandContext createCommandContext(CommandContext<CommandSourceStack> baseContext) {
        return new CommandContextImpl(baseContext);
    }

}
