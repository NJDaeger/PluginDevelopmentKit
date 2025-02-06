package com.njdaeger.pdk.command.brigadier.flags;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class FlagMap {

    private final Map<String, Object> flagMap;

    public FlagMap() {
        this.flagMap = new HashMap<>();
    }

    public boolean hasFlag(String flagName) {
        return flagMap.containsKey(flagName.toLowerCase());
    }

    @Nullable
    public <T> T getFlag(String flagName) {
        return (T) flagMap.get(flagName.toLowerCase());
    }

    @Contract("_, _ -> param2")
    public <T> T getFlag(String flagName, T defaultValue) {
        return hasFlag(flagName) ? getFlag(flagName) : defaultValue;
    }

    public void setFlag(String flagName, Object value) {
        flagMap.put(flagName.toLowerCase(), value);
    }

}
