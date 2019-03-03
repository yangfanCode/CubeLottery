package com.cp2y.cube.network;

import android.app.Application;

import com.cp2y.cube.network.converter.CustomGsonConverterFactory;
import com.cp2y.cube.network.cookieStore.PersistentCookieStore;
import com.cp2y.cube.network.response.ProgressListener;
import com.cp2y.cube.network.response.ProgressResponseBody;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created by js on 2016/11/29.
 */
public final class NetClient {

    private static Retrofit mRetrofit;
    private static Retrofit dRetrofit;
    private static Application mApp;

    /**
     * 初始化NetClient
     * @param application
     */
    public static void initWithApplication(Application application) {
        mApp = application;
    }

    /**
     * 获取全局的网络连接
     * @return
     */
    public static Retrofit GlobalClient() {
        if (mRetrofit == null) {
            OkHttpClient client = new OkHttpClient
                    .Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .cookieJar(new CookieJar() {
                        private PersistentCookieStore cookieStore =  new PersistentCookieStore(mApp);
                        @Override
                        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                            try {
                                if (cookies != null && cookies.size() > 0) {
                                    for (Cookie item : cookies) {
                                        cookieStore.add(url, item);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public List<Cookie> loadForRequest(HttpUrl url) {
                            return cookieStore.get(url);
                        }
                    })
                    .build();
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(NetConst.dynamicBaseUrl())
                    .callFactory(client)
                    .addConverterFactory(CustomGsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return mRetrofit;
    }

    /**
     * 下载网络连接
     * @return
     */
    public static Retrofit DownloadClient() {
        if (dRetrofit == null) {
            dRetrofit = new Retrofit.Builder()
                    .baseUrl(NetConst.dynamicBaseUrl())
                    .callFactory(new OkHttpClient.Builder().build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return dRetrofit;
    }

    public static Retrofit DownloadClient(final ProgressListener listener) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        if (listener != null) builder.addNetworkInterceptor((chain) -> {
            Request request = chain.request();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .body(new ProgressResponseBody(response.body(), listener))
                    .build();
        });
        return new Retrofit.Builder()
                .baseUrl(NetConst.dynamicBaseUrl())
                .callFactory(builder.build())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

}
