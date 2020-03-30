package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.flag.Flag;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.HelpTopic;

public class PDKHelpTopic extends HelpTopic {
    
    private final PDKCommand command;
    
    PDKHelpTopic(PDKCommand command) {
        this.command = command;
    }
    
    @Override
    public boolean canSee(CommandSender commandSender) {
        if (commandSender instanceof ConsoleCommandSender) return true;
        else return hasAnyPermission(commandSender, command.getPermissions());
    }
    
    //Possible examples, expanded flag descriptions,
    @Override
    public String getFullText(CommandSender forWho) {
        StringBuilder builder = new StringBuilder();
        builder.append(command.getDescription()).append("\n");
        builder.append(command.getUsage()).append("\n");
        if (!command.getFlags().isEmpty()) {
            builder.append("Flags:\n");
            for (Flag<?> flag : command.getFlags()) {
                builder.append(" ").append(flag.getUsage()).append("\n");
                builder.append("    ").append(flag.getDescription()).append("\n");
            }
        }
        return builder.toString();
    }
    
    @Override
    public String getShortText() {
        return command.getUsage();
    }
    
    @Override
    public String getName() {
        return command.getName();
    }
    
    private boolean hasAnyPermission(CommandSender sender, String[] permissions) {
        if (permissions.length == 0) return true;
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) return true;
        }
        return false;
    }
    
}

