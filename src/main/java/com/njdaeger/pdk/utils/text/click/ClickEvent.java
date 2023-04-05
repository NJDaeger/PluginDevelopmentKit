package com.njdaeger.pdk.utils.text.click;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.njdaeger.pdk.utils.text.JsonSerializable;

/**
 * Represents a click event.
 * @param <T> The type of the content of the click event.
 */
public class ClickEvent<T extends JsonSerializable> implements JsonSerializable {

    private final ClickAction<T> action;
    private final T content;

    /**
     * Creates a new click event with the given action and content
     * @param action The action of the click event
     * @param content The content of the click event
     */
    public ClickEvent(ClickAction<T> action, T content) {
        this.action = action;
        this.content = content;
    }

    /**
     * Gets the action of the click event
     * @return The action of the click event
     */
    public ClickAction<T> getAction() {
        return action;
    }

    /**
     * Gets the content of the click event
     * @return The content of the click event
     */
    public T getContent() {
        return content;
    }

    @Override
    public JsonElement getJson() {
        var object = new JsonObject();
        object.addProperty("action", action.getAction());
        if (content.getJson().isJsonPrimitive()) object.addProperty("value", content.getJson().getAsString());
        else object.add("value", content.getJson());
        return object;
    }
}
