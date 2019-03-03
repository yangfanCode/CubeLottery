package com.cp2y.cube.widgets.wheel;

import android.app.Activity;

import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;

import cn.qqtheme.framework.picker.LinkagePicker;
import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by js on 2017/1/12.
 */
public class WheelFactory {

    /**
     * 弹窗生成器
     * @return
     */
    public static LinkagePicker generateWheelWithConfig(Activity activity, WheelConfig config) {
        LinkagePicker picker = new LinkagePicker(activity, config.provider);
        //picker.setCycleDisable(false);
        picker.setSubmitTextColor(ColorUtils.NORMAL_BLUE);
        picker.setSubmitTextSize(18);
        picker.setTextColor(ColorUtils.BLACK);
        picker.setLineConfig(config.lineConfig);
        picker.setCancelVisible(false);
        return picker;
    }

    /**
     * 弹窗生成器
     * @param activity
     * @param config
     * @return
     */
    public static OptionPicker generateWheelWithNumbers(Activity activity, WheelConfig config) {
        OptionPicker picker = new OptionPicker(activity, config.list);
//        picker.setCycleDisable(false);
        picker.setSubmitTextColor(ColorUtils.NORMAL_BLUE);
        picker.setTextColor(ColorUtils.BLACK);
        picker.setLineConfig(config.lineConfig);
        picker.setCancelVisible(false);
        return picker;
    }
}
