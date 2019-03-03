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
public class MyFilterConditionMantissaAdapter extends MyBaseAdapter<Integer> {
    private Set<Integer>list_select = new LinkedHashSet<>();
    private View view;
    public MyFilterConditionMantissaAdapter(Context context, int resId, View view) {
        super(context, resId);
        this.view=view;
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
    public int getCount() {return 10;}

    @Override
    public void bindData(ViewHolder holder, int postion) {
        RelativeLayout layout= (RelativeLayout) holder.findView(R.id.item_filter_condition_span_layout);
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_span_tv);
        TextView tv_singel= (TextView) view.findViewById(R.id.alert_dialog_mantissa_layout_tvSingel);
        int val = postion;
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

