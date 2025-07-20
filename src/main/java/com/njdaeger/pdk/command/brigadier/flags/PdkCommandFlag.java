package com.njdaeger.pdk.command.brigadier.flags;

import com.mojang.brigadier.arguments.ArgumentType;

public class PdkCommandFlag<T> implements IPdkCommandFlag<T> {

    private final String name;
    private final String tooltipMessage;
    private final ArgumentType<T> type;
    private final boolean hide;

    public PdkCommandFlag(String name, String tooltipMessage, boolean hide) {
        this.name = name;
        this.tooltipMessage = tooltipMessage;
        this.type = null;
        this.hide = hide;
    }

    public PdkCommandFlag(String name, String tooltipMessage, ArgumentType<T> type, boolean hide) {
        this.name = name;
        this.type = type;
        this.tooltipMessage = tooltipMessage;
        this.hide = hide;
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
    public boolean isHidden() {
        return hide;
    }

    @Override
    public ArgumentType<T> getType() {
        return type;
    }
}
