package com.cp2y.cube.helper;

import com.cp2y.cube.acache.ACache;
import com.cp2y.cube.network.NetConst;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by yangfan on 2017/7/17.
 */
public class ACacheHelper {
    /**retrofit对象**/
    public static final Retrofit RETROFIT = new Retrofit.Builder().baseUrl(NetConst.dynamicBaseUrl()).addCallAdapterFactory(RxJavaCallAdapterFactory.create()).addConverterFactory(GsonConverterFactory.create()).build();
    /**缓存对象**/
    private static final ACache aCache= ACache.get(ContextHelper.getApplication());
    /**存**/
    public static void put(String key,String json){
        aCache.put(key, json);
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
