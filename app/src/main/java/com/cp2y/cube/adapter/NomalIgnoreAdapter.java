package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.IgnoreTrendNumModel;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by admin on 2017/3/14.
 */
public class NomalIgnoreAdapter extends BaseAdapter {
    private int tab=0,pos=0;
    private Context context;
    private LayoutInflater inflater;
    private List<List<IgnoreTrendNumModel.MissData>> list=new ArrayList<>();
    public NomalIgnoreAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.get(0).size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    //第一次进入按照不同类型加载数据
    public void loadData(List<List<IgnoreTrendNumModel.MissData>> data, int pos){
        list.clear();
        this.pos=pos;
        list.addAll(data);
        notifyDataSetChanged();
    }
    //tab切换数据
    public void reloadData(List<List<IgnoreTrendNumModel.MissData>> data,int tab){
        list.clear();
        list.addAll(data);
        this.tab=tab;
        notifyDataSetChanged();
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.nomal_ignore_item,parent,false);
        }
        TextView tv_num= (TextView) convertView.findViewById(R.id.nomal_ignore_tvNum);// 球号
        TextView tv_percent= (TextView) convertView.findViewById(R.id.ignore_tv_percent);//百分比
        TextView tv_AppearCount= (TextView) convertView.findViewById(R.id.ignore_tv_AppearCount);//出现次数
        TextView tv_probability= (TextView) convertView.findViewById(R.id.ignore_tv_probability);//欲出几率
        TextView tv_IgnoreCount= (TextView) convertView.findViewById(R.id.ignore_tv_IgnoreCount);//遗漏次数
        ProgressBar progressBar_left= (ProgressBar) convertView.findViewById(R.id.nomal_ignore_progress_left);
        ProgressBar progressBar_right= (ProgressBar) convertView.findViewById(R.id.nomal_ignore_progress_right);

        try {
            tv_num.setText(pos==0?CommonUtil.preZeroForBall(list.get(0).get(position).getValue()):list.get(0).get(position).getValue());//补0
            if(pos==0){//红篮球球号
                tv_num.setTextColor(tab==0? ColorUtils.NORMAL_RED:ColorUtils.MID_BLUE);//红篮球切换改变球文字颜色
                tv_num.setBackgroundResource(R.drawable.lottery_ball_mid);
            }else{//跨度  和值  和尾数
                tv_num.setTextColor(ColorUtils.NORMAL_GRAY);
                tv_num.setBackground(null);
            }
            //设置百分比
            tv_percent.setText(CommonUtil.changeDoubleTwo(((double)list.get(0).get(position).getShowBi())/100)+"% ");
            //出现次数
            tv_AppearCount.setText("("+String.valueOf(list.get(0).get(position).getShow())+")");
            //欲出几率
            tv_probability.setText(CommonUtil.changeDoubleTwo((double) list.get(0).get(position).getEmerge()/100));
            //本期遗漏
            tv_IgnoreCount.setText("("+String.valueOf(list.get(0).get(position).getMiss())+")");
            //左右进度 精确到两位小数
            double left_progress=Double.parseDouble(CommonUtil.changeDoubleTwo((double)(list.get(0).get(position).getShowBi()*100)/calcLeftProgress()));
            double right_progress=Double.parseDouble(CommonUtil.changeDoubleTwo((double)(list.get(0).get(position).getEmerge()*100)/calcRightProgress()));
            left_progress=left_progress>0&&left_progress<1?1:left_progress;//最小值为0 0.几为1
            right_progress=right_progress>0&&right_progress<1?1:right_progress;//最小值为0 0.几为1
            progressBar_left.setProgress((int)left_progress);//设置进度
            progressBar_right.setProgress((int)right_progress);//设置进度
            LinearLayout ll_left= (LinearLayout) convertView.findViewById(R.id.nomal_ignore_ll_left);
            LinearLayout ll_right= (LinearLayout) convertView.findViewById(R.id.nomal_ignore_ll_right);
            setMargin(ll_left,(int)left_progress,true);//设置左边距
            setMargin(ll_right,(int)right_progress,false);//设置右边距
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("处理异常，请反馈客服400-666-7575");
        }
        return convertView;
    }
    //设置文字边距
    public void setMargin(LinearLayout layout,int progress,boolean left){
        int width=context.getResources().getDisplayMetrics().widthPixels;
        //计算 灰色进度宽度
        int gray_progress=(((width/2)- DisplayUtil.dip2px(25f))*(100-progress))/100;
        layout.measure(0,0);
        //获得文字宽度
        int text_width=layout.getMeasuredWidth();
        //计算margin
        int margin=gray_progress-(text_width/2)+DisplayUtil.dip2px(5f);
        //margin过小设置5dp
        margin=margin<DisplayUtil.dip2px(5f)?DisplayUtil.dip2px(5f):margin;//小于5dp直接设置5dp
        //margin过大设置贴边
        margin=margin>((width/2)- DisplayUtil.dip2px(21f))-text_width?((width/2)- DisplayUtil.dip2px(20f))-text_width:margin;
        LayoutParams params= (LayoutParams) layout.getLayoutParams();
        if(left){
            params.setMargins(margin,0,0,0);//左边边距
        }else{
            params.setMargins(0,0,margin,0);//右边边距
        }
    }
    //计算左进度  拿到数据 找比例里最大进5 计算 进度
    public int calcLeftProgress(){
        List<Integer>calc=new ArrayList<>();
        for(IgnoreTrendNumModel.MissData num:list.get(0)){
            calc.add(num.getShowBi());
        }
        int max=Collections.max(calc);
        //int max= CommonUtil.calcDivide5(Collections.max(calc));//最大值被5整除
        return max;
    }
    //计算右进度  拿到数据 找比例里最大进5 计算 进度
    public int calcRightProgress(){
        List<Integer>calc=new ArrayList<>();
        for(IgnoreTrendNumModel.MissData num:list.get(0)){
            calc.add(num.getEmerge());
        }
        int max=Collections.max(calc);
        //int max= CommonUtil.calcDivide5(Collections.max(calc));//最大值被5整除
        return max;
    }
}
