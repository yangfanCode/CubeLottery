package com.cp2y.cube.fragment.doubleball.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.TrendAdapter;
import com.cp2y.cube.adapter.TrendSimpleAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.widgets.trend.D3TrendRangeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendSumMantissaAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    private Context context;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public TrendSumMantissaAdapter(Context context, ConditionModel condition) {
        this.context = context;
        this.condition = condition;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_summantissa, null);
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
            List<Integer> miss = issueAnalyse.getTvm().get(0).get(0);
            D3TrendRangeView rangeView = (D3TrendRangeView) convertView.findViewById(R.id.trend_range_num);
            rangeView.setNums(currNum, prevNum, nextNum, miss);
            //是否显示遗漏
            rangeView.setShowMiss(condition.isShowMiss());
            showSeperateLine(convertView,position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
