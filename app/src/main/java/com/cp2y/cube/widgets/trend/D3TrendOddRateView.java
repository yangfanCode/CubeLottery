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
import java.util.Locale;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendOddRateView extends View {
    /**2福彩3D,3排列3,5重庆3星,7重庆2星**/
    private int flag=0;
    private static float fontTop,fontBottom,size,padding,size2;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
        padding = DisplayUtil.dip2px(4f);
        size2 = res.getDimension(R.dimen.trend_title_height);
    }

    public D3TrendOddRateView(Context context) {
        super(context);
    }

    public D3TrendOddRateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public D3TrendOddRateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private int currentNum = -1, prevNum = -1, nextNum = -1;
    private List<Integer> miss = null;
    private boolean calcBlue = true;
    private boolean showMiss = true;

    public void setShowMiss(boolean showMiss) {
        this.showMiss = showMiss;
    }
    public void setFlag(int flag) {
        this.flag = flag;
    }

    /**
     * 设置偶数个数
     * @param currentNum
     * @param prevNum
     * @param nextNum
     */
    public void setNums(int currentNum, int prevNum, int nextNum,List<Integer> miss) {
        this.currentNum = currentNum;
        this.prevNum = prevNum;
        this.nextNum = nextNum;
        this.miss = miss;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            float left, top = 0, right, bottom = size;
            int autoSize=(flag==7?3:4);
            for (int i = 0; i < autoSize; i++) {//背景和边框
                left = size2 * i;
                right = size2 * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
                float centerX = (left + right) /2;
                if (showMiss) canvas.drawText(String.valueOf(miss.get(i)), (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
            }
            if (currentNum > -1) {
                left = size2 * currentNum;
                right = size2 * (currentNum + 1);
                float centerX = (left + right) /2;
                float centerY = size / 2;
                int count = (flag==7?2:3);
                if (prevNum != -1) {//画折线1
                    float destX  = centerX + (prevNum - currentNum)*size2;
                    float destY = centerY - size;//当前中心点向上飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                if (nextNum != -1) {//画折线2
                    float destX = centerX + (nextNum - currentNum)*size2;
                    float destY = centerY + size;//当前中心点向下飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, PaintUtils.FILL_RED_BACKGROUND);//先画背景
                canvas.drawText(String.format(Locale.CHINA,"%d:%d", count-currentNum,currentNum),(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
    }
}
