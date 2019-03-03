package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.bean.WelfareLottery;

import java.util.List;

/**
 * Created by admin on 2016/12/2.
 */
public class MyFilterBaseAdapter extends BaseAdapter {
    private List<WelfareLottery>list;
    private LayoutInflater inflater;

    public MyFilterBaseAdapter(List<WelfareLottery> list, Context context) {
        inflater= LayoutInflater.from(context);
        this.list = list;
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
        return null;
    }
    class ViewHouder{
        TextView tv;
        ImageView iv;
    }
}
