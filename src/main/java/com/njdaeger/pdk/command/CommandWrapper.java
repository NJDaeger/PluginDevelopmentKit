package com.njdaeger.pdk.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class CommandWrapper extends Command implements PluginIdentifiableCommand, HelpTopicFactory<CommandWrapper> {
    
    private final PDKCommand command;
    private final Plugin plugin;
    
    protected CommandWrapper(Plugin plugin, PDKCommand command) {
        super(command.getName());
        this.command = command;
        this.plugin = plugin;
    }
    
    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        command.execute(new CommandContext(command, sender, alias, args));
        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return command.complete(new TabContext(command, sender, alias, args));
    }
    
    @Override
    public Plugin getPlugin() {
        return plugin;
    }
    
    @Override
    public HelpTopic createTopic(CommandWrapper commandWrapper) {
        return new PDKHelpTopic(command);
    }
}
