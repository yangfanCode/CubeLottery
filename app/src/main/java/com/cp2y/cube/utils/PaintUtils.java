package com.cp2y.cube.utils;

import android.content.res.Resources;
import android.graphics.Paint;

import com.cp2y.cube.R;
import com.cp2y.cube.helper.ContextHelper;

/**
 * Created by js on 2016/12/29.
 */
public class PaintUtils {

    /**走势图背景画笔**/
    public static final Paint TREND_BALL_BACKGROUND = new Paint();
    /**走势图边框画笔**/
    public static final Paint TREND_BORDER = new Paint();
    /**小号字体灰色**/
    public static final Paint SMALL_GRAY_TEXT = new Paint();
    /**小号字体白色**/
    public static final Paint SMALL_WHITE_TEXT = new Paint();
    /**实心红色画笔**/
    public static final Paint FILL_RED_BACKGROUND = new Paint();
    /**中号字体白色**/
    public static final Paint NORMAL_WHITE_TEXT = new Paint();
    /**实心蓝色画笔**/
    public static final Paint FILL_BLUE_BACKGROUND = new Paint();
    /**灰色线条**/
    public static final Paint GRAY_LINE = new Paint();
    /**实心绿色画笔**/
    public static final Paint FILL_GREEN_BACKGROUND = new Paint();
    /**红蓝绿画笔数组**/
    public static Paint[] PAINTS={FILL_RED_BACKGROUND,FILL_BLUE_BACKGROUND,FILL_GREEN_BACKGROUND};
    public static void init() {
        Resources res = ContextHelper.getApplication().getResources();
        TREND_BALL_BACKGROUND.setStyle(Paint.Style.FILL);//实心矩形框
        TREND_BALL_BACKGROUND.setColor(ColorUtils.TREND_BALL_BACKGROUND);
        TREND_BORDER.setStrokeWidth(DisplayUtil.dip2px(1f));
        TREND_BORDER.setStyle(Paint.Style.STROKE);//空心矩形框
        TREND_BORDER.setColor(ColorUtils.WHITE);
        SMALL_GRAY_TEXT.setTextSize(res.getDimension(R.dimen.small_textSize));
        SMALL_GRAY_TEXT.setColor(ColorUtils.LIGHT_GRAY);
        SMALL_GRAY_TEXT.setTextAlign(Paint.Align.CENTER);
        FILL_RED_BACKGROUND.setStyle(Paint.Style.FILL);
        FILL_RED_BACKGROUND.setColor(ColorUtils.NORMAL_RED);
        FILL_RED_BACKGROUND.setStrokeWidth(DisplayUtil.dip2px(4f));
        NORMAL_WHITE_TEXT.setTextSize(res.getDimension(R.dimen.mid_textSize));
        NORMAL_WHITE_TEXT.setColor(ColorUtils.WHITE);
        NORMAL_WHITE_TEXT.setTextAlign(Paint.Align.CENTER);
        FILL_BLUE_BACKGROUND.setStyle(Paint.Style.FILL);
        FILL_BLUE_BACKGROUND.setColor(ColorUtils.NORMAL_BLUE);
        FILL_BLUE_BACKGROUND.setStrokeWidth(DisplayUtil.dip2px(4f));
        GRAY_LINE.setStrokeWidth(DisplayUtil.dip2px(4f));
        GRAY_LINE.setStyle(Paint.Style.FILL);
        GRAY_LINE.setColor(ColorUtils.NORMAL_GRAY);
        FILL_GREEN_BACKGROUND.setStyle(Paint.Style.FILL);
        FILL_GREEN_BACKGROUND.setColor(ColorUtils.NORMAL_GREEN);
        FILL_GREEN_BACKGROUND.setStrokeWidth(DisplayUtil.dip2px(4f));
        SMALL_WHITE_TEXT.setTextSize(res.getDimension(R.dimen.small_textSize));
        SMALL_WHITE_TEXT.setColor(ColorUtils.WHITE);
        SMALL_WHITE_TEXT.setTextAlign(Paint.Align.CENTER);
    }

}
