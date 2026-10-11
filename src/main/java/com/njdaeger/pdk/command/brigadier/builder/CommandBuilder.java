package com.njdaeger.pdk.command.brigadier.builder;

import com.njdaeger.pdk.command.brigadier.AsyncCommandContextImpl;
import com.njdaeger.pdk.command.brigadier.CommandContextImpl;
import com.njdaeger.pdk.command.brigadier.IAsyncCommandContext;
import com.njdaeger.pdk.command.brigadier.IAsyncCommandExecutor;
import com.njdaeger.pdk.command.brigadier.ICommandContext;
import com.njdaeger.pdk.command.brigadier.ISyncCommandExecutor;

public class CommandBuilder {

    /**
     * Creates a new {@link IPdkRootNodeBuilder} with the given aliases. This is a synchronous command builder.
     * @param aliases the aliases for the command
     * @return a new {@link IPdkRootNodeBuilder} instance
     */
    public static IPdkRootNodeBuilder<ISyncCommandExecutor, ICommandContext> of(String... aliases) {
        return new PdkRootNodeBuilder<>(aliases, ctx -> ctx.error("There is no default command executor defined!"), CommandContextImpl::new);
    }

    /**
     * Creates a new {@link IPdkRootNodeBuilder} with the given aliases. This is an asynchronous command builder.
     * @param aliases the aliases for the command
     * @return a new {@link IPdkRootNodeBuilder} instance
     */
    public static IPdkRootNodeBuilder<IAsyncCommandExecutor, IAsyncCommandContext> ofAsync(String... aliases) {
        return new PdkRootNodeBuilder<>(aliases, ctx -> ctx.error("There is no default command executor defined!"), AsyncCommandContextImpl::new);
    }

}
