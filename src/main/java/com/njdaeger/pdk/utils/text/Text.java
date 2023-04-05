package com.njdaeger.pdk.utils.text;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.njdaeger.pdk.utils.text.click.ClickAction;
import com.njdaeger.pdk.utils.text.click.ClickEvent;
import com.njdaeger.pdk.utils.text.hover.HoverAction;
import com.njdaeger.pdk.utils.text.hover.HoverEvent;
import com.njdaeger.pdk.utils.text.reflection.ChatSender;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MinecraftFont;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Text implements IText {

    private List<IText> appendList;

    /**
     * Creates a new text section
     * @param text The text to start out with in this section.
     * @return The section created
     */
    public static Section of(String text) {
        var container = new Text();
        var newSection = new Section(true, null, container);
        newSection.text = text;
        container.appendList.add(newSection);
        return newSection;
    }

    /**
     * Gets the nearest ChatColor to the given color string
     * @param string Either a hex color or a chat color name.
     * @return The nearest ChatColor to the given color string
     */
    public static ChatColor getNearestColor(String string) {
        var chatColor = Stream.of(ChatColor.values()).filter(c -> c.name().equalsIgnoreCase(string)).findFirst();
        if (chatColor.isPresent()) return chatColor.get();
        else {
            try {
                var color = Color.fromRGB(Integer.parseInt(string.replace("#", ""), 16));
                //get the closest color from the ChatColor enum to the given color
                final ChatColor[] closest = {ChatColor.WHITE};
                final double[] lastDistance = {Double.MAX_VALUE};
                Stream.of(ChatColor.values()).filter(ChatColor::isColor).forEach(cc -> {
                    var c = cc.asBungee();
                    var distance = Math.sqrt(Math.pow(color.getRed() - c.getColor().getRed(), 2) + Math.pow(color.getGreen() - c.getColor().getGreen(), 2) + Math.pow(color.getBlue() - c.getColor().getBlue(), 2));
                    if (distance < lastDistance[0]) {
                        closest[0] = cc;
                        lastDistance[0] = distance;
                    }
                });
                return closest[0];
            }
            catch (NumberFormatException e) {
                return ChatColor.WHITE;
            }
        }
    }

    /**
     * Get the pixel width of the given text
     * @param text The text to get the pixel width of
     * @return The pixel width of the given text
     */
    public static int getMinecraftPixelWidth(Text.Section text) {
        var str = text.asUnformattedString();
        var width = 0;

        for (int i = 0; i < str.length(); i++) {
            var chr = MinecraftFont.Font.getChar(str.charAt(i));
            if (chr != null) width += chr.getWidth();
        }
        return width;
    }

    private Text() {
        this.appendList = new ArrayList<>();
    }

    @Override
    public JsonElement getJson() {
        JsonArray array = new JsonArray();
        array.add(""); //this will allow each section to have formatting that doesnt carry from the first section
        for (IText section : appendList) {
            array.add(section.getJson());
        }
        return array;
    }

    @Override
    public List<IText> getChildren() {
        return appendList;
    }

    @Override
    public void sendTo(CommandSender... senders) {
        //rather than trying to run the nms adapter, lets use the tellraw command internally to send the json, this way we dont have to do anything special to get it to work.

        Stream.of(senders).forEach(sender -> {
            if (sender instanceof Player p) ChatSender.getChatSender().sendJson(this.getJson().toString(), p);
            else {
                var string = new StringBuilder();
                Section.appendFormattedString(this, string);
                sender.sendMessage(string.toString());
            }
        });
    }

    @Override
    public String toString() {
        return getJson().toString();
    }

    /**
     * A section of text that can be formatted and interacted with.
     */
    public static class Section implements IText {

        //denotes if this is a root section
        private boolean isRoot;
        //the parent text section (eg. if this section is a part of a child of another section)
        private Section parent;
        //the VERY root container of this whole text object.
        private final Text container;

        //the list of children this section has.
        private final List<IText> childrenList;

        //the current color of this section.
        //If section is a root and this is null, defaults to white, if section is not a root and this is null, it defaults to its parent sections color
        private String color;
        private String font;
        private Boolean bold;
        private Boolean italic;
        private Boolean underlined;
        private Boolean strike;
        private Boolean obfuscated;

        private String text;

        private boolean inheritInsertion;
        private String insertion;

        private boolean inheritClick;
        private ClickEvent<?> clickEvent;

        private boolean inheritHover;
        private HoverEvent<?> hoverEvent;


        private Section(boolean isRoot, Section parent, Text container) {
            this.childrenList = new ArrayList<>();
            this.container = container;
            this.isRoot = isRoot;

            this.bold = null;
            this.italic = null;
            this.underlined = null;
            this.strike = null;
            this.obfuscated = null;
            this.insertion = null;
            this.color = null;
            this.font = null;

            if (isRoot) {
                //set default values for everything to null when a root node
                this.parent = null;
                this.clickEvent = null;
                this.hoverEvent = null;
            }
            else {
                if (parent == null) throw new RuntimeException("Section must have a parent object if it isn't a root section. Section: " );
                this.parent = parent;

                //if we have a parent section, always inherit these properties by default.
                this.inheritInsertion = true;
                this.inheritClick = true;
                this.inheritHover = true;
            }
        }

        /**
         * Appends a new root section to the text container.
         * @param text The text to append
         * @return The new section created
         */
        public Section appendRoot(String text) {
            var section = new Section(true, null, container);
            section.setText(text);
            container.appendList.add(section);
            return section;
        }

        /**
         * Appends a new root section to the text container.
         * @param section The section to append
         * @return The section this section was added to. NOT the new section.
         */
        public Section appendRoot(Section section) {
            container.appendList.add(section.container);
            return this;
        }

        /**
         * Append a new child section to this section
         * @param text The text to append
         * @return The new section created
         */
        public Section append(String text) {
            var section = new Section(false, this, container);
            section.text = text;
            childrenList.add(section);
            return section;
        }

        /**
         * Append a new child section to this section
         * @param section the section to append
         * @return The section this section was added to. NOT the new section.
         */
        public Section append(Section section) {
            section.isRoot = false;
            section.parent = this;
            childrenList.add(section);
            return this;
        }

        /**
         * Append a new child section to the parent of this section
         * @param text The text to append
         * @return The new section created
         */
        public Section appendParent(String text) {
            if (this.isRoot) throw new RuntimeException("Cannot appendParent to a root section. Section: '" + text + "'" );
            var section = new Section(false, parent, container);
            section.text = text;
            parent.childrenList.add(section);
            return section;
        }

        /**
         * Append a new child section to the parent of this section
         * @param section The section to append
         * @return The section this section was added to. NOT the new section.
         */
        public Section appendParent(Section section) {
            if (this.isRoot) throw new RuntimeException("Cannot appendParent to a root section. Section: '" + section.text + "'" );
            section.isRoot = false;
            section.parent = parent;
            parent.childrenList.add(section.container.getChildren().size() > 1 ? section.container : section);
            return this;
        }

        /**
         * Sets the text of this section
         * @param text The text to set
         */
        public Section setText(String text) {
            this.text = text;
            return this;
        }

        /**
         * Gets the text of this section
         * @return The text of this section
         */
        public String getText() {
            return text;
        }

        /**
         * Sets the color of this section via a {@link ChatColor}
         * @param color The color to set this section to
         * @return This section
         */
        public Section setColor(ChatColor color) {
            this.color = color.name();
            return this;
        }

        /**
         * Sets the color of this section via an rgb value
         * @param r The red value
         * @param g The green value
         * @param b The blue value
         * @return This section
         */
        public Section setColor(int r, int g, int b) {
            this.color = String.format("#%02x%02x%02x", r, g, b);
            return this;
        }

        /**
         * Sets the color of this section via a {@link Color}
         * @param color The color to set this section to
         * @return This section
         */
        public Section setColor(Color color) {
            return setColor(color.getRed(), color.getGreen(), color.getBlue());
        }

        /**
         * Get the color of this section. It may be a hex value or a {@link ChatColor} name.
         * @return The color of this section
         */
        public String getColor() {
            if (color == null && parent != null) return parent.getColor();
            else return color != null ? color : "white";
        }

        /**
         * Set the font of this section (note, the client must have this font in their resourcepack for it to show up properly)
         * @param font The font to set this section to
         * @return This section
         */
        public Section setFont(String font) {
            this.font = font;
            return this;
        }

        /**
         * Get the font of this section
         * @return The font of this section
         */
        public String getFont() {
            if (font == null && parent != null) return parent.getFont();
            else return font != null ? font : "minecraft:default";
        }

        /**
         * Set whether this section is obfuscated
         * @param obfuscated If this section should be obfuscated
         * @return This section
         */
        public Section setObfuscated(boolean obfuscated) {
            this.obfuscated = obfuscated;
            return this;
        }

        /**
         * Get if this section is obfuscated
         * @return True if obfuscated, false if not
         */
        public boolean isObfuscated() {
            if (obfuscated == null && parent != null) return parent.isObfuscated();
            else return obfuscated != null && obfuscated;
        }

        /**
         * Set whether this section has a strikethrough
         * @param strike If this section should have a strikethrough
         * @return This section
         */
        public Section setStrikethrough(boolean strike) {
            this.strike = strike;
            return this;
        }

        /**
         * Get if this section has a strikethrough
         * @return True if strikethrough, false if not
         */
        public boolean isStrikethrough() {
            if (strike == null && parent != null) return parent.isStrikethrough();
            else return strike != null && strike;
        }

        /**
         * Set whether this section is underlined
         * @param underlined If this section should be underlined
         * @return This section
         */
        public Section setUnderlined(boolean underlined) {
            this.underlined = underlined;
            return this;
        }

        /**
         * Get if this section is underlined
         * @return True if underlined, false if not
         */
        public boolean isUnderlined() {
            if (underlined == null && parent != null) return parent.isUnderlined();
            else return underlined != null && underlined;
        }

        /**
         * Set whether this section is italicized
         * @param italic If this section should be italicized
         * @return This section
         */
        public Section setItalic(boolean italic) {
            this.italic = italic;
            return this;
        }

        /**
         * Get if this section is italicized
         * @return True if italicized, false if not
         */
        public boolean isItalicized() {
            if (italic == null && parent != null) return parent.isItalicized();
            else return italic != null && italic;
        }

        /**
         * Set whether this section is bolded
         * @param bold If this section should be bolded
         * @return This section
         */
        public Section setBold(boolean bold) {
            this.bold = bold;
            return this;
        }

        /**
         * Get if this section is bolded
         * @return True if bolded, false if not
         */
        public boolean isBolded() {
            if (bold == null && parent != null) return parent.isBolded();
            else return bold != null && bold;
        }

        /**
         * Set the hover event of this section
         * @param action The action to perform on hover
         * @param value The value to pass to the action
         * @param <T> The type of the value
         * @return This section
         */
        public <T extends JsonSerializable> Section setHoverEvent(HoverAction<T> action, T value) {
            this.hoverEvent = new HoverEvent<>(action, value);
            this.inheritHover = false;
            return this;
        }

        /**
         * Set the hover event of this section
         * @param event The hover event to set
         * @param <T> The type of the value
         * @return This section
         */
        public <T extends JsonSerializable> Section setHoverEvent(HoverEvent<T> event) {
            this.hoverEvent = event;
            this.inheritHover = false;
            return this;
        }

        /**
         * Get the hover event of this section
         * @return The hover event of this section. May be null.
         */
        public HoverEvent<?> getHoverEvent() {
            if (inheritHover) return parent.getHoverEvent();
            else return hoverEvent;
        }

        /**
         * Set the click event of this section
         * @param action The action to perform on click
         * @param value The value to pass to the action
         * @param <T> The type of the value
         * @return This section
         */
        public <T extends JsonSerializable> Section setClickEvent(ClickAction<T> action, T value) {
            this.clickEvent = new ClickEvent<>(action, value);
            this.inheritClick = false;
            return this;
        }

        /**
         * Set the click event of this section
         * @param event The click event to set
         * @param <T> The type of the value
         * @return This section
         */
        public <T extends JsonSerializable> Section setClickEvent(ClickEvent<T> event) {
            this.clickEvent = event;
            this.inheritClick = false;
            return this;
        }

        /**
         * Get the click event of this section
         * @return The click event of this section. May be null.
         */
        private ClickEvent<?> getClickEvent() {
            if (inheritClick) return parent.getClickEvent();
            else return clickEvent;
        }

        /**
         * Set the insertion of this section
         * @param insertion The insertion to set
         * @return This section
         */
        public Section setInsertion(String insertion) {
            this.insertion = insertion;
            this.inheritInsertion = false;
            return this;
        }

        /**
         * Get the insertion of this section
         * @return The insertion of this section. May be null.
         */
        public String getInsertion() {
            if (inheritInsertion) return parent.getInsertion();
            else return insertion;
        }

        /**
         * Clear the color of this section
         * @return This section
         */
        public Section clearColor() {
            this.color = null;
            return this;
        }

        /**
         * Clear the formatting of this section
         * @return This section
         */
        public Section clearFormatting() {
            this.font = null;
            this.bold = null;
            this.italic = null;
            this.obfuscated = null;
            this.underlined = null;
            this.strike = null;
            return this;
        }

        /**
         * Clear the events of this section
         * @return This section
         */
        public Section clearEvents() {
            setClickEvent(null);
            setHoverEvent(null);
            setInsertion(null);
            return this;
        }

        private void clearEventsDeep(IText next) {
            if (next.getChildren() != null && !next.getChildren().isEmpty()) next.getChildren().forEach(child -> {
                if (child instanceof Section s) {
                    s.clearEvents();
                }
                clearEventsDeep(child);
            });
        }

        /**
         * Clear all events from the entire container of sections
         * @return The current section
         */
        public Section clearAllEvents() {
            clearEventsDeep(container);
            return this;
        }

        /**
         * This will assemble the entire container into a single string that retains text formatting and color, but does not retain events.
         *
         * Note: All colors, if set to custom colors outside of Bukkit's ChatColor, will be converted to the closest matching ChatColor.
         *
         * @return The colored string representation of the entire container
         */
        public String asFormattedString() {
            StringBuilder builder = new StringBuilder();
            appendFormattedString(container, builder);
            return builder.toString();
        }

        private static void appendFormattedString(IText next, StringBuilder builder) {
            if (next.getChildren() != null && !next.getChildren().isEmpty()) next.getChildren().forEach(child -> {
                if (child instanceof Section s) {
                    builder.append(getNearestColor(s.getColor()));
                    if (s.isBolded()) builder.append(ChatColor.BOLD);
                    if (s.isItalicized()) builder.append(ChatColor.ITALIC);
                    if (s.isObfuscated()) builder.append(ChatColor.MAGIC);
                    if (s.isUnderlined()) builder.append(ChatColor.UNDERLINE);
                    if (s.isStrikethrough()) builder.append(ChatColor.STRIKETHROUGH);
                    builder.append(s.getText());
                }
                appendFormattedString(child, builder);
            });
        }

        /**
         * This will assemble the entire container into a single string that does not retain text formatting, color, or events.
         * @return The unformatted string representation of the entire container
         */
        public String asUnformattedString() {
            StringBuilder builder = new StringBuilder();
            appendUnformattedString(container, builder);
            return builder.toString();
        }

        private static void appendUnformattedString(IText next, StringBuilder builder) {
            if (next.getChildren() != null && !next.getChildren().isEmpty()) next.getChildren().forEach(child -> {
                if (child instanceof Section s) {
                    builder.append(s.getText());
                }
                appendUnformattedString(child, builder);
            });
        }

        /**
         * Get the root container of this entire section
         * @return The root container of this entire section
         */
        public Text getContainer() {
            return container;
        }

        @Override
        public String toString() {
            return container.getJson().toString();
        }

        @Override
        public JsonElement getJson() {
            var object = new JsonObject();

            object.addProperty("text", text);

            if (!inheritHover) {
                if (hoverEvent == null) {
                    if (!isRoot) {
                        var obj = new JsonObject();
                        obj.addProperty("action", "show_text");
                        obj.addProperty("contents", "");
                        object.add("hoverEvent", obj);
                    }
                }
                else object.add("hoverEvent", getHoverEvent().getJson());
            }

            if (!inheritClick) {
                if (clickEvent == null) {
                    if (!isRoot) {
                        var obj = new JsonObject();
                        obj.addProperty("action", "run_command");
                        obj.addProperty("value", "");
                        object.add("clickEvent", obj);
                    }
                }
                else object.add("clickEvent", getClickEvent().getJson());

            }

            if (!inheritInsertion) {
                if (insertion == null){
                    if (!isRoot) object.addProperty("insertion", "");
                }
                else object.addProperty("insertion", getInsertion());
            }

            if (color != null) object.addProperty("color", color.toLowerCase());

            if (font != null) object.addProperty("font", font.toLowerCase());

            if (bold != null) object.addProperty("bold", bold);

            if (italic != null) object.addProperty("italic", italic);

            if (obfuscated != null) object.addProperty("obfuscated", obfuscated);

            if (underlined != null) object.addProperty("underlined", underlined);

            if (strike != null) object.addProperty("strikethrough", strike);

            if (!childrenList.isEmpty()) {
                var arr = new JsonArray();
                childrenList.forEach(child -> arr.add(child.getJson()));
                object.add("extra", arr);
            }
            return object;
        }

        @Override
        public List<IText> getChildren() {
            return childrenList;
        }

        @Override
        public void sendTo(CommandSender... players) {
            container.sendTo(players);
        }
    }
}
