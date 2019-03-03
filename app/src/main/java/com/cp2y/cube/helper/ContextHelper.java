package com.cp2y.cube.helper;

import android.util.SparseArray;

import com.cp2y.cube.CubeApplication;
import com.cp2y.cube.activity.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by js on 2016/11/29.
 */
public class ContextHelper {

    private static CubeApplication mApp;

    private static SparseArray<WeakReference<BaseActivity>> mAct = new SparseArray<>();
    private static WeakReference<BaseActivity> mLast = null;

    public static void initWithApplication(CubeApplication application) {
        mApp = application;
    }

    public static CubeApplication getApplication() {
        return mApp;
    }

    /**
     * 设置最后的
     * @param act
     */
    public static void setLastActivity(BaseActivity act) {
        if (mLast != null && mLast.get() == act) return;
        mLast = new WeakReference<>(act);
    }

    /**
     * 最后一个Activity
     * @return
     */
    public static BaseActivity getLastActivity() {
        if (mLast == null) return null;
        return mLast.get();
    }

    /**
     * 添加act
     * @param act
     * @param <T>
     */
    public static <T extends BaseActivity> void addActivity(T act) {
        int code = act.getClass().hashCode();
        mAct.put(code, new WeakReference<BaseActivity>(act));
    }

    /**
     * 获取act
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T extends BaseActivity> T getActivity(Class<T> tClass) {
        WeakReference<BaseActivity> ref = mAct.get(tClass.hashCode());
        return ref != null ? (T) ref.get() : null;
    }

    /**
     * 移除Act
     * @param tClass
     * @param <T>
     */
    public static <T extends BaseActivity> void removeActivity(Class<T> tClass) {
        mAct.remove(tClass.hashCode());
    }

}
