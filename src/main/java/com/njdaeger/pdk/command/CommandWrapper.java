package com.njdaeger.pdk.command;

import com.njdaeger.pdk.command.exception.PDKCommandException;
import com.njdaeger.pdk.command.exception.PermissionDeniedException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CommandWrapper extends Command implements PluginIdentifiableCommand {

    private final String[] permissions;
    private final PDKCommand command;
    private final Plugin plugin;
    
    protected CommandWrapper(Plugin plugin, PDKCommand command) {
        super(command.getName());
        setAliases(Arrays.asList(command.getAliases()));
        setDescription(command.getDescription());
        setUsage(command.getUsage());
        this.permissions = command.getPermissions();
        this.command = command;
        this.plugin = plugin;
    }

    /**
     * Tests to see if the command sender has permission to run this command or not. This is used for the help map.
     * @param sender The command sender to test the permission of
     * @return True if the sender has permission to run this command, false otherwise
     */
    @Override
    public boolean testPermission(CommandSender sender) {
        if (testPermissionSilent(sender)) return true;
        try {
            throw new PermissionDeniedException();
        }
        catch (PDKCommandException e) {
            e.showError(sender);
        }
        return false;

    }

    /**
     * Tests to see if the command sender has permission to run this command or not. This is also used for the help map
     * @param sender The sender to test the permission of
     * @return True if the sender has permission to run this command, false otherwise
     */
    @Override
    public boolean testPermissionSilent(CommandSender sender) {
        if (this.permissions == null || this.permissions.length == 0) return true;
        return Stream.of(permissions).anyMatch(sender::hasPermission);
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        command.execute(new CommandContext(plugin, command, sender, alias, args));
        return true;
    }
    
    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return command.complete(new TabContext(plugin, command, sender, alias, args));
    }
    
    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    public PDKCommand getBaseCommand() {
        return command;
    }
}
