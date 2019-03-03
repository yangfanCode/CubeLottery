package com.cp2y.cube.widgets.trend;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.cp2y.cube.R;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.PaintUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/12/28.
 */
public class TrendBaseView extends View {

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

    public TrendBaseView(Context context) {
        super(context);
    }

    public TrendBaseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TrendBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<Integer> reds = null, prevReds = null, nextReds = null, prevReds2 = null, nextReds2 = null, redMiss = null;
    private boolean showMutiNum,showConnectNum,showEdgeNum,showSerialNum;

    public void setShowNum(boolean showMutiNum,boolean showConnectNum,boolean showEdgeNum,boolean showSerialNum) {
        this.showMutiNum = showMutiNum;
        this.showConnectNum = showConnectNum;
        this.showEdgeNum = showEdgeNum;
        this.showSerialNum = showSerialNum;
    }

    public void setData(List<Integer> reds,List<Integer> prevReds,List<Integer> nextReds, List<Integer> prevReds2,List<Integer> nextReds2, List<Integer> redMiss) {
        this.reds = reds;
        this.prevReds = prevReds;
        this.nextReds = nextReds;
        this.prevReds2 = prevReds2;
        this.nextReds2 = nextReds2;
        this.redMiss = redMiss;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (reds == null || redMiss == null) return;
        float left, top = 0, right, bottom = size;
        Set<Integer> nums = new HashSet<>();
        for (int i = 0; i< reds.size(); i++) {
            int num = reds.get(i);
            nums.add(num);
        }
        for (int i = 0,j = 0; i < redMiss.size(); i++) {//遗漏
            left = size * i;
            right = size * (i + 1);
            canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BALL_BACKGROUND);//先画背景
            canvas.drawRect(left, top, right, bottom, PaintUtils.TREND_BORDER);//再画边框
            String text = String.valueOf(redMiss.get(i));
            float centerX = (left + right) /2;
            float centerY = size / 2;
            canvas.drawText(text, (int) centerX,(int) (size/2 - fontTop/2 - fontBottom/2), PaintUtils.SMALL_GRAY_TEXT);//再画文字
            int currNum = i+1;
            if (nums.contains(currNum)) {
                boolean highLight = false;
                Set<Integer> curr = new HashSet<>(), prev = new HashSet<>(), prev2 = new HashSet<>(), next = new HashSet<>(), next2 = new HashSet<>();
                curr.addAll(reds);
                if (prevReds != null) prev.addAll(prevReds);
                if (nextReds != null) next.addAll(nextReds);
                if (showMutiNum && ((prevReds != null && prevReds.contains(currNum)) || (nextReds != null && nextReds.contains(currNum)))) {//重号
                    highLight = true;
                } else if (showConnectNum && ((j>0 && reds.get(j-1) == currNum -1) || (j < reds.size()-1 && reds.get(j+1) == currNum + 1))) {//连号
                    highLight = true;
                } else if (showEdgeNum && (prevReds != null && (prevReds.contains(currNum - 1) || prevReds.contains(currNum + 1)))) {//边号
                    highLight = true;
                } else if (showEdgeNum && (nextReds != null && (nextReds.contains(currNum - 1) || nextReds.contains(currNum + 1)))) {//边号
                    highLight = true;
                } else if (showSerialNum) {//串号
                    if (prevReds2 != null) prev2.addAll(prevReds2);
                    if (nextReds2 != null) next2.addAll(nextReds2);
                    if (curr.contains(currNum - 1) && curr.contains(currNum + 1)) {//横向三连号
                        highLight = true;
                    } else if (curr.contains(currNum - 2) && curr.contains(currNum - 1)) {//横向三连号
                        highLight = true;
                    } else if (curr.contains(currNum + 1) && curr.contains(currNum + 2)) {//横向三连号
                        highLight = true;
                    } else if (prev.contains(currNum) && prev2.contains(currNum)) {//纵向三连号
                        highLight = true;
                    } else if (prev.contains(currNum) && next.contains(currNum)) {//纵向三连号
                        highLight = true;
                    } else if (next.contains(currNum) && next2.contains(currNum)) {//纵向三连号
                        highLight = true;
                    } else if (prev.contains(currNum - 1) && prev2.contains(currNum - 2)) {//斜向三连号
                        highLight = true;
                    } else if (prev.contains(currNum + 1) && prev2.contains(currNum + 2)) {//斜向三连号
                        highLight = true;
                    } else if (prev.contains(currNum - 1) && next.contains(currNum + 1)) {//斜向三连号
                        highLight = true;
                    } else if (prev.contains(currNum + 1) && next.contains(currNum - 1)) {//纵向三连号
                        highLight = true;
                    } else if (next.contains(currNum - 1) && next2.contains(currNum - 2)) {//纵向三连号
                        highLight = true;
                    } else if (next.contains(currNum + 1) && next2.contains(currNum + 2)) {//纵向三连号
                        highLight = true;
                    }
                }
                j++;
                canvas.drawCircle(centerX, centerY, textSize , highLight? PaintUtils.FILL_BLUE_BACKGROUND:PaintUtils.FILL_RED_BACKGROUND);//再画圆
                text = "0".concat(String.valueOf(i+1));
                text = text.substring(text.length() - 2, text.length());
                canvas.drawText(text,(int) centerX,(int) (size/2 - fontTop2/2 - fontBottom2/2), PaintUtils.NORMAL_WHITE_TEXT);//最后画圆内的文字
            }
        }
    }
}
