package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.utils.CommonUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/7.
 */
public class MyLeftMenuGridViewAdapter extends BaseAdapter {
    private List<CustomModel.Detail> list = new ArrayList<>();
    private LayoutInflater inflater;

    public MyLeftMenuGridViewAdapter(Context context){
        inflater=LayoutInflater.from(context);
    }
    public void loadData(List<CustomModel.Detail> data) {
        list.clear();
        list.addAll(data);
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
            convertView=inflater.inflate(R.layout.item_app_left_gv,parent,false);
        }
        SimpleDraweeView iv_lotteryType = (SimpleDraweeView) convertView.findViewById(R.id.item_left_lottery_type_iv);
        TextView tv_lotteryType = (TextView) convertView.findViewById(R.id.item_left_lottery_type_tv);
        int lotteryId=list.get(position).getLotteryID();
        if (lotteryId==10000) {
            iv_lotteryType.setImageResource(R.mipmap.sidebar_icon_more);
            tv_lotteryType.setText("更多");
        } else {
            iv_lotteryType.setImageURI(CommonUtil.concatImgUrl(Integer.valueOf(lotteryId)));
            tv_lotteryType.setText(list.get(position).getLotteryName());
        }
        return convertView;
    }
}
