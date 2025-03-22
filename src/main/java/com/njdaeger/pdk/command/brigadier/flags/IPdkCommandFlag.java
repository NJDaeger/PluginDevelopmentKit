package com.njdaeger.pdk.command.brigadier.flags;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.ArgumentType;

public interface IPdkCommandFlag<T> {

    /**
     * Gets the name of the flag.
     * @return The name of the flag.
     */
    String getName();

    /**
     * Gets the type of the flag.
     * @return The type of the flag.
     */
    ArgumentType<T> getType();

    /**
     * Gets the tooltip of the flag.
     * @return The tooltip of the flag.
     */
    String getTooltip();

    /**
     * Determines if the flag is hidden from tab completion.
     * @return True if the flag is hidden, false otherwise.
     */
    boolean isHidden();

    /**
     * Determines if the flag is a boolean flag (takes no arguments).
     * @return True if the flag is a boolean flag, false otherwise.
     */
    default boolean isBooleanFlag() {
        return getType() == null;
    }

    /**
     * Gets the tooltip as a message.
     * @return The tooltip as a message.
     */
    default Message getTooltipAsMessage() {
        return new LiteralMessage(getTooltip());
    }

}
