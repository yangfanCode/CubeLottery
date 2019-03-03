package com.cp2y.cube.widgets.trend;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.PaintUtils;

import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendLocateView extends View {

    private static float fontTop,fontBottom,fontTop2,fontBottom2,size,textSize;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        textSize = res.getDimension(R.dimen.small_textSize);
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        Paint.FontMetrics fontMetrics2 = PaintUtils.NORMAL_WHITE_TEXT.getFontMetrics();
        fontTop2 = fontMetrics2.top;
        fontBottom2 = fontMetrics2.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
    }

    private int currentNum = -1, prevNum = -1, nextNum = -1;
    private List<Integer> miss = null;
    private Range numRange;//3D定位写死,不用范围,双色球 大乐透动态设置
    private boolean showMiss;
    private int type=0;

    public int getCurrentNum() {
        return currentNum;
    }

    public int getPrevNum() {
        return prevNum;
    }

    public int getNextNum() {
        return nextNum;
    }

    public void setNums(int currentNum, int prevNum, int nextNum, List<Integer> miss) {
        this.currentNum = currentNum;
        this.prevNum = prevNum;
        this.nextNum = nextNum;
        this.miss = miss;
    }

    public void setShowMiss(boolean showMiss) {
        this.showMiss = showMiss;
    }

    public void setNumRange(Range numRange) {
        this.numRange = numRange;
    }

    public void setType(int type){this.type=type;}

    public D3TrendLocateView(Context context) {
        super(context);
    }

    public D3TrendLocateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public D3TrendLocateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (miss == null) return;
            float left, top = 0, right, bottom = size;
            for (int i = 0; i <= 9; i++) {
                left = size * (i);
                right = size * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
                String text = String.valueOf(miss.get(i));
                float centerX = (left + right) /2;
                if (showMiss) canvas.drawText(text, (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
                if (currentNum == i) {
                    continue;
                }
            }
            if (currentNum > -1) {
                left = size * (currentNum);
                right = size * (currentNum + 1);
                float centerX = (left + right) /2;
                float centerY = size / 2;
                if (prevNum != -1) {//画折线1
                    float destX  = centerX + (prevNum - currentNum)*size;
                    float destY = centerY - size;//当前中心点向上飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.PAINTS[type%3]);
                }
                if (nextNum != -1) {//画折线2
                    float destX = centerX + (nextNum - currentNum)*size;
                    float destY = centerY + size;//当前中心点向下飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.PAINTS[type%3]);
                }
                canvas.drawCircle(centerX, centerY, textSize , PaintUtils.PAINTS[type%3]);//再画圆
                String text = String.valueOf(currentNum);
                canvas.drawText(text,(int) centerX,(int) (size/2 - fontTop2/2 - fontBottom2/2), PaintUtils.NORMAL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
    }
}
