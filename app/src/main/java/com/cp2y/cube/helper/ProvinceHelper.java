package com.cp2y.cube.helper;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangfan on 2017/8/3.
 */
public class ProvinceHelper {
    private ProvinceHelper(){ }
    private static String PROVINCE=null;
    //定义单例map对象
    private static final Map<String, String> objMap = new HashMap<String, String>(){{
        put("全国","1001");
        put("北京市","1002");
        put("上海市","1003");
        put("天津市","1004");
        put("重庆市","1005");
        put("浙江省","1006");
        put("广东省","1007");
        put("江苏省","1008");
        put("山东省","1009");
        put("辽宁省","1010");
        put("安徽省","1011");
        put("河北省","1012");
        put("河南省","1013");
        put("湖北省","1014");
        put("湖南省","1015");
        put("福建省","1016");
        put("山西省","1017");
        put("江西省","1018");
        put("广西壮族自治区","1019");
        put("新疆维吾尔自治区","1020");
        put("黑龙江省","1021");
        put("内蒙古自治区","1022");
    }};

    //获取
    public static String getService(String key){
        if(!TextUtils.isEmpty(key)){
            return objMap.get(key);
        }
        return "";
    }

    //对象
    public static Map getMap(){
        return  objMap;
    }

    //设置省份
    public static void setProvince(String province){
        PROVINCE=province;
    }

    //获得省份
    public static String getProvince(){
        return PROVINCE;
    }
}
