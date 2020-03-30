package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.PDKCommandException;

public interface CommandExecutor {
    
    void execute(CommandContext context) throws PDKCommandException;
    
}
