package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cp2y.cube.R;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.model.LotteryProvinceModel;
import com.cp2y.cube.utils.CommonUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/3.
 */
public class CustomProvinceDetailAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private MyInterface.customCount call;
    private List<LotteryProvinceModel.LotteryDetail>list=new ArrayList<>();
    public CustomProvinceDetailAdapter(Context context){
        inflater=LayoutInflater.from(context);
    }

    private View.OnClickListener listener=(v -> {
        int lotteryId= (int) v.getTag(R.id.lotteryId);
        String lotteryName= String.valueOf( v.getTag(R.id.lotteryName));
        ImageView custom_select= (ImageView) v.getTag(R.id.custom_select);
        boolean isFull=true;
        if(call!=null) isFull= call.customCounts(lotteryId);
        if(isFull)return;//选够了6个
        if(!CustomLotteryList.contains(lotteryId)){
            custom_select.setVisibility(View.VISIBLE);
            CustomLotteryList.put(lotteryId,lotteryName);
        }else{
            custom_select.setVisibility(View.GONE);
            CustomLotteryList.delete(lotteryId);
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

    //引导图请求数据
    public void LoadData(List<LotteryProvinceModel.LotteryDetail> data ){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }


    public void setCall(MyInterface.customCount call){
        this.call=call;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_custom,parent,false);
        }
        int lotteryId=list.get(position).getLotteryID();//id
        String lotteryName=list.get(position).getLotteryName();//name
        SimpleDraweeView icon= (SimpleDraweeView) convertView.findViewById(R.id.custom_icon);
        ImageView custom_select=(ImageView) convertView.findViewById(R.id.custom_select);
        icon.setImageURI(CommonUtil.concatImgUrl(lotteryId));
        icon.setOnClickListener(listener);//监听
        icon.setTag(R.id.lotteryId,lotteryId);
        icon.setTag(R.id.lotteryName,lotteryName);
        icon.setTag(R.id.custom_select,custom_select);//选择透明图
        if(CustomLotteryList.contains(lotteryId)){//展示已选的彩种
            custom_select.setVisibility(View.VISIBLE);
        }else{
            custom_select.setVisibility(View.GONE);
        }
        return convertView;
    }
}
