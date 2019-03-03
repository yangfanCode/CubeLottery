package com.cp2y.cube.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.activity.selectnums.Select3DNumActivity;
import com.cp2y.cube.activity.selectnums.SelectCQ2XingNumActivity;
import com.cp2y.cube.activity.selectnums.SelectCQ3XingNumActivity;
import com.cp2y.cube.activity.selectnums.SelectCQ5NumActivity;
import com.cp2y.cube.activity.selectnums.SelectNumFilterActivity;
import com.cp2y.cube.activity.selectnums.SelectP3NumActivity;
import com.cp2y.cube.activity.selectnums.SelectP5NumActivity;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/15.
 */
public class FilterCustomAdapter extends BaseAdapter {
    private Context context;
    private List<CustomModel.Detail> list=new ArrayList<>();
    private LayoutInflater inflater;
    public FilterCustomAdapter(Context context){
        inflater=LayoutInflater.from(context);
        this.context=context;
    }
    private View.OnClickListener listener=(v -> {
        int lotteryId= (int) v.getTag(R.id.lotteryId);
        if(MapUtils.FILTER_DEVELOP.contains(lotteryId)){
            Intent intent=new Intent();
            if(lotteryId==10002){
                intent.setClass(context, SelectNumFilterActivity.class);
            }else if(lotteryId==10088){
                intent.setClass(context, LottoSelectNumActivity.class);
            }else if(lotteryId==10001){
                intent.setClass(context, Select3DNumActivity.class);
            }else if(lotteryId==10003){
                intent.setClass(context, SelectP3NumActivity.class);
            }else if(lotteryId==10004){
                intent.setClass(context, SelectP5NumActivity.class);
            }else if(lotteryId==10093){
                intent.setClass(context, SelectCQ3XingNumActivity.class);
            }else if(lotteryId==10089){
                intent.setClass(context, SelectCQ5NumActivity.class);
            }else if(lotteryId==10095){
                intent.setClass(context, SelectCQ2XingNumActivity.class);
            }
            context.startActivity(intent);
        }
    });
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

    public void loadData(List<CustomModel.Detail> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.filter_custom_layout,parent,false);
        }
        SimpleDraweeView icon= (SimpleDraweeView) convertView.findViewById(R.id.filter_custom_icon);
        TextView name= (TextView) convertView.findViewById(R.id.filter_custom_name);
        int lotteryId=list.get(position).getLotteryID();
        icon.setImageURI(CommonUtil.concatImgUrl(lotteryId));
        boolean isDevelop=MapUtils.FILTER_DEVELOP.contains(lotteryId);
        String lotteryName= isDevelop?list.get(position).getLotteryName():"敬请期待";
        name.setText(lotteryName);
        name.setTextColor(isDevelop? ColorUtils.GRAY_LEFT:ColorUtils.GRAY_LIGHT);
        icon.setTag(R.id.lotteryId,lotteryId);
        icon.setOnClickListener(listener);
        return convertView;
    }
}
