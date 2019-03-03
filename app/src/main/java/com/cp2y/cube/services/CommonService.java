package com.cp2y.cube.services;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by js on 2017/1/18.
 */
public class CommonService extends Service {

    public Observable<Long> runDelay(long delay) {
        return Observable.timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
