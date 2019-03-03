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
public class MyFilterCondition3DDaXiaoAdapter extends MyBaseAdapter<String> {
    //福彩3D大小
    private Set<String> list_select = new LinkedHashSet<>();
    private Context context;
    private View view;
    public MyFilterCondition3DDaXiaoAdapter(Context context, int resId, View daxiao_view,List<String> list) {
        super(list,context,resId);
        this.context=context;
        this.view=daxiao_view;
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
        TextView tv_daxiaobi= (TextView) view.findViewById(R.id.alert_dialog_daxiao_layout_tvRange);
        tv.setText(list.get(postion));
        tv.setOnClickListener(view1 -> {
            tv_daxiaobi.setTextColor(ColorUtils.NORMAL_BLUE);
            if(!list_select.contains(list.get(postion))){
                tv.setTextColor(ColorUtils.NORMAL_BLUE);
                tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
                list_select.add(list.get(postion));
            }else{
                tv.setTextColor(ColorUtils.GRAY);
                tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
                list_select.remove(list.get(postion));
            }
            if(list_select.size()==0){
                tv_daxiaobi.setTextColor(ColorUtils.GRAY);
            }
        });
        //第二次点击记录
        if(list_select.contains(list.get(postion))){
            tv.setTextColor(ColorUtils.NORMAL_BLUE);
            tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
        }else{
            tv.setTextColor(ColorUtils.GRAY);
            tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
        }
    }
}
