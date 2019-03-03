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
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.PaintUtils;

import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class CQ2TrendDivideView extends View {

    private static float fontTop,fontBottom,size, padding;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
        padding = DisplayUtil.dip2px(4f);
    }

    private int currentNum = -1, prevNum = -1, nextNum = -1;
    private List<Integer> miss;
    private boolean showMiss = true;

    public void setShowMiss(boolean showMiss) {
        this.showMiss = showMiss;
    }
    /**
     * 设置偶数个数
     * @param currentNum
     * @param prevNum
     * @param nextNum
     */
    public void setNums(int currentNum, int prevNum, int nextNum, List<Integer> miss) {
        this.currentNum = currentNum;
        this.prevNum = prevNum;
        this.nextNum = nextNum;
        this.miss = miss;
        invalidate();
    }

    public CQ2TrendDivideView(Context context) {
        super(context);
    }

    public CQ2TrendDivideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CQ2TrendDivideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (miss == null) return;
            float left, top = 0, right, bottom = size;
            int autoSize=9;//除3余列数8个从0开始
            for (int i = 0; i < autoSize; i++) {
                left = size * i;
                right = size * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
                float centerX = (left + right) /2;
                if (showMiss) canvas.drawText(String.valueOf(miss.get(i)), (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
            }
            if (currentNum > -1) {
                left = size * (currentNum );
                right = size * (currentNum +1);
                float centerX = (left + right) /2;
                float centerY = size / 2;
                if (prevNum != -1) {//画折线1
                    float destX  = centerX + (prevNum - currentNum)*size;
                    float destY = centerY - size;//当前中心点向上飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                if (nextNum != -1) {//画折线2
                    float destX = centerX + (nextNum - currentNum)*size;
                    float destY = centerY + size;//当前中心点向下飘30
                    canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_RED_BACKGROUND);
                }
                canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, PaintUtils.FILL_RED_BACKGROUND);//先画背景
                String numText= MapUtils.MAP_CQ2_DIVIDE_INDEX.get(currentNum);
                canvas.drawText(numText,(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
    }
}
