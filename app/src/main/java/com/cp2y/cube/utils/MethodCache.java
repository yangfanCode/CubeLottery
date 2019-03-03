package com.cp2y.cube.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by js on 2017/1/10.
 */
public class MethodCache {

    private static Map<String, Method[]> cache = new HashMap<String, Method[]>();//缓存方法

    private static Map<Method, String> paramCache = new HashMap<Method, String>();//缓存参数类型

    public static Method[] getMethods(Class<?> clazz) {
        String clazzName = clazz.getName();
        if(cache.containsKey(clazzName)){
            return cache.get(clazzName);
        }
        synchronized (cache) {
            if(!cache.containsKey(clazzName)) {
                cache.put(clazzName, clazz.getMethods());
            }
        }
        return cache.get(clazzName);
    }

    public static String getSetMethodIndex(Method mhd) {
        if(paramCache.containsKey(mhd)){
            return paramCache.get(mhd);
        }
        synchronized (paramCache) {
            if(!paramCache.containsKey(mhd)) {
                try {
                    String n = mhd.getName().toLowerCase();
                    if (n.startsWith("set")){
                        n = n.substring(3);
                        paramCache.put(mhd, n);
                    }
                } catch (Exception e) {
                    // TODO 自动生成的 catch 块
                }
            }
        }
        return paramCache.get(mhd);
    }

}