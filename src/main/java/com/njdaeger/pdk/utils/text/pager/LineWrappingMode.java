package com.njdaeger.pdk.utils.text.pager;

public enum LineWrappingMode {


    /**
     * Always (at most) 8 items in body, will wrap items to new lines if it's too long for one line.
     * The resulting body may be longer than 8 lines.
     */
    FIXED_ITEMS_WRAP,

    /**
     * Always (at most) 8 lines in body, will wrap items to new lines if it's too long for one line.
     * If the items are too long for a page, they will be continued on the following page.
     */
    FIXED_LINES_WRAP,

    /**
     * Always (at most) 8 items in body, no wrapping will occur if lines are too long, rather; the
     * items will be truncated and an ellipsis will be added with a hover event containing the full text of the line.
     */
    ELLIPSIS,

    /**
     * Always (at most) 8 items in the body. If a line is too long, it is truncated and the remainder will not be displayed.
     */
    TRUNCATE

}
