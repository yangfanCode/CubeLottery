package com.cp2y.cube.utils;

import android.os.Build;

/**
 * Created by js on 2017/1/9.
 */
public class SystemUtil {

    public static String UUID() {
        String uuid = SPUtil.getString("UUID");
        if (uuid == null) {
            uuid = getDeviceID() + "_" + System.currentTimeMillis();
            SPUtil.put("UUID", uuid);
        }
        return uuid;
    }

    /**
     * 获取设备ID
     * @return
     */
    public static String getDeviceID() {
        return  "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
    }

}
