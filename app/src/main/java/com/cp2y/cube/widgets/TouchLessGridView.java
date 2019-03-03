package com.cp2y.cube.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.cp2y.cube.custom.MyGridView;

/**
 * Created by js on 2017/1/18.
 */
public class TouchLessGridView extends MyGridView {

    public TouchLessGridView(Context context) {
        super(context);
    }

    public TouchLessGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
