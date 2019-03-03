package com.cp2y.cube.widgets.wheelconfig;

import com.cp2y.cube.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.LinkagePicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by js on 2017/1/12.
 * 走势弹窗配置参数
 */
public class WheelConfig {

    public LinkagePicker.DataProvider provider;
    public WheelView.LineConfig lineConfig;
    public List<String> list;

    public WheelConfig(LinkagePicker.DataProvider provider, WheelView.LineConfig lineConfig) {
        this.provider = provider;
        this.lineConfig = lineConfig;
    }

    public WheelConfig(LinkagePicker.DataProvider provider, WheelView.LineConfig lineConfig, List<String> list) {
        this.provider = provider;
        this.lineConfig = lineConfig;
        this.list = list;
    }


    private static LinkagePicker.DataProvider generateLinkagedProviderWithData(List<String> data) {
        return new LinkagePicker.DataProvider() {
            @Override
            public boolean isOnlyTwo() {
                return true;
            }

            @Override
            public List<String> provideFirstData() {
                return data;
            }

            @Override
            public List<String> provideSecondData(int firstIndex) {
                return data;
            }

            @Override
            public List<String> provideThirdData(int firstIndex, int secondIndex) {
                return null;
            }
        };
    }

    private static final ArrayList<String> DIVIDE_LIST = new ArrayList<>();
    private static final ArrayList<String> D3_DIVIDE_LIST = new ArrayList<>();
    private static final ArrayList<String> P5_DIVIDE_LIST = new ArrayList<>();
    private static final ArrayList<String> CQ2_DIVIDE_LIST = new ArrayList<>();
    private static final ArrayList<String> RANGE_LIST = new ArrayList<>();
    private static final ArrayList<String> LOTTO_RANGE_LIST = new ArrayList<>();
    private static final ArrayList<String> D3_RANGE_LIST = new ArrayList<>();
    private static final ArrayList<String> P5_RANGE_LIST = new ArrayList<>();
    private static final ArrayList<String> SUM_LIST = new ArrayList<>();
    private static final ArrayList<String> LOTTO_SUM_LIST = new ArrayList<>();
    private static final ArrayList<String> D3_SUM_LIST = new ArrayList<>();
    private static final ArrayList<String> P5_SUM_LIST = new ArrayList<>();
    private static final ArrayList<String> CQ2_SUM_LIST = new ArrayList<>();
    private static final ArrayList<String> BIG_LIST = new ArrayList<>();
    private static final ArrayList<String> ODD_LIST = new ArrayList<>();
    private static final ArrayList<String> DIVID_LIST = new ArrayList<>();

