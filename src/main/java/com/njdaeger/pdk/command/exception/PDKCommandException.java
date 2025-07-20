package com.njdaeger.pdk.command.exception;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public class PDKCommandException extends Exception implements CommandExceptionType, Message {

    private boolean quiet;
    private TextComponent componentMessage;

    public PDKCommandException(TextComponent message, boolean quiet) {
        super(message.content(), null, true, false);
        this.componentMessage = message;
        this.quiet = quiet;
    }

    public PDKCommandException(TextComponent message) {
        super(message.content(), null, true, false);
        this.componentMessage = message;
    }

    public PDKCommandException(String message, boolean quiet) {
        super(message, null, true, false);
        this.componentMessage = Component.text(message);
        this.quiet = quiet;
    }

    public PDKCommandException(String message) {
        this(message, false);
        this.componentMessage = Component.text(message);
    }

    public PDKCommandException() {
        this((String) null, true);
    }

    /**
     * Shows the error message to the command sender whenever an error arises
     * @param sender The sender to send the message to
     */
    public void showError(CommandSender sender) {
        if (!quiet && componentMessage != null) sender.sendMessage(componentMessage);
    }

    @Override
    public String getString() {
        return getMessage();
    }
}
