package com.handmark.pulltorefresh.library.internal;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.R;

/**
 * 帧动画  实现加载自定义的动画   实现的是帧动画
 * Created by dingzuoqiang on 2017/12/29.
 * 530858106@qq.com
 */

public class FrameAnimationLayout extends LoadingLayout {

    private AnimationDrawable mAnimationDrawable;

    public FrameAnimationLayout(Context context, PullToRefreshBase.Mode mode,
                                PullToRefreshBase.Orientation scrollDirection, TypedArray attrs) {
        super(context, mode, scrollDirection, attrs);
        mHeaderImage.setImageResource(R.drawable.loading);
        mAnimationDrawable = (AnimationDrawable) mHeaderImage.getDrawable();
    }

    @Override
    protected int getDefaultDrawableResId() {
        //返回   自定义动画布局
        return R.drawable.loading;
    }

    @Override
    protected void onLoadingDrawableSet(Drawable imageDrawable) {

    }

    @Override
    protected void onPullImpl(float scaleOfLayout) {

    }

    @Override
    protected void pullToRefreshImpl() {

    }

    //刷新的时候
    @Override
    protected void refreshingImpl() {
        mAnimationDrawable.start();//开启动画
    }

    @Override
    protected void releaseToRefreshImpl() {

    }

    @Override
    protected void resetImpl() {
        mHeaderImage.clearAnimation();
        mHeaderImage.setVisibility(View.VISIBLE);
    }
}
