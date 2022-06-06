package com.njdaeger.pdk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Stream;

public final class Util {

    private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
    private static Class<?> baseCompClass;
    private static Class<?> messageTypeClass;
    private static Constructor<?> constructor;
    private static Class<?> packet;
    
    static {
        try {
            baseCompClass = Util.getNMSClass("IChatBaseComponent", "net.minecraft.network.chat.");
            messageTypeClass = Util.getNMSClass("ChatMessageType", "net.minecraft.network.chat.");
            packet = Util.getNMSClass("Packet", "net.minecraft.network.protocol.");
            
            try {
                constructor = Util.getNMSClass("PacketPlayOutChat", "net.minecraft.network.protocol.game.").getConstructor(baseCompClass, messageTypeClass);
            }
            catch (NoSuchMethodException e) {
                constructor = Util.getNMSClass("PacketPlayOutChat", "net.minecraft.network.protocol.game.").getConstructor(baseCompClass, messageTypeClass, UUID.class);
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
    
    public static Class<?> getNMSClass(String className, String post117Pckg) throws ClassNotFoundException {
        try {
            return Class.forName("net.minecraft.server." + VERSION + "." + className);
        } catch (ClassNotFoundException e) {
            return Class.forName(post117Pckg + className);
        }
    }
    
    public static Class<?> getOBCClass(String className) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + className);
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
        Object connection;
        try {
            connection = basePlayer.getClass().getField("playerConnection").get(basePlayer);
        } catch (Exception e) {
            connection = basePlayer.getClass().getField("b").get(basePlayer);
        }
        if (connection == null) throw new RuntimeException("Unable to send chat packet. Cannot find player connection.");
        Object chatPacket = createPacket(json, player, type);
        Method sendMethod;
        try {
            sendMethod = connection.getClass().getMethod("sendPacket", packet);
        } catch (Exception e) {
            try {
                sendMethod = connection.getClass().getMethod("a", packet);
            } catch (Exception e2) {
//                Stream.of(connection.getClass().getDeclaredMethods()).forEach(m -> {
//                    System.out.print(m.getName());
//                    Stream.of(m.getParameters()).forEach(param -> {
//                        System.out.print("   " + param.getType().getName());
//                    });
//                    System.out.println();
//                });

                e2.printStackTrace();
                throw new RuntimeException("Unable to send chat packet. Cannot find packet send method.");
            }
        }
        sendMethod.invoke(connection, chatPacket);
        
    }
    
}
