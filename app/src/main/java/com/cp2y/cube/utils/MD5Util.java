package com.cp2y.cube.utils;

import java.security.MessageDigest;

/**
 * Created by js on 2017/1/9.
 */
public class MD5Util {

    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            result = result.toLowerCase();
            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }

    /**
     * MD5 16位
     * @param sourceStr
     * @return
     */
    public static String MD5With16Bits(String sourceStr) {
        String result = MD5(sourceStr);
        if (result.length() > 0) result = result.substring(8,24);
        return result;
    }

}
