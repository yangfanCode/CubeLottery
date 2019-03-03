package com.cp2y.cube.network;

import com.cp2y.cube.enums.AppEnvEnum;
import com.cp2y.cube.enums.AppEnvHelper;

/**
 * Created by js on 2016/11/29.
 */
public class NetConst {
    /**此处为线上域名**/
    public static final String PRODUCT_HOST = "https://app.binguo.com";//线上主域名
    public static final String CUSTOM_ICON_HOST = "https://res.cp2y.com";//定制彩种图线上
    public static final String PRODUCT_HOST_TREND = "http://chart.cp2y.com";//H5走势线上


    /**此处为测试域名**/
    public static final String PRODUCT_HOST_TEST = "http://115.231.223.139:6813";//debug主域名
    public static final String CUSTOM_ICON_HOST_TEST = "http://ceres.cp2y.com:5555/h5";//定制彩种图debug
    public static final String PRODUCT_HOST_TREND_TEST = "http://115.231.223.139:8086";//H5走势debug


    public static final String API_HOST = PRODUCT_HOST;
    public static final int VERSION_CONTROL_ID = 9;//版本兼容ID


    /**以下为接口地址**/
    public static final String NEWS_DETAIL = dynamicBaseUrl()+"/news/ViewNewsDetails?id=";//资讯详情H5
    public static final String DOWN_NUMBER_URL = "/numberLibrary/downloadNumber";
    public static final String DOWN_NUMBER_TEXT_URL = "/numberLibrary/downloadNumberTxt";
    public static final String APP_SHARE_URL = "https://app.binguo.com/download/downloadH5";
    public static final String CUSTOM_ICON_URL = "/cp2y_data/lottery_icon/";//定制图接口
    public static final String TREND_ICON_URL = "/cp2y_data/trend_icon/";//走势图接口


    //主域名
    public static String dynamicBaseUrl() {
        if (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE) {
            return PRODUCT_HOST;
        }
        return PRODUCT_HOST;
    }

    //H5走势图域名
    public static String dynamicBaseUrlForH5() {
        if (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE) {
            return PRODUCT_HOST_TREND;
        }
        return PRODUCT_HOST_TREND_TEST;
    }

    //彩种icon域名 目前都用线上
    public static String dynamicBaseUrlForLottery() {
        if (AppEnvHelper.currentEnv() == AppEnvEnum.ONLINE) {
            return CUSTOM_ICON_HOST;
        }
        return CUSTOM_ICON_HOST;
    }
}
