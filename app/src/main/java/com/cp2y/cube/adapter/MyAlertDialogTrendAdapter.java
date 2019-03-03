package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.List;

/**
 * Created by admin on 2016/12/8.
 */
public class MyAlertDialogTrendAdapter extends MyBaseAdapter<String> {
    private List<String>list;
    public MyAlertDialogTrendAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv_myTrend= (TextView) holder.findView(R.id.item_mytrend_dialog_tvThredTitle);
        tv_myTrend.setText(list.get(postion));
    }
}
