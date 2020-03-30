package com.njdaeger.pdk.types;

import com.njdaeger.pdk.command.exception.PDKCommandException;

public abstract class ParsedType<T> {
    
    public ParsedType() {

    }
    
    public abstract T parse(String input) throws PDKCommandException;
    
    public abstract Class<T> getType();
    
}
