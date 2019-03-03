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
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.PaintUtils;

import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendDivideNumberView extends View {
    /**2福彩3D,3排列3,5重庆3星,7重庆2星**/
    private int flag=0;
    private static float fontTop,fontBottom,fontTop2,fontBottom2,size,textSize,padding;

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
        padding = DisplayUtil.dip2px(4f);
    }

    public D3TrendDivideNumberView(Context context) {
        super(context);
    }

    public D3TrendDivideNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public D3TrendDivideNumberView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private byte[] currPattern, prevPattern, nextPattern;
    private List<Integer> miss = null;
    private boolean showMiss;

    public void setShowMiss(boolean showMiss) {
        this.showMiss = showMiss;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setPatterns(byte[] currPattern, byte[] prevPattern, byte[] nextPattern, List<Integer> miss) {
        this.currPattern = currPattern;
        this.prevPattern = prevPattern;
        this.nextPattern = nextPattern;
        this.miss = miss;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (miss == null) return;
            int autoSize=(flag==7?9:12);//12个格子
            float left, top = 0, right, bottom = size;
            for (int i = 0; i < autoSize; i++) {//背景和边框
                left = size * i;
                right = size * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
                float centerX = (left + right) /2;
                if (showMiss) canvas.drawText(String.valueOf(miss.get(i)), (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
            }
            int length =(flag==7?3:4);
            for (int i = 0;i < currPattern.length; i++) { //连接线
                byte currNum = currPattern[i];
                int index = i * length + currNum;
                left = size * index + padding;
                right = size * (index + 1) - padding;
                float centerX = (left + right) /2;
                float centerY = size / 2;
                if (prevPattern != null) {//画折线1
                    byte prevNum = prevPattern[i];
                    float destX  = centerX + (prevNum - currNum)*size;
                    float destY = centerY - size;//当前中心点向上飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.PAINTS[i%3]);
                }
                if (nextPattern != null) {//画折线2
                    byte nextNum = nextPattern[i];
                    float destX = centerX + (nextNum - currNum)*size;
                    float destY = centerY + size;//当前中心点向下飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.PAINTS[i%3]);
                }
            }
            Paint[] bgPaint = new Paint[]{PaintUtils.FILL_RED_BACKGROUND, PaintUtils.FILL_BLUE_BACKGROUND, PaintUtils.FILL_GREEN_BACKGROUND};
            for (int i = 0; i < currPattern.length; i++) {//中间矩形和文字
                byte b = currPattern[i];
                int index = i * length + b;
                left = size * index + padding;
                right = size * (index + 1) - padding;
                canvas.drawRect(left, top + padding, right, bottom - padding, bgPaint[i]);//先画背景
                //再画文字
                float centerX = (left + right) /2;
                canvas.drawText(String.valueOf(b),(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }

    }
}
