package com.cp2y.cube.adapter;

import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.trend.TrendDividePatternView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendDividePatternAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /*0是双色球,1是大乐透,4排列5*/
    private int flag=-1;
    private Context context;
    private List<byte[]> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();
    private SparseIntArray ballCount=new SparseIntArray(){{put(0,7);put(1,7);put(4,5);}};

    public TrendDividePatternAdapter(Context context, ConditionModel condition,int flag) {
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

    public void initNums() {
        nums.clear();
        int index = condition.isCalcBlue()?1:0;
        for (int i = 0; i < getCount(); i++) {
            byte[] num = new byte[ballCount.get(flag)];
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            String pattern = issueAnalyse.getTs().get(index);
            for (int j = 0; j < num.length; j++) {
                num[j] = Byte.valueOf(pattern.substring(j,j+1));
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
            if(flag==4){//排列5
                convertView = LayoutInflater.from(context).inflate(R.layout.item_p5_trend_divide_pattern, null);
            }else{//双色球,大乐透
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_divide_pattern, null);
            }
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView patternText = (TextView) convertView.findViewById(R.id.trend_divide_pattern_text);
            patternText.setText(CommonUtil.join(issueAnalyse.getTs().get(condition.isCalcBlue()?1:0).split(""), " "));
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            byte[] currNum, prevNum = null, nextNum = null;
            currNum = nums.get(position);
            if (position > 0) {
                prevNum = nums.get(position - 1);
            }
            if (position < getCount()-1) {
                nextNum = nums.get(position + 1);
            }
            TrendDividePatternView trendDividePatternView = (TrendDividePatternView) convertView.findViewById(R.id.trend_divide_pattern);
            trendDividePatternView.setFlag(flag);
            trendDividePatternView.setCalcBlue(condition.isCalcBlue());
            trendDividePatternView.setPatterns(currNum, prevNum, nextNum);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
