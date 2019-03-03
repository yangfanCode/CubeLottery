package com.cp2y.cube.network.api;

import com.cp2y.cube.network.NetClient;
import com.cp2y.cube.network.NetProxy;
import com.cp2y.cube.network.response.ProgressListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by js on 2016/11/29.
 */
public class ApiHelper {

    /**网络服务接口缓存**/
    static private Map<String, Object> _caches = Collections.synchronizedMap(new HashMap<>());

    /**
     * 获取接口服务
     * @param serviceClass
     */
    protected <T> T getApiService(Class<T> serviceClass) {
        if (serviceClass == null) return null;
        String serviceKey = serviceClass.getSimpleName();
        T service = (T) _caches.get(serviceClass.getSimpleName());
        if (service == null) {
            service = new NetProxy(NetClient.GlobalClient()).getProxy(serviceClass);//代理API
            _caches.put(serviceKey, service);
        }
        return service;
    }

    /**
     * 获取下载服务
     * @param serviceClass
     * @param <T>
     * @return
     */
    protected <T> T getDownloadService(Class<T> serviceClass, ProgressListener... listener) {
        if (listener != null && listener.length > 0) return NetClient.DownloadClient(listener[0]).create(serviceClass);
        return NetClient.DownloadClient().create(serviceClass);
    }

    /**
     * 变换Scheduler
     * @param <T>
     * @return
     */
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return observable -> observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取传输的基础参数
     * @return
     */
    protected HttpParam getBasicTransBody() {
        HttpParam map = new HttpParam();
        return map;
    }

    public static class HttpParam extends HashMap<String, Object> {

        @Override
        public HttpParam put(String key, Object value) {
            super.put(key, value);
            return this;
        }

    }

}
