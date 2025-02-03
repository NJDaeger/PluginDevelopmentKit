package com.njdaeger.pdk.utils.text.pager;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.stream.Stream;

public class PageResult<T> {

    private final int requestedPage;
    private final int maxPage;
    private final TextComponent message;
    private final List<T> results;

    PageResult(int requestedPage, int maxPage, TextComponent message, List<T> results) {
        this.requestedPage = requestedPage;
        this.maxPage = maxPage;
        this.message = message;
        this.results = results;
    }

    /**
     * Get the requested page number.
     * @return The requested page number.
     */
    public int getRequestedPage() {
        return requestedPage;
    }

    /**
     * Get the maximum page number.
     * @return The maximum page number.
     */
    public int getMaxPage() {
        return maxPage;
    }

    /**
     * Get the message to send to the user.
     * @return The message to send to the user.
     */
    public TextComponent getMessage() {
        return message;
    }

    /**
     * Get the results of the page.
     * @return The results of the page.
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * Will send a message to the specified users if the message is not null.
     * @param users The users to send the message to.
     */
     public void sendTo(CommandSender... users) {
        if (message != null) Stream.of(users).forEach(u -> u.sendMessage(message));
    }

    /**
     * Will send a message to the specified users if the message is not null.
     * @param unknownPage The message to send if the message is null.
     * @param users The users to send the message to.
     */
    public void sendTo(TextComponent unknownPage, CommandSender... users) {
        if (message != null) Stream.of(users).forEach(u -> u.sendMessage(message));
        else Stream.of(users).forEach(u -> u.sendMessage(unknownPage));
    }

}
