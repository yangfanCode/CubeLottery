package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.LotteryHistoryModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;

import java.util.List;

/**
 * Created by admin on 2016/12/7.
 */
public class MyHistoryOpenLotteryAdapter extends BaseAdapter {
    private List<LotteryHistoryModel.HistoryLottery>list;
    private Context context;
    private LotteryHistoryModel args;
    public MyHistoryOpenLotteryAdapter(List<LotteryHistoryModel.HistoryLottery> list, Context context,LotteryHistoryModel args) {
        this.list = list;
        this.context = context;
        this.args=args;
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_doubleball_history,parent,false);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.openlottery_history_tv_ll);
        TextView tv_year= (TextView) view.findViewById(R.id.openlottery_history_tv_year);
        TextView tv_date= (TextView) view.findViewById(R.id.openlottery_history_tv_date);
        tv_year.setText(list.get(position).getIssue()+"期");
        tv_date.setText(list.get(position).getDrawTime());
        //动态创建红球
        if(list.get(position).getDrawRedNumber()!=null){
            String[] lotteryRedName=list.get(position).getDrawRedNumber().split(",");
            for (int i = 0; i < lotteryRedName.length; i++) {
                TextView tv_ball= (TextView) LayoutInflater.from(context).inflate(R.layout.openlottery_ball,null).findViewById(R.id.openlottery_ball_tv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,DisplayUtil.dip2px(3f),0);//4个参数按顺序分别是左上右下
                tv_ball.setLayoutParams(layoutParams);
                tv_ball.setText(lotteryRedName[i]);
                ll.addView(tv_ball);
            }
        }
        //动态创建蓝球
        if(list.get(position).getDrawBlueNumber()!=null&&!list.get(position).getDrawBlueNumber().equals("false")){
            String[] lotteryBlueName=list.get(position).getDrawBlueNumber().split(",");
            for (int i = 0; i < lotteryBlueName.length; i++) {
                TextView tv_ball= (TextView) LayoutInflater.from(context).inflate(R.layout.openlottery_ball,null).findViewById(R.id.openlottery_ball_tv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,DisplayUtil.dip2px(3f),0);//4个参数按顺序分别是左上右下
                tv_ball.setLayoutParams(layoutParams);
                tv_ball.setText(lotteryBlueName[i]);
                tv_ball.setTextColor(ColorUtils.MID_BLUE);
                ll.addView(tv_ball);
            }
        }
        return view;
    }
}
