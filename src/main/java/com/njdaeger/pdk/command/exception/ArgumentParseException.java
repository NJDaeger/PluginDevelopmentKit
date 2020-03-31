package com.njdaeger.pdk.command.exception;

import org.bukkit.ChatColor;

public class ArgumentParseException extends PDKCommandException {

    public ArgumentParseException(String message, boolean silent) {
        super(message, silent);
    }

    public ArgumentParseException(String message) {
        super(message);
    }

    public ArgumentParseException() {
        this(ChatColor.RED + "Could not parse argument.");
    }

}
