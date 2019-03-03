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
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.trend.TrendOddNumView;
import com.cp2y.cube.widgets.trend.TrendOddRateView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendOddNumberAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    private int flag=-1;
    private Context context;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public TrendOddNumberAdapter(Context context, ConditionModel condition,int flag) {
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
        Collections.reverse(data);
        initNums();
    }

    @Override
    public void initNums() {
        nums.clear();
        int pos = condition.isCalcBlue()?1:0;
        for (int i = 0; i < getCount(); i++) {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            List<Integer> tv = issueAnalyse.getTv().get(pos);
            nums.add(tv.get(1));
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
        /*0,双色球,1,大乐透,4排列5*/
        if (convertView == null) {
            if(flag==0){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_odd_number, null);
            }else if(flag==1){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_lotto_odd_number, null);
            }else if(flag==4){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_p5_odd_number, null);
            }
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView rate = (TextView) convertView.findViewById(R.id.trend_odd_text);
            int pos = condition.isCalcBlue()?1:0;
            rate.setText(issueAnalyse.getTp().get(pos));
            int currNum, prevNum = -1, nextNum = -1;
            currNum = nums.get(position);
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNum = nums.get(position + 1);
            }
            List<Integer> miss_rate = new ArrayList<>();//奇偶个数比遗漏
            for (Integer tpm:issueAnalyse.getTpm().get(pos)) {
                miss_rate.add(tpm);
            }
            List<Integer> miss = new ArrayList<>();//奇偶遗漏
            for (List<Integer> tvm:issueAnalyse.getTvm().get(pos)) {
                miss.addAll(tvm);
            }
            ViewUtils.showViewsVisible(condition.isCalcBlue(), convertView.findViewById(R.id.trend_mark1), convertView.findViewById(R.id.trend_mark2));
            TrendOddRateView rateView = (TrendOddRateView) convertView.findViewById(R.id.trend_odd_rate);
            rateView.setFlag(flag);
            rateView.setCalcBlue(condition.isCalcBlue());
            rateView.setShowMiss(condition.isShowMiss());
            rateView.setNums(currNum, prevNum, nextNum,miss_rate);
            TrendOddNumView numView = (TrendOddNumView) convertView.findViewById(R.id.trend_odd_num);
            numView.setFlag(flag);
            numView.setCalcBlue(condition.isCalcBlue());
            numView.setShowMiss(condition.isShowMiss());
            numView.setNums(currNum, prevNum, nextNum, miss);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
