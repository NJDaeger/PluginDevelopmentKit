package com.njdaeger.pdk.utils.text.hover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.njdaeger.pdk.utils.text.JsonSerializable;
import com.njdaeger.pdk.utils.text.Text;

/**
 * Represents a hover event. This is used to display a hover event in the text component
 * @param <T> The type of the content of the hover event.
 */
public class HoverEvent<T extends JsonSerializable> implements JsonSerializable {

    private final HoverAction<T> action;
    private final T content;

    /**
     * Creates a new hover event with the given action and content
     * @param action The action of the hover event
     * @param content The content of the hover event
     */
    public HoverEvent(HoverAction<T> action, T content) {
        this.action = action;
        this.content = content;
    }

    /**
     * Gets the action of the hover event
     * @return The action of the hover event
     */
    public HoverAction<T> getAction() {
        return action;
    }

    /**
     * Gets the content of the hover event
     * @return The content of the hover event
     */
    public T getContent() {
        return content;
    }

    @Override
    public JsonElement getJson() {
        var object = new JsonObject();
        object.addProperty("action", action.getAction());
        if (content instanceof Text.Section s) object.add("contents", s.getContainer().getJson());
        else object.add("contents", content.getJson());
        return object;
    }
}
