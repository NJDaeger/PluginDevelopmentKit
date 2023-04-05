package com.njdaeger.pdk.utils.text;

import org.bukkit.command.CommandSender;

public interface IText extends JsonSerializable, SelfContainable<IText> {

    /**
     * Send this Text object to a user. Can be sent to any CommandSender.
     * @param listOfCommandSenders The CommandSender(s) to send the text to.
     */
    void sendTo(CommandSender... listOfCommandSenders);

}
