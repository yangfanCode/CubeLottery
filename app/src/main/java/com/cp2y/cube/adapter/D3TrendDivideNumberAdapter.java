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
import com.cp2y.cube.widgets.trend.D3TrendDivideNumberView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendDivideNumberAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /**2福彩3D,3排列3,5重庆3星,7重庆2星**/
    private int flag=0;
    private Context context;
    private List<byte[]> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public D3TrendDivideNumberAdapter(Context context, ConditionModel condition,int flag) {
        this.context = context;
        this.condition = condition;
        this.flag=flag;
        initNums();
    }

    public void initNums() {
        nums.clear();
        for (int i = 0; i < getCount(); i++) {
            byte[] num = new byte[3];
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            List<Integer> tv = issueAnalyse.getTv().get(0);
            for (int j = 0; j < num.length; j++) {
                num[j] = tv.get(j).byteValue();
            }
            nums.add(num);
        }
        notifyDataSetChanged();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_d3_trend_divide_number, null);
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView numberText = (TextView) convertView.findViewById(R.id.trend_divide_number_text);
            numberText.setText(issueAnalyse.getTp().get(0));
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());int colorId=check3DNumTypeColor(issueAnalyse.getN());
            date.setTextColor(colorId==0? ColorUtils.NORMAL_GRAY:(colorId==2?ColorUtils.MID_BLUE:ColorUtils.NORMAL_RED));

            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            byte[] currNum, prevNum = null, nextNum = null;
            currNum = nums.get(position);
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNum = nums.get(position + 1);
            }
            List<Integer> miss = new ArrayList<>();
            for (List<Integer> tvm:issueAnalyse.getTvm().get(0)) {
                miss.addAll(tvm);
            }
            //ViewUtils.showViewsVisible(condition.isCalcBlue(), convertView.findViewById(R.id.trend_mark1), convertView.findViewById(R.id.trend_mark2));
            D3TrendDivideNumberView trendDivideNumberView = (D3TrendDivideNumberView) convertView.findViewById(R.id.trend_divide_number);
            trendDivideNumberView.setShowMiss(condition.isShowMiss());
            trendDivideNumberView.setFlag(flag);
            trendDivideNumberView.setPatterns(currNum, prevNum, nextNum, miss);
            showSeperateLine(convertView, position);//显示分割线
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }

}
