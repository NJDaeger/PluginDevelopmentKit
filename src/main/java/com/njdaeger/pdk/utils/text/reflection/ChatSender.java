package com.njdaeger.pdk.utils.text.reflection;

import com.njdaeger.pdk.versionhelper.Version;

public class ChatSender {

    private static IChatSender chatSender;

    public static IChatSender getChatSender() {
        if (chatSender == null) loadChatSender();
        return chatSender;
    }

    private static void loadChatSender() {
        chatSender = switch (Version.getCurrentVersion()) {
            default -> new DefaultChatSender();
        };

    }

}
