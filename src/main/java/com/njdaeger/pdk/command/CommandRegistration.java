package com.njdaeger.pdk.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Map;

public class CommandRegistration {
    
    private static CommandMap COMMAND_MAP;
    private static Field KNOWN_COMMANDS;
    
    private static void verifyMaps() {
        if (COMMAND_MAP != null && KNOWN_COMMANDS != null) return;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            COMMAND_MAP = (CommandMap) field.get(Bukkit.getServer());
            
            KNOWN_COMMANDS = COMMAND_MAP.getClass().getDeclaredField("knownCommands");
            KNOWN_COMMANDS.setAccessible(true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (COMMAND_MAP == null) throw new RuntimeException("Cannot find Bukkit Command Map");
        if (KNOWN_COMMANDS == null) Bukkit.getLogger().warning("Can't find known-commands map. Some plugins may not behave correctly.");
    }
    
    public static Map<String, Command> getAllCommands() {
        verifyMaps();
        Map<String, Command> commands = null;
        try {
            commands = (Map<String, Command>)KNOWN_COMMANDS.get(COMMAND_MAP);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (commands == null) throw new RuntimeException("Unable to find knownCommands map.");
        return commands;
    }
    
    public static void unregisterCommand(String nameOrAlias) {
        verifyMaps();
        getAllCommands().get(nameOrAlias).unregister(COMMAND_MAP);
        getAllCommands().remove(nameOrAlias);
    }
    
    public static void registerCommand(Plugin plugin, PDKCommand command) {
        verifyMaps();
        CommandWrapper wrapper = new CommandWrapper(plugin, command);
        COMMAND_MAP.register(command.getName(), plugin.getName(), wrapper);
        Bukkit.getHelpMap().registerHelpTopicFactory(CommandWrapper.class, wrapper);
    }
    
    public static boolean isRegistered(Plugin plugin, String nameOrAlias) {
        verifyMaps();
        return getAllCommands().containsKey(nameOrAlias);
    }
    
    
}
