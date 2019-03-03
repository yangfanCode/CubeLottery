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
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.trend.D3TrendOddPatternView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendOddPatternAdapter extends TrendSimpleAdapter implements TrendAdapter<IssueAnalyseDataModel.IssueAnalyse> {
    /**2福彩3D,3排列3,5重庆3星,7重庆2星**/
    private int flag=0;
    private Context context;
    private List<String> nums = new ArrayList<>();
    private List<IssueAnalyseDataModel.IssueAnalyse> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();

    public D3TrendOddPatternAdapter(Context context, ConditionModel condition,int flag) {
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
        for (int i = 0; i < getCount(); i++) {
            IssueAnalyseDataModel.IssueAnalyse issueAnalyse = data.get(i);
            nums.add(issueAnalyse.getTs().get(0));
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
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_cq2_odd_pattern, null);
            }else{
                convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_d3_odd_pattern, null);
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
            int currNum, prevNum = -1, nextNum = -1;
            Map<String, Integer> map_data=(flag==7?MapUtils.ODD_CQ2_EVEN_MAP:MapUtils.ODD_3D_EVEN_MAP);
            currNum = map_data.get(nums.get(position));
            //sum.setText(String.valueOf(currNum));
            if (position > 0) {
                prevNum = map_data.get(nums.get(position-1));
            }
            if (position < getCount()-1) {
                nextNum = map_data.get(nums.get(position+1));
            }
            ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line1));
//        ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line2));
//        ViewUtils.showViewsVisible(condition.isCalcBlue(),convertView.findViewById(R.id.trend_line3));
            D3TrendOddPatternView oddPatternView = (D3TrendOddPatternView) convertView.findViewById(R.id.trend_odd_pattern);
            List<Integer> miss =issueAnalyse.getTsm().get(0);
            oddPatternView.setPatterns(currNum, prevNum, nextNum,miss);
            oddPatternView.setFlag(flag);
//            TextView text = (TextView) convertView.findViewById(R.id.trend_odd_text);
//            StringBuilder sb = new StringBuilder();
//            for (byte b:currNum) {
//                sb.append(MapUtils.ODD_EVEN[b]);
//            }
//            SpannableString spannableString = new SpannableString(sb.toString());
//            int i = 0;
//            for (byte b:currNum) {
//                spannableString.setSpan(new ForegroundColorSpan(b == 0 ? ColorUtils.NORMAL_RED:ColorUtils.NORMAL_BLUE), i, i+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                i++;
//            }
//            text.setText(spannableString);
            //是否显示遗漏
            oddPatternView.setShowMiss(condition.isShowMiss());
            if(flag==7){//2星屏幕不够改变虚线宽度
                setSeperateLineWidth(convertView, position,condition.isShowBaseNum(), DisplayUtil.dip2px(317f),DisplayUtil.dip2px(240f));
            }else{
                showSeperateLine(convertView, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
