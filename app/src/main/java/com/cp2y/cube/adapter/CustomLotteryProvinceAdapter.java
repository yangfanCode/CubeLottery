package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.CustomLotteryProvinceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/7.
 */
public class CustomLotteryProvinceAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<CustomLotteryProvinceModel.LotteryDetail> list=new ArrayList<>();
    public CustomLotteryProvinceAdapter(Context context){
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void loadData(List<CustomLotteryProvinceModel.LotteryDetail> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.custom_province,parent,false);
        }
        TextView province= (TextView) convertView.findViewById(R.id.custom_lottery_Province_tv);
        province.setText(list.get(position).getProvince());
        return convertView;
    }
}
