package com.cp2y.cube.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * Created by yangfan on 2017/8/9.
 */
public class DragGridViewContainer extends RelativeLayout{
    private ScrollView scrollView;
    public DragGridViewContainer(Context context) {
        super(context);
    }

    public DragGridViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView){
        this.scrollView=scrollView;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollView.requestDisallowInterceptTouchEvent(false);//拦截父类事件

        } else  {
            scrollView.requestDisallowInterceptTouchEvent(true);
        }

        return super.dispatchTouchEvent(ev);
    }
}
