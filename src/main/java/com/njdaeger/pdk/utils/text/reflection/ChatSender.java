package com.njdaeger.pdk.utils.text.reflection;

import com.njdaeger.pdk.versionhelper.Version;
import org.bukkit.Bukkit;

public class ChatSender {

    private static IChatSender chatSender;

    public static IChatSender getChatSender() {
        if (chatSender == null) loadChatSender();
        return chatSender;
    }

    private static void loadChatSender() {
        System.out.println(Version.getCurrentVersion().getNiceName());
        chatSender = switch (Version.getCurrentVersion()) {
            case v1_20, v1_20_2 -> new v120ChatSender();
            case v1_20_3 -> new v1204ChatSender();
            default -> new DefaultChatSender();
        };

        Bukkit.getLogger().info("[PDK] Loaded chat sender: " + chatSender.getClass().getSimpleName());

    }

}
