package com.njdaeger.pdk.command.usage;

import java.util.ArrayList;
import java.util.List;

public class CommandUsage {

    private String commandName;
    private final List<UsagePart> usage;

    public CommandUsage() {
        this.usage = new ArrayList<>();
    }

    protected void setName(String name) {
        this.commandName = name;
    }

    public void add(UsagePart part) {
        usage.add(part);
    }

}
