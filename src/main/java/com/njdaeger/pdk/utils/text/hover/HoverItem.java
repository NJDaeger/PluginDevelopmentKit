package com.njdaeger.pdk.utils.text.hover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.njdaeger.pdk.utils.text.JsonSerializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a hover item. This is used in the show_item hover event.
 */
public class HoverItem implements JsonSerializable {

    private final ItemStack item;

    private HoverItem(ItemStack itemStack) {
        this.item = itemStack;
    }

    private HoverItem(Material material, int count) {
        this.item = new ItemStack(material, count);
    }

    /**
     * Creates a new hover item from the given item stack
     * @param itemStack The item stack to create the hover item from
     * @return The hover item
     */
    public static HoverItem of(ItemStack itemStack) {
        return new HoverItem(itemStack);
    }

    /**
     * Creates a new hover item from the given material and count
     * @param material The material of the item
     * @param count The count of the item
     * @return The hover item
     */
    public static HoverItem of(Material material, int count) {
        return new HoverItem(material, count);
    }

    /**
     * Gets the item stack of this hover item
     * @return The item stack
     */
    public ItemStack getItem() {
        return item;
    }

    @Override
    public JsonElement getJson() {
        var json = new JsonObject();
        json.addProperty("id", item.getType().getKey().getKey());
        json.addProperty("count", item.getAmount());
        return json;
    }
}
