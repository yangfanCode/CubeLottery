package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.LotterySummaryModel;

import java.util.List;

/**
 * Created by admin on 2016/12/9.
 */
public class MyOpenLotterySummaryAdapter extends BaseAdapter{
    private List<LotterySummaryModel.SummaryLottery.Summaryitems> list;
    private Context context;

    public MyOpenLotterySummaryAdapter(List<LotterySummaryModel.SummaryLottery.Summaryitems> list, Context context) {
        this.list = list;
        this.context = context;
    }


    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_awards_summary,parent,false);
        TextView tv_awards= (TextView) view.findViewById(R.id.item_awards_type);
        TextView tv_money= (TextView) view.findViewById(R.id.item_awards_money);
        TextView tv_count= (TextView) view.findViewById(R.id.item_awards_count);
        tv_awards.setText(list.get(position).getPrizeItem());
        tv_money.setText(Integer.parseInt(list.get(position).getPrizeAmount())==1?"- -":list.get(position).getPrizeAmount()+"元");
        tv_count.setText(Integer.parseInt(list.get(position).getPrizeAmount())==1?"- -":list.get(position).getNumber()+"注");
        return view;
    }
}
