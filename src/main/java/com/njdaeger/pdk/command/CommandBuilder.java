package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.flag.Flag;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;

import java.util.Collection;

public class CommandBuilder {
    
    private final PDKCommand command;
    
    public static CommandBuilder of(String... aliases) {
        Validate.notEmpty(aliases, "You must provide a name for your command.");
        return new CommandBuilder(aliases);
    }
    
    public static CommandBuilder of(Collection<String> aliases) {
        Validate.notEmpty(aliases, "You must provide a name for your command.");
        return of(aliases.toArray(new String[0]));
    }
    
    private CommandBuilder(String[] aliases) {
        command = new PDKCommand(aliases);
    }
    
    public CommandBuilder usage(String usage) {
        command.setUsage(usage);
        return this;
    }
    
    public CommandBuilder description(String description) {
        command.setDescription(description);
        return this;
    }
    
    public CommandBuilder executor(CommandExecutor executor) {
        command.setCommandExecutor(executor);
        return this;
    }
    
    public CommandBuilder completer(TabExecutor executor) {
        command.setTabExecutor(executor);
        return this;
    }
    
    public <V, T extends Flag<V>> CommandBuilder flag(T flag) {
        command.addFlag(flag);
        return this;
    }
    
    public CommandBuilder permissions(String... permissions) {
        command.setPermissions(permissions);
        return this;
    }

    public CommandBuilder min(int min) {
        command.setMinArgs(min);
        return this;
    }

    public CommandBuilder max(int max) {
        command.setMaxArgs(max);
        return this;
    }

    public PDKCommand build() {
        return command;
    }
    
    public void register(Plugin plugin) {
        command.register(plugin);
    }
    
    
}
