package com.cp2y.cube.widgets.trend;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.PaintUtils;

import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendOddNumView extends View {

    private static float fontTop,fontBottom,size,padding;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
        padding = DisplayUtil.dip2px(4f);
    }

    private int currentNum = -1, prevNum = -1, nextNum = -1;
    private List<Integer> miss = null;
    private boolean showMiss = true;
    private boolean calcBlue = true;
    private int flag=-1;

    public void setCalcBlue(boolean calcBlue) {
        this.calcBlue = calcBlue;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if(flag==0){
            lp.width =  DisplayUtil.dip2px(calcBlue ? 480f:420f);
        }else if(flag==1){
            lp.width =  DisplayUtil.dip2px(calcBlue ? 480f:360f);
        }else if(flag==4){
            lp.width =  DisplayUtil.dip2px(360f);
        }

        setLayoutParams(lp);
    }
    public  void setFlag(int flag){
        this.flag=flag;
    }
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

    public TrendOddNumView(Context context) {
        super(context);
    }

    public TrendOddNumView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendOddNumView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (miss == null) return;
        float left, top = 0, right, bottom = size;
        int autoSize=0,index=0,count=0;
        if(flag==0){
            int blueCount = calcBlue ? 0 : 2;//含篮球少2个格子
            autoSize=16-blueCount;
            index= calcBlue ? 8 : 7;//偶数位置
            count = calcBlue ? 7 : 6;//总数
        }else if(flag==1){
            int blueCount = calcBlue ? 0 : 4;
            autoSize=16-blueCount;
            index= calcBlue ? 8 : 6;
            count = calcBlue ? 7 : 5;
        }else if(flag==4){
            autoSize=12;
            index=6;
            count=5;
        }
        for (int i = 0; i < autoSize; i++) {
            left = size * i;
            right = size * (i + 1);
            canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
            canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
            float centerX = (left + right) /2;
            if (showMiss) canvas.drawText(String.valueOf(miss.get(i)), (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
        }
        if (currentNum > -1) {
            left = size * (currentNum + index);
            right = size * (currentNum + index + 1);
            float centerX = (left + right) /2;//画偶数部分坐标
            float centerY = size / 2;
            int rCurrentNum = count - currentNum;//奇数个数
            float rLeft = size * rCurrentNum;//画奇数部分坐标
            float rRight = size * (rCurrentNum + 1);
            float rCenterX = (rLeft + rRight)/2;
            if (prevNum != -1) {//画折线1
                float destX  = centerX + (prevNum - currentNum)*size;
                float destY = centerY - size;//当前中心点向上飘30
                canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_BLUE_BACKGROUND);//偶数线
                float rDestX = rCenterX + (currentNum - prevNum)*size;
                canvas.drawLine(rCenterX, centerY, rDestX, destY, PaintUtils.FILL_RED_BACKGROUND);//奇数线
            }
            if (nextNum != -1) {//画折线2
                float destX = centerX + (nextNum - currentNum)*size;
                float destY = centerY + size;//当前中心点向下飘30
                canvas.drawLine(centerX, centerY, destX, destY, PaintUtils.FILL_BLUE_BACKGROUND);
                float rDestX = rCenterX + (currentNum - nextNum)*size;
                canvas.drawLine(rCenterX, centerY, rDestX, destY, PaintUtils.FILL_RED_BACKGROUND);//奇数线
            }
            canvas.drawRect(left + padding, top + padding, right - padding, bottom - padding, PaintUtils.FILL_BLUE_BACKGROUND);//先画背景
            canvas.drawRect(rLeft + padding, top + padding, rRight - padding, bottom - padding, PaintUtils.FILL_RED_BACKGROUND);//先画背景
            canvas.drawText(String.valueOf(currentNum),(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            canvas.drawText(String.valueOf(rCurrentNum),(int) rCenterX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
        }
    }
}
