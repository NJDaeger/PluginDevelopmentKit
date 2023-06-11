package com.njdaeger.pdk.utils.text.reflection;

import com.njdaeger.pdk.utils.Util;
import org.bukkit.entity.Player;

public class DefaultChatSender implements IChatSender {

    @Override
    public void sendChatJson(String json, Player player) {
        try {
            Util.sendChatPacket(player, json, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendActionBarJson(String json, Player player) {
        try {
            Util.sendChatPacket(player, json, 2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
