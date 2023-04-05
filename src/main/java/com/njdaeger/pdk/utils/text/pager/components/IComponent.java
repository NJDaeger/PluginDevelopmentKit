package com.njdaeger.pdk.utils.text.pager.components;

import com.njdaeger.pdk.utils.text.Text;
import com.njdaeger.pdk.utils.text.pager.ChatPaginator;

import java.util.List;

public interface IComponent<T, B> {

    Text.Section getText(B generatorInfo, ChatPaginator<T, B> paginator, List<T> results, int currentPage);

}
