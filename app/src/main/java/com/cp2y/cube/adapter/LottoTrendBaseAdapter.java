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
import com.cp2y.cube.widgets.trend.LottoTrendBaseView;
import com.cp2y.cube.widgets.trend.LottoTrendSpecialView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class LottoTrendBaseAdapter extends TrendSimpleAdapter {

    private Context context;
    private List<IssueNumberDataModel.IssueNumber> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public void reloadData(List<IssueNumberDataModel.IssueNumber> data) {
        this.data.clear();
        this.data.addAll(data);
        initBlues();
        notifyDataSetChanged();
    }

    public void reverseData() {
        Collections.reverse(this.data);
        initBlues();
        notifyDataSetChanged();
    }

    private void initBlues() {
        blueNums.clear();
        for (int i = 0; i < getCount(); i++) {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(i);
            blueNums.add(issueNumber.getSn().get(0));
        }
    }

    private List<Integer> blueNums = new ArrayList<>();

    public LottoTrendBaseAdapter(Context context, ConditionModel condition) {
        this.context = context;
        this.condition = condition;
        initBlues();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_lotto_trend_base, null);
        }
        try {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(position);
            //开奖号
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueNumber.getN());
            //是否展示开奖号
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView sum = (TextView) convertView.findViewById(R.id.trend_sum);
            sum.setText(issueNumber.getTs().get(0).get(0));
            //三分区
            int first_area=0;
            int second_area=0;
            for(Integer getBn:issueNumber.getBn()){
                if(getBn>0&&getBn<=12){
                    first_area++;
                }else if(getBn>11&&getBn<=23){
                    second_area++;
                }
            }
            TextView three_area= (TextView) convertView.findViewById(R.id.trend_three_area);
            three_area.setText(first_area+":"+second_area+":"+(5-first_area-second_area));
            //遗漏
            List<Integer> redMiss = issueNumber.getMs().get(0);
            List<Integer> blueMiss = issueNumber.getMs().get(1);
            //上一个，当前，下一个
//        int currBlue, prevBlue = -1, nextBlue = -1;
//        currBlue = blueNums.get(position);
            List<Integer> currReds = issueNumber.getBn(), prevReds = null, nextReds = null, prevReds2 = null, nextReds2 = null;
            List<Integer> currBlues = issueNumber.getSn(), prevBlues = null, nextBlues = null;
            if (position > 0) {
                prevBlues = data.get(position - 1).getSn();
                prevReds = data.get(position - 1).getBn();
            }
            if (position > 1) {
                prevReds2 = data.get(position - 2).getBn();
            }
            if (position < getCount()-1) {
                nextBlues = data.get(position + 1).getSn();
                nextReds = data.get(position + 1).getBn();
            }
            if (position < getCount()-2) {
                nextReds2 = data.get(position + 2).getBn();
            }
            LottoTrendBaseView reds = (LottoTrendBaseView) convertView.findViewById(R.id.basic_balls);
            reds.setShowNum(condition.isShowMutiNum(), condition.isShowConnectNum(), condition.isShowEdgeNum(), condition.isShowSerialNum());
            reds.setData(currReds, prevReds, nextReds, prevReds2, nextReds2, redMiss);
            LottoTrendSpecialView blues = (LottoTrendSpecialView) convertView.findViewById(R.id.special_balls);
            blues.setData(currBlues, prevBlues, nextBlues, blueMiss);
            //虚线
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
