package com.cp2y.cube.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

/**
 * Created by admin on 2017/1/11.
 */
public class SPUtils {
    public static StringBuilder stringBuilder=new StringBuilder();
    // 保存到手机了 文件名称
    public static final String FILE_NAME = "trend_history";

    /**
     * 保存数据的方法
     */
    public static void put(Context context,String data) {

        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("trend_history",data);
        editor.commit();
    }

    /**
     * 获取数据
     * @param context
     * @return
     */
    public static String get(Context context) {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString("trend_history",null);


    }

    /**
     * 清除数据
     * @param context
     */
    public static void remove(Context context){
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.remove("trend_history");
        editor.commit();
    }

    /**
     * 返回所有的键值对
     *
     * @param context
     * @return
     */
    public static Map<String, ?> getAll(Context context)
    {
        SharedPreferences sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }

}

