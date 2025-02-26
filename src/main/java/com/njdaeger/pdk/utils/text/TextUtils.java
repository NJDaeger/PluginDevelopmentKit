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
        return width != null ? width.getWidth() + 1 + (isBold ? 1 : 0) : null;
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
        if (maxPixelLength <= 0) return null;
        var rootComponentLength = getMinecraftPixelWidthNoChildren(component);
        if (rootComponentLength > maxPixelLength) return truncateComponentToPixelLengthNoChildren(component, maxPixelLength);
        return component.toBuilder().mapChildren(child -> {
            if (!(child instanceof TextComponent textChild)) return child;
            var truncatedChild = truncateComponentToPixelLength(textChild, maxPixelLength - rootComponentLength);
            if (truncatedChild == null) return child;
            return truncatedChild;
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
            if (currentWidth + charWidth > pixelLength) break;
            currentComponentSb.append(content.charAt(i));
            currentWidth += charWidth;
        }
        return component.toBuilder().content(currentComponentSb.toString()).build();

    }

}
