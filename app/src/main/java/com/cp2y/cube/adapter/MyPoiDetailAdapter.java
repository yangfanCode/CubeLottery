package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.BetModel;

import java.util.List;

/**
 * Created by admin on 2016/12/12.
 */
public class MyPoiDetailAdapter extends MyBaseAdapter<BetModel.delail> {
    public MyPoiDetailAdapter(Context context, int resId) {
        super(context, resId);
    }
    public void loadData(List<BetModel.delail> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView tv_lotteryName= (TextView) holder.findView(R.id.item_poidetail_tvLotteryName);
        TextView tv_endDate= (TextView) holder.findView(R.id.item_poidetail_tvEndDate);
        tv_lotteryName.setText(list.get(postion).getLotteryName());
        tv_endDate.setText(list.get(postion).getDrawTime());
    }
}
