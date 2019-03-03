package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.PersonalActivity;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.model.RankModel;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/2/28.
 */

public class RankAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private MyInterface.SubscribePersonal subscribePersonal;
    private List<RankModel.Detail> list = new ArrayList<>();
    private int[] imgs={R.mipmap.bd_four,R.mipmap.bd_five,R.mipmap.bd_six,R.mipmap.bd_seven,R.mipmap.bd_eight,
            R.mipmap.bd_nine,R.mipmap.bd_ten};
    private View.OnClickListener listener=(v -> {
        int position= (int) v.getTag(R.id.position);
        int type=list.get(position).subscribeType;
        subscribePersonal.subscribePersonal(position,type);
    });
    private View.OnClickListener iconOnClick=v -> {
        int userId= (int) v.getTag(R.id.id);
        Intent intent=new Intent(context, PersonalActivity.class);
        intent.putExtra("otherUserId",userId);
        context.startActivity(intent);
    };
    public RankAdapter(Context context,List<RankModel.Detail> data) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        list.clear();
        list.addAll(data);
    }
    public void setSubscribePersonal(MyInterface.SubscribePersonal subscribePersonal){
        this.subscribePersonal=subscribePersonal;
    }
    public void loadData(List<RankModel.Detail> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reLoadData(List<RankModel.Detail> data){
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public RankModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_rank,parent,false);
        }
        RankModel.Detail detail=list.get(position);
        TextView tvName= (TextView) convertView.findViewById(R.id.item_rank_tvName);//名字
        ImageView ivRank= (ImageView) convertView.findViewById(R.id.item_rank_ivRank);//排名icon
        SimpleDraweeView ivIcon= (SimpleDraweeView) convertView.findViewById(R.id.item_rank_ivIcon);//头像
        TextView tvRate= (TextView) convertView.findViewById(R.id.item_rank_tvRate);//回报率
        TextView tvAttention= (TextView) convertView.findViewById(R.id.item_rank_tvAttention);//关注状态
        tvName.setText(detail.nickName);
        ivIcon.setImageURI(detail.headUrl);
        ivIcon.setOnClickListener(iconOnClick);
        ivIcon.setTag(R.id.id,detail.userId);
        tvName.setOnClickListener(iconOnClick);
        tvName.setTag(R.id.id,detail.userId);
        tvRate.setText("回报率: "+detail.returnRate);
        ivRank.setImageResource(imgs[position]);//排名
        tvAttention.setBackgroundResource(detail.subscribeType==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
        tvAttention.setVisibility(LoginSPUtils.getInt("id", 0)==detail.userId?View.GONE:View.VISIBLE);//隐藏自己
        tvAttention.setOnClickListener(listener);
        tvAttention.setTag(R.id.position,position);
        return convertView;
    }

    public List<RankModel.Detail> getList(){
        return list;
    }
}
