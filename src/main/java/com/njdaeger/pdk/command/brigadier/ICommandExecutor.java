package com.njdaeger.pdk.command.brigadier;

import com.njdaeger.pdk.command.exception.PDKCommandException;

public interface ICommandExecutor {

    void execute(ICommandContext context) throws PDKCommandException;

}
