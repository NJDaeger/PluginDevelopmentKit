package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.text.pager.ChatPaginator;
import com.njdaeger.pdk.utils.text.pager.PageItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.HoverEvent;

import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ResultCountComponent<T extends PageItem<B>, B> implements IComponent<T, B> {

    private final int padding;
    private final boolean compact;
    private final NumberFormat formatter =  CompactNumberFormat.getCompactNumberInstance(Locale.US, CompactNumberFormat.Style.SHORT);

    public ResultCountComponent(int padding) {
        this.padding = padding;
        this.compact = false;
    }

    public ResultCountComponent(boolean compact) {
        this.padding = compact ? -1 : 4;
        this.compact = compact;
        formatter.setMaximumFractionDigits(2);
    }

    @Override
    public TextComponent getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int currentPage) {
        if (compact) return Component.text()
                .content(formatter.format(results.size()))
                .color(paginator.getHighlightColor())
                .hoverEvent(HoverEvent.showText(Component.text(results.size() + "", paginator.getGrayColor())))
                .build();
        else return Component.text()
                .content(String.format("%-" + padding + "d", results.size()))
                .color(paginator.getHighlightColor())
                .append(Component.text(" Matches").appendSpace())
                .build();
    }
}
