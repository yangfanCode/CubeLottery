package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/15.
 */
public class MyFilter3DAdapter extends MyBaseAdapter<String> {
    private List<String>list = new ArrayList<>();
    private Set<String>draws = new HashSet<>();
    public void reloadData(List<String> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reloadDraws(Set<String> draws) {
        this.draws.clear();
        this.draws.addAll(draws);
        notifyDataSetChanged();
    }

    public MyFilter3DAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_double_redBall);
        int val = CommonUtil.parseInt(list.get(postion));
        if(val>=50&&val<100){
            tv.setText(String.valueOf(val-50));
        }else if(val>=100){
            tv.setText(String.valueOf(val-100));
        }else{
            tv.setText(String.valueOf(val));
        }
    }
}
