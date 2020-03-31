package com.njdaeger.pdk.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.Collection;

public class CommandRegistration {
    
    private static SimpleCommandMap COMMAND_MAP;
    private static boolean registeredHelpFactory = false;

    private static void verifyMaps() {
        if (!registeredHelpFactory) {
            Bukkit.getHelpMap().registerHelpTopicFactory(CommandWrapper.class, new PDKHelpTopicFactory());
            registeredHelpFactory = true;
        }
        if (COMMAND_MAP != null) return;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            COMMAND_MAP = (SimpleCommandMap) field.get(Bukkit.getServer());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if (COMMAND_MAP == null) throw new RuntimeException("Cannot find Bukkit Command Map");
    }
    
    public static Collection<Command> getAllCommands() {
        verifyMaps();
        return COMMAND_MAP.getCommands();
    }
    
    public static void unregisterCommand(String nameOrAlias) {
        verifyMaps();
        Command command = COMMAND_MAP.getCommand(nameOrAlias);
        if (command == null) throw new RuntimeException("Cannot find command: " + nameOrAlias);
        else command.unregister(COMMAND_MAP);
    }
    
    public static void registerCommand(Plugin plugin, PDKCommand command) {
        verifyMaps();
        CommandWrapper wrapper = new CommandWrapper(plugin, command);
        COMMAND_MAP.register(command.getName(), plugin.getName(), wrapper);
    }
    
    public static boolean isRegistered(Plugin plugin, String nameOrAlias) {
        verifyMaps();
        return COMMAND_MAP.getCommand(nameOrAlias) != null;
    }
    
    
}
