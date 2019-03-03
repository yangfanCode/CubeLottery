package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.RulesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/21.
 */
public class RulesAdapter extends BaseAdapter {
    private List<RulesModel.detail>list=new ArrayList<>();
    private LayoutInflater inflater;
    public RulesAdapter(Context context){
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

    public void loadData(List<RulesModel.detail>data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.rules_item,parent,false);
        }
        TextView level= (TextView) convertView.findViewById(R.id.rules_awardLevel);
        TextView explain= (TextView) convertView.findViewById(R.id.rules_explain);
        TextView bonus= (TextView) convertView.findViewById(R.id.rules_bonus);
        level.setText(list.get(position).getAwardLevel());//奖级
        explain.setText(list.get(position).getExplain());//说明
        bonus.setText(list.get(position).getBonus());//奖金
        return convertView;
    }
}
