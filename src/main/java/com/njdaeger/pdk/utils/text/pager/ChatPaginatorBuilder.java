package com.njdaeger.pdk.utils.text.pager;

import com.njdaeger.pdk.utils.text.pager.components.IComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public class ChatPaginatorBuilder<T extends PageItem<B>, B> {

    private final ChatPaginator<T, B> paginator;

    ChatPaginatorBuilder() {
        this.paginator = new ChatPaginator<>();
    }

    /**
     * Add a component to this chat paginator header or footer.
     *
     * @param component The component to add.
     * @param position The position to add the component to.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> addComponent(IComponent<T, B> component, ComponentPosition position) {
        paginator.addComponent(component, position);
        return this;
    }

    /**
     * Add a component to this chat paginator header or footer.
     *
     * @param section The section to add.
     * @param position The position to add the component to.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> addComponent(TextComponent section, ComponentPosition position) {
        paginator.addComponent((info, pager, results, page) -> section, position);
        return this;
    }

    /**
     * Add a component to this chat paginator header or footer.
     *
     * @param text The text to add.
     * @param position The position to add the component to.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> addComponent(String text, ComponentPosition position) {
        paginator.addComponent((info, pager, results, page) -> Component.text(text), position);
        return this;
    }

    /**
     * The default color to highlight important information or buttons with in the header or footer.
     *
     * @param highlightColor The color to highlight important information with.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setHighlightColor(TextColor highlightColor) {
        paginator.setHighlightColor(highlightColor);
        return this;
    }

    /**
     * The default color to highlight important information or buttons with in the header or footer.
     *
     * @param color The color to highlight important information with.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setHighlightColor(NamedTextColor color) {
        paginator.setHighlightColor(TextColor.color(color.red(), color.green(), color.blue()));
        return this;
    }

    /**
     * The default color to highlight important information or buttons with in the header or footer.
     *
     * @param red The red value of the color.
     * @param green The green value of the color.
     * @param blue The blue value of the color.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setHighlightColor(int red, int green, int blue) {
        paginator.setHighlightColor(TextColor.color(red, green, blue));
        return this;
    }

    /**
     * The default color to effectively "gray out" highlighted information. Basically, information that would be
     * highlighted with the highlighted color normally, but can't be for some reason, should take on this color.
     *
     * @param grayedOutColor The color to gray out highlighted information with.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayedOutColor(TextColor grayedOutColor) {
        paginator.setGrayedOutColor(grayedOutColor);
        return this;
    }

    /**
     * The default color to effectively "gray out" highlighted information. See {@link #setGrayedOutColor(TextColor)} for more information.
     * @param color The color to gray out highlighted information with.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayedOutColor(NamedTextColor color) {
        paginator.setGrayedOutColor(TextColor.color(color.red(), color.green(), color.blue()));
        return this;
    }

    /**
     * The default color to effectively "gray out" highlighted information. See {@link #setGrayedOutColor(TextColor)} for more information.
     * @param red The red value of the color.
     * @param green The green value of the color.
     * @param blue The blue value of the color.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayedOutColor(int red, int green, int blue) {
        paginator.setGrayedOutColor(TextColor.color(red, green, blue));
        return this;
    }

    /**
     * The default color for non-highlighted information in the header or footer.
     * @param grayColor The color to use for non-highlighted information.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayColor(TextColor grayColor) {
        paginator.setGrayColor(grayColor);
        return this;
    }

    /**
     * The default color for non-highlighted information in the header or footer.
     * @param color The color to use for non-highlighted information.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayColor(NamedTextColor color) {
        paginator.setGrayColor(TextColor.color(color.red(), color.green(), color.blue()));
        return this;
    }

    /**
     * The default color for non-highlighted information in the header or footer.
     * @param red The red value of the color.
     * @param green The green value of the color.
     * @param blue The blue value of the color.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setGrayColor(int red, int green, int blue) {
        paginator.setGrayColor(TextColor.color(red, green, blue));
        return this;
    }

    /**
     * Set the line wrapping mode for this paginator
     * @param lineWrappingMode The line wrapping mode for this paginator
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setLineWrappingMode(LineWrappingMode lineWrappingMode) {
        paginator.setLineWrappingMode(lineWrappingMode);
        return this;
    }

    /**
     * Set the number of equal signs to use in the header and footer. This is split per side. 27 signs is the default per side of the header and footer. So, total length is 44.
     * @param equalSignCount The number of equal signs to use in the left/right side of the header and footer.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setEqualSignCount(int equalSignCount) {
        paginator.setEqualSignCount(equalSignCount);
        return this;
    }

    /**
     * Set the chat width in pixels. This is used to determine how many characters can fit on a single line in chat. Default width is 320 pixels.
     * @param chatWidth The chat width in pixels.
     * @return This builder.
     */
    public ChatPaginatorBuilder<T, B> setChatWidthInPixels(int chatWidth) {
        paginator.setChatWidthInPixels(chatWidth);
        return this;
    }


    /**
     * The default color for the page number in the header or footer.
     * @return This builder.
     */
    public ChatPaginator<T, B> build() {
        return paginator;
    }

}
