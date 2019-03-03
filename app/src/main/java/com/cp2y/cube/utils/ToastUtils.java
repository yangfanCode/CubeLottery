package com.cp2y.cube.utils;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;

/**
 * Created by admin on 2017/2/6.
 */
public class ToastUtils {
    private static TipsToast tipsToast;
    private static Toast toast;
    public static void showToast(Context context, String content) {
        if (toast == null)
            toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        else
            toast.setText(content);
        toast.show();
    }


    public static  void showTips(String msg) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        } else {
            tipsToast = TipsToast.makeText(ContextHelper.getApplication(), msg, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        tipsToast.setText(msg);
    }
}
