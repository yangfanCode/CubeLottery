package com.cp2y.cube.widgets;

import android.content.Context;
import android.util.AttributeSet;

import com.baoyz.swipemenulistview.SwipeMenuListView;

/**
 * Created by js on 2017/1/18.
 */
public class MostSwipeMenuList extends SwipeMenuListView {

    public MostSwipeMenuList(Context context) {
        super(context);
    }

    public MostSwipeMenuList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MostSwipeMenuList(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, measureSpec);
    }

}
