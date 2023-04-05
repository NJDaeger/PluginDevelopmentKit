package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.text.Text;
import com.njdaeger.pdk.utils.text.pager.ChatPaginator;

import java.text.CompactNumberFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ResultCountComponent<T, B> implements IComponent<T, B> {

    private final int padding;
    private final boolean compact;
    private final NumberFormat formatter =  CompactNumberFormat.getCompactNumberInstance(Locale.US, CompactNumberFormat.Style.SHORT);

    public ResultCountComponent(int padding) {
        this.padding = padding;
        this.compact = false;
    }

    public ResultCountComponent(boolean compact) {
        this.padding = -1;
        this.compact = compact;
        formatter.setMaximumFractionDigits(2);
    }

    @Override
    public Text.Section getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int currentPage) {
        if (compact) return Text.of(formatter.format(results.size())).setColor(paginator.getHighlightColor());
        else return Text.of(String.format("%-" + padding + "d", results.size())).setColor(paginator.getHighlightColor()).append(" Matches");
    }
}
