package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.text.pager.ChatPaginator;
import com.njdaeger.pdk.utils.text.pager.PageItem;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public interface IComponent<T extends PageItem<B>, B> {

    TextComponent getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int currentPage);

}
