package com.cp2y.cube.fragment.pailie5.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyBaseAdapter;
import com.cp2y.cube.adapter.ViewHolder;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/15.
 */
public class MyFilterP5Adapter extends MyBaseAdapter<String> {
    private List<String>list = new ArrayList<>();
    public void reloadData(List<String> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }


    public MyFilterP5Adapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_double_redBall);
        int val = CommonUtil.parseInt(list.get(postion));
        if(val>=50&&val<100){
            tv.setText(String.valueOf(val-50));
        }else if(val>=100&&val<150){
            tv.setText(String.valueOf(val-100));
        }else if(val>=150&&val<200){
            tv.setText(String.valueOf(val-150));
        }else if(val>=200){
            tv.setText(String.valueOf(val-200));
        }else{
            tv.setText(String.valueOf(val));
        }
    }
}
