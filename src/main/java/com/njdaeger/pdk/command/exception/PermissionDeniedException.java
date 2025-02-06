package com.njdaeger.pdk.command.exception;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PermissionDeniedException extends PDKCommandException {

    public PermissionDeniedException() {
        super(Component.text("You do not have sufficient permissions to run this command.", NamedTextColor.RED));
    }

    public PermissionDeniedException(String message) {
        super(Component.text(message, NamedTextColor.RED));
    }

    public PermissionDeniedException(TextComponent message) {
        super(message);
    }
}
