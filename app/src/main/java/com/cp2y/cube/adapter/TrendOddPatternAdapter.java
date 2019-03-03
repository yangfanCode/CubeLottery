package com.cp2y.cube.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.trend.TrendOddPatternView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class TrendOddPatternAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    private int flag=-1;
    private Context context;
    private List<byte[]> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();
    private SparseIntArray calcBlueLength=new SparseIntArray(){{put(0,7);put(1,7);put(4,5);}};//含篮球个数
    private SparseIntArray noCalcBlueLength=new SparseIntArray(){{put(0,6);put(1,5);put(4,5);}};//不含篮球个数

    public TrendOddPatternAdapter(Context context, ConditionModel condition,int flag) {
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

    @Override
    public void initNums() {
        nums.clear();
        int length= condition.isCalcBlue()?calcBlueLength.get(flag):noCalcBlueLength.get(flag);
        int pos = condition.isCalcBlue()?1:0;
        for (int i = 0; i < getCount(); i++) {
            byte[] num = new byte[length];
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            String pattern = issueAnalyse.getTs().get(pos);
            for (int j = 0; j < num.length; j++) {
                num[j] = MapUtils.ODD_EVEN_MAP.get(pattern.substring(j,j+1)).byteValue();
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
            if(flag==4){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_p5_odd_pattern, null);
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_odd_pattern, null);
            }
        }
        try {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(position);
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
            ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line1));
//        ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line2));
//        ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line3));
            TrendOddPatternView oddPatternView = (TrendOddPatternView) convertView.findViewById(R.id.trend_odd_pattern);
            oddPatternView.setFlag(flag);
            oddPatternView.setCalcBlue(condition.isCalcBlue());
            oddPatternView.setPatterns(currNum, prevNum, nextNum);
            TextView text = (TextView) convertView.findViewById(R.id.trend_odd_text);
            StringBuilder sb = new StringBuilder();
            for (byte b:currNum) {
                sb.append(MapUtils.ODD_EVEN[b]);
            }
            SpannableString spannableString = new SpannableString(sb.toString());//形态颜色
            int i = 0;
            for (byte b:currNum) {
                spannableString.setSpan(new ForegroundColorSpan(b == 0 ? ColorUtils.NORMAL_RED:ColorUtils.NORMAL_BLUE), i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                i++;
            }
            text.setText(spannableString);
            showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
