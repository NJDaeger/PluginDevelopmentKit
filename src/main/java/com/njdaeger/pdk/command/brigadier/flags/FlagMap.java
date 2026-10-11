package com.njdaeger.pdk.command.brigadier.flags;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlagMap {

    private final Map<String, Object> flagMap;
    private final Map<String, String> rawFlagValues;

    public FlagMap() {
        this.flagMap = new ConcurrentHashMap<>();
        this.rawFlagValues = new ConcurrentHashMap<>();
    }

    public Map<String, Object> getFlags() {
        return flagMap;
    }

    public Map<String, String> getRawFlagValues() {
        return rawFlagValues;
    }

    public boolean hasFlag(String flagName) {
        return flagMap.containsKey(flagName.toLowerCase());
    }

    @Nullable
    public <T> T getFlag(String flagName) {
        return (T) flagMap.get(flagName.toLowerCase());
    }

    @Nullable
    public String getRawFlagValue(String flagName) {
        return rawFlagValues.get(flagName.toLowerCase());
    }

    @Contract("_, _ -> param2")
    public <T> T getFlag(String flagName, T defaultValue) {
        return hasFlag(flagName) ? getFlag(flagName) : defaultValue;
    }

    public void setFlag(String flagName, Object value, String rawValue) {
        flagMap.put(flagName.toLowerCase(), value);
        rawFlagValues.put(flagName.toLowerCase(), rawValue);
    }

}
