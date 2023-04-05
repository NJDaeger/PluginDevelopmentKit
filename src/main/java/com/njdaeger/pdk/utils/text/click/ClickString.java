package com.njdaeger.pdk.utils.text.click;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.njdaeger.pdk.utils.text.JsonSerializable;

/**
 * Represents the resulting string from a click event
 */
public class ClickString implements JsonSerializable {

    private final String string;

    private ClickString(String string) {
        this.string = string;
    }

    /**
     * Create a click string from the given string
     * @param string The string to create the click string from
     * @return The click string
     */
    public static ClickString of(String string) {
        return new ClickString(string);
    }

    /**
     * Get the string
     * @return The string
     */
    public String getString() {
        return string;
    }

    @Override
    public JsonElement getJson() {
        return new JsonPrimitive(string);
    }

}
