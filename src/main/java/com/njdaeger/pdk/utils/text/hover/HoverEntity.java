package com.njdaeger.pdk.utils.text.hover;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.njdaeger.pdk.utils.text.JsonSerializable;
import com.njdaeger.pdk.utils.text.Text;
import org.bukkit.entity.EntityType;

import java.util.UUID;

/**
 * Represents a hover entity. This is used to display an entity in the text component
 */
public class HoverEntity implements JsonSerializable {

    private final EntityType type;
    private final Text.Section name;
    private final UUID id;

    private HoverEntity(EntityType type) {
        this.type = type;
        this.name = Text.of(type.getKey().getKey());
        this.id = new UUID(0, 0);
    }

    private HoverEntity(EntityType type, Text.Section name) {
        this.type = type;
        this.name = name;
        this.id = new UUID(0, 0);
    }

    private HoverEntity(EntityType type, Text.Section name, UUID id) {
        this.type = type;
        this.name = name;
        this.id = id;
    }

    /**
     * Creates a new hover entity with the given type
     * @param type The type of entity
     * @return The hover entity
     */
    public static HoverEntity of(EntityType type) {
        return new HoverEntity(type);
    }

    /**
     * Creates a new hover entity with the given type and name
     * @param type The type of entity
     * @param name The name of the entity
     * @return The hover entity
     */
    public static HoverEntity of(EntityType type, Text.Section name) {
        return new HoverEntity(type, name);
    }

    /**
     * Creates a new hover entity with the given type, name and id
     * @param type The type of entity
     * @param name The name of the entity
     * @param id The id of the entity
     * @return The hover entity
     */
    public static HoverEntity of(EntityType type, Text.Section name, UUID id) {
        return new HoverEntity(type, name, id);
    }

    @Override
    public JsonElement getJson() {
        var json = new JsonObject();
        json.addProperty("type", type.getKey().getKey());
        json.addProperty("id", id.toString());
        json.add("name", name.getJson());
        return json;
    }

}
