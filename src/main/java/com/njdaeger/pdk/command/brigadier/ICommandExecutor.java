package com.njdaeger.pdk.command.brigadier;

import com.njdaeger.pdk.command.exception.PDKCommandException;

/**
 * Represents a command executor that can be used to execute a command.
 */
@FunctionalInterface
public interface ICommandExecutor {

    /**
     * Executes the command.
     * @param context The current command context
     * @throws PDKCommandException If the command failed.
     */
    void execute(ICommandContext context) throws PDKCommandException;

}
