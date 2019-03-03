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
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.trend.D3TrendOddNumView;
import com.cp2y.cube.widgets.trend.D3TrendOddRateView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendOddNumberAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /**2福彩3D,3排列3,5重庆3星,7重庆2星**/
    private int flag=0;
    private Context context;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public D3TrendOddNumberAdapter(Context context, ConditionModel condition,int flag) {
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
        for (int i = 0; i < getCount(); i++) {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            List<Integer> tv = issueAnalyse.getTv().get(0);
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
        if (convertView == null) {
            if(flag==7){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_cq2_odd_number, null);
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_d3_odd_number, null);
            }
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            int colorId=check3DNumTypeColor(issueAnalyse.getN());//号码颜色
            String text=issueAnalyse.getN().replace(" ","").trim();
            if(text.length()==3){//字体颜色
                date.setTextColor(colorId==0? ColorUtils.NORMAL_GRAY:(colorId==2?ColorUtils.MID_BLUE:ColorUtils.NORMAL_RED));
            }
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView rate = (TextView) convertView.findViewById(R.id.trend_odd_text);
            rate.setText(issueAnalyse.getTp().get(0));
            int currNum, prevNum = -1, nextNum = -1;
            currNum = nums.get(position);
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
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
            D3TrendOddRateView rateView = (D3TrendOddRateView) convertView.findViewById(R.id.trend_odd_rate);
            rateView.setShowMiss(condition.isShowMiss());
            rateView.setFlag(flag);
            rateView.setNums(currNum, prevNum, nextNum,miss_rate);
            D3TrendOddNumView numView = (D3TrendOddNumView) convertView.findViewById(R.id.trend_odd_num);
            numView.setShowMiss(condition.isShowMiss());
            numView.setFlag(flag);
            numView.setNums(currNum, prevNum, nextNum, miss);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
