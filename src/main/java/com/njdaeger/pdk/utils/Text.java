package com.njdaeger.pdk.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static com.njdaeger.pdk.utils.Util.getNMSClass;

/**
 * A text component
 */
public abstract class Text {
    
    /**
     * Creates a new text section from a starting string.
     * @param text The intial text
     * @return The newly created text section
     */
    public static TextSection of(String text) {
        return new TextSection(true, null);
    }
    
    /**
     * Sends a given text section to a player
     * @param text The text section to send to the player
     * @param player The player to send the text section to
     */
    public static void sendTo(TextSection text, Player player) {
        try {
            Object craftPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = craftPlayer.getClass().getField("playerConnection").get(craftPlayer);
            Class<?> baseComponent = getNMSClass("IChatBaseComponent");
            Class<?> serializer = getNMSClass("IChatBaseComponent$ChatSerializer");
            Class<?> chatPacket = getNMSClass("PacketPlayOutChat");
            Constructor packet = chatPacket.getConstructor(baseComponent, getNMSClass("ChatMessageType"));
            
            Object component = serializer.getDeclaredMethod("a", String.class).invoke(null, text.toString());
            connection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(connection, packet.newInstance(component, getChatMessageType((byte)0)));
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchFieldException | InstantiationException | ClassNotFoundException e) {
            e.getCause().printStackTrace();
        }
    }
    
