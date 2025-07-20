package com.njdaeger.pdk.command.exception;

import org.bukkit.command.CommandSender;

public class CommandSenderTypeException extends PDKCommandException {

    public CommandSenderTypeException() {
        super("You cannot run this command from this type of sender.");
    }

    public CommandSenderTypeException(CommandSender currentSender, Class<? extends CommandSender> type) {
        super("This command requires a command sender of type " + type.getSimpleName() + " but you are currently a " + currentSender.getClass().getSimpleName() + ".");
    }

    public CommandSenderTypeException(String message) {
        super(message);
    }
}
