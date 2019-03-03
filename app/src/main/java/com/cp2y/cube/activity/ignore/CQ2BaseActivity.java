package com.cp2y.cube.activity.ignore;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.D3NumberMiss;
import com.cp2y.cube.model.IgnoreTrendNumModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CQ2BaseActivity extends BaseActivity {
    private RadioGroup radioGroup;//期数标题
    private List<String>list_total=new ArrayList<>();
    private int num_red = 0;
    private int num_blue = 0;
    private int Span_checkCount=0;
    private int Odd_checkCount=0;
    private PopupWindow popupWindow=null;
    private MyInterface.IgnoreDateChange ignoreDateChange;
    private MyInterface.IgnoreIsBlue ignoreIsBlue;
    public void setIgnoreDateChange(MyInterface.IgnoreDateChange ignoreDateChange){
        this.ignoreDateChange=ignoreDateChange;
    }
    public void setIgnoreCalcBlue(MyInterface.IgnoreIsBlue ignoreIsBlue){
        this.ignoreIsBlue=ignoreIsBlue;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //遗漏引导
        setGuideDialog("ignoreSort", R.mipmap.ignore_sort);
    }

    //跨度保存条件 重庆时时彩2星
    public void saveCQSpan(List<String> list_span,List<String>list_span_flag){
        if(list_span!=null&&list_span.size()>0){
            list_span_flag.clear();
            list_span_flag.addAll(list_span);
            SingletonMapFilter.registerCQ2Service("4", list_span);
        }
        TipsToast.showTips("已成功保存至过滤条件");
    }
    //和值保存条件 重庆时时彩2星
    public void saveCQ2Sum(List<String> list_sum,List<String>list_sum_flag){
        if(list_sum!=null&&list_sum.size()>0){
            list_sum_flag.clear();
            list_sum_flag.addAll(list_sum);
            SingletonMapFilter.registerCQ2Service("1", list_sum);
        }
        TipsToast.showTips("已成功保存至过滤条件");
    }
    //和尾数保存条件 重庆时时彩2星
    public void saveCQ2SumMantissa(List<String> list_sumMantissa,List<String>list_sumMantissa_flag){
        if(list_sumMantissa!=null&&list_sumMantissa.size()>0){
            list_sumMantissa_flag.clear();
            list_sumMantissa_flag.addAll(list_sumMantissa);
            SingletonMapFilter.registerCQ2Service("11", list_sumMantissa);
        }
        TipsToast.showTips("已成功保存至过滤条件");
    }
    //跨度条件选择监听
    public void setSpanCheckListener(List<CheckBox> list_check,TextView tv_submit){
        for (int i = 0; i < list_check.size(); i++) {
            list_check.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        Span_checkCount++;
                        tv_submit.setEnabled(true);
                        tv_submit.setText("保存");
                    }else{
                        Span_checkCount--;
                    }
                    if(Span_checkCount==0){
                        tv_submit.setEnabled(false);
                        tv_submit.setText("条件");
                    }
                }
            });
        }
    }
    //和值条件选择监听
    public void setSumSelectListener(RadioGroup rg,TextView tv_submit){
        for(int i=0;i<rg.getChildCount();i++){
            if(rg.getChildAt(i) instanceof RadioButton){
                ((RadioButton) rg.getChildAt(i)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            tv_submit.setEnabled(true);
                            tv_submit.setText("保存");
                        }
                    }
                });
            }
        }
    }
    //下拉popwindow
    public void popWindow(ImageView pop_iv,TextView tv_date,int posType,int tab,int check,int flag){
            View contentView = LayoutInflater.from(this)
                    .inflate(R.layout.ignore_xiala_pop, null);
            TextView switch_tv= (TextView) contentView.findViewById(R.id.ignore_pop_includeBlue);
            switch_tv.setText(flag==0?"含篮球":"含后区");
            //对号布局
            LinearLayout ll_pop= (LinearLayout) contentView.findViewById(R.id.ignore_pop_ll);//勾布局
            radioGroup= (RadioGroup) contentView.findViewById(R.id.ignore_pop_rg);//按钮
            RelativeLayout layout= (RelativeLayout) contentView.findViewById(R.id.ignore_pop_isBlue_layout);//含篮球布局
            layout.setVisibility(View.GONE);//是否显示含篮球
            RadioButton rb= (RadioButton) radioGroup.getChildAt(check);
            rb.setChecked(true);//默认选中100
            ll_pop.getChildAt(check).setVisibility(View.VISIBLE);//默认选中100
            for(int i=0;i<radioGroup.getChildCount();i++){
                if(radioGroup.getChildAt(i) instanceof RadioButton){
                    final int finalI = i;
                    ((RadioButton) radioGroup.getChildAt(i)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                //隐藏所有的勾
                                for(int i=0;i<ll_pop.getChildCount();i++){
                                    if(ll_pop.getChildAt(i) instanceof TextView){
                                        ll_pop.getChildAt(i).setVisibility(View.INVISIBLE);
                                    }
                                }
                                //显示对应的勾
                                ll_pop.getChildAt(finalI).setVisibility(View.VISIBLE);
                                //改变title
                                tv_date.setText(((RadioButton) radioGroup.getChildAt(finalI)).getText().toString());
                                if((posType==3&& tab ==0)||(posType==2)||(posType==4)||(posType==7)){//和值 跨度 和尾数 除3余个数 type=1/2
                                    ignoreDateChange.IgnoreChange(finalI, 1);//接口处理方法,刷新数据
                                }else if((posType==5&& tab ==0)||(posType==6&& tab ==0)){//奇偶比 大小比
                                    ignoreDateChange.IgnoreChange(finalI, 3);//接口处理方法,刷新数据
                                }else if((posType==5&& tab ==1)||(posType==3&& tab ==1)||(posType==6&& tab ==1)){//除3余形态  奇偶形态  大小形态
                                    ignoreDateChange.IgnoreChange(finalI, 5);//接口处理方法,刷新数据
                                }else if(posType==0){//基本号
                                    ignoreDateChange.IgnoreChange(finalI,3);//接口处理方法,刷新数据
                                }
                                pop_iv.setImageResource(R.mipmap.icon_xiaola);
                                popupWindow.dismiss();
                            }
                        }
                    });
                }
            }
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setContentView(contentView);
            //展示在toolbar下方
            pop_iv.setImageResource(R.mipmap.icon_xiala_top);
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
            //popupWindow.showAtLocation(getToolBar(), Gravity.TOP, 0, 250);
    }

    /**
     * 号码排序
     * @param data
     * @param i
     */
    //出现次数排序
    public void sortShowBiMax(List<List<D3NumberMiss.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<D3NumberMiss.MissData>() {
                @Override
                public int compare(D3NumberMiss.MissData o1, D3NumberMiss.MissData o2) {
                    return o1.getShowBi()<o2.getShowBi()?-1:1;
                }
            });
    }
    //出现次数倒序
    public void sortShowBigBiMax(List<List<D3NumberMiss.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<D3NumberMiss.MissData>() {
                @Override
                public int compare(D3NumberMiss.MissData o1, D3NumberMiss.MissData o2) {
                    return o1.getShowBi()<o2.getShowBi()?1:-1;
                }
            });
    }
    //欲出几率排序
    public void sortEmerge(List<List<D3NumberMiss.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<D3NumberMiss.MissData>() {
                @Override
                public int compare(D3NumberMiss.MissData o1, D3NumberMiss.MissData o2) {
                    return o1.getEmerge()<o2.getEmerge()?-1:1;
                }

            });
    }
    //欲出几率倒序
    public void sortBigEmerge(List<List<D3NumberMiss.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<D3NumberMiss.MissData>() {
                @Override
                public int compare(D3NumberMiss.MissData o1, D3NumberMiss.MissData o2) {
                    return o1.getEmerge()<o2.getEmerge()?1:-1;
                }

            });
    }
    /**
     * 指标排序
     * @param data
     * @param i
     */
    //出现次数排序
    public void sortShowBiMaxs(List<List<IgnoreTrendNumModel.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<IgnoreTrendNumModel.MissData>() {
                @Override
                public int compare(IgnoreTrendNumModel.MissData o1, IgnoreTrendNumModel.MissData o2) {
                    return o1.getShowBi()<o2.getShowBi()?-1:1;
                }
            });
    }
    //出现次数倒序
    public void sortShowBigBiMaxs(List<List<IgnoreTrendNumModel.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<IgnoreTrendNumModel.MissData>() {
                @Override
                public int compare(IgnoreTrendNumModel.MissData o1, IgnoreTrendNumModel.MissData o2) {
                    return o1.getShowBi()<o2.getShowBi()?1:-1;
                }
            });
    }
    //欲出几率排序
    public void sortEmerges(List<List<IgnoreTrendNumModel.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<IgnoreTrendNumModel.MissData>() {
                @Override
                public int compare(IgnoreTrendNumModel.MissData o1, IgnoreTrendNumModel.MissData o2) {
                    return o1.getEmerge()<o2.getEmerge()?-1:1;
                }

            });
    }
    //欲出几率倒序
    public void sortBigEmerges(List<List<IgnoreTrendNumModel.MissData>> data,int i){
            Collections.sort(data.get(i), new Comparator<IgnoreTrendNumModel.MissData>() {
                @Override
                public int compare(IgnoreTrendNumModel.MissData o1, IgnoreTrendNumModel.MissData o2) {
                    return o1.getEmerge()<o2.getEmerge()?1:-1;
                }

            });
    }
    //除3余个数监听
    public void setDivideNumListener(List<RadioGroup> list_number,TextView tv_submit){
        for (int i = 0; i < list_number.size(); i++) {
            for (int j = 0; j < list_number.get(i).getChildCount(); j++) {
                View child=list_number.get(i).getChildAt(j);
                if(child instanceof RadioButton){
                    ((RadioButton) child).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked){
                                tv_submit.setEnabled(true);
                                tv_submit.setText("保存");
                            }
                        }
                    });
                }
            }
        }
    }
    //奇偶比监听
    public void setOddNumListener(List<CheckBox>list_number,TextView tv_submit){
        for (int i = 0; i < list_number.size(); i++) {
            list_number.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //控制选择完毕,清空按钮变换
                    if (isChecked) {
                        Odd_checkCount++;
                        tv_submit.setEnabled(true);
                        tv_submit.setText("保存");
                    } else {
                        Odd_checkCount--;
                    }
                    if (Odd_checkCount == 0) {
                        tv_submit.setEnabled(false);
                        tv_submit.setText("条件");
                    }
                }

            });
        }
    }
    //切换选项卡重新设置期数
    public void setRadioButtonCheck(int position){
        if(popupWindow==null)return;
        popupWindow.dismiss();
        RadioButton radioButton= (RadioButton) radioGroup.getChildAt(position);
        radioButton.setChecked(true);
    }
    public void closePop(ImageView pop_iv){
        if(popupWindow==null)return;
        pop_iv.setImageResource(R.mipmap.icon_xiaola);
        popupWindow.dismiss();
    }
    public boolean isPopShowing(){
        if(popupWindow==null)return false;
        if(popupWindow.isShowing()){
            return true;
        }else{
            return false;
        }
    }
    //初始化排序状态
    public void initSort(boolean...b){
        for(boolean b_sort:b){
            b_sort=true;
        }
    }

    /**
     * 号码排序
     * @param lists
     * @param count
     */
    //次数占比从小到大排
    public void setBiSmallSort(List<List<D3NumberMiss.MissData>> lists, int count){
        for(int i=0;i<count;i++){
            sortShowBiMax(lists, i);
        }
    }
    //次数占比从大到小排
    public void setBiBigSort(List<List<D3NumberMiss.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortShowBigBiMax(lists, i);
        }
    }
    //欲出几率从小到大排
    public void setEmergeSmallSort(List<List<D3NumberMiss.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortEmerge(lists, i);
        }
    }
    //欲出几率从大到小排
    public void setEmergeBigSort(List<List<D3NumberMiss.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortBigEmerge(lists, i);
        }
    }
    /**
     * 指标排序
     * @param lists
     * @param count
     */
    //次数占比从小到大排
    public void setBiSmallSorts(List<List<IgnoreTrendNumModel.MissData>> lists, int count){
        for(int i=0;i<count;i++){
            sortShowBiMaxs(lists, i);
        }
    }
    //次数占比从大到小排
    public void setBiBigSorts(List<List<IgnoreTrendNumModel.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortShowBigBiMaxs(lists, i);
        }
    }
    //欲出几率从小到大排
    public void setEmergeSmallSorts(List<List<IgnoreTrendNumModel.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortEmerges(lists, i);
        }
    }
    //欲出几率从大到小排
    public void setEmergeBigSorts(List<List<IgnoreTrendNumModel.MissData>> lists,int count){
        for(int i=0;i<count;i++){
            sortBigEmerges(lists, i);
        }
    }
    //去除0数据
    public void RemoveData0(List<List<IgnoreTrendNumModel.MissData>> data){
//        for(int i=0;i<data.get(0).size();i++){
//            if(data.get(0).get(i).getShow()==0){
//                data.get(0).remove(i);
//            }
//        }
        Iterator<IgnoreTrendNumModel.MissData> it=data.get(0).iterator();
        while (it.hasNext()){
            IgnoreTrendNumModel.MissData missData=it.next();
            if(missData.getShow()==0){
                it.remove();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GuideFlag.isClick_Save=true;
    }
}
