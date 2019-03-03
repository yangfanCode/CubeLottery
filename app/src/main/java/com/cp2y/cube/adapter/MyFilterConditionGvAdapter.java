package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterConditionGvAdapter extends MyBaseAdapter<String> {

    public MyFilterConditionGvAdapter(Context context, int resId) {
        super(context, resId);
    }

    public MyFilterConditionGvAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
    }

    public void reloadData(List<String> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void reloadData(Set<String> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_gv);
        tv.setText(list.get(postion));
    }
}
