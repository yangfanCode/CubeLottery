package com.cp2y.cube.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by admin on 2017/2/4.
 */
public class GuideSharedPreferences {

    private SharedPreferences sp;

    // 保存
    public void saveSharedPreferences(Context context, String save,String saveKey) {
        sp = context.getSharedPreferences(saveKey, context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString("state", save);
        editor.commit();// 保存新数据
    }

    // 取出
    public boolean takeSharedPreferences(Context context,String saveKey) {
        String str = "";
        sp = context.getSharedPreferences(saveKey, context.MODE_PRIVATE);
        str = sp.getString("state", "");
        if (str.equals("")) {
            return false;
        }else{
            return true;
        }
    }
}
