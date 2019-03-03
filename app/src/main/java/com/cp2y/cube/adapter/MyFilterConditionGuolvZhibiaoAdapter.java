package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;

import java.util.List;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterConditionGuolvZhibiaoAdapter extends MyBaseAdapter<String> {
    private List<String>list;
    private List<String>list_select;
    private Context context;
    public MyFilterConditionGuolvZhibiaoAdapter(List<String> list, Context context, int resId, List<String> list_span_select_data) {
        super(list, context, resId);
        this.list=list;
        this.list_select=list_span_select_data;
        this.context=context;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_gv);
        tv.setText(list.get(postion));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!list_select.contains(list.get(postion))){
                    tv.setTextColor(ColorUtils.MID_BLUE);
                    tv.setBackgroundResource(R.drawable.app_myfilter_ignore_selected);
                    list_select.add(list.get(postion)+"");
                }else{
                    tv.setTextColor(ColorUtils.GRAY);
                    tv.setBackgroundColor(ColorUtils.LIGHTBACK_GRAY);
                    list_select.remove(list.get(postion));
                }
            }
        });
    }
}
