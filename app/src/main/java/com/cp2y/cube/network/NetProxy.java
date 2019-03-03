package com.cp2y.cube.network;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import retrofit2.Retrofit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by js on 2016/11/14.
 */
public class NetProxy implements InvocationHandler {

    private static final String TAG = "NetProxy";
    private Retrofit retrofit;
    private Object target;
    public NetProxy(Retrofit retrofit) {
        this.retrofit = retrofit;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object obj = method.invoke(target, args);
        if (obj instanceof Observable) {
            Observable<Object> ob = (Observable<Object>) obj;
            return ob.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
        } else {
            return obj;
        }
    }

    public <T> T getProxy(Class<T> clazz) {
        target = retrofit.create(clazz);
        T proxy = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, this);
        return proxy;
    }

}
