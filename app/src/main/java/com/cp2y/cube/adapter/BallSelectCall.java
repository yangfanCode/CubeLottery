package com.cp2y.cube.adapter;

import android.widget.BaseAdapter;

/**
 * Created by js on 2017/1/10.
 */
public interface BallSelectCall {
    /**选择球之前验证**/
    boolean onBeforeSelected(BaseAdapter adapter, String val,boolean isLongClick);
    /**当前选中或者取消的球**/
    void onBallSelected(BaseAdapter adapter, String val, boolean selected);
    /**大乐透后区选择球之前验证**/
    boolean onLottoBeforeSelected(BaseAdapter adapter, String val);
}
