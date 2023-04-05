package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.TriFunction;
import com.njdaeger.pdk.utils.text.Text;
import com.njdaeger.pdk.utils.text.click.ClickAction;
import com.njdaeger.pdk.utils.text.click.ClickString;
import com.njdaeger.pdk.utils.text.hover.HoverAction;
import com.njdaeger.pdk.utils.text.pager.ChatPaginator;

import java.util.List;

public class PageNavigationComponent<T, B> implements IComponent<T, B> {

    private final TriFunction<B, List<T>, Integer, String> nextPage;
    private final TriFunction<B, List<T>, Integer, String> previousPage;
    private final TriFunction<B, List<T>, Integer, String> firstPage;
    private final TriFunction<B, List<T>, Integer, String> lastPage;

    public PageNavigationComponent(TriFunction<B, List<T>, Integer, String> firstPage,
                                   TriFunction<B, List<T>, Integer, String> previousPage,
                                   TriFunction<B, List<T>, Integer, String> nextPage,
                                   TriFunction<B, List<T>, Integer, String> lastPage) {
        this.nextPage = nextPage;
        this.previousPage = previousPage;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
    }

    @Override
    public Text.Section getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int page) {
        int maxPage = (int) Math.ceil(results.size() / 8.0);
        var component = Text.of("");
        if (page > 1) {
            component.appendRoot("|<--")
                    .setColor(paginator.getHighlightColor())
                    .setClickEvent(ClickAction.RUN_COMMAND, ClickString.of(firstPage.apply(generatorInfo, results, page)))
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("Go to the first page").setColor(paginator.getGrayColor()));
            component.appendRoot(" ");
            component.appendRoot("<-")
                    .setColor(paginator.getHighlightColor())
                    .setClickEvent(ClickAction.RUN_COMMAND, ClickString.of(previousPage.apply(generatorInfo, results, page)))
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("Go to the previous page").setColor(paginator.getGrayColor()));
        } else {
            component.appendRoot("|<--")
                    .setColor(paginator.getGrayedOutColor())
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("You are on the first page").setColor(paginator.getGrayColor()));
            component.appendRoot(" ");
            component.appendRoot("<-")
                    .setColor(paginator.getGrayedOutColor())
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("You are on the first page").setColor(paginator.getGrayColor()));
        }

        component.appendRoot(" = ").setColor(paginator.getGrayColor());
        component.appendRoot("[" + String.format("%-4d/%4d", page, maxPage)).setColor(paginator.getHighlightColor()).append("]");
        component.appendRoot(" = ").setColor(paginator.getGrayColor());

        if (page < maxPage) {
            component.appendRoot("->")
                    .setColor(paginator.getHighlightColor())
                    .setClickEvent(ClickAction.RUN_COMMAND, ClickString.of(nextPage.apply(generatorInfo, results, page)))
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("Go to the next page").setColor(paginator.getGrayColor()));
            component.appendRoot(" ");
            component.appendRoot("-->|")
                    .setColor(paginator.getHighlightColor())
                    .setClickEvent(ClickAction.RUN_COMMAND, ClickString.of(lastPage.apply(generatorInfo, results, page)))
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("Go to the last page").setColor(paginator.getGrayColor()));
        } else {
            component.appendRoot("->")
                    .setColor(paginator.getGrayedOutColor())
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("You are on the last page").setColor(paginator.getGrayColor()));
            component.appendRoot(" ");
            component.appendRoot("-->|")
                    .setColor(paginator.getGrayedOutColor())
                    .setHoverEvent(HoverAction.SHOW_TEXT, Text.of("You are on the last page").setColor(paginator.getGrayColor()));
        }
        return component;
    }
}
