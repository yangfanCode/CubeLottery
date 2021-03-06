package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.MapUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterConditionDaXiaoBiAdapter extends MyBaseAdapter<String> {
    private Set<String> list_select = new LinkedHashSet<>();
    private int count=0;//数据总数
    private View view;
    //key为count,value为数据
    private static Map<Integer,String[]> totalList=new HashMap<Integer,String[]>(){{
        put(8,MapUtils.BALL_NUM_RATE);//双色球 大乐透
        put(6,MapUtils.P5_NUM_RATE);//排列5
    }};
    public MyFilterConditionDaXiaoBiAdapter(Context context, int resId, View daxiao_view,int count) {
        super(Arrays.asList(totalList.get(count)),context, resId);
        this.count=count;
        this.view=daxiao_view;
    }

    @Override
    public int getCount() {
        return count;
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