    /**
     * 线条配置
     */
    public static cn.qqtheme.framework.widget.WheelView.LineConfig LINE_CONFIG  = new cn.qqtheme.framework.widget.WheelView.LineConfig();
    static {//赋值
        LINE_CONFIG.setRatio(1);
        LINE_CONFIG.setColor(ColorUtils.GRAY);
        for (int i = 22; i <= 199; i++) {
            SUM_LIST.add(String.valueOf(i));
        }
        for (int i = 18; i <= 188; i++) {
            LOTTO_SUM_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 27; i++) {
            D3_SUM_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 45; i++) {
            P5_SUM_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 18; i++) {
            CQ2_SUM_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 7; i++) {
            DIVIDE_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 3; i++) {
            D3_DIVIDE_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 5; i++) {
            P5_DIVIDE_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 2; i++) {
            CQ2_DIVIDE_LIST.add(String.valueOf(i));
        }
        for (int i = 5; i <= 32; i++) {
            RANGE_LIST.add(String.valueOf(i));
        }
        for (int i = 4; i <= 34; i++) {
            LOTTO_RANGE_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 9; i++) {
            D3_RANGE_LIST.add(String.valueOf(i));
        }
        for (int i = 0; i <= 9; i++) {
            P5_RANGE_LIST.add(String.valueOf(i));
        }
        BIG_LIST.add("大小");
        BIG_LIST.add("大");
        BIG_LIST.add("小");
        ODD_LIST.add("奇偶");
        ODD_LIST.add("奇");
        ODD_LIST.add("偶");
        DIVID_LIST.add("012");
        DIVID_LIST.add("0");
        DIVID_LIST.add("1");
        DIVID_LIST.add("2");
    }
    /***
     * 滚轮范围
     */
    private static final LinkagePicker.DataProvider SUM_WHEEL_PROVIDER = generateLinkagedProviderWithData(SUM_LIST);
    private static final LinkagePicker.DataProvider LOTTO_SUM_WHEEL_PROVIDER = generateLinkagedProviderWithData(LOTTO_SUM_LIST);
    private static final LinkagePicker.DataProvider D3_SUM_WHEEL_PROVIDER = generateLinkagedProviderWithData(D3_SUM_LIST);
    private static final LinkagePicker.DataProvider P5_SUM_WHEEL_PROVIDER = generateLinkagedProviderWithData(P5_SUM_LIST);
    private static final LinkagePicker.DataProvider CQ2_SUM_WHEEL_PROVIDER = generateLinkagedProviderWithData(CQ2_SUM_LIST);
    private static final LinkagePicker.DataProvider RANGE_WHEEL_PROVIDER = generateLinkagedProviderWithData(RANGE_LIST);
    private static final LinkagePicker.DataProvider LOTTO_RANGE_WHEEL_PROVIDER = generateLinkagedProviderWithData(LOTTO_RANGE_LIST);
    private static final LinkagePicker.DataProvider D3_RANGE_WHEEL_PROVIDER = generateLinkagedProviderWithData(D3_RANGE_LIST);
    private static final LinkagePicker.DataProvider P5_RANGE_WHEEL_PROVIDER = generateLinkagedProviderWithData(P5_RANGE_LIST);
    private static final LinkagePicker.DataProvider DIVIDE_WHEEL_PROVIDER = generateLinkagedProviderWithData(DIVIDE_LIST);
    private static final LinkagePicker.DataProvider D3_DIVIDE_WHEEL_PROVIDER = generateLinkagedProviderWithData(D3_DIVIDE_LIST);
    private static final LinkagePicker.DataProvider P5_DIVIDE_WHEEL_PROVIDER = generateLinkagedProviderWithData(P5_DIVIDE_LIST);
    private static final LinkagePicker.DataProvider CQ2_DIVIDE_WHEEL_PROVIDER = generateLinkagedProviderWithData(CQ2_DIVIDE_LIST);

    /**和值过滤**/
    public static final WheelConfig SUM_WHEEL_CONFIG = new WheelConfig(SUM_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig LOTTO_SUM_WHEEL_CONFIG = new WheelConfig(LOTTO_SUM_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig D3_SUM_WHEEL_CONFIG = new WheelConfig(D3_SUM_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig P5_SUM_WHEEL_CONFIG = new WheelConfig(P5_SUM_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig CQ2_SUM_WHEEL_CONFIG = new WheelConfig(CQ2_SUM_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig RANGE_WHEEL_CONFIG = new WheelConfig(RANGE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig LOTTO_RANGE_WHEEL_CONFIG = new WheelConfig(LOTTO_RANGE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig D3_RANGE_WHEEL_CONFIG = new WheelConfig(D3_RANGE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig P5_RANGE_WHEEL_CONFIG = new WheelConfig(P5_RANGE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig DIVIDE_WHEEL_CONFIG = new WheelConfig(DIVIDE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig D3_DIVIDE_WHEEL_CONFIG = new WheelConfig(D3_DIVIDE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig P5_DIVIDE_WHEEL_CONFIG = new WheelConfig(P5_DIVIDE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig CQ2_DIVIDE_WHEEL_CONFIG = new WheelConfig(CQ2_DIVIDE_WHEEL_PROVIDER, LINE_CONFIG);
    public static final WheelConfig BIG_WHEEL_CONFIG = new WheelConfig(null, LINE_CONFIG, BIG_LIST);
    public static final WheelConfig ODD_WHEEL_CONFIG = new WheelConfig(null, LINE_CONFIG, ODD_LIST);
    public static final WheelConfig DIVID_WHEEL_CONFIG = new WheelConfig(null, LINE_CONFIG, DIVID_LIST);
}
