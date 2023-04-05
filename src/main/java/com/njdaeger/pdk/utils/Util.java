package com.njdaeger.pdk.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

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
            Class<?> cls = tryNMSClass("net.minecraft.network.protocol.game.", "PacketPlayOutChat", "ClientboundSystemChatPacket");
//            if (VERSION.contains("1_19_R1")) cls = tryNMSClass("net.minecraft.network.protocol.game", "ClientboundPlayerChatPacket");
            if (cls == null) throw new RuntimeException("Could not find chat packet.");
            else if (cls.getName().endsWith("PacketPlayOutChat")) {
                try {
                    constructor = cls.getConstructor(baseCompClass, messageTypeClass);
                }
                catch (NoSuchMethodException e) {
                    constructor = cls.getConstructor(baseCompClass, messageTypeClass, UUID.class);
                }
            } else {
//                if (cls.getName().endsWith("PlayerChatPacket")) {
//                    constructor = cls.getConstructor(base)
//                }
                try {
                    constructor = cls.getConstructor(baseCompClass, int.class);
                }
                catch (NoSuchMethodException e) {
                    constructor = cls.getConstructor(baseCompClass, boolean.class);
                }
            }

            
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
    
    private static Object createPacket(String json, Player player, int type) throws Exception {
        Object base = baseCompClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, json);
        if (constructor.getParameterCount() == 2) {
            if (constructor.getParameters()[1].getType() == int.class) {
                return constructor.newInstance(base, type);
            }
            else if (constructor.getParameters()[1].getType() == boolean.class) {
                return constructor.newInstance(base, type == 2);//if type is 2, send as actionbar, otherwise send in chat
            }
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
            try {
                return Class.forName(post117Pckg + className);
            } catch (ClassNotFoundException e2) {}
        }
        return null;
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
        String[] fields = new String[] {"playerConnection", "b", "connection"};
        Object connection = tryGetField(basePlayer.getClass(), basePlayer, fields);
        if (connection == null) throw new RuntimeException("Unable to find a field named " + Arrays.toString(fields) + " in class " + basePlayer.getClass().getName());
//        try {
//            connection = basePlayer.getClass().getField("playerConnection").get(basePlayer);
//        } catch (Exception e) {
//            connection = basePlayer.getClass().getField("b").get(basePlayer);
//        }
//        if (connection == null) throw new RuntimeException("Unable to send chat packet. Cannot find player connection.");
        Object chatPacket = createPacket(json, player, type);
        Method sendMethod = tryMethod(connection.getClass(), new Class<?>[]{packet}, "sendPacket", "a", "send");
        if (sendMethod == null) throw new RuntimeException("Unable to send chat packet. Cannot find packet send method.");
        sendMethod.invoke(connection, chatPacket);
        
    }

    public static Method tryMethod(Class<?> cls, Class<?>[] params, String... methodNames) {
        Method method = null;
        for (String methodName : methodNames) {
            try {
                method = cls.getDeclaredMethod(methodName, params);
            } catch (Exception ignored) {
            }
            if (method != null) {
                method.setAccessible(true);
                break;
            }
        }
        return method;
    }

    public static Class<?> tryNMSClass(String pckgAsOf117, String... classNames) {
        Class<?> cls = null;
        for (String className : classNames) {
            try {
                cls = getNMSClass(className, pckgAsOf117);
            } catch (Exception ignored) {}
            if (cls != null) return cls;
        }
        return cls;
    }

    public static Field tryField(Class<?> cls, String... fieldNames) {
        Field field = null;
        for (String fieldName : fieldNames) {
            try {
                field = cls.getDeclaredField(fieldName);
            } catch (Exception ignored) {}
            if (field != null) {
                field.setAccessible(true);
                break;
            }
        }
        return field;
    }

    public static Object tryGetField(Class<?> cls, Object get, String... fieldNames) {
        Field field = tryField(cls, fieldNames);
        if (field != null) {
            try {
                return field.get(get);
            } catch (Exception ignored) {}
        }
        return null;
    }

}
