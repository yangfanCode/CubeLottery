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
import java.util.Locale;

/**
 * Created by admin on 2017/3/14.
 */
public class PartIgnoreAdapter extends BaseAdapter {
    private int flag=-1;//0双色球,其大乐透
    private int tab=0,pos=0;
    private int maxLeft=0,maxRight=0;
    private Context context;
    private LayoutInflater inflater;
    private List<IgnoreTrendNumModel.MissData> data_total=new ArrayList<>();//除3余数据总集合
    private List<List<IgnoreTrendNumModel.MissData>> list=new ArrayList<>();
    public PartIgnoreAdapter(Context context,int flag){
        this.context=context;
        this.flag=flag;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if(pos==3&&tab==0){//除3余个数
            return list!=null&&list.size()==3?(list.get(0).size()+list.get(1).size()+list.get(2).size()+3):0;
        }else if((pos==3&&tab==1)||(pos==5&&tab==1)||(pos==6&&tab==1)){//除3余形态 奇偶形态 大小形态
            int size=0;
            for(List<IgnoreTrendNumModel.MissData> dataList:list){
                size+=dataList.size();
            }
            if(flag==0&&list.size()==6){//双色球不含蓝球
                return list!=null&&list.size()>0?(size+6):0;
            }else if(flag==1&&list.size()==5){//大乐透不含蓝球
                return list!=null&&list.size()>0?(size+5):0;
            }else{
                return list!=null&&list.size()>0?(size+7):0;
            }

        }else{//奇偶比 大小比
            return list!=null&&list.size()>0?list.get(0).size():0;
        }
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
    public int getItemViewType(int position) {
        boolean add_part = false;
        if ((pos == 5 && tab == 0)||(pos == 6 && tab == 0)) {//正常布局
            //奇偶比 大小比
            return 1;
        } else {//分区布局
            if ((pos == 5 && tab == 1)||(pos == 6 && tab == 1)) {//奇偶形态 大小形态
                add_part = (position % 3 == 0);
            } else if (pos == 3 && tab == 0) {//除3余个数
                if(position==list.get(0).size()+1||position==list.get(0).size()+list.get(1).size()+2){
                    add_part=true;//余1 或 余2
                }else{
                    add_part=false;
                }
            } else if (pos == 3 && tab == 1) {//除3余形态
                add_part = (position % 4 == 0);
            }
            if(position==0){
                return 0;//头部
            }else if(position!=0&&add_part){
                return 2;
            }else{
                return 1;//正常数据
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    //加载数据
    public void loadData(List<List<IgnoreTrendNumModel.MissData>> data, int pos,int tab){
        list.clear();
        this.pos=pos;
        this.tab=tab;
        list.addAll(data);
        //除3余数据结构整理 奇偶形态
        if(pos==3||(pos==5&&tab==1)||(pos==6&&tab==1)){
            initDivideData(pos);
        }else if((pos==5&&tab==0)||(pos==6&&tab==0)){//奇偶比 大小数据整理
            initOddData(pos);
        }
        maxLeft=calcLeftProgress();
        maxRight=calcRightProgress();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);
        if(convertView==null){
            if(type==0){//头布局
                convertView=inflater.inflate(R.layout.part_ignore_head_item,parent,false);
            }else if(type==1){//正常布局
                convertView=inflater.inflate(R.layout.nomal_ignore_item,parent,false);
            }else{//中间分区
                convertView=inflater.inflate(R.layout.part_ignore_mid_item,parent,false);

            }
        }
        if(type==0){//头布局
            TextView tv_head= (TextView) convertView.findViewById(R.id.part_ignore_head);
            if(pos==3&&tab==0){
                tv_head.setText("余0");
                tv_head.setTextColor(ColorUtils.ORANGE);
            }else if((pos==3&&tab==1)||(pos==5&&tab==1)||(pos==6&&tab==1)){
                tv_head.setText(flag==0?"红1":"前1");
                tv_head.setTextColor(ColorUtils.NORMAL_RED);
            }
        }else if(type==1){//正常布局
            try {
                if(data_total==null||data_total.size()==0)return convertView;//如果数据为空 返回view
                TextView tv_num= (TextView) convertView.findViewById(R.id.nomal_ignore_tvNum);// 球号
                TextView tv_percent= (TextView) convertView.findViewById(R.id.ignore_tv_percent);//百分比
                TextView tv_AppearCount= (TextView) convertView.findViewById(R.id.ignore_tv_AppearCount);//出现次数
                TextView tv_probability= (TextView) convertView.findViewById(R.id.ignore_tv_probability);//欲出几率
                TextView tv_IgnoreCount= (TextView) convertView.findViewById(R.id.ignore_tv_IgnoreCount);//遗漏次数
                View endLine=convertView.findViewById(R.id.ignore_end_line);
                endLine.setVisibility(View.GONE);//隐藏掉
                if((pos==5&&tab==0)||(pos==6&&tab==0)){//奇偶大小终端线
                    endLine.setVisibility(position==getCount()-1?View.VISIBLE:View.GONE);//终端线
                }
                ProgressBar progressBar_left= (ProgressBar) convertView.findViewById(R.id.nomal_ignore_progress_left);
                ProgressBar progressBar_right= (ProgressBar) convertView.findViewById(R.id.nomal_ignore_progress_right);
                tv_num.setText(data_total.get(position).getValue());
                tv_num.setTextColor(ColorUtils.NORMAL_GRAY);
                tv_num.setBackground(null);
                //设置百分比
                tv_percent.setText(CommonUtil.changeDoubleTwo(((double)data_total.get(position).getShowBi())/100)+"% ");
                //出现次数
                tv_AppearCount.setText("("+String.valueOf(data_total.get(position).getShow())+")");
                //欲出几率
                tv_probability.setText(CommonUtil.changeDoubleTwo((double) data_total.get(position).getEmerge()/100));
                //本期遗漏
                tv_IgnoreCount.setText("("+String.valueOf(data_total.get(position).getMiss())+")");
                //左右进度 精确到两位小数
                double left_progress=Double.parseDouble(CommonUtil.changeDoubleTwo((double)(data_total.get(position).getShowBi()*100)/maxLeft));
                double right_progress=Double.parseDouble(CommonUtil.changeDoubleTwo((double)(data_total.get(position).getEmerge()*100)/maxRight));
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
        }else{//中间分区
            TextView tv_mid_title= (TextView) convertView.findViewById(R.id.ignore_mid_tv);// 分区标题
            if(pos==3&&tab==0){//除3余个数
                tv_mid_title.setTextColor(ColorUtils.ORANGE);
                if(position<=list.get(0).size()){//余0
                    tv_mid_title.setText("余0");
                }else if(position>list.get(0).size()&&position<=list.get(0).size()+list.get(1).size()+1){
                    tv_mid_title.setText("余1");
                }else{
                    tv_mid_title.setText("余2");
                }
            }else if(pos==3&&tab==1){//除3余形态
                if(flag==0){//双色球
                    OddDividePatternBlue(position, tv_mid_title,4);
                }
                else if(flag==1){//大乐透
                    OddDividePatternEnd(position, tv_mid_title,4);
                }
            }else if((pos==5&&tab==1)||(pos==6&&tab==1)){//奇偶形态 大小形态
                if(flag==0){//双色球
                    OddDividePatternBlue(position, tv_mid_title,3);
                }
                else if(flag==1){//大乐透
                    OddDividePatternEnd(position, tv_mid_title,3);
                }
            }
        }
        return convertView;
    }
    //蓝球文字
    private void OddDividePatternBlue(int position, TextView tv_mid_title,int pos) {
        if((position/pos)+1==7){
            tv_mid_title.setText("蓝");
            tv_mid_title.setTextColor(ColorUtils.MID_BLUE);
        }else{
            tv_mid_title.setText(String.format(Locale.CHINA, "红%d", (position/pos)+1));
            tv_mid_title.setTextColor(ColorUtils.NORMAL_RED);
        }
    }
    //后区文字
    private void OddDividePatternEnd(int position, TextView tv_mid_title,int pos) {
        if((position/pos)+1==6){
            tv_mid_title.setText("后1");
            tv_mid_title.setTextColor(ColorUtils.MID_BLUE);
        }else if((position/pos)+1==7){
            tv_mid_title.setText("后2");
            tv_mid_title.setTextColor(ColorUtils.MID_BLUE);
        }else{
            tv_mid_title.setText(String.format(Locale.CHINA, "前%d", (position/pos)+1));
            tv_mid_title.setTextColor(ColorUtils.NORMAL_RED);
        }
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
        for(IgnoreTrendNumModel.MissData num:data_total){
            if(num==null)continue;
            calc.add(num.getShowBi());
        }
        int max=Collections.max(calc);
        //int max= CommonUtil.calcDivide5(Collections.max(calc));//最大值被5整除
        return max;
    }
    //计算右进度  拿到数据 找比例里最大进5 计算 进度
    public int calcRightProgress(){
        List<Integer>calc=new ArrayList<>();
        for(IgnoreTrendNumModel.MissData num:data_total){
            if(num==null)continue;
            calc.add(num.getEmerge());
        }
        int max=Collections.max(calc);
        //int max= CommonUtil.calcDivide5(Collections.max(calc));//最大值被5整除
        return max;
    }
    //数据整理
    public void initDivideData(int pos){
        if(pos==3||pos==5||pos==6){//除3余数据 奇偶数据 包括不含蓝球
            if(list!=null&&(list.size()==3||list.size()==7||list.size()==6||list.size()==5)){
                data_total.clear();
                for(int i=0;i<list.size();i++){
                    data_total.add(null);
                    data_total.addAll(list.get(i));
                }
            }
        }
    }
    //数据整理
    public void initOddData(int pos){
        if(pos==5||pos==6){//除3余数据
            if(list!=null&&list.size()==1){
                data_total.clear();
                data_total.addAll(list.get(0));
            }
        }
    }
}
