package com.cp2y.cube.utils;

import android.os.Looper;
import android.widget.Toast;

import com.cp2y.cube.helper.ContextHelper;

/**
 * Created by js on 2017/1/5.
 */
public class ToastUtil {

    private static String lastMsg;
    private static long lastToastTime = 0L;

    /**
     * 显示Toast消息,短时间
     * @param msg
     */
    public static void showShortToast(final String msg) {
        if (msg == null) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastToastTime < 4000 && lastMsg.equals(msg)) {//3秒内不连续弹出Toast
            return;
        }
        lastToastTime = currentTime;
        lastMsg = msg;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toast.makeText(ContextHelper.getApplication(), msg, Toast.LENGTH_SHORT).show();
        } else {
            ContextHelper.getApplication().runOnUiThread(()->Toast.makeText(ContextHelper.getApplication(), msg, Toast.LENGTH_SHORT).show());
        }
    }
}
