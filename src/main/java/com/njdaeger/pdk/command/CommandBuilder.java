package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.flags.Flag;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CommandBuilder {
    
    private final String name;
    private final String[] aliases;
    private final List<Class<? extends Flag<?>>> flags;
    
    private String usage;
    private String description;
    private TabExecutor tabExecutor;
    private CommandExecutor executor;
    
    public static CommandBuilder of(String... aliases) {
        Validate.notEmpty(aliases, "You must provide a name for your command.");
        return new CommandBuilder(aliases);
    }
    
    public static CommandBuilder of(Collection<String> aliases) {
        Validate.notEmpty(aliases, "You must provide a name for your command.");
        return of(aliases.toArray(new String[0]));
    }
    
    private CommandBuilder(String[] aliases) {
        this.name = aliases[0];
        this.flags = new ArrayList<>();
        this.aliases = aliases.length > 1 ? Arrays.copyOfRange(aliases, 1, aliases.length) : new String[0];
    }
    
    public CommandBuilder usage(String usage) {
    
    }
    
    public CommandBuilder description(String description) {
    
    }
    
    public CommandBuilder executor(CommandExecutor executor) {
    
    }
    
    public CommandBuilder completer(TabExecutor executor) {
    
    }
    
    public <V, T extends Flag<V>> CommandBuilder flag(Class<T> flag) {
        flags.add(flag);
        return this;
    }
    
}
