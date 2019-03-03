package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterCondition3DJiouAdapter extends MyBaseAdapter<String> {
    private Set<String> list_select = new LinkedHashSet<>();
    private Context context;
    private View view;
    public MyFilterCondition3DJiouAdapter(Context context, int resId, View view, List<String> list) {
        super(list, context, resId);
        this.context=context;
        this.view=view;
    }

    public void reloadData(Set<String> data) {
        list_select.clear();
        list_select.addAll(data);
        notifyDataSetChanged();
    }

    public Set<String> getList() {
        return list_select;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_span_tv);
        TextView tv_jioubi= (TextView) view.findViewById(R.id.alert_dialog_jiou_layout_tvRange);
        tv.setText(list.get(postion)+"");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_jioubi.setTextColor(ColorUtils.MID_BLUE);
                if(!list_select.contains(list.get(postion))){
                    tv.setTextColor(ColorUtils.MID_BLUE);
                    tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
                    list_select.add(list.get(postion));
                }else{
                    tv.setTextColor(ColorUtils.GRAY);
                    tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
                    list_select.remove(list.get(postion));
                }
                if(list_select.size()==0){
                    tv_jioubi.setTextColor(ColorUtils.GRAY);
                }
            }
        });
        //第二次点击记录
        if(list_select.contains(list.get(postion))){
            tv.setTextColor(ColorUtils.MID_BLUE);
            tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
        }else{
            tv.setTextColor(ColorUtils.GRAY);
            tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
        }
    }
}
