package com.cp2y.cube.activity.numlibrary.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/23.
 */
public class LibraryDetailAdapter extends BaseAdapter {
    private Context context;
    private int flag=0;
    private LayoutInflater inflater;
    private List<String> list=new ArrayList<>();
    //是否有空格的彩种 双色球 大乐透 flag
    private Set<Integer> spaceFlag=new HashSet<Integer>(){{
        add(0);
        add(1);
    }};
    public LibraryDetailAdapter(Context context,int flag) {
        this.context=context;
        this.flag=flag;
        inflater=LayoutInflater.from(context);
    }

    public void reloadData(List<String> data) {
        this.list.clear();
        this.list.addAll(data);
        notifyDataSetChanged();
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_more_five,parent,false);
        }
        String numStr = list.get(position);
        String[] numArr;
        if(spaceFlag.contains(flag)){
            numArr= numStr.split(" ");
        }else{
            numArr= numStr.split("");
        }
        LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.filter_result_ll);
        ll.removeAllViews();
        for(int i=0;i<numArr.length;i++){
            TextView tv= (TextView) LayoutInflater.from(context).inflate(R.layout.filter_result_tv,parent,false);
            tv.setText(numArr[i]);
            ll.addView(tv);
            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) tv.getLayoutParams();
            params.setMargins(0,0,10,0);
            tv.setLayoutParams(params);
        }
        return convertView;
    }
}
