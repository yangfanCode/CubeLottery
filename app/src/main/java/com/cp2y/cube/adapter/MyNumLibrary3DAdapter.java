package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/15.
 */
public class MyNumLibrary3DAdapter extends BaseAdapter {
    private List<String>list = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private int type;//类型
    private boolean isAward;//是否中奖号码
    private int locationCount;//直选定位各位区分
    private List<String>awardNums=new ArrayList<>();//开奖号
    public MyNumLibrary3DAdapter(List<String> list, Context context, List<String> awardNums,int type,boolean isAward) {
        this.list=list;
        this.context=context;
        this.awardNums=awardNums;
        this.type=type;
        this.isAward=isAward;
        inflater=LayoutInflater.from(context);
    }
    //直选定位各位区分
    public void setLocationCount(int locationCount){
        this.locationCount=locationCount;
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
            convertView= inflater.inflate(R.layout.item_filter_double_redball,parent,false);
        }
        String dataStr=list.get(position);//号码
        TextView tv= (TextView) convertView.findViewById(R.id.item_filter_double_redBall);
        tv.setBackgroundResource(R.drawable.lottery_ball_big);
        tv.setText(dataStr);
        if(type==2&&isAward){//直选定位中奖
            if(dataStr.equals(awardNums.get(locationCount))){
                tv.setBackgroundResource(R.drawable.lottery_ball_red);
            }
        }else if((type==3&&isAward)||(type==4&&isAward)||(type==6&&isAward)||(type==7&&isAward)){//组3组6中奖
            if(awardNums.contains(dataStr)){
                tv.setBackgroundResource(R.drawable.lottery_ball_red);
            }
        }
        return convertView;
    }
}
