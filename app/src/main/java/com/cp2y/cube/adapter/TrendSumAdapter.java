package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.widgets.trend.TrendSumView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendSumAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueNumberDataModel.IssueNumber> {
    /*0双色球,1大乐透,4排列5*/
    private int flag=-1;
    private Context context;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueNumberDataModel.IssueNumber> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public TrendSumAdapter(Context context, ConditionModel condition,int flag) {
        this.context = context;
        this.condition = condition;
        this.flag=flag;
        initNums();
    }

    @Override
    public void reloadData(List<IssueNumberDataModel.IssueNumber> data) {
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
        int pos = condition.isCalcBlue()?1:0;
        for (int i = 0; i < getCount(); i++) {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(i);
            nums.add(Integer.valueOf(issueNumber.getTs().get(0).get(pos)));
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
            if(flag==4){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_p5_trend_sum, null);
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_sum, null);
            }
        }
        try {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueNumber.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView sum = (TextView) convertView.findViewById(R.id.trend_sum_text);
            int currNum, prevNum = -1, nextNum = -1;
            currNum = nums.get(position);
            sum.setText(String.valueOf(currNum));
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNum = nums.get(position + 1);
            }
            TrendSumView sumView = (TrendSumView) convertView.findViewById(R.id.trend_sum_percent);
            sumView.setFlag(flag);
            sumView.setCalcBlue(condition.isCalcBlue());
            sumView.setNums(currNum, prevNum, nextNum);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
