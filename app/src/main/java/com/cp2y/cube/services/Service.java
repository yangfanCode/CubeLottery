package com.cp2y.cube.services;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by js on 2017/1/9.
 */
public abstract class Service {

    private static Map<Integer, Service> _cache = new HashMap<>();
    public static <T extends Service> T getService(Class<T> tClass) {
        int hash = tClass.hashCode();
        if (!_cache.containsKey(tClass.hashCode())) {
            try {
                _cache.put(hash, tClass.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) _cache.get(hash);
    }

}
