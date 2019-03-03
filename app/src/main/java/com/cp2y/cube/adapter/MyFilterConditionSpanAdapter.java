package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterConditionSpanAdapter extends MyBaseAdapter<Integer> {
    private Set<Integer>list_select = new LinkedHashSet<>();
    private int count =0;//数据总数
    private View view;
    private int rangeStart=0;//起始数字
    public MyFilterConditionSpanAdapter(Context context, int resId, View view,int rangeStart,int count) {
        super(context, resId);
        this.count=count;
        this.view=view;
        this.rangeStart=rangeStart;
    }

    public void reloadData(Set<Integer> data) {
        list_select.clear();
        list_select.addAll(data);
        notifyDataSetChanged();
    }

    public Set<Integer> getList() {
        return list_select;
    }

    @Override
    public int getCount() {return count;}

    @Override
    public void bindData(ViewHolder holder, int postion) {
        RelativeLayout layout= (RelativeLayout) holder.findView(R.id.item_filter_condition_span_layout);
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_span_tv);
        TextView tv_singel= (TextView) view.findViewById(R.id.alert_dialog_span_layout_tvSingel);
        int val = (postion+rangeStart);
        tv.setText(String.valueOf(val));
        layout.setOnClickListener(view1 -> {
            tv_singel.setTextColor(ColorUtils.MID_BLUE);
            if(!list_select.contains(val)){
                tv.setTextColor(ColorUtils.MID_BLUE);
                tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
                list_select.add(val);
            }else{
                tv.setTextColor(ColorUtils.GRAY);
                tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
                list_select.remove(val);
            }
            if(list_select.size()==0){
                tv_singel.setTextColor(ColorUtils.GRAY);
            }
        });
        if(list_select.contains(val)){
            tv.setTextColor(ColorUtils.MID_BLUE);
            tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
        }else{
            tv.setTextColor(ColorUtils.GRAY);
            tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
        }

     }
    }

