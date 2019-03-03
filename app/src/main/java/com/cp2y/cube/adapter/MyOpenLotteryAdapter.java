package com.cp2y.cube.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.LotteryDrawModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2016/12/6.
 */
public class MyOpenLotteryAdapter extends BaseAdapter{
    private List<LotteryDrawModel.DrawLottery>list=new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    public MyOpenLotteryAdapter( Context context) {
        this.context=context;
        inflater=LayoutInflater.from(context);

    }
    public void loadData(List<LotteryDrawModel.DrawLottery> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
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
        View view=inflater.inflate(R.layout.app_item_openlottery,parent,false);
        SimpleDraweeView iv_icon= (SimpleDraweeView) view.findViewById(R.id.app_item_openlottery_lotteryicon);
        TextView tv_name= (TextView) view.findViewById(R.id.app_item_openlottery_lotteryname);
        TextView tv_year= (TextView) view.findViewById(R.id.app_item_openlottery_tv_year);
        TextView tv_date= (TextView) view.findViewById(R.id.app_item_openlottery_tv_date);
        TextView tv_week= (TextView) view.findViewById(R.id.app_item_openlottery_tv_week);
        tv_name.setText(list.get(position).getLotteryName());
        tv_year.setText(list.get(position).getIssue().concat("期"));
        tv_date.setText(list.get(position).getDrawTime());
        tv_week.setText("("+list.get(position).getWeek()+")");
        tv_week.setVisibility((!TextUtils.isEmpty(list.get(position).getWeek())?View.VISIBLE:View.GONE));//部分彩种显示星期
        iv_icon.setImageURI(CommonUtil.concatImgUrl(list.get(position).getLotteryId()));
        //动态创建红球
        if(list.get(position).getDrawRedNumber()!=null){
            String[] lotteryRedName=list.get(position).getDrawRedNumber().split(",");
            LinearLayout ll= (LinearLayout) view.findViewById(R.id.app_item_openlottery_ll);
            for (int i = 0; i < lotteryRedName.length; i++) {
                TextView tv_ball= (TextView) LayoutInflater.from(context).inflate(R.layout.openlottery_ball,null).findViewById(R.id.openlottery_ball_tv);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0, DisplayUtil.dip2px(3f),0);//4个参数按顺序分别是左上右下
                tv_ball.setLayoutParams(layoutParams);
                tv_ball.setText(lotteryRedName[i]);
                ll.addView(tv_ball);
            }
        }
        //动态创建蓝球
        if(list.get(position).getDrawBlueNumber()!=null&&!list.get(position).getDrawBlueNumber().equals("false")){
            String[] lotteryBlueName=list.get(position).getDrawBlueNumber().split(",");
            LinearLayout ll= (LinearLayout) view.findViewById(R.id.app_item_openlottery_ll);
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
