package com.cp2y.cube.helper;

import com.cp2y.cube.acache.ACache;

/**
 * 微信登录保存状态
 * Created by yangfan on 2017/7/17.
 */
public class WeChatLoginHelper {
    public static final String LOGIN="YES";//登录状态
    public static final String NO_LOGIN="NO";//退出登录状态
    /**缓存对象**/
    private static final ACache aCache= ACache.get(ContextHelper.getApplication());
    /**存**/
    public static void put(String key,String value){
        aCache.put(key, value);
    }
    /**取**/
    public static String getAsString(String key){
        return aCache.getAsString(key);
    }
    /**全删**/
    public static void clear(){
        aCache.clear();
    }
    /**删除指定key**/
    public static boolean remove(String key){
        return aCache.remove(key);
    }
}
