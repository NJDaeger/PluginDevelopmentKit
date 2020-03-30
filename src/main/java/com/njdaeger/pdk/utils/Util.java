package com.njdaeger.pdk.utils;

import org.bukkit.Bukkit;

public final class Util {

    private Util() {}

    public static Class<?> getNMSClass(String className) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("net.minecraft.server." + version + "." + className);
    }
    
    public static Class<?> getOBCClass(String className) throws ClassNotFoundException {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        return Class.forName("org.bukkit.craftbukkit." + version + "." + className);
    }

    public static String formatString(String message, Object... placeholders) {
        for (int i = 0; i < placeholders.length; i++) {
            message = message.replace("{" + i + "}", (placeholders[i] != null ? placeholders[i].toString() : "null"));
        }
        return message;
    }

    
}
