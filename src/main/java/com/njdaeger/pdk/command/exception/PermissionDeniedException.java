package com.njdaeger.pdk.command.exception;

import org.bukkit.ChatColor;

public class PermissionDeniedException extends PDKCommandException {

    public PermissionDeniedException() {
        this(ChatColor.RED + "You do not have sufficient permissions to run this command.");
    }

    public PermissionDeniedException(String message) {
        super(message);
    }
}
