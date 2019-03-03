package com.cp2y.cube.activity.recognize.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.ScanCashNumModel;
import com.cp2y.cube.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/4/7.
 */
public class CashLottoMoneyAdapter extends BaseAdapter {
    private String[] prizeItem={"一等","二等","三等","四等","五等","六等"};
    private Context context;
    private LayoutInflater inflater;
    private List<ScanCashNumModel.prize> data=new ArrayList<>();
    public CashLottoMoneyAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void LoadData(List<ScanCashNumModel.prize> list){
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return data!=null&&data.size()>0?data.size():0;
    }

    @Override
    public ScanCashNumModel.prize getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==5){//无追加
            return 1;
        }else{//有追加
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);
        if(convertView==null){
            if(type==0){
                convertView=inflater.inflate(R.layout.lotto_scan_money_addlayout,parent,false);
            }else{
                convertView=inflater.inflate(R.layout.lotto_scan_money_layout,parent,false);
            }
        }
        if(type==0){//有追加
            LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.scan_money_ll);
            ll.setBackgroundColor(position%2==0? ColorUtils.LIGHTBACK_GRAY:ColorUtils.WHITE);
            TextView tv_prizeItem= (TextView) convertView.findViewById(R.id.cashmoney_prizeItem);
            TextView tv_prize= (TextView) convertView.findViewById(R.id.cashmoney_nomal_prize);
            TextView tv_prizeAward= (TextView) convertView.findViewById(R.id.cashmoney_add_prize);
            TextView tv_count= (TextView) convertView.findViewById(R.id.cashmoney_nomal_count);
            TextView tv_countAward= (TextView) convertView.findViewById(R.id.cashmoney_add_count);
            TextView tv_money= (TextView) convertView.findViewById(R.id.cashmoney_nomal_money);
            TextView tv_moneyAward= (TextView) convertView.findViewById(R.id.cashmoney_add_money);
            tv_prizeItem.setText(prizeItem[position]);//奖项名
            tv_prize.setText(!"0".equals(data.get(position).getNumber())?("--".equals(data.get(position).getMoney())?"--":data.get(position).getPrizeAmount()):data.get(position).getPrizeAmount());//中奖奖金
            tv_prizeAward.setText(!"0".equals(data.get(position).getNumber())?("--".equals(data.get(position).getMoney())?"--":data.get(position).getPrizeAmountAward()):data.get(position).getPrizeAmountAward());//追加中奖奖金
            tv_count.setText(data.get(position).getNumber());//注数
            tv_countAward.setText(data.get(position).getNumberAward());//追加注数
            tv_money.setText(data.get(position).getMoney());//奖金
            tv_moneyAward.setText(data.get(position).getMoneyAward());//追加奖金
        }else{//无追加
            TextView tv_prizeItem= (TextView) convertView.findViewById(R.id.cashmoney_prizeItem);
            TextView tv_prize= (TextView) convertView.findViewById(R.id.cashmoney_prize);
            TextView tv_count= (TextView) convertView.findViewById(R.id.cashmoney_count);
            TextView tv_money= (TextView) convertView.findViewById(R.id.cashmoney_money);
            tv_prizeItem.setText(prizeItem[position]);//奖项名
            tv_prize.setText(!"0".equals(data.get(position).getNumber())?("--".equals(data.get(position).getMoney())?"--":data.get(position).getPrizeAmount()):data.get(position).getPrizeAmount());//中奖奖金
            tv_count.setText(data.get(position).getNumber());//注数
            tv_money.setText(data.get(position).getMoney());//奖金
        }
        return convertView;
    }
}
