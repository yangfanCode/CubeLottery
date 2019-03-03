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
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.trend.D3TrendLocateView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by js on 2016/12/28.
 */
public class D3TrendLocateAdapter extends TrendSimpleAdapter {

    private Context context;
    private int type = 0;
    private List<Integer> nums = new ArrayList<>();
    private List<IssueNumberDataModel.IssueNumber> data = new ArrayList<>();
    private ConditionModel condition = new ConditionModel();
    //private Range numRange = new Range(0,0);

//    public Range getNumRange() {
//        return numRange;
//    }

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
        //所有球跨度范围
        for (int i = 0; i < getCount(); i++) {
            IssueNumberDataModel.IssueNumber issueNumber = data.get(i);
            nums.add(issueNumber.getBn().get(type));//接口号码数据 按位数取
        }
        //numRange = CommonUtil.calcRangeFromList(nums);
        notifyDataSetChanged();
    }

    public D3TrendLocateAdapter(Context context, ConditionModel condition) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_trend_3d_locate, null);
        }
        try {
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
            TextView nums= (TextView) convertView.findViewById(R.id.trend_nums);
            nums.setText(issueNumber.getN());//开奖号
            int colorId=check3DNumTypeColor(issueNumber.getN());
            String text=issueNumber.getN().replace(" ","").trim();
            if(text.length()==3){//字体颜色
                nums.setTextColor(colorId==0? ColorUtils.NORMAL_GRAY:(colorId==2?ColorUtils.MID_BLUE:ColorUtils.NORMAL_RED));
            }
            nums.setVisibility(condition.isShowBaseNum()?View.VISIBLE:View.GONE);//是否展示开奖号
            D3TrendLocateView locateView = (D3TrendLocateView) convertView.findViewById(R.id.trend_locate);
            ViewGroup.LayoutParams lp = locateView.getLayoutParams();
            lp.width = DisplayUtil.dip2px(30f*10);
            locateView.setLayoutParams(lp);
            locateView.setShowMiss(condition.isShowMiss());
            locateView.setNums(currNum, prevNum, nextNum, miss);
            locateView.setType(type);
            locateView.invalidate();
            //根据开奖号改变宽度
            setSeperateLineWidth(convertView, position,condition.isShowBaseNum(), DisplayUtil.dip2px(369f),DisplayUtil.dip2px(300f));
            //showSeperateLine(convertView, position);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
}
