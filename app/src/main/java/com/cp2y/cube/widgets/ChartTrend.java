package com.cp2y.cube.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by js on 2016/11/28.
 */
public class ChartTrend extends View {

    private List<String> data = new ArrayList<String>() {
        {
            add("05");
            add("08");
            add("31");
            add("33");
        }
    };

    private Paint mPaint = new Paint();
    private Paint mPaint2 = new Paint();

    private float radis = 50;

    public ChartTrend(Context context) {
        super(context);
    }

    public ChartTrend(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartTrend(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.RED);// 设置红色
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint2.setColor(Color.BLUE);
        mPaint2.setTextSize(50);
        mPaint2.setStrokeWidth(3);
        for (int i=0;i<data.size();i++) {
            canvas.drawCircle((int)(radis*i*2.2)+radis, radis, radis, mPaint);
            //X是左边的距离,Y是baseline的位置
            canvas.drawText(data.get(i),(int)(radis*i*2.2)+radis/2,(int)(radis*1.2), mPaint2);
        }
    }
}
