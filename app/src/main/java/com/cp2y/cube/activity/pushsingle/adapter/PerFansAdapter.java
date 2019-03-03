package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.model.AttentionFansListModel;
import com.cp2y.cube.model.MessageEvent;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/1/9.
 */
public class PerFansAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private MyInterface.SubscribePersonal subscribePersonal;
    private List<AttentionFansListModel.Detail>list=new ArrayList<>();
    private View.OnClickListener listener=(v -> {
        int position= (int) v.getTag(R.id.position);
        int type=list.get(position).getSubscribeType();
        subscribePersonal.subscribePersonal(position,type);
    });
    private View.OnClickListener onClickListener=v -> {
        int position= (int) v.getTag(R.id.position);
        int userId=list.get(position).getUserId();
        EventBus.getDefault().post(new MessageEvent(MessageEvent.PERSONAL_OPENACT,userId));

    };
    public PerFansAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }

    public void setSubscribePersonal(MyInterface.SubscribePersonal subscribePersonal){
        this.subscribePersonal=subscribePersonal;
    }

    public void loadData(List<AttentionFansListModel.Detail>data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reloadData(List<AttentionFansListModel.Detail>data){
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public AttentionFansListModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.attention_fans_item,parent,false);
        }
        AttentionFansListModel.Detail detail=list.get(position);
        TextView tv_name= (TextView) convertView.findViewById(R.id.atten_fans_name);
        TextView tv_message= (TextView) convertView.findViewById(R.id.atten_fans_message);
        SimpleDraweeView icon= (SimpleDraweeView) convertView.findViewById(R.id.atten_fans_icon);
        ImageView button= (ImageView) convertView.findViewById(R.id.attention_fans_button);
        tv_name.setText(detail.getNickName());//名字
        tv_name.setOnClickListener(onClickListener);
        tv_name.setTag(R.id.position,position);
        icon.setImageURI(detail.getHeadUrl());//头像
        icon.setOnClickListener(onClickListener);
        icon.setTag(R.id.position,position);
        tv_message.setText(detail.getInterest());//擅长
        button.setImageResource(detail.getSubscribeType()==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);//关注状态
        button.setVisibility(detail.getUserId()== LoginSPUtils.getInt("id", 0)? View.GONE:View.VISIBLE);//自己隐藏
        button.setOnClickListener(listener);
        button.setTag(R.id.position,position);
        return convertView;
    }
    public List<AttentionFansListModel.Detail> getList(){
        return list;
    }
}
