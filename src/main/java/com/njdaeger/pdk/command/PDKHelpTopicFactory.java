package com.njdaeger.pdk.command;

import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

public class PDKHelpTopicFactory implements HelpTopicFactory<CommandWrapper> {

    @Override
    public HelpTopic createTopic(CommandWrapper commandWrapper) {
        return new PDKHelpTopic(commandWrapper.getBaseCommand());
    }
}
