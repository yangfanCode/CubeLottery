package com.cp2y.cube.activity.multicalc.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.MultiCalcResultModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class MultiCalcResultAdapter extends BaseAdapter {
    private List<MultiCalcResultModel>list=new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;

    public MultiCalcResultAdapter(Context context) {
        this.context = context;
        inflater=LayoutInflater.from(context);
    }

    public void loadData(List<MultiCalcResultModel>data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public MultiCalcResultModel getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_multi_calc_result,parent,false);
        }
        TextView tvDate= (TextView) convertView.findViewById(R.id.item_multi_calc_tvDate);//期数
        TextView tvMiti= (TextView) convertView.findViewById(R.id.item_multi_calc_tvMulti);//倍数
        TextView tvPeriodNow= (TextView) convertView.findViewById(R.id.item_multi_calc_tvPeriodNow);//本期投入
        TextView tvPeriodTotal= (TextView) convertView.findViewById(R.id.item_multi_calc_tvPeriodTotal);//累计投入
        TextView tvIncome= (TextView) convertView.findViewById(R.id.item_multi_calc_tvIncome);//累计收益
        TextView tvIncomeRate= (TextView) convertView.findViewById(R.id.item_multi_calc_tvIncomeRate);//期收益率
        MultiCalcResultModel model=list.get(position);
        tvDate.setText(String.valueOf(position+1));
        tvMiti.setText(String.valueOf(model.multi));
        tvPeriodNow.setText(String.valueOf(model.periodNow));
        tvPeriodTotal.setText(String.valueOf(model.periodTotal));
        tvIncome.setText(String.valueOf(model.income));
        tvIncomeRate.setText(model.incomeRate+"%");
        return convertView;
    }
}
