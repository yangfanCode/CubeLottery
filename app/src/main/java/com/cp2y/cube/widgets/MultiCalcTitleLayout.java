package com.cp2y.cube.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.cp2y.cube.R;

/**
 * Created by yangfan on 2018/4/2.
 */

public class MultiCalcTitleLayout extends LinearLayout {
    public MultiCalcTitleLayout(Context context) {
        super(context);
    }

    public MultiCalcTitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(getContext()).inflate(R.layout.multi_calc_title_layout, this, true);
    }
}
