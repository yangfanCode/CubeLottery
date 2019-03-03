package com.cp2y.cube.network.subscriber;

import rx.Subscriber;

/**
 * Created by js on 2016/5/23.
 */
public class CommonSubscriber<T> extends Subscriber<T> {

    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onNext(T t) {

    }

    public static CommonSubscriber create() {
        return new CommonSubscriber<>();
    }

}
