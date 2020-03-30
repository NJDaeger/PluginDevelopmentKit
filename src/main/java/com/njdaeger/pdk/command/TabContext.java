package com.njdaeger.pdk.command;

import org.bukkit.command.CommandSender;

public class TabContext extends CommandContext {
    
    public TabContext(PDKCommand command, CommandSender sender, String alias, String[] args) {
        super(command, sender, alias, args);
    }
}
