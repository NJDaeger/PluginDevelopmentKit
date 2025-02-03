package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.text.pager.ChatPaginator;
import net.kyori.adventure.text.TextComponent;

import java.util.List;

public interface IComponent<T, B> {

    TextComponent getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int currentPage);

}
