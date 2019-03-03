package com.cp2y.cube.network.subscriber;

import com.cp2y.cube.Settings;
import com.cp2y.cube.custom.TipsToast;

import rx.Subscriber;

/**
 * Created by js on 2016/5/24.
 */
public class SafeOnlyNextSubscriber<T> extends Subscriber<T> {

    private static long mLastConnectErrorTime = 0;

    private static final String TAG = "SafeOnlyNextSubscriber";

    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public SafeOnlyNextSubscriber() {
        super(CommonSubscriber.create());//just hack
    }

    public SafeOnlyNextSubscriber(Subscriber<? super T> actual) {
        super(actual);
    }

    @Override
    public void onError(Throwable e) {
        try {
            e.printStackTrace();
            if (Settings.DEBUG_MODE) TipsToast.showTips(e.getMessage());
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onNext(T args){

    }
}
