package com.cp2y.cube.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.cp2y.cube.enums.AppEnvEnum;
import com.cp2y.cube.enums.AppEnvHelper;

/**
 * 判断包是debug还是release
 * Created by yangfan on 2018/1/2.
 */
public class LogUtils {
    public static boolean APP_DBG = false; // 是否是debug模式

    public static void init(Context context){
        APP_DBG = isApkDebugable(context);
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }

    private boolean isDebug(){
        if (AppEnvHelper.currentEnv() == AppEnvEnum.DEBUG) {
            return true;
        }
        return false;
    }

}
