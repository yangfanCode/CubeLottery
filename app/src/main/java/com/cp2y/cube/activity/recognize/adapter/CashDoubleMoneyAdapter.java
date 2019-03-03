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
public class CashDoubleMoneyAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<ScanCashNumModel.prize> data = new ArrayList<>();

    public CashDoubleMoneyAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void LoadData(List<ScanCashNumModel.prize> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data != null && data.size() > 0 ? data.size() : 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.double_scan_money_layout, parent, false);
        }
        LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.scan_money_ll);
        ll.setBackgroundColor(position % 2 == 0 ? ColorUtils.LIGHTBACK_GRAY : ColorUtils.WHITE);
        TextView tv_prizeItem = (TextView) convertView.findViewById(R.id.cashmoney_item);
        TextView tv_prize = (TextView) convertView.findViewById(R.id.cashmoney_prize);
        TextView tv_count = (TextView) convertView.findViewById(R.id.cashmoney_count);
        TextView tv_money = (TextView) convertView.findViewById(R.id.cashmoney_money);
        tv_prizeItem.setText(data.get(position).getPrizeItem());//奖项名
        tv_money.setText("-1".equals(data.get(position).getMoney())?"--":data.get(position).getMoney());//奖金
        tv_count.setText(data.get(position).getNumber());//注数
        tv_count.setTextColor("0".equals(data.get(position).getNumber())?ColorUtils.COLOR_323232:ColorUtils.NORMAL_RED);
        //-1 浮动奖 中了--  未中0
        //tv_prize.setText("-1".equals(data.get(position).getNumber())?("0".equals(data.get(position).getNumber())?"0":"--"):data.get(position).getNumber());
        tv_money.setTextColor("0".equals(data.get(position).getNumber())?ColorUtils.COLOR_323232:ColorUtils.NORMAL_RED);//注数大于0红色
        tv_prize.setText("-1".equals(data.get(position).getPrizeAmount())?"--":data.get(position).getPrizeAmount());//中奖奖金
        return convertView;
    }
}
