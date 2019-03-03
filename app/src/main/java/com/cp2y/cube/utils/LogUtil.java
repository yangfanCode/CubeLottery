package com.cp2y.cube.utils;

import android.util.Log;

import com.cp2y.cube.enums.AppEnvEnum;


public class LogUtil {
    private final static String TAG = "LogUtil";
    private static AppEnvEnum appEnvEnum = AppEnvEnum.DEBUG;

    public static void setAppEnvEnum(AppEnvEnum appEnvEnum) {
        LogUtil.appEnvEnum = appEnvEnum;
    }

    public static void LogD(Class classz, String str) {
        if (appEnvEnum != null && appEnvEnum == AppEnvEnum.DEBUG)
            Log.d(TAG, classz.getCanonicalName() + "--->" + str);
    }

    public static void LogI(Class classz, String str) {
        if (appEnvEnum != null && appEnvEnum == AppEnvEnum.DEBUG)
            Log.i(TAG, classz.getCanonicalName() + "--->" + str);
    }

    public static void LogE(Class classz, String str) {
        if (appEnvEnum != null && appEnvEnum == AppEnvEnum.DEBUG)
            Log.e(TAG, classz.getCanonicalName() + "--->" + str);
    }

    public static void LogV(Class classz, String str) {
        if (appEnvEnum != null && appEnvEnum == AppEnvEnum.DEBUG)
            Log.v(TAG, classz.getCanonicalName() + "--->" + str);
    }

    public static void LogException(Class c, Throwable e) {
        if (appEnvEnum != null && appEnvEnum == AppEnvEnum.DEBUG) {
            try {
                StringBuilder exceptionInfo = new StringBuilder();
                if (e == null) {
                    exceptionInfo.append("Exception:"
                            + "e is null,probably null pointer exception"
                            + "\n");
                } else {
                    e.printStackTrace();
                    exceptionInfo.append(e.getClass().getCanonicalName() + ":"
                            + e.getMessage() + "\n");
                    StackTraceElement[] stes = e.getStackTrace();
                    for (StackTraceElement ste : stes) {
                        exceptionInfo.append("at " + ste.getClassName() + "$"
                                + ste.getMethodName() + "$" + ste.getFileName()
                                + ":" + ste.getLineNumber() + "\n");
                    }
                }
                LogE(c, exceptionInfo.toString());
            } catch (Exception ex) {
                LogE(c, ex.toString());
            }
        }

    }
}
