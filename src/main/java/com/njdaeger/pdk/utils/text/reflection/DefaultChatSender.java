package com.njdaeger.pdk.utils.text.reflection;

import com.njdaeger.pdk.utils.Util;
import org.bukkit.entity.Player;

public class DefaultChatSender implements IChatSender {

    @Override
    public void sendJson(String json, Player player) {
        try {
            Util.sendChatPacket(player, json, 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
