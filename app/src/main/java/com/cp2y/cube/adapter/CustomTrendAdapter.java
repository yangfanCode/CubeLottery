package com.cp2y.cube.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.percent.PercentRelativeLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.ignore.doubleball.IgnoreActivity;
import com.cp2y.cube.activity.ignore.doubleball.PartIgnoreActivity;
import com.cp2y.cube.activity.ignore.fucai3d.Ignore3DActivity;
import com.cp2y.cube.activity.ignore.fucai3d.PartIgnore3DActivity;
import com.cp2y.cube.activity.ignore.lotto.IgnoreLottoActivity;
import com.cp2y.cube.activity.ignore.lotto.PartIgnoreLottoActivity;
import com.cp2y.cube.activity.ignore.pailie3.IgnoreP3Activity;
import com.cp2y.cube.activity.ignore.pailie3.PartIgnoreP3Activity;
import com.cp2y.cube.activity.ignore.pailie5.IgnoreP5Activity;
import com.cp2y.cube.activity.ignore.pailie5.PartIgnoreP5Activity;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.ViewUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/14.
 */
public class CustomTrendAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private int lotteryId;

    private List<Integer>list=new ArrayList<>();
    public CustomTrendAdapter(Context context,int lotteryId){
        this.context=context;
        this.lotteryId=lotteryId;
        inflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void loadData(List<Integer>data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.trend_item,parent,false);
        }
        SimpleDraweeView iv= (SimpleDraweeView) convertView.findViewById(R.id.trend_iv);
        iv.setImageResource(list.get(position));
        setMargin(position,iv);//设置边距
        return convertView;
    }

    private void setMargin(int pos,SimpleDraweeView iv){
        PercentRelativeLayout.LayoutParams params= (PercentRelativeLayout.LayoutParams) iv.getLayoutParams();
        if(pos%2==0){
            params.setMargins(DisplayUtil.dip2px(18f),0,0,0);
        }else{
            params.setMargins(0,0,DisplayUtil.dip2px(18f),0);
        }
    }

    private void initTrend(View convertView,int lotteryId){
        //走势图按钮初始化
        TextView tv_trend1= (TextView) convertView.findViewById(R.id.tv_trend1);
        TextView tv_trend2= (TextView) convertView.findViewById(R.id.tv_trend2);
        TextView tv_trend3= (TextView) convertView.findViewById(R.id.tv_trend3);
        TextView tv_trend4= (TextView) convertView.findViewById(R.id.tv_trend4);
        TextView tv_trend5= (TextView) convertView.findViewById(R.id.tv_trend5);
        TextView tv_trend6= (TextView) convertView.findViewById(R.id.tv_trend6);
        TextView tv_trend7= (TextView) convertView.findViewById(R.id.tv_trend7);
        TextView tv_trend8= (TextView) convertView.findViewById(R.id.tv_trend8);
        //遗漏按钮
        TextView tv_ignore1= (TextView) convertView.findViewById(R.id.tv_ignore1);
        TextView tv_ignore2= (TextView) convertView.findViewById(R.id.tv_ignore2);
        TextView tv_ignore3= (TextView) convertView.findViewById(R.id.tv_ignore3);
        TextView tv_ignore4= (TextView) convertView.findViewById(R.id.tv_ignore4);
        TextView tv_ignore5= (TextView) convertView.findViewById(R.id.tv_ignore5);
        TextView tv_ignore6= (TextView) convertView.findViewById(R.id.tv_ignore6);
        TextView tv_ignore7= (TextView) convertView.findViewById(R.id.tv_ignore7);
        TextView tv_ignore8= (TextView) convertView.findViewById(R.id.tv_ignore8);
        //hot标识
        ImageView iv_hot1= (ImageView) convertView.findViewById(R.id.trend_hot1);
        ImageView iv_hot2= (ImageView) convertView.findViewById(R.id.trend_hot2);
        ImageView iv_hot3= (ImageView) convertView.findViewById(R.id.trend_hot3);
        ImageView iv_hot4= (ImageView) convertView.findViewById(R.id.trend_hot4);
        ImageView iv_hot5= (ImageView) convertView.findViewById(R.id.trend_hot5);
        ImageView iv_hot6= (ImageView) convertView.findViewById(R.id.trend_hot6);
        ImageView iv_hot7= (ImageView) convertView.findViewById(R.id.trend_hot7);
        ImageView iv_hot8= (ImageView) convertView.findViewById(R.id.trend_hot8);
        initIgnore(lotteryId,convertView,tv_ignore1,tv_ignore2,tv_ignore3,tv_ignore4,tv_ignore5,tv_ignore6,tv_ignore7,tv_ignore8);//遗漏
        initIgnoreListener(lotteryId,tv_ignore1,tv_ignore2,tv_ignore3,tv_ignore4,tv_ignore5,tv_ignore6);//遗漏监听
        initHot(lotteryId,iv_hot1,iv_hot2,iv_hot3,iv_hot4,iv_hot5,iv_hot6,iv_hot7,iv_hot8);//hto标识
    }
    private void initH5Trend(View convertView,int lotteryId){
        //走势图按钮初始化
        TextView tv_trend1= (TextView) convertView.findViewById(R.id.tv_trend1);
        TextView tv_trend2= (TextView) convertView.findViewById(R.id.tv_trend2);
        TextView tv_trend3= (TextView) convertView.findViewById(R.id.tv_trend3);
        TextView tv_trend4= (TextView) convertView.findViewById(R.id.tv_trend4);
        TextView tv_trend5= (TextView) convertView.findViewById(R.id.tv_trend5);
        TextView tv_trend6= (TextView) convertView.findViewById(R.id.tv_trend6);
        TextView tv_trend7= (TextView) convertView.findViewById(R.id.tv_trend7);
        TextView tv_trend8= (TextView) convertView.findViewById(R.id.tv_trend8);
        //hot标识
        ImageView iv_hot1= (ImageView) convertView.findViewById(R.id.trend_hot1);
        ImageView iv_hot2= (ImageView) convertView.findViewById(R.id.trend_hot2);
        ImageView iv_hot3= (ImageView) convertView.findViewById(R.id.trend_hot3);
        ImageView iv_hot4= (ImageView) convertView.findViewById(R.id.trend_hot4);
        ImageView iv_hot5= (ImageView) convertView.findViewById(R.id.trend_hot5);
        ImageView iv_hot6= (ImageView) convertView.findViewById(R.id.trend_hot6);
        ImageView iv_hot7= (ImageView) convertView.findViewById(R.id.trend_hot7);
        ImageView iv_hot8= (ImageView) convertView.findViewById(R.id.trend_hot8);
        initHot(lotteryId,iv_hot1,iv_hot2,iv_hot3,iv_hot4,iv_hot5,iv_hot6,iv_hot7,iv_hot8);//hto标识
    }

    //遗漏
    private void initIgnore(int lotteryId,View convertView,TextView...tv) {
        //有遗漏的彩种 部分遗漏
        if(lotteryId==10002||lotteryId==10088||lotteryId==10001||lotteryId==10003||lotteryId==10004){
            ViewUtils.showViewsVisible(true,tv);
            ViewUtils.showViewsVisible(false,tv[1]);//隐藏振幅遗漏
            ViewUtils.showViewsVisible(false,tv[6]);//隐藏大小遗漏
            ViewUtils.showViewsVisible(false,tv[7]);//隐藏和尾数遗漏
            ViewUtils.showViewsVisible(false,convertView.findViewById(R.id.view_ignore2));//隐藏遗漏的线
            ViewUtils.showViewsVisible(false,convertView.findViewById(R.id.view_ignore7));
            ViewUtils.showViewsVisible(false,convertView.findViewById(R.id.view_ignore8));
        }else{//完全没有遗漏的彩种H5
            ViewUtils.showViewsVisible(false,tv);//隐藏遗漏入口
            ViewUtils.showViewsVisible(false,convertView.findViewById(R.id.view_ignore1),convertView.findViewById(R.id.view_ignore2),
                    convertView.findViewById(R.id.view_ignore3),convertView.findViewById(R.id.view_ignore4),
                    convertView.findViewById(R.id.view_ignore5),convertView.findViewById(R.id.view_ignore6));//隐藏分区线
        }
    }
    //Hot标识
    private void initHot(int lotteryId,ImageView...tv){
        for(int i=0;i<tv.length;i++){
            if(lotteryId==10001||lotteryId==10003||lotteryId==10004){
                tv[i].setVisibility((i==0||i==2||i==4)?View.VISIBLE:View.GONE);
            }else{
                tv[i].setVisibility((i==0||i==1||i==4)?View.VISIBLE:View.GONE);
            }
        }
    }

    //遗漏点击监听
    private void initIgnoreListener(int lotteryId,TextView...tv){
        for(int i=0;i<tv.length;i++){
            final int finalI = i;
            tv[i].setOnClickListener((v -> {
                if(lotteryId==10002){
                    gotoDoubleIgnore(finalI);
                }else if(lotteryId==10088){
                    gotoLottoIgnore(finalI);
                }else if(lotteryId==10001){
                    goto3DIgnore(finalI);
                }else if(lotteryId==10003){
                    gotoP3Ignore(finalI);
                }else if(lotteryId==10004){
                    gotoP5Ignore(finalI);
                }
            }));
        }
    }
    //跳转双色球遗漏
    public void gotoDoubleIgnore(int position){
        Intent intent=new Intent();
        if(position==3||position==5){
            intent.setClass(context, PartIgnoreActivity.class);
        }else{
            intent.setClass(context, IgnoreActivity.class);
        }
        intent.putExtra("pos",position);
        context.startActivity(intent);
    }
    //跳转大乐透遗漏
    public void gotoLottoIgnore(int position){
        Intent intent=new Intent();
        if(position==3||position==5){
            intent.setClass(context, PartIgnoreLottoActivity.class);
        }else{
            intent.setClass(context, IgnoreLottoActivity.class);
        }
        intent.putExtra("pos",position);
        context.startActivity(intent);
    }
    //跳转3d遗漏
    public void goto3DIgnore(int position){
        Intent intent=new Intent();
        if(position==2||position==4){
            intent.setClass(context, Ignore3DActivity.class);
        }else{
            intent.setClass(context, PartIgnore3DActivity.class);
        }
        intent.putExtra("pos",position);
        context.startActivity(intent);
    }
    //跳转排列3遗漏
    public void gotoP3Ignore(int position){
        Intent intent=new Intent();
        if(position==2||position==4){
            intent.setClass(context, IgnoreP3Activity.class);
        }else{
            intent.setClass(context, PartIgnoreP3Activity.class);
        }
        intent.putExtra("pos",position);
        context.startActivity(intent);
    }
    //跳转排列5遗漏
    public void gotoP5Ignore(int position){
        Intent intent=new Intent();
        if(position==2||position==4){
            intent.setClass(context, IgnoreP5Activity.class);
        }else{
            intent.setClass(context, PartIgnoreP5Activity.class);
        }
        intent.putExtra("pos",position);
        context.startActivity(intent);
    }
}
