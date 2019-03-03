package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
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
public class MyFilterDoubleModeBlueAdapter extends MyBaseAdapter<String> {
    private List<String>list = new ArrayList<>();
    private Set<String> draws = new HashSet<>();
    /*0加50,1加100,2加150,*/
    private int flag=-1;
    public void reloadData(List<String> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reloadDraws(Set<String>draws) {
        this.draws.clear();
        this.draws.addAll(draws);
        notifyDataSetChanged();
    }

    public MyFilterDoubleModeBlueAdapter(List<String> list, Context context, int resId,int flag) {
        super(list, context, resId);
        this.list=list;
        this.flag=flag;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv= (TextView) holder.findView(R.id.item_filter_double_blueBall);
        TextView tv_none= (TextView) holder.findView(R.id.item_filter_double_blueBall_none);
        int val = CommonUtil.parseInt(list.get(postion));
        String str="";
        if(flag==1){
            val = val > 100?val-100:val;
        }else if(flag==2){
            val = val > 150?val-150:val;
        }else if(flag==0){
            val = val > 50?val-50:val;
        }
        //后胆没选时传100 显示无
        if(val==100){
            tv_none.setVisibility(View.VISIBLE);
            tv.setVisibility(View.GONE);
        }else{
            tv_none.setVisibility(View.GONE);
            tv.setVisibility(View.VISIBLE);
            str=CommonUtil.preZeroForBall(val);
            tv.setText(str);
        }
        tv.setBackgroundResource(draws.contains(str)?R.drawable.lottery_ball_blue:R.drawable.lottery_ball);
    }
}
