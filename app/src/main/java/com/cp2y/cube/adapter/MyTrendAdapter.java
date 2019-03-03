package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.List;

/**
 * Created by admin on 2016/12/8.
 */
public class MyTrendAdapter extends MyBaseAdapter<String> {
    private String[] myTrend={"双色球-号码走势","双色球-定位走势","双色球-跨度走势","双色球-除3余走势","双色球-和值走势","双色球-奇偶走势"};
    private String[] Lotto_myTrend={"大乐透-号码走势","大乐透-定位走势","大乐透-跨度走势","大乐透-除3余走势","大乐透-和值走势","大乐透-奇偶走势"};
    private List<String>list;
    public MyTrendAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv_myTrend= (TextView) holder.findView(R.id.item_mytrend_tvThredTitle);
        if(list.get(postion).contains("_")){//大乐透

        }else{//双色球
            tv_myTrend.setText(list.get(postion));
        }

    }
}
