package com.njdaeger.pdk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.UUID;

public final class Util {
    
    private static Class<?> baseCompClass;
    private static Class<?> messageTypeClass;
    private static Constructor<?> constructor;
    private static Class<?> packet;
    
    static {
        try {
            baseCompClass = Util.getNMSClass("IChatBaseComponent");
            messageTypeClass = Util.getNMSClass("ChatMessageType");
            packet = Util.getNMSClass("Packet");
            
            try {
                constructor = Util.getNMSClass("PacketPlayOutChat").getConstructor(baseCompClass, messageTypeClass);
            }
            catch (NoSuchMethodException e) {
                constructor = Util.getNMSClass("PacketPlayOutChat").getConstructor(baseCompClass, messageTypeClass, UUID.class);
            }
            
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    private static Object createPacket(String json, Player player, int type) throws Exception {
        Object base = baseCompClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json);
        if (constructor.getParameterCount() == 2) {
            return constructor.newInstance(base, messageTypeClass.getEnumConstants()[type]);
        } else {
            return constructor.newInstance(base, messageTypeClass.getEnumConstants()[type], player.getUniqueId());
        }
    }
    
    private Util() {
    }
    
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
    
    /**
     * Sends a chat packet to a player
     *
     * @param player The player to send the packet to
     * @param json   The json to send in this packet
     * @param type   The type of chat to send. (0 = chat, 1 = system, 2 = game info)
     * @throws Exception Issues with reflection
     */
    public static void sendChatPacket(Player player, String json, int type) throws Exception {
        Object basePlayer = player.getClass().getMethod("getHandle").invoke(player);
        Object connection = basePlayer.getClass().getField("playerConnection").get(basePlayer);
        Object chatPacket = createPacket(json, player, type);
        connection.getClass().getMethod("sendPacket", packet).invoke(connection, chatPacket);
        
    }
    
}
