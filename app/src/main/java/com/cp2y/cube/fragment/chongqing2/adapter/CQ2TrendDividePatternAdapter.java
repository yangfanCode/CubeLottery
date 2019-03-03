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
import com.cp2y.cube.widgets.trend.CQ2TrendDivideView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class CQ2TrendDividePatternAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    private Context context;
    private List<String> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public CQ2TrendDividePatternAdapter(Context context, ConditionModel condition) {
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
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            String pattern = issueAnalyse.getTs().get(0);
            nums.add(pattern);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cq2_trend_divide, null);
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
            TextView date = (TextView) convertView.findViewById(R.id.trend_date);
            date.setText(issueAnalyse.getN());
            date.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);
            TextView range = (TextView) convertView.findViewById(R.id.trend_range_text);
            int currNum, prevNum = -1, nextNum = -1;
            currNum = MapUtils.DIVIDE_CQ2_EVEN_MAP.get(nums.get(position));
            range.setText(nums.get(position));
            if (position > 0) {
                prevNum = MapUtils.DIVIDE_CQ2_EVEN_MAP.get(nums.get(position - 1));
            }
            if (position < getCount()-1) {
                nextNum = MapUtils.DIVIDE_CQ2_EVEN_MAP.get(nums.get(position + 1));
            }
            List<Integer> miss = issueAnalyse.getTsm().get(0);
            CQ2TrendDivideView divideView = (CQ2TrendDivideView) convertView.findViewById(R.id.trend_range_num);
            divideView.setNums(currNum, prevNum, nextNum, miss);
            //是否显示遗漏
            divideView.setShowMiss(condition.isShowMiss());
            showSeperateLine(convertView,position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
