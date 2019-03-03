package com.cp2y.cube.widgets;

import android.graphics.drawable.ColorDrawable;

import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.cp2y.cube.R;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;

/**
 * Created by js on 2017/1/18.
 */
public class SwipeMenuHelper {

    public static SwipeMenuItem generateDeleteItem() {
        int width = DisplayUtil.dip2px(40);
        SwipeMenuItem deleteItem = new SwipeMenuItem(ContextHelper.getApplication());
        deleteItem.setTitle("删除");
        deleteItem.setTitleSize(12);
        deleteItem.setTitleColor(ColorUtils.WHITE);
        deleteItem.setBackground(new ColorDrawable(ColorUtils.NORMAL_RED));
        deleteItem.setWidth(width);
        deleteItem.setIcon(R.mipmap.icon_delete);
        return deleteItem;
    }
}
