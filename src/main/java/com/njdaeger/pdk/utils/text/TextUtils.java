package com.njdaeger.pdk.utils.text;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.map.MinecraftFont;

import java.util.function.BiFunction;

public class TextUtils {

    /**
     * A function that gets the width of a character in pixels.
     */
    private static final BiFunction<Character, Boolean, Integer> charWidthFunction = (chr, isBold) -> {
        var width = MinecraftFont.Font.getChar(chr);
        //the +1 accounts for the spacing between characters
        return width != null ? width.getWidth() + 1 + ((isBold && chr != ' ') ? 1 : 0) : null;
    };

    public static int getMinecraftPixelWidth(TextComponent component) {
        var childrenWidth = component.children().stream().filter(c -> c instanceof TextComponent).map(c -> (TextComponent)c).mapToInt(TextUtils::getMinecraftPixelWidth).sum();
        var width = getMinecraftPixelWidthNoChildren(component);
        return width + childrenWidth;
    }

    private static int getMinecraftPixelWidthNoChildren(TextComponent component) {
        var str = component.content();
        var width = 0;
        var isBold = component.hasDecoration(TextDecoration.BOLD);

        for (int i = 0; i < str.length(); i++) {
            var charWidth = charWidthFunction.apply(str.charAt(i), isBold);
            if (charWidth != null) width += charWidth;
        }

        return width;
    }

    public static TextComponent truncateComponentToPixelLength(TextComponent component, int maxPixelLength) {
        int[] remainingPixels = new int[]{maxPixelLength};
        return truncateComponentToPixelLength(component, remainingPixels);
    }

    private static TextComponent truncateComponentToPixelLength(TextComponent component, int[] remainingPixels) {
        if (remainingPixels[0] <= 0) return null;

        // First handle the content of this component
        int contentWidth = getMinecraftPixelWidthNoChildren(component);

        // If just this component's text exceeds the limit, truncate it
        if (contentWidth > remainingPixels[0]) {
            return truncateComponentToPixelLengthNoChildren(component, remainingPixels[0]);
        }

        // Reduce available space by this component's width
        remainingPixels[0] -= contentWidth;

        // If no children or no space left, return the component as is
        if (component.children().isEmpty() || remainingPixels[0] <= 0) {
            return component;
        }

        // Process children with continuously updated remaining space
        return component.toBuilder().mapChildren(child -> {
            if (!(child instanceof TextComponent textChild)) return child;

            if (remainingPixels[0] <= 0) {
                return textChild.content(""); // No space left for this child
            }

            TextComponent truncatedChild = truncateComponentToPixelLength(textChild, remainingPixels);
            return truncatedChild != null ? truncatedChild : textChild.content("");
        }).build();
    }

    private static TextComponent truncateComponentToPixelLengthNoChildren(TextComponent component, int pixelLength) {
        if (pixelLength <= 0) return null;
        var content = component.content();
        var isBold = component.hasDecoration(TextDecoration.BOLD);
        var currentWidth = 0;

        var currentComponentSb = new StringBuilder();
        for (int i = 0; i < content.length(); i++) {
            var charWidth = charWidthFunction.apply(content.charAt(i), isBold);
            if (charWidth == null) continue;
            if ((currentWidth + charWidth) > pixelLength) break;
            currentComponentSb.append(content.charAt(i));
            currentWidth += charWidth;
        }
        return component.toBuilder().content(currentComponentSb.toString()).build();

    }

}
