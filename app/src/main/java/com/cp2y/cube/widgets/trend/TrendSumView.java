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

/**
 * Created by js on 2016/12/28.
 */
public class TrendSumView extends View {

    private static float fontTop,fontBottom,size,size2, padding;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
        padding = DisplayUtil.dip2px(4f);
        size2 = res.getDimension(R.dimen.trend_title_width);
    }
    /*0双色球,1大乐透,4排列5*/
    private int flag=-1;
    private int currentNum = -1, prevNum = -1, nextNum = -1;
    private boolean calcBlue = true;

    public void setCalcBlue(boolean calcBlue) {
        this.calcBlue = calcBlue;
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    /**
     * 设置偶数个数
     * @param currentNum
     * @param prevNum
     * @param nextNum
     */
    public void setNums(int currentNum, int prevNum, int nextNum) {
        this.currentNum = currentNum;
        this.prevNum = prevNum;
        this.nextNum = nextNum;
        invalidate();
    }

    public TrendSumView(Context context) {
        super(context);
    }

    public TrendSumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendSumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float getDoubleIndex(int num) {
        if (calcBlue)  return (num - 22) * 10f / 180;
        return (num - 21) * 10f / 170;
    }
    private float getLottoIndex(int num){
        if (calcBlue)  return (num - 18) * 10f / 170;
        return (num - 15) * 10f / 150;
    }
    private float getP5Index(int num){
        return num  * 10f / 50;
    }

    private float getIndex(int num){
        switch (flag){
            case 0:
                return getDoubleIndex(num);
            case 1:
                return getLottoIndex(num);
            case 4:
                return getP5Index(num);
        }
        return 0;
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            float left, top = 0, right, bottom = size;
            for (int i = 0; i < 10; i++) {
                left = size2 * i;
                right = size2 * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
            }
            if (currentNum > -1) {
                float index=0,line1=0,line2=0;

                index = getIndex(currentNum);
                line1=getIndex(prevNum);
                line2=getIndex(nextNum);
                left = size2 * (index - 0.5f);
                right = size2 * (index + 0.5f);
                float centerX = (left + right) /2;
                float centerY = size / 2;
                if (prevNum != -1) {//画折线1
                    float destX  = centerX + ( line1- index)*size2;
                    float destY = centerY - size;//当前中心点向上飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                if (nextNum != -1) {//画折线2
                    float destX = centerX + (line2 - index)*size2;
                    float destY = centerY + size;//当前中心点向下飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, PaintUtils.FILL_RED_BACKGROUND);//先画背景
                canvas.drawText(String.valueOf(currentNum),(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
    }
}
