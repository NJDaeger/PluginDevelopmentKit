package com.njdaeger.pdk.command.brigadier.flags;

import com.mojang.brigadier.arguments.ArgumentType;

public class PdkCommandFlag<T> implements IPdkCommandFlag<T> {

    private final String name;
    private final String tooltipMessage;
    private final ArgumentType<T> type;

    public PdkCommandFlag(String name, String tooltipMessage) {
        this.name = name;
        this.tooltipMessage = tooltipMessage;
        this.type = null;
    }

    public PdkCommandFlag(String name, String tooltipMessage, ArgumentType<T> type) {
        this.name = name;
        this.type = type;
        this.tooltipMessage = tooltipMessage;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getTooltip() {
        return tooltipMessage;
    }

    @Override
    public ArgumentType<T> getType() {
        return type;
    }
}
