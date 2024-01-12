package com.njdaeger.pdk.utils.text.reflection;

import com.njdaeger.pdk.utils.Util;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class v1204ChatSender implements IChatSender {

    private static Class<?> baseComponentClass;
    private static Class<?> packetClass;
    private static Class<?> chatPacketClass;
    private static Class<?> playerConnectionClass;
    private static Class<?> serverCommonPacketListenerImplClass;

    private static Constructor<?> chatPacketConstructor;

    private static Method chatComponentCreateMethod;
    private static Method sendPacketMethod;

    v1204ChatSender() {
        try {
            baseComponentClass = Util.getNMSClass("IChatBaseComponent", "net.minecraft.network.chat.");
            if (baseComponentClass == null) throw new RuntimeException("Could not find base component class. Looked for: " + "net.minecraft.network.chat.IChatBaseComponent");
            packetClass = Util.getNMSClass("Packet", "net.minecraft.network.protocol.");
            if (packetClass == null) throw new RuntimeException("Could not find packet class. Looked for: " + "net.minecraft.network.protocol.Packet");
            chatPacketClass = Util.getNMSClass("ClientboundSystemChatPacket", "net.minecraft.network.protocol.game.");
            if (chatPacketClass == null) throw new RuntimeException("Could not find chat packet class. Looked for: " + "net.minecraft.network.protocol.game.ClientboundSystemChatPacket");
            playerConnectionClass = Util.getNMSClass("PlayerConnection", "net.minecraft.server.network.");
            if (playerConnectionClass == null) throw new RuntimeException("Could not find player connection class. Looked for: " + "net.minecraft.server.network.PlayerConnection");
            serverCommonPacketListenerImplClass = Util.getNMSClass("ServerCommonPacketListenerImpl", "net.minecraft.server.network.");
            if (serverCommonPacketListenerImplClass == null) throw new RuntimeException("Could not find server common packet listener impl class. Looked for: " + "net.minecraft.server.network.ServerCommonPacketListenerImpl");

            chatPacketConstructor = chatPacketClass.getConstructor(baseComponentClass, boolean.class);
            chatComponentCreateMethod = baseComponentClass.getDeclaredClasses()[0].getMethod("a", String.class);
            sendPacketMethod = serverCommonPacketListenerImplClass.getMethod("b", packetClass);

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static Object createPacket(String json, int type) throws Exception {
        Object base = chatComponentCreateMethod.invoke(null, json);
        return chatPacketConstructor.newInstance(base, type == 2);
    }

    @Override
    public void sendChatJson(String json, Player player) throws Exception {
        Object basePlayer = IChatSender.getBasePlayer(player); //EntityPlayer
        Object connection = basePlayer.getClass().getDeclaredField("c").get(basePlayer); //PlayerConnection
        if (connection == null) throw new RuntimeException("Could not find player connection field. Looked for: c");

        Object packet = createPacket(json, 0);
        sendPacketMethod.invoke(connection, packet);
    }

    @Override
    public void sendActionBarJson(String json, Player player) throws Exception {
        Object basePlayer = IChatSender.getBasePlayer(player);
        Object connection = basePlayer.getClass().getDeclaredField("c").get(basePlayer);
        if (connection == null) throw new RuntimeException("Could not find player connection field. Looked for: c");

        Object packet = createPacket(json, 2);
        sendPacketMethod.invoke(connection, packet);
    }
}
