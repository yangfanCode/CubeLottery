package com.cp2y.cube.custom;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by yangfan on 2017/10/27.
 */
public class EnLargeGridView extends MyGridView {
    private final String TAG = "yangfan";
    private int mCurrposition = 0;

    public EnLargeGridView(Context context) {
        super(context);
    }

    public EnLargeGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public void setCurrentPosition(int pos) {
        this.mCurrposition = pos;
    }

    @Override
    protected void setChildrenDrawingOrderEnabled(boolean enabled) {
        super.setChildrenDrawingOrderEnabled(enabled);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        //把第一个和最后一个的绘制顺序交换一下
        if (i == childCount - 1) {
            return mCurrposition;
        }
        if (i == mCurrposition) {
            return childCount - 1;
        }
        return super.getChildDrawingOrder(childCount, i);
    }

}
