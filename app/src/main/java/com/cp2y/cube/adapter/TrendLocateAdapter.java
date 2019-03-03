package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.trend.TrendLocateView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendLocateAdapter extends TrendSimpleAdapter {

    private Context context;
    /**红球蓝球类型0到5表示红球，6表示蓝球**/
    private int type = 0;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueNumberDataModel.IssueNumber> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();
    private Range numRange = new Range(0,0);

    public Range getNumRange() {
        return numRange;
    }

    public void reloadData(List<IssueNumberDataModel.IssueNumber> data) {
        this.data.clear();
        this.data.addAll(data);
        initNums();
    }

    public void reverseData() {
        Collections.reverse(this.data);
        initNums();
    }

    public void reloadType(int type) {
        this.type = type;
        initNums();
    }

    public void initNums() {
        nums.clear();
        for (int i = 0; i < getCount(); i++) {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(i);
            if (type == 6) {
                nums.add(issueNumber.getSn().get(0));
            } else {
                nums.add(issueNumber.getBn().get(type));
            }
        }
        numRange = CommonUtil.calcRangeFromList(nums);
        notifyDataSetChanged();
    }

    public TrendLocateAdapter(Context context, ConditionModel condition) {
        this.context = context;
        this.condition = condition;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_locate, null);
        }
        IssueNumberDataModel.IssueNumber issueNumber = data.get(position);
        List<Integer> miss = issueNumber.getMs().get(type);
        int currNum, prevNum = -1, nextNum = -1;
        currNum = nums.get(position);
        if (position > 0) {
            prevNum = nums.get(position - 1);
        }
        if (position < getCount()-1) {
            nextNum = nums.get(position + 1);
        }
        TrendLocateView locateView = (TrendLocateView) convertView.findViewById(R.id.trend_locate);
        ViewGroup.LayoutParams lp = locateView.getLayoutParams();
        lp.width = DisplayUtil.dip2px(30f*(numRange.end - numRange.start+1));
        locateView.setLayoutParams(lp);
        locateView.setShowMiss(condition.isShowMiss());
        locateView.setNums(currNum, prevNum, nextNum, miss);
        locateView.setNumRange(numRange);
        locateView.setRed(type != 6);
        locateView.invalidate();
        showSeperateLine(convertView, position);
        return convertView;
    }
}
