package com.njdaeger.pdk.utils;

import com.njdaeger.pdk.utils.text.reflection.ChatSender;
import org.bukkit.entity.Player;

public final class ActionBar {
    
    /**
     * Create an action bar with the initial text provided from the parameter
     *
     * @param text The text to start the actionbar with
     * @return The newly created actionbar
     */
    public static ActionBar of(String text) {
        return of(com.njdaeger.pdk.utils.text.Text.of(text));
    }
    
    /**
     * Create an action bar with the initial {@link com.njdaeger.pdk.utils.Text.TextSection} provided from the parameter
     *
     * @param text The text to use in this actionbar
     * @return The newly created actionbar
     */
    public static ActionBar of(com.njdaeger.pdk.utils.text.Text.Section text) {
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
            ChatSender.getChatSender().sendActionBarJson(actionBar.text.toString(), player);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private final com.njdaeger.pdk.utils.text.Text.Section text;
    
    private ActionBar(com.njdaeger.pdk.utils.text.Text.Section text) {
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
