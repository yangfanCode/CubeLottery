package com.cp2y.cube.widgets.trend;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;

/**
 * Created by js on 2017/1/3.
 */
public class VerticalDashedLineView extends View {

    private Paint paint = null;

    public VerticalDashedLineView(Context context) {
        this(context, null);
    }

    public VerticalDashedLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style. STROKE);
        this.paint.setColor(ColorUtils.LIGHT_BLUE);
        this.paint.setAntiAlias( true);
        this.paint.setStrokeWidth(DisplayUtil.dip2px(4.0F));
        float[] arrayOfFloat = new float[4];
        arrayOfFloat[0] = DisplayUtil.dip2px(2.0F);
        arrayOfFloat[1] = DisplayUtil.dip2px(6.0F);
        arrayOfFloat[2] = DisplayUtil.dip2px(2.0F);
        arrayOfFloat[3] = DisplayUtil.dip2px(6.0F);
        DashPathEffect pe = new DashPathEffect(arrayOfFloat, DisplayUtil.dip2px(0.0F));
        this.paint.setPathEffect(pe);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path path = new Path();
        path.moveTo(0.0F, 0.0F);
        path.lineTo(0.0F, getMeasuredHeight());
        canvas.drawPath(path, this.paint);
    }

}
