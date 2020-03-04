package com.njdaeger.pdk.utils;

import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public final class ActionBar {
    
    /**
     * Create an action bar with the initial text provided from the parameter
     *
     * @param text The text to start the actionbar with
     * @return The newly created actionbar
     */
    public static ActionBar of(String text) {
        return of(Text.of(text));
    }
    
    /**
     * Create an action bar with the initial {@link com.njdaeger.pdk.utils.Text.TextSection} provided from the parameter
     *
     * @param text The text to use in this actionbar
     * @return The newly created actionbar
     */
    public static ActionBar of(Text.TextSection text) {
        return new ActionBar(text);
    }
    
    /**
     * Sends an actionbar to a player
     *
     * @param actionBar The actionbar to send to the player
     * @param player    The player to send the actionbar to
     */
    public static void sendTo(ActionBar actionBar, Player player) {
        try {
            Class<?> baseCompClass = Util.getNMSClass("IChatBaseComponent");
            Class<?> messageTypeClass = Util.getNMSClass("ChatMessageType");
            Constructor<?> constructor = Util.getNMSClass("PacketPlayOutChat").getConstructor(baseCompClass, messageTypeClass);
            
            Object base = baseCompClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, actionBar.text.toString());
            Object packet = constructor.newInstance(base, messageTypeClass.getEnumConstants()[2]);
            Object basePlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = basePlayer.getClass().getField("playerConnection").get(basePlayer);
            
            connection.getClass().getMethod("sendPacket", Util.getNMSClass("Packet")).invoke(connection, packet);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private final Text.TextSection text;
    
    private ActionBar(Text.TextSection text) {
        this.text = text;
    }
    
    /**
     * Sends this actionbar to a list of players
     *
     * @param players How many players are to receive the actionbar
     */
    public void sendTo(Player... players) {
        for (Player player : players) {
            sendTo(this, player);
        }
    }
    
}
