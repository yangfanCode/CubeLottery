package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.PushListModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/3/6.
 */

public class PushSetAdpater extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private MyInterface.PushSetClick pushSetClick;
    private List<PushListModel.Detail> list=new ArrayList<>();

    public PushSetAdpater(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void loadData(List<PushListModel.Detail> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }
    public void setPushSetClick(MyInterface.PushSetClick pushSetClick){
        this.pushSetClick=pushSetClick;
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public PushListModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_pushset,parent,false);
        }
        PushListModel.Detail detail=list.get(position);
        SimpleDraweeView icIcon= (SimpleDraweeView) convertView.findViewById(R.id.pushset_ivIcon);
        TextView tvName= (TextView) convertView.findViewById(R.id.pushset_ivName);
        TextView tvMessage= (TextView) convertView.findViewById(R.id.pushset_ivMessage);
        icIcon.setImageURI(CommonUtil.concatImgUrl(detail.lotteryId));
        tvName.setText(detail.lotteryName);
        tvMessage.setText(detail.lotteryDetail);
        SwitchButton switchButton= (SwitchButton) convertView.findViewById(R.id.pushset_switch);
        switchButton.setChecked(!(detail.type==0));
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!CommonUtil.isNetworkConnected(context)){
                    TipsToast.showTips(context.getString(R.string.netOff));
                    switchButton.setChecked(!isChecked);
                }else{
                    pushSetClick.pushSetClick(position,isChecked);
                }
            }
        });
        return convertView;
    }
}
