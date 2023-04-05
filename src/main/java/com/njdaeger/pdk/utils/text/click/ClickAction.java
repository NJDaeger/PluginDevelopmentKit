package com.njdaeger.pdk.utils.text.click;

import com.njdaeger.pdk.utils.text.JsonSerializable;

public class ClickAction<T extends JsonSerializable> {

    /**
     * The open_url click action. (Opens a link)
     */
    public static ClickAction<ClickString> OPEN_URL = new ClickAction<>(ClickString.class, "open_url");
    /**
     * The open_file click action (Opens a file)
     */
    public static ClickAction<ClickString> OPEN_FILE = new ClickAction<>(ClickString.class, "open_file");
    /**
     * The run_command click action (Runs a command)
     */
    public static ClickAction<ClickString> RUN_COMMAND = new ClickAction<>(ClickString.class, "run_command");
    /**
     * The change_page click action (Changes page of a book)
     */
    public static ClickAction<ClickString> CHANGE_PAGE = new ClickAction<>(ClickString.class, "change_page");
    /**
     * The suggest_command click action (Suggests a command in the chat bar)
     */
    public static ClickAction<ClickString> SUGGEST_COMMAND = new ClickAction<>(ClickString.class, "suggest_command");
    /**
     * The copy_to_clipboard click action (Copies the value to the users clipboard)
     */
    public static ClickAction<ClickString> COPY_TO_CLIPBOARD = new ClickAction<>(ClickString.class, "copy_to_clipboard");

    private final Class<T> type;
    private final String action;

    /**
     * Creates a new click action type
     *
     * @param type   The type of value the click action accepts
     * @param action The internal minecraft name of the click action
     */
    public ClickAction(Class<T> type, String action) {
        this.type = type;
        this.action = action;
    }

    /**
     * Gets the data type required for this click action
     *
     * @return The required data type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Gets the internal minecraft name for this click action
     *
     * @return The minecract name for this click action.
     */
    public String getAction() {
        return action;
    }

}
