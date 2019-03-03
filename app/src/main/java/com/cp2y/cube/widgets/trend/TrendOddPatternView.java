package com.cp2y.cube.widgets.trend;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.PaintUtils;

/**
 * Created by js on 2016/12/28.
 */
public class TrendOddPatternView extends View {

    private static float fontTop,fontBottom,size,padding;

    static {
        Resources res =  ContextHelper.getApplication().getResources();
        Paint.FontMetrics fontMetrics = PaintUtils.SMALL_GRAY_TEXT.getFontMetrics();
        fontTop = fontMetrics.top;
        fontBottom = fontMetrics.bottom;
        size = res.getDimension(R.dimen.trend_item_height);
        padding = DisplayUtil.dip2px(4f);
    }

    public TrendOddPatternView(Context context) {
        super(context);
    }

    public TrendOddPatternView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendOddPatternView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    /*0双色球,1大乐透*/
    private int flag=-1;
    private byte[] currPattern, prevPattern, nextPattern;
    private boolean calcBlue = true;
    public void setFlag(int flag){
        this.flag=flag;
    }
    public void setCalcBlue(boolean calcBlue) {
        this.calcBlue = calcBlue;
        ViewGroup.LayoutParams lp = getLayoutParams();
        if(flag==0){
            lp.width =  DisplayUtil.dip2px(calcBlue ? 420f:360f);
        }else if(flag==1){
            lp.width =  DisplayUtil.dip2px(calcBlue ? 420f:300f);
        }else if(flag==4){
            lp.width =  DisplayUtil.dip2px(300f);
        }
        setLayoutParams(lp);
    }

    public void setPatterns(byte[] currPattern,byte[] prevPattern,byte[] nextPattern) {
        this.currPattern = currPattern;
        this.prevPattern = prevPattern;
        this.nextPattern = nextPattern;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            float left, top = 0, right, bottom = size;
            int autoSize=0;
            if(flag==0){
                int blueCount = calcBlue ? 0 : 2;
                autoSize=14-blueCount;
            }else if(flag==1){
                int blueCount = calcBlue ? 0 : 4;
                autoSize=14-blueCount;
            }else if(flag==4){
                autoSize=10;
            }

            for (int i = 0; i < autoSize; i++) {//背景和边框
                left = size * i;
                right = size * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
            }
            for (int i = 0;i < currPattern.length; i++) { //连接线
                byte currNum = currPattern[i];
                int index = i * 2 + currNum;
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
                int index = i * 2 + b;
                left = size * index + padding;
                right = size * (index + 1) - padding;
                canvas.drawRect(left, top + padding, right, bottom - padding, bgPaint[i % 3]);//先画背景
                //再画文字
                float centerX = (left + right) /2;
                canvas.drawText(MapUtils.ODD_EVEN[b],(int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_WHITE_TEXT);//最后画圆内的文字
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }

    }
}
