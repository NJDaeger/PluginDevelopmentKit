package com.njdaeger.pdk.utils.text.pager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

/**
 * @param <G> The type of generator info that this item will use to generate its text.
 */
public interface PageItem<G> {


    /**
     * Gets the text for this item. This is what is shown in the body of the page per item.
     * @param generatorInfo The generator info for this item.
     * @return The text for this item.
     */
    default TextComponent getItemText(ChatPaginator<?, G> paginator, G generatorInfo) {
        return Component.text(getPlainItemText(paginator, generatorInfo)).color(paginator.getHighlightColor());
    }

    /**
     * Gets the hover text for this item. This is what is shown when the item is hovered over when using the ELLIPSIS line wrapping mode.
     * @param generatorInfo The generator info for this item.
     * @return The hover text for this item.
     */
    default TextComponent getEllipsisHoverText(ChatPaginator<?, G> paginator, G generatorInfo) {
        return Component.text(getPlainItemText(paginator, generatorInfo)).color(paginator.getGrayColor());
    }

    /**
     * Gets the plain text for this item. This is the text that is shown in the body of the page per item.
     * @param generatorInfo The generator info for this item.
     * @return The plain text for this item.
     */
    String getPlainItemText(ChatPaginator<?, G> paginator, G generatorInfo);



}
