package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.PDKCommandException;

public interface TabExecutor {
    
    void complete(TabContext context) throws PDKCommandException;
    
}
