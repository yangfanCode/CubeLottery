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
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.trend.D3TrendDividePatternView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendDividePatternAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    private Context context;
    private List<byte[]> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public D3TrendDividePatternAdapter(Context context, ConditionModel condition) {
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

    public void initNums() {
        nums.clear();
        for (int i = 0; i < getCount(); i++) {
            byte[] num = new byte[3];
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            String pattern = issueAnalyse.getTs().get(0);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_d3_trend_divide_pattern, null);
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView patternText = (TextView) convertView.findViewById(R.id.trend_divide_pattern_text);
            patternText.setText(CommonUtil.join(issueAnalyse.getTs().get(0).split(""), " "));
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            int colorId=check3DNumTypeColor(issueAnalyse.getN());
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
            D3TrendDividePatternView trendDividePatternView = (D3TrendDividePatternView) convertView.findViewById(R.id.trend_divide_pattern);
            trendDividePatternView.setPatterns(currNum, prevNum, nextNum);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
