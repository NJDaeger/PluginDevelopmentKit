package com.njdaeger.pdk.utils.text.reflection;

import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public interface IChatSender {

    void sendChatJson(String json, Player player) throws Exception;

    void sendActionBarJson(String json, Player player) throws Exception;

    static Object getBasePlayer(Player player) {
        try {
            return player.getClass().getDeclaredMethod("getHandle").invoke(player);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}
