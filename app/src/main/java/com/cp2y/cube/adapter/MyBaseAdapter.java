package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/6.
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    // 无法确定数据类型 故而使用泛型
    protected List<T> list = new ArrayList<>();
    private Context context;
    private int resId;//  item的id

    public MyBaseAdapter(Context context, int resId) {
        this.context = context;
        this.resId = resId;
    }

    public MyBaseAdapter(List<T> list, Context context, int resId) {
        this.list = list;
        this.context = context;
        this.resId = resId;
    }

    /**
     * 重置数据
     */
    protected void reset() {
        this.list.clear();
        notifyDataSetChanged();
    }

    /**
     * 初始化数据
     * @param data
     */
    public void initData(List<T> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
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
        // 实例化ViewHolder
        ViewHolder holder = ViewHolder.getHolder(context, resId, convertView);
        // 执行调用绑定数据的方法
        bindData(holder, position);
        // 返回值返回
        return holder.getView();
    }
    // 定义一个抽象方法      继承该类的时候需要重写 进行数据绑定
    public abstract void bindData(ViewHolder holder, int postion);
}
