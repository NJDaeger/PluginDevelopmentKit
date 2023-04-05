package com.njdaeger.pdk.utils.text.hover;

import com.njdaeger.pdk.utils.text.JsonSerializable;
import com.njdaeger.pdk.utils.text.Text;

/**
 * Represents a hover action. This is used to show certain things when hovering over a text component.
 * @param <T> The type of value to return when hovering
 */
public class HoverAction<T extends JsonSerializable> {

    /**
     * The show_text hover event. (Shows a text component when hovered)
     */
    public static final HoverAction<Text.Section> SHOW_TEXT = new HoverAction<>(Text.Section.class, "show_text");
    /**
     * The show_item hover event. (Shows an item when hovered)
     */
    public static final HoverAction<HoverItem> SHOW_ITEM = new HoverAction<>(HoverItem.class, "show_item");
    /**
     * The show_entity hover event. (Shows an entity when hovered)
     */
    public static final HoverAction<HoverEntity> SHOW_ENTITY = new HoverAction<>(HoverEntity.class, "show_entity");

    private final Class<T> type;
    private final String action;

    /**
     * Creates a new hover action
     * @param type The type of value to return when hovering
     * @param action The internal name of this hover action
     */
    public HoverAction(Class<T> type, String action) {
        this.type = type;
        this.action = action;
    }

    /**
     * Gets the data type required for this hover action
     *
     * @return The required data type
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Gets the internal minecraft name for this hover action
     *
     * @return The minecract name for this hover action.
     */
    public String getAction() {
        return action;
    }

}
