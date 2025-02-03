package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.TriFunction;
import com.njdaeger.pdk.utils.text.pager.ChatPaginator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

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
    public TextComponent getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int page) {
        int maxPage = (int) Math.ceil(results.size() / 8.0);
        var component = Component.text();
        if (page > 1) {
            component.append(Component.text()
                            .content("|<--")
                            .color(paginator.getHighlightColor())
                            .hoverEvent(HoverEvent.showText(Component.text("Go to the first page", paginator.getGrayColor())))
                            .clickEvent(ClickEvent.runCommand(firstPage.apply(generatorInfo, results, page)))
                    );
            component.appendSpace();
            component.append(Component.text()
                            .content("<-")
                            .color(paginator.getHighlightColor())
                            .hoverEvent(HoverEvent.showText(Component.text("Go to the previous page", paginator.getGrayColor())))
                            .clickEvent(ClickEvent.runCommand(previousPage.apply(generatorInfo, results, page)))
                    );
        } else {
            component.append(Component.text()
                            .content("|<--")
                            .color(paginator.getGrayedOutColor())
                            .hoverEvent(HoverEvent.showText(Component.text("You are on the first page", paginator.getGrayColor())))
                    );
            component.appendSpace();
            component.append(Component.text()
                            .content("<-")
                            .color(paginator.getGrayedOutColor())
                            .hoverEvent(HoverEvent.showText(Component.text("You are on the first page", paginator.getGrayColor())))
                    );
        }

        component.append(Component.text(" = ", paginator.getGrayColor()));
        component.append(Component.text("[" + String.format("%-4d/%4d", page, maxPage) + "]", paginator.getHighlightColor()));
        component.append(Component.text(" = ", paginator.getGrayColor()));

        if (page < maxPage) {
            component.append(Component.text()
                            .content("->")
                            .color(paginator.getHighlightColor())
                            .hoverEvent(HoverEvent.showText(Component.text("Go to the next page", paginator.getGrayColor())))
                            .clickEvent(ClickEvent.runCommand(nextPage.apply(generatorInfo, results, page)))
                    );
            component.appendSpace();
            component.append(Component.text()
                            .content("-->|")
                            .color(paginator.getHighlightColor())
                            .hoverEvent(HoverEvent.showText(Component.text("Go to the last page", paginator.getGrayColor())))
                            .clickEvent(ClickEvent.runCommand(lastPage.apply(generatorInfo, results, page)))
                    );
        } else {
            component.append(Component.text()
                            .content("->")
                            .color(paginator.getGrayedOutColor())
                            .hoverEvent(HoverEvent.showText(Component.text("You are on the last page", paginator.getGrayColor())))
                    );
            component.appendSpace();
            component.append(Component.text()
                            .content("-->|")
                            .color(paginator.getGrayedOutColor())
                            .hoverEvent(HoverEvent.showText(Component.text("You are on the last page", paginator.getGrayColor())))
                    );
        }
        return component.build();
    }
}
