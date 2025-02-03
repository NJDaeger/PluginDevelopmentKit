package com.njdaeger.pdk.utils.text;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.map.MinecraftFont;

public class TextUtils {

    public static int getMinecraftPixelWidth(TextComponent component) {
        var str = component.content();
        var width = 0;

        for (int i = 0; i < str.length(); i++) {
            var chr = MinecraftFont.Font.getChar(str.charAt(i));
            if (chr != null) width += chr.getWidth();
        }
        return width;
    }

}
