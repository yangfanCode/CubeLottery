package com.cp2y.cube.fragment.chongqing2.adapter;

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
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.trend.CQ2TrendDivideRateView;
import com.cp2y.cube.widgets.trend.D3TrendDivideNumberView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by js on 2016/12/28.
 */
public class CQ2TrendDivideNumberAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /**7重庆2星**/
    private int flag=0;
    private Context context;
    private List<String> rates = new ArrayList<>();
    private List<byte[]> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public CQ2TrendDivideNumberAdapter(Context context, ConditionModel condition,int flag) {
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
        rates.clear();
        nums.clear();
        for (int i = 0; i < getCount(); i++) {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            rates.add(issueAnalyse.getTp().get(0));
            byte[] num = new byte[3];
            List<Integer> tv = issueAnalyse.getTv().get(0);
            for (int j = 0; j < num.length; j++) {
                num[j] = tv.get(j).byteValue();
            }
            nums.add(num);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_cq2_divide_number, null);
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView rate = (TextView) convertView.findViewById(R.id.trend_odd_text);
            rate.setText(issueAnalyse.getTp().get(0));
            int currNumRate, prevNumRate = -1, nextNumRate = -1;
            byte[] currNum, prevNum = null, nextNum = null;//初始化
            Map<String, Integer> map_data=MapUtils.DIVIDE_NUM_CQ2_EVEN_MAP;
            currNumRate = map_data.get(rates.get(position));
            currNum = nums.get(position);
            if (position > 0) {
                prevNumRate = map_data.get(rates.get(position - 1));
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNumRate = map_data.get(rates.get(position + 1));
                nextNum = nums.get(position + 1);
            }
            List<Integer> miss_rate = new ArrayList<>();
            for (Integer tpm:issueAnalyse.getTpm().get(0)) {
                miss_rate.add(tpm);
            }
            List<Integer> miss = new ArrayList<>();
            for (List<Integer> tvm:issueAnalyse.getTvm().get(0)) {
                miss.addAll(tvm);
            }
            ViewUtils.showViewsVisible(condition.isCalcBlue(), convertView.findViewById(R.id.trend_mark1), convertView.findViewById(R.id.trend_mark2));
            CQ2TrendDivideRateView rateView = (CQ2TrendDivideRateView) convertView.findViewById(R.id.trend_odd_rate);
            rateView.setShowMiss(condition.isShowMiss());
            rateView.setNums(currNumRate, prevNumRate, nextNumRate,miss_rate);
            D3TrendDivideNumberView trendDivideNumberView = (D3TrendDivideNumberView) convertView.findViewById(R.id.trend_divide_number);
            trendDivideNumberView.setShowMiss(condition.isShowMiss());
            trendDivideNumberView.setFlag(flag);
            trendDivideNumberView.setPatterns(currNum, prevNum, nextNum, miss);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
