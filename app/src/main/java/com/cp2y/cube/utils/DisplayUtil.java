package com.cp2y.cube.utils;

/**
 * Created by js on 2016/6/4.
 */
public class DisplayUtil {
    public static float SCALE=1;
    public static int dip2px( float dpValue) {
        return (int) (dpValue * SCALE + 0.5f);
    }

    public static int px2dip( float pxValue) {
        return (int) (pxValue / SCALE + 0.5f);
    }

}
