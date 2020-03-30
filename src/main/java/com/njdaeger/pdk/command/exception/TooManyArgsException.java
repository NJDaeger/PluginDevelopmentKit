package com.njdaeger.pdk.command.exception;

import org.bukkit.ChatColor;

public class TooManyArgsException extends PDKCommandException {

    public TooManyArgsException() {
        this(ChatColor.RED + "You have provided too many arguments to run this command.");
    }

    public TooManyArgsException(String message) {
        super(message);
    }
}
