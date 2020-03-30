package com.njdaeger.pdk.command.exception;

import org.bukkit.ChatColor;

public class NotEnoughArgsException extends PDKCommandException {

    public NotEnoughArgsException() {
        this(ChatColor.RED + "You have not provided enough arguments to run this command.");
    }

    public NotEnoughArgsException(String message) {
        super(message);
    }
}