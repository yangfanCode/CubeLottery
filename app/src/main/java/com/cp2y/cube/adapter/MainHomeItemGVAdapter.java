package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.MainHomeModel;

import java.util.List;

/**
 * Created by yangfan on 2018/1/4.
 */
public class MainHomeItemGVAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<MainHomeModel.Item>list;
    public MainHomeItemGVAdapter(Context context,List<MainHomeModel.Item>list){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.list=list;
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public MainHomeModel.Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.main_home_detail_item,parent,false);
        }
        TextView type= (TextView) convertView.findViewById(R.id.main_home_detail_item_type);
        TextView nums= (TextView) convertView.findViewById(R.id.main_home_detail_item_nums);
        View divide=convertView.findViewById(R.id.main_home_item_divide);
        type.setText(list.get(position).getName());
        nums.setText(list.get(position).getNote()+"注");
        divide.setVisibility(setDivideVisible(position)?View.VISIBLE:View.GONE);//分割线显示隐藏
        return convertView;
    }
    //处理注数字符串
    private String noteStr(int note){
        if(note>999999)return "999999+";
        return String.valueOf(note);
    }
    //显示隐藏分割线
    private boolean setDivideVisible(int position){
        if(position==list.size()-1)return false;
        return position%2==0;
    }
}
