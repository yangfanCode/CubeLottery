package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.widgets.wheel.WheelFactory;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

/**
 * Created by admin on 2016/12/16.
 */
public class MyFilterConditionJiouXingTaiAdapter extends MyBaseAdapter<String> {
    private int count=0;//数据总数
    private Map<Integer,String> list_select = new LinkedHashMap<>();
    private View view;
    public MyFilterConditionJiouXingTaiAdapter(Context context, int resId,View view,int count) {
        super(context, resId);
        this.view=view;
        this.count=count;
    }

    @Override
    public int getCount() {
        return count;
    }

    public void reloadData(Map<Integer, String> data) {
        list_select.clear();
        list_select.putAll(data);
        notifyDataSetChanged();
    }

    public Map<Integer, String> getList() {
        return list_select;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_condition_jiouxingtai_tv);
        TextView tv_jiouXingTai= (TextView) view.findViewById(R.id.alert_dialog_jiou_layout_tvSingel);
        tv.setText("奇偶");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_jiouXingTai.setTextColor(ColorUtils.MID_BLUE);
                OptionPicker picker = WheelFactory.generateWheelWithNumbers(ContextHelper.getLastActivity(), WheelConfig.ODD_WHEEL_CONFIG);
                if (list_select.containsKey(postion)) picker.setSelectedItem(list_select.get(postion));
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
                        tv.setText(item);
                        if(!"奇偶".equals(item)){
                            list_select.put(postion,item);
                            tv.setTextColor(ColorUtils.MID_BLUE);
                            tv.setBackgroundResource(R.mipmap.gltj_btn_xiala_50_pre);
                        }else{
                            list_select.remove(postion);
                            tv.setTextColor(ColorUtils.GRAY);
                            tv.setBackgroundResource(R.mipmap.gltj_btn_xiala_50);
                            tv_jiouXingTai.setTextColor(ColorUtils.GRAY);
                        }
                    }
                });
                picker.show();
            }
        });
        if(list_select.size()>0&&list_select!=null){
            if("奇".equals(list_select.get(postion))||"偶".equals(list_select.get(postion))){
                tv.setText(list_select.get(postion));
                tv.setTextColor(ColorUtils.MID_BLUE);
                tv.setBackgroundResource(R.mipmap.gltj_btn_xiala_50_pre);
            }else{
                tv.setText("奇偶");
                tv.setTextColor(ColorUtils.GRAY);
                tv.setBackgroundResource(R.mipmap.gltj_btn_xiala_50);
            }
        }else{
            tv.setText("奇偶");
            tv.setTextColor(ColorUtils.GRAY);
            tv.setBackgroundResource(R.mipmap.gltj_btn_xiala_50);
        }
    }
}
