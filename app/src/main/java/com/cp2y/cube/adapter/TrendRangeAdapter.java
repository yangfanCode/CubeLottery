package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.widgets.trend.TrendRangeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendRangeAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /*0代表双色球,1代表大乐透*/
    private int flag=-1;
    private Context context;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public TrendRangeAdapter(Context context, ConditionModel condition,int flag) {
        this.context = context;
        this.condition = condition;
        this.flag=flag;
        initNums();
    }

    @Override
    public void reloadData(List<IssueAnalyseDataModel.IssueAnalyse> data) {
        this.data.clear();
        this.data.addAll(data);
        initNums();
    }

    public void reverseData() {
        Collections.reverse(this.data);
        initNums();
    }

    @Override
    public void initNums() {
        nums.clear();
        int pos = condition.isCalcBlue() ? 1 : 0;
        for (int i = 0; i < getCount(); i++) {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            nums.add(issueAnalyse.getTv().get(pos).get(0));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            if(flag==0){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_range, null);
            }else if(flag==1){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_lotto_trend_range, null);
            }
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView range = (TextView) convertView.findViewById(R.id.trend_range_text);
            int currNum, prevNum = -1, nextNum = -1;
            currNum = nums.get(position);
            range.setText(String.valueOf(currNum));
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNum = nums.get(position + 1);
            }
            List<Integer> miss = issueAnalyse.getTvm().get(condition.isCalcBlue()?1:0).get(0);
            TrendRangeView rangeView = (TrendRangeView) convertView.findViewById(R.id.trend_range_num);
            rangeView.setNums(currNum, prevNum, nextNum, miss);
            //是否显示遗漏
            rangeView.setShowMiss(condition.isShowMiss());
            //区分大乐透双色球
            rangeView.setFlag(flag);
            showSeperateLine(convertView,position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
