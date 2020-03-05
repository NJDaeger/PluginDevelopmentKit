package com.njdaeger.pdk.command;

public interface TabExecutor {
    
    void complete(CommandContext command, TabContext context);
    
}
