package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.flag.Flag;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.help.HelpTopic;

import static org.bukkit.ChatColor.*;

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
        builder.append(DARK_AQUA).append(BOLD).append("Description: ")
                .append(GRAY).append(command.getDescription()).append("\n")
                .append(DARK_AQUA).append(BOLD).append("Usage: ")
                .append(GRAY).append(command.getUsage()).append("\n");
        if (!command.getFlags().isEmpty()) {
            builder.append(DARK_AQUA).append(BOLD).append("Flags:\n");
            for (Flag<?> flag : command.getFlags()) {
                builder.append(GRAY).append(flag.getUsage())
                        .append(ITALIC).append(" ").append(flag.getDescription()).append("\n");
            }
        }
        return builder.toString();
    }
    
    @Override
    public String getShortText() {
        return command.getDescription();
    }
    
    @Override
    public String getName() {
        return "/" + command.getName();
    }
    
    private boolean hasAnyPermission(CommandSender sender, String[] permissions) {
        if (permissions.length == 0) return true;
        for (String permission : permissions) {
            if (sender.hasPermission(permission)) return true;
        }
        return false;
    }
    
}

