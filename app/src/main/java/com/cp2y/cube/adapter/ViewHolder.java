package com.cp2y.cube.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;


public class ViewHolder {

    View convertView;
    Map<Integer, View> map = new HashMap<>();
    SparseArray<View> array = new SparseArray<>();

    public ViewHolder(Context context, int resId) {
        convertView = LayoutInflater.from(context).inflate(resId, null);
        convertView.setTag(this);
    }
    public View getView() {
        return convertView;
    }

    //   返回当前的ViewHolder对象
    public static ViewHolder getHolder(Context context, int resId, View view) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder(context, resId);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        return holder;
    }

    //查找Id　的过程
    public View findView(int id) {
        View view = array.get(id);
        if (view == null) {
            view = convertView.findViewById(id);
            array.append(id, view);
        }
        return view;
    }
}