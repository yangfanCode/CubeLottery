package com.cp2y.cube.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.trends.LottoTrendPicActivity;
import com.cp2y.cube.activity.trends.TrendPicActivity;
import com.cp2y.cube.adapter.MyAlertDialogTrendAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.SPUtils;

import java.util.Arrays;

public class TrendDialogBaseAcitvity extends BaseActivity {
    private AlertDialog dialog=null;
    private TextView dialog_title;
    private MyGridView alertdialog_gv;
    private String[]trendDialog={"号码走势","定位走势","跨度走势","除3余走势","和值走势","奇偶走势"};
    private String[]dialogFuTitle={"双色球走势","福彩3D走势","七乐彩走势","华东15选5走势"};
    private String[]dialogSportTitle={"大乐透走势","浙江6+1走势","排列5走势","排列3走势"};
   public void setTrendGoinDialog(Context context,int flag,int pos){
       AlertDialog.Builder builder = new AlertDialog.Builder(context);
       //获取自定义的VIew
       View trend_gv = LayoutInflater.from(context).inflate(R.layout.alertdialog_trend, null);
       //添加自定义VIew
       builder.setView(trend_gv);
       ImageView iv_close= (ImageView) trend_gv.findViewById(R.id.alertdialog_trend_close);
       dialog_title = (TextView) trend_gv.findViewById(R.id.alertdialog_trend_title);
       alertdialog_gv = (MyGridView) trend_gv.findViewById(R.id.alertdialog_trend_gv);
       MyAlertDialogTrendAdapter adapter_alertDialog=new MyAlertDialogTrendAdapter(Arrays.asList(trendDialog),context,R.layout.item_mytrend_alertdialog_gv);
       alertdialog_gv.setAdapter(adapter_alertDialog);
       //展示Dialog
       dialog_title.setText(flag==0?dialogFuTitle[pos]:dialogSportTitle[pos]);
       dialog=builder.show();
       iv_close.setOnClickListener((v) -> dialog.dismiss());
       alertdialog_gv.setOnItemClickListener((a, b, c, d) -> {
                        /*保存记录*/
           dialog.dismiss();
           StringBuilder stringBuilder=new StringBuilder();
           if(flag==0){
               //存储双色球走势 0-5
               StringBufferTrend(CommonUtil.preZeroForBall(String.valueOf(c)),stringBuilder);
           }else if(flag==1){//大乐透
               //存储双色球走势 6-11
               StringBufferTrend(CommonUtil.preZeroForBall(String.valueOf((c+6))),stringBuilder);
           }//加彩种跟范围判断
           Intent it = new Intent();
           //根据标记跳转
           if (flag==0){
               it.setClass(context, TrendPicActivity.class);
           }else{
               it.setClass(context, LottoTrendPicActivity.class);
           }
           it.putExtra("pos",c);
           startActivity(it);
       });
   }
    //存走势记录
    public void saveMyTrend(String data){
        SPUtils.put(TrendDialogBaseAcitvity.this,data);
    }
    //取走势记录
    public String getMyTrend(){
        return SPUtils.get(TrendDialogBaseAcitvity.this);
    }
    //删除走势记录
    public void removeMyTrend(){
        SPUtils.remove(TrendDialogBaseAcitvity.this);
    }
    //保存处理字符串
    public void StringBufferTrend(String pos,StringBuilder stringBuilder){
        String history_data=SPUtils.get(TrendDialogBaseAcitvity.this);
        if(history_data!=null&&history_data.length()>0){
            String str=pos+",";
            if(history_data.contains(str)){
                history_data=SPUtils.get(TrendDialogBaseAcitvity.this).replace(str,"");
            }
        }else{
            history_data="";
        }
        stringBuilder.append(history_data+pos+",");
        saveMyTrend(stringBuilder.toString());
    }
}
