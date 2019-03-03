package com.cp2y.cube.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.percent.PercentRelativeLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.FastLotteryHistoryActivity;
import com.cp2y.cube.activity.OpenLotterySummaryActivity;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.activity.pushsingle.PushSingleSummaryActivity;
import com.cp2y.cube.model.MainHomeModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.TouchLessGridView;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/1/3.
 */
public class MainHomeLotteryAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<MainHomeModel.Detail> list=new ArrayList<>();
    //开奖点击事件
    private RelativeLayout.OnClickListener onClickListener=new RelativeLayout.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position= (int) v.getTag(R.id.position);
            Intent intent = new Intent();
            if (list.get(position).getDraw().isDetail()) {//有开奖详情
                intent.setClass(context, OpenLotterySummaryActivity.class);
                intent.putExtra("issueOrder", list.get(position).getDraw().getIssue());
            } else {//无开奖详情
                intent.setClass(context, FastLotteryHistoryActivity.class);
                intent.putExtra("isQuick", list.get(position).getDraw().isQuick());
            }
            intent.putExtra("lottery_id", list.get(position).getDraw().getLotteryId());
            intent.putExtra("lottery_Name", list.get(position).getDraw().getLotteryName());
            context.startActivity(intent);
        }
    };
    public MainHomeLotteryAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    //刷新数据
    public void loadData(List<MainHomeModel.Detail> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public MainHomeModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.main_home_lottery_item,parent,false);
        }
        /******头部数据******/
        RelativeLayout head_layout= (RelativeLayout) convertView.findViewById(R.id.main_home_lottery_item_layout);
        SimpleDraweeView iv_icon= (SimpleDraweeView) convertView.findViewById(R.id.app_item_openlottery_lotteryicon);
        TextView tv_year= (TextView) convertView.findViewById(R.id.app_item_openlottery_tv_year);
        TextView tv_date= (TextView) convertView.findViewById(R.id.app_item_openlottery_tv_date);
        TextView tv_week= (TextView) convertView.findViewById(R.id.app_item_openlottery_tv_week);
        LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.app_item_openlottery_ll);
        LinearLayout ll_detail= (LinearLayout) convertView.findViewById(R.id.main_home_detail_ll);
        tv_year.setText(list.get(position).getDraw().getIssue().concat("期"));
        tv_date.setText(list.get(position).getDraw().getDrawTime());
        tv_week.setText("("+list.get(position).getDraw().getWeek()+")");
        tv_week.setVisibility((!TextUtils.isEmpty(list.get(position).getDraw().getWeek())?View.VISIBLE:View.GONE));//部分彩种显示星期
        iv_icon.setImageURI(CommonUtil.concatImgUrl(list.get(position).getDraw().getLotteryId()));
        head_layout.setTag(R.id.position,position);//positionTag
        head_layout.setOnClickListener(onClickListener);
        ll.removeAllViews();//清空数据
        //动态创建红球
        if(list.get(position).getDraw().getDrawRedNumber()!=null){
            String[] lotteryRedName=list.get(position).getDraw().getDrawRedNumber().split(",");
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
        if(list.get(position).getDraw().getDrawBlueNumber()!=null&&!list.get(position).getDraw().getDrawBlueNumber().equals("false")){
            String[] lotteryBlueName=list.get(position).getDraw().getDrawBlueNumber().split(",");
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
        /*******内容*******/
        ll_detail.removeAllViews();//清空数据
        if(list.get(position).getPushSingle()!=null&&list.get(position).getPushSingle().size()>0){
            addDetailData(ll_detail,position);
        }
        return convertView;
    }

    //添加号码库推单详情布局
    private void addDetailData(LinearLayout ll_detail,int position){
        for(int i=0,size=list.get(position).getPushSingle().size();i<size;i++){
            View detail_layout=LayoutInflater.from(context).inflate(R.layout.main_home_detail,ll_detail,false);
            PercentRelativeLayout layout= (PercentRelativeLayout) detail_layout.findViewById(R.id.item_main_home_deail_layout);
            RelativeLayout head_layout= (RelativeLayout) detail_layout.findViewById(R.id.main_home_detail_head);//中奖未中背景布局
            /**每一条内容**/
            TextView tv_type= (TextView) head_layout.findViewById(R.id.main_home_detail_type);//号码库或者推单
            TextView tv_money= (TextView) head_layout.findViewById(R.id.main_home_detail_money);//号码库或者推单
            TouchLessGridView gv= (TouchLessGridView) detail_layout.findViewById(R.id.main_home_gv);
            View divide=detail_layout.findViewById(R.id.main_home_view1);//竖线高度动态设置
            gv.setSelector(new ColorDrawable(Color.TRANSPARENT));//无点击效果
            MainHomeModel.PushDetail pushDetail=list.get(position).getPushSingle().get(i);//每一条数据
            String price=pushDetail.getPrize();//奖金
            int type=pushDetail.getType();//号码库或者推单类型
            int pushSingleId=pushDetail.getPushSingleID();
            detail_layout.setTag(R.id.pushSingleId,pushSingleId);//每条layout标记
            List<MainHomeModel.Item> item=pushDetail.getNnList();//子项数据
            head_layout.setBackgroundResource("0".equals(price)?R.mipmap.user_weizhong:R.mipmap.user_zhongjiang);//中奖背景
            if(type==1){//号码库
                tv_type.setText("0".equals(price)?"号码库":"号码库中奖");
                int finalI = i;
                layout.setOnClickListener((v -> {
                    Intent intent=new Intent(context,NumLibraryActivity.class);
                    intent.putExtra("flag", finalI);
                    context.startActivity(intent);
                }));
            }else{//推单
                tv_type.setText("0".equals(price)?"推单":"推单中奖");
                layout.setOnClickListener((v -> {
                    Intent intent = new Intent();
                    intent.setClass(context, PushSingleSummaryActivity.class);
                    intent.putExtra("pushSingleID", pushSingleId);
                    context.startActivity(intent);
                }));
            }
            tv_money.setTextColor(ColorUtils.GRAY);//初始颜色
            if("0".equals(price)){
                tv_money.setText("未中");
            }else if("-1".equals(price)){
                tv_money.setText("- -");
            }else{//换颜色
                price="¥".concat(price);
                SpannableString spannableString = new SpannableString(price);//金额颜色
                for(int j=0;j<price.length();j++){
                    spannableString.setSpan(new ForegroundColorSpan(j==0?ColorUtils.GRAY:ColorUtils.NORMAL_RED), j, j+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                tv_money.setText(spannableString);
            }
            MainHomeItemGVAdapter itemGVAdapter=new MainHomeItemGVAdapter(context,item);
            gv.setAdapter(itemGVAdapter);
            /**分割线高度设置**/
            detail_layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    detail_layout.removeOnLayoutChangeListener(this);
                    PercentRelativeLayout.LayoutParams params= (PercentRelativeLayout.LayoutParams) divide.getLayoutParams();
                    params.height=(detail_layout.getHeight()-DisplayUtil.dip2px(30f));
                    divide.setLayoutParams(params);
                }
            });
            ll_detail.addView(detail_layout);
        }

    }
    //处理奖金字符串
    private String priceStr(String price){
        int prices=Integer.valueOf(price);
        if(prices>999999)return "100万+";
        return price;
    }
}
