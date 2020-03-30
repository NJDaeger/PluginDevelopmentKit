package com.njdaeger.pdk.command.flag;

public abstract class AbstractOptionalFlag<T> extends Flag<T> {
    
    public AbstractOptionalFlag(Class<T> type, String flag, boolean followingArgument) {
        super(type, flag, followingArgument);
    }
}
