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
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.PaintUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/12/28.
 */
public class LottoTrendSpecialView extends View {

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
    private List<Integer> currBlues = null, prevBlues = null, nextBlues = null;
    private List<Integer> blueMiss;

    public int getCurrentNum() {
        return currentNum;
    }

    public int getPrevNum() {
        return prevNum;
    }

    public int getNextNum() {
        return nextNum;
    }

    public void setData(List<Integer> blues,List<Integer> prevBlues,List<Integer> nextBlues, List<Integer> blueMiss) {
        this.currBlues = blues;
        this.prevBlues = prevBlues;
        this.nextBlues = nextBlues;
        this.blueMiss = blueMiss;
        invalidate();
    }

    public LottoTrendSpecialView(Context context) {
        super(context);
    }

    public LottoTrendSpecialView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LottoTrendSpecialView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (blueMiss == null) return;
            Set<Integer> nums = new HashSet<>();
            for (int i = 0; i< currBlues.size(); i++) {
                int num = currBlues.get(i);
                nums.add(num);
            }
            float left, top = 0, right, bottom = size;
            for (int i = 0; i < blueMiss.size(); i++) {
                left = size * i;
                right = size * (i + 1);
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
                canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
                String text = String.valueOf(blueMiss.get(i));
                float centerX = (left + right) /2;
                float centerY = size / 2;
                //写遗漏
                canvas.drawText(text, (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
                int currNum = i+1;
                //包含中奖号画篮球
                if(nums.contains(currNum)){
    //                left = size * (currentNum - 1);
    //                right = size * currentNum;
                    canvas.drawCircle(centerX, centerY, textSize , PaintUtils.FILL_BLUE_BACKGROUND);//再画圆
                    String text_blue = String.valueOf(currNum);
                    canvas.drawText(CommonUtil.preZeroForBall(text_blue),(int) centerX,(int) (size/2 - fontTop2/2 - fontBottom2/2), PaintUtils.NORMAL_WHITE_TEXT);//最后画圆内的文字
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
    }
}