    private static Object getChatMessageType(byte type) {
        try {
            Class<?> chatMessage = getNMSClass("ChatMessageType");
            return chatMessage.getMethod("a", byte.class).invoke(null, type);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Represents a section of text
     */
    public static class TextSection extends Text {
        
        private ClickEvent<?> click = null;
        private HoverEvent<?> hover = null;
        private String insertion = null;
        private boolean isBold = false;
        private boolean isItalic = false;
        private boolean isParent = false;
        private boolean isUnderlined = false;
        private boolean isObfuscated = false;
        private boolean isStrikethrough = false;
        private ChatColor color = ChatColor.RESET;
        
        private String text = "";
        private List<TextSection> extra;
        private TextSection parent = null;
        
        private TextSection(boolean isParent, TextSection parent) {
            this.isParent = isParent;
            if (!isParent) {
                this.parent = parent;
                this.insertion = parent.insertion;
                this.isBold = parent.isBold;
                this.isItalic = parent.isItalic;
                this.isUnderlined = parent.isUnderlined;
                this.isObfuscated = parent.isObfuscated;
                this.isStrikethrough = parent.isStrikethrough;
                this.color = parent.color;
                this.hover = parent.hover;
                this.click = parent.click;
            } else {
                this.extra = new ArrayList<>();
            }
        }
        
        /**
         * Set the color of the current text section and all proceeding it, unless they specify a new formatting.
         *
         * @param color The color to set this and the following text sections to
         * @return This text section
         */
        public TextSection setColor(ChatColor color) {
            if (21 > color.ordinal() && color.ordinal() > 15) {
                return this;
            }
            this.color = color;
            return this;
        }
        
        /**
         * Adds a new text section to the end of tje current text section
         *
         * @param text What the new text section will say
         * @return The newly created text section
         */
        public final TextSection append(String text) {
            TextSection section = new TextSection(false, getParent()).setText(text);
            getExtra().add(section);
            return section;
        }
        
        /**
         * Adds a new text section after the current text section
         *
         * @param textSection What text section to add to the end of the current text section
         * @return The end text section newly created text section.
         */
        public final TextSection append(TextSection textSection) {
            getExtra().add(textSection.getParent());
            return textSection;
        }
        
        /**
         * Adds a new text section after the current text section
         *
         * @param consumer What text to add to the end of the current section
         * @return The end text section of the newly created text section.
         */
        public final TextSection append(Consumer<TextSection> consumer) {
            TextSection section = new TextSection(false, getParent());
            consumer.accept(section);
            getExtra().add(section);
            return section;
        }
        
        /**
         * Sets the current and proceeding text sections to bold
         *
         * @param bold True enables bold, false disables it
         * @return The current text section
         */
        public TextSection setBold(boolean bold) {
            isBold = bold;
            return this;
        }
        
        /**
         * Sets the current and proceeding text sections to italics
         *
         * @param italic True enables italics, false disables it.
         * @return The current text section
         */
        public TextSection setItalic(boolean italic) {
            isItalic = italic;
            return this;
        }
        
        /**
         * Sets the current and proceeding text sections to obfuscation
         *
         * @param obfuscated True enables obfuscation, false disables it
         * @return The current text section
         */
        public TextSection setObfuscated(boolean obfuscated) {
            isObfuscated = obfuscated;
            return this;
        }
        
        /**
         * Sets the current and proceeding text sections to underlined
         *
         * @param underlined True enables underlines, false disables it
         * @return The current text section
         */
        public TextSection setUnderlined(boolean underlined) {
            isUnderlined = underlined;
            return this;
        }
        
        /**
         * Sets the current and proceeding text sections to strikethrough
         *
         * @param strikethrough True enables strikethrough, false disables it
         * @return The current text section
         */
        public TextSection setStrike(boolean strikethrough) {
            isStrikethrough = strikethrough;
            return this;
        }
        
        /**
         * Set the text of the current text section
         *
         * @param text The text the current text section says
         * @return The current text section
         */
        public TextSection setText(String text) {
            this.text = text;
            return this;
        }
        
        /**
         * When the text is shift-clicked, insert text at the index of the user's cursor.
         *
         * @param insertion The text to insert at the users cursor
         * @return The current text section
         */
        public TextSection setInsertion(String insertion) {
            this.insertion = insertion;
            return this;
        }
        
        /**
         * Specify a hover event for the current and the proceeding text sections
         *
         * @param action The action to perform when the text sections are hovered over
         * @param value  The value to use when the action is performed.
         * @return The current text section
         */
        public <T> TextSection hoverEvent(HoverAction<T> action, T value) {
            this.hover = new HoverEvent<>(action, value);
            return this;
        }
        
        /**
         * Specify a hover event for the current and the proceeding text sections
         *
         * @param event The new hover event to perform when the sections are hovered over
         * @return The current text section
         */
        public <T> TextSection hoverEvent(HoverEvent<T> event) {
            this.hover = event;
            return this;
        }
        
        /**
         * Specify a click event for the current and the proceeding text sections
         *
         * @param action The action to perform when the text sections are clicked.
         * @param value  The value to use when the action is performed
         * @return The current text section
         */
        public <T> TextSection clickEvent(ClickAction<T> action, T value) {
            this.click = new ClickEvent<>(action, value);
            return this;
        }
        
        /**
         * Specify a click event for the current and the proceeding text sections
         *
         * @param event The new click event to perform when the sections are clicked on
         * @return The current text section
         */
        public <T> TextSection clickEvent(ClickEvent<T> event) {
            this.click = event;
            return this;
        }
        
        /**
         * Clears the color from this text section and the proceeding sections
         *
         * @return This text section
         */
        public TextSection clearColor() {
            this.color = ChatColor.RESET;
            return this;
        }
        
        /**
         * Clears special formatting from this text and the proceeding sections. (bold, italic, obfuscation, underlines, and strikethroughs)
         *
         * @return This text section
         */
        public TextSection clearFormatting() {
            this.isBold = false;
            this.isItalic = false;
            this.isObfuscated = false;
            this.isUnderlined = false;
            this.isStrikethrough = false;
            return this;
        }
        
        /**
         * Clears all events from the current and proceeding text sections.
         */
        public TextSection clearEvents() {
            this.click = null;
            this.hover = null;
            return this;
        }
        
        /**
         * Send this current text section to an array of players
         *
         * @param players The players to send the message to
         */
        public final void sendTo(Player... players) {
            for (Player player : players) {
                sendTo(this, player);
            }
        }
        
        /**
         * Checks if this current section is a parent section
         *
         * @return True if this section is the parent section, false otherwise.
         */
        public final boolean isParent() {
            return isParent;
        }
        
        /**
         * Returns the parent of the entire TextComponent
         *
         * @return The parent TextSection
         */
        public final TextSection getParent() {
            if (isParent) {
                return this;
            } else {
                return parent;
            }
        }
        
        /**
         * Gets all the additional text sections
         *
         * @return all the additional text sections.
         */
        public List<TextSection> getExtra() {
            if (isParent) {
                return extra;
            } else {
                return getParent().getExtra();
            }
        }
        
        /**
         * Get the text currently represented by this section
         *
         * @return The section text
         */
        public String getText() {
            return text;
        }
        
        /**
         * Gets a list of sections this textsection consists of
         *
         * @return A list of textsections
         */
        public List<TextSection> getSections() {
            List<TextSection> sections = new ArrayList<>(getParent().getExtra());
            sections.add(0, getParent());
            return sections;
        }
        
        /**
         * Returns this TextSection as a JsonObject
         *
         * @return The TextSection JsonObject.
         */
        public JsonObject getJson() {
            
            JsonObject hover = null;
            if (this.hover != null) {
                hover = new JsonObject();
                hover.addProperty("action", this.hover.getAction().getAction());
                Class<?> type = this.hover.action.getType();
                if (type.isEnum()) {
                    hover.addProperty("value", ((Enum)this.hover.getValue()).name().toLowerCase());
                } else if (type.isAssignableFrom(TextSection.class)) {
                    List<JsonObject> val = new ArrayList<>();
                    val.add(((TextSection)this.hover.getValue()).getJson());
                    hover.add("value", new Gson().toJsonTree(val));
                } else {
                    hover.addProperty("value", "unknown");
                }
            }
            
            JsonObject click = null;
            if (this.click != null) {
                click = new JsonObject();
                click.addProperty("action", this.click.getAction().getAction());
                click.addProperty("value", this.click.getValue().toString());
            }
            
            JsonObject json = new JsonObject();
            json.addProperty("text", text);
            json.addProperty("color", color.name().toLowerCase());
            if (isBold) {
                json.addProperty("bold", true);
            }
            if (isItalic) {
                json.addProperty("italic", true);
            }
            if (isUnderlined) {
                json.addProperty("underlined", true);
            }
            if (isObfuscated) {
                json.addProperty("obfuscated", true);
            }
            if (insertion != null) {
                json.addProperty("insertion", insertion);
            }
            if (isStrikethrough) {
                json.addProperty("strikethrough", true);
            }
            if (hover != null) {
                json.add("hoverEvent", hover);
            }
            if (click != null) {
                json.add("clickEvent", click);
            }
            if (isParent && !extra.isEmpty()) {
                List<JsonObject> sections = new ArrayList<>();
                for (TextSection section : extra) {
                    sections.add(section.getJson());
                }
                json.add("extra", new Gson().toJsonTree(sections));
            }
            return json;
        }
        
        /**
         * Gets a simplified String that isn't a json object. (No click or hover events are included, just text with formatting)
         *
         * @return - Formatted String
         */
        public String getFormatted() {
            StringBuilder stringBuilder = new StringBuilder();
            if (isBold) {
                stringBuilder.append(ChatColor.BOLD);
            }
            if (isItalic) {
                stringBuilder.append(ChatColor.ITALIC);
            }
            if (isUnderlined) {
                stringBuilder.append(ChatColor.UNDERLINE);
            }
            if (isStrikethrough) {
                stringBuilder.append(ChatColor.STRIKETHROUGH);
            }
            if (isObfuscated) {
                stringBuilder.append(ChatColor.MAGIC);
            }
            
            stringBuilder.append(color);
            stringBuilder.append(text);
            if (isParent) {
                if (!extra.isEmpty()) {
                    getExtra().forEach(textSection -> stringBuilder.append(textSection.getFormatted()));
                }
            }
            
            return stringBuilder.toString();
        }
        
        /**
         * Returns a very simple text. Removes all color, removes all formatting.
         *
         * @return The plain text
         */
        public String toUnformatted() {
            StringBuilder builder = new StringBuilder();
            getParent().getSections().stream().map(TextSection::getText).forEach(builder::append);
            return builder.toString();
        }
        
        /**
         * Turns this text object into the proper JSON format
         *
         * @return Json string
         */
        @Override
        public String toString() {
            if (isParent) {
                return getJson().toString();
            }
            return parent.getJson().toString();
        }
        
    }
    
    /**
     * Indicates a click event for a text section
     */
    public static class ClickEvent<T> {
        
        private final ClickAction<T> action;
        private final T value;
        
        /**
         * Create a new abstract click event not tied to a text section.
         *
         * @param action The action to perform when a text section is clicked.
         * @param value  The value to be used when the action is performed.
         */
        public ClickEvent(ClickAction<T> action, T value) {
            this.action = action;
            this.value = value;
        }
        
        /**
         * The action to be performed
         *
         * @return The action to be performed
         */
        public ClickAction getAction() {
            return action;
        }
        
        /**
         * The value to be used when the action is performed.
         *
         * @return The value to use when the text section is pressed.
         */
        public T getValue() {
            return value;
        }
    }
    
    /**
     * Indicates the click actions associated with the click event.
     */
    public static class ClickAction<T> {
        
        /**
         * The open_url click action. (Opens a link)
         */
        public static ClickAction<String> OPEN_URL = new ClickAction<>(String.class, "open_url");
        /**
         * The open_file click action (Opens a file)
         */
        public static ClickAction<String> OPEN_FILE = new ClickAction<>(String.class, "open_file");
        /**
         * The run_command click action (Runs a command)
         */
        public static ClickAction<String> RUN_COMMAND = new ClickAction<>(String.class, "run_command");
        /**
         * The change_page click action (Changes page of a book)
         */
        public static ClickAction<Integer> CHANGE_PAGE = new ClickAction<>(Integer.class, "change_page");
        /**
         * The suggest_command click action (Suggests a command in the chat bar)
         */
        public static ClickAction<String> SUGGEST_COMMAND = new ClickAction<>(String.class, "suggest_command");
        /**
         * The copy_to_clipboard click action (Copies the value to the users clipboard)
         */
        public static ClickAction<String> COPY_TO_CLIPBOARD = new ClickAction<>(String.class, "copy_to_clipboard");
        
        private final Class<T> type;
        private final String action;
        
        /**
         * Creates a new click action type
         *
         * @param type   The type of value the click action accepts
         * @param action The internal minecraft name of the click action
         */
        private ClickAction(Class<T> type, String action) {
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
    
    /**
     * Indicates a hover event for a text section
     */
    public static class HoverEvent<T> {
        
        private final T value;
        private final HoverAction<T> action;
        
        /**
         * Create a new abstract hover event not tied to a text section.
         *
         * @param action The action to perform when a text section is hovered over.
         * @param value  The value to be used when the action is performed.
         */
        public HoverEvent(HoverAction<T> action, T value) {
            this.action = action;
            this.value = value;
        }
        
        /**
         * The action to be performed
         *
         * @return The action to be performed
         */
        public HoverAction<T> getAction() {
            return action;
        }
        
        /**
         * The value to be used when the action is performed.
         *
         * @return The value to use when the text section is pressed.
         */
        public T getValue() {
            return value;
        }
        
    }
    
    /**
     * Indicates the hover actions associated with the over event.
     */
    public static class HoverAction<T> {
        
        /**
         * The show_text hover event. (Shows a text component when hovered)
         */
        public static final HoverAction<TextSection> SHOW_TEXT = new HoverAction<>(TextSection.class, "show_text");
        /**
         * The show_item hover event. (Shows an item when hovered)
         */
        public static final HoverAction<Material> SHOW_ITEM = new HoverAction<>(Material.class, "show_item");
        /**
         * The show_entity hover event. (Shows an entity when hovered)
         */
        public static final HoverAction<EntityType> SHOW_ENTITY = new HoverAction<>(EntityType.class, "show_entity");
        
        private final Class<T> type;
        private final String action;
        
        /**
         * Creates a new hover action type
         *
         * @param type   The type of value the hover action accepts
         * @param action The internal minecraft name of the hover action
         */
        private HoverAction(Class<T> type, String action) {
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
    
}
