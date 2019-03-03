package com.cp2y.cube.activity.ignore.chongqing2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.ignore.CQ2BaseActivity;
import com.cp2y.cube.adapter.D3NomalIgnoreAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IgnoreTrendNumModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通不分区形态遗漏activity  展示重庆时时彩2星,跨度,和值遗漏 和尾数
 */
public class IgnoreCQ2Activity extends CQ2BaseActivity implements MyInterface.IgnoreDateChange{
    private List<String> list_span;//保存集合
    private List<String> list_span_flag;//重复保存保存集合
    private List<String> list_sumMantissa;//保存集合
    private List<String> list_sumMantissa_flag;//重复保存保存集合
    private List<String> list_sum_flag;//重复保存保存集合
    private List<String> list_sum;//和值数据保存到集合
    private List<List<IgnoreTrendNumModel.MissData>> data_condition;//条件数据
    private int pos=-1;
    private TextView title,tv_submit,selectDate,ignore_title;
    private TabLayout tabLayout;
    private ListView lv;
    private D3NomalIgnoreAdapter adapter;
    private HorizontalScrollView Hscrollview;
    private List<CheckBox>list_sum_check=new ArrayList<>();
    private List<CheckBox>list_span_check=new ArrayList<>();
    private List<CheckBox>list_sumMantissa_check=new ArrayList<>();
    private ImageView pop_iv,netOff;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout title_layout,scale_layout,probability_layout;
    private boolean scale_sort_red=true;//出现次数排序标记
    private boolean scale_sort_blue=true;
    private boolean emerge_sort_red=true;//出现次数排序标记
    private boolean emerge_sort_blue=true;
    private boolean scale_sort_condition=true;//出现次数条件排序
    private boolean emerge_sort_condition=true;//欲出几率条件排序
    private int posDate=2;//记录期数


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3d_ignore);
        setNavigationIcon(R.mipmap.back_chevron);//回退按钮
        setNavigationOnClickListener((v -> finish()));
        pos=getIntent().getIntExtra("pos",0);
        initView();
        initData();//初始化静态布局
        initNetOff();//断网控制
        initListener();
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initNetData();
    }

    private void initListener() {
        tv_submit.setOnClickListener((v) -> {
            if(list_span_check!=null&&list_span_check.size()>0&&pos==2){
                //跨度条件保存
                if(list_span!=null&&list_span.size()>0){
                    initSpanData();
                    if(CommonUtil.ListCheck(list_span_flag,list_span)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSpanData();
                saveCQSpan(list_span,list_span_flag);
            }else if(list_sum_check!=null&&list_sum_check.size()>0&&pos==4){//和值保存条件
                //和值条件保存
                if(list_sum!=null&&list_sum.size()>0){
                    initSumData();
                    if(CommonUtil.ListCheck(list_sum_flag,list_sum)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSumData();
                saveCQ2Sum(list_sum,list_sum_flag);
            }else if(list_sumMantissa_check!=null&&list_sumMantissa_check.size()>0&&pos==7){//和尾数保存条件
                //和尾数条件保存
                if(list_sumMantissa!=null&&list_sumMantissa.size()>0){
                    initSumMantissaData();
                    if(CommonUtil.ListCheck(list_sumMantissa_flag,list_sumMantissa)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSumMantissaData();
                saveCQ2SumMantissa(list_sumMantissa,list_sumMantissa_flag);
            }
        });
        //两个排序监听
        scale_layout.setOnClickListener((v1 -> {//次数占比排序
            sort(true);//排序方法
        }));
        probability_layout.setOnClickListener((v1 -> {//欲出几率排序
            sort(false);//排序方法
        }));
        //下拉框期数监听
        selectDate.setOnClickListener((v -> {
            if(isPopShowing()){
                closePop(pop_iv);
            }else{
                popWindow(pop_iv,selectDate,pos,0,posDate,1);
            }
        }));
        if(list_span_check!=null&&list_span_check.size()>0&&pos==2){//跨度选择条件监听
            //跨度选择监听
            setSpanCheckListener(list_span_check,tv_submit);
        }else if(list_sum_check!=null&&list_sum_check.size()>0&&pos==4){//和值选择监听
            setSpanCheckListener(list_sum_check,tv_submit);
        }else if(list_sumMantissa_check!=null&&list_sumMantissa_check.size()>0&&pos==7){//和值选择监听
            setSpanCheckListener(list_sumMantissa_check,tv_submit);
        }
    }
    private void initData() {
        if(pos==2){//跨度
            setNomalTitle("跨度遗漏");//设置标题
            ignore_title.setText("跨度");
        }else if(pos==4){//和值
            setNomalTitle("和值遗漏");//设置标题
            ignore_title.setText("和值");
        }else if(pos==7){
            setNomalTitle("和尾数遗漏");//设置标题
            ignore_title.setText("和尾");
        }
        tv_submit.setText("条件");
        addHscrollview(pos);//添加下方滑动布局
        adapter=new D3NomalIgnoreAdapter(this);
        lv.setAdapter(adapter);
    }
    private void initView() {
        title = (TextView) findViewById(R.id.toolbar_title);//普通标题
        ignore_title = (TextView) findViewById(R.id.ignore_nomal_title);//中间标记标题
        selectDate = (TextView) findViewById(R.id.selectNum_filter_ignore);//下拉框
        tv_submit = (TextView) findViewById(R.id.trend_submit);//普通标题
        tabLayout = (TabLayout) findViewById(R.id.trend_ignore_tablayout);//tablayout标题
        lv = (ListView) findViewById(R.id.ignore_nomal_lv);//普通不分区listview
        Hscrollview = (HorizontalScrollView) findViewById(R.id.trend_ignore_submit_layout);//下方选号区域
        pop_iv = (ImageView) findViewById(R.id.ignore_xiala);//下拉三角
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        title_layout = (RelativeLayout) findViewById(R.id.ignore_nomal_layout);
        scale_layout = (RelativeLayout) findViewById(R.id.trend_ignore_scale_layout);//出现次数占比布局
        probability_layout = (RelativeLayout) findViewById(R.id.trend_ignore_probability_layout);//欲出几率布局
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        tv_submit.setEnabled(false);
        setIgnoreDateChange(this);//初始化接口
        data_condition=new ArrayList<>();//接口条件数据
        list_span=new ArrayList<>();//跨度保存集合
        list_span_flag=new ArrayList<>();//跨度重复保存集合
        list_sumMantissa=new ArrayList<>();//跨度保存集合
        list_sumMantissa_flag=new ArrayList<>();//跨度重复保存集合
        list_sum=new ArrayList<>();//和值保存集合
        list_sum_flag=new ArrayList<>();//和值重复保存集合
    }
    //设置普通标题
    public void setNomalTitle(String str){
        tabLayout.setVisibility(View.GONE);//切换标题
        title.setVisibility(View.VISIBLE);//切换标题
        title.setText(str);
    }
    //和值  跨度
    private void LoadConditionData(int targetID,int date,int type){
        NetHelper.LOTTERY_API.getConditionMissTrend("10095",type,date,targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                controlHoscrollVisible(true);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition.clear();
                data_condition=args.getMiss();
                RemoveData0(data_condition);//去0
                adapter.loadData(data_condition,pos);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }
    //添加下方滑动布局_初始化控件
    public void addHscrollview(int pos){
        Hscrollview.removeAllViews();
        View view=null;
        view= LayoutInflater.from(this).inflate((pos==2||pos==7)?R.layout.ignore_d3_span_scroll:R.layout.ignore_cq2_sum_scroll,null);
        //初始化获取控件
        LinearLayout layout= (LinearLayout) view.findViewById((pos==2||pos==7)?R.id.span_num_ll:R.id.sum_num_ll);
        for(int i=0;i<layout.getChildCount();i++){
            if(layout.getChildAt(i) instanceof CheckBox){
                if(pos==2){
                    list_span_check.add((CheckBox) layout.getChildAt(i));//跨度
                }else if(pos==4){
                    list_sum_check.add((CheckBox) layout.getChildAt(i));//和值
                }else if(pos==7){
                    list_sumMantissa_check.add((CheckBox) layout.getChildAt(i));//和尾数
                }
            }
        }
        if(view!=null){
            Hscrollview.addView(view);
        }
    }
    //跨度数据
    public void initSpanData(){
        list_span.clear();
        for (int i = 0; i < list_span_check.size(); i++) {
            if(list_span_check.get(i).isChecked()){
                list_span.add(String.valueOf(i));
            }
        }
    }
    //和尾数数据
    public void initSumMantissaData(){
        list_sumMantissa.clear();
        for (int i = 0; i < list_sumMantissa_check.size(); i++) {
            if(list_sumMantissa_check.get(i).isChecked()){
                list_sumMantissa.add(String.valueOf(i));
            }
        }
    }
    //和值数据
    public void initSumData(){
        list_sum.clear();
        for (int i = 0; i < list_sum_check.size(); i++) {
            if(list_sum_check.get(i).isChecked()){
                list_sum.add(String.valueOf(i));
            }
        }
    }
    //请求网络 默认数据
    public void initNetData(){
        if(pos==4){
            LoadConditionData(10002,100,1);//请求网络加载和值
        }else if(pos==7){
            LoadConditionData(10027,100,1);//请求网络加载和尾数
        }else{
            LoadConditionData(10013,100,1);//请求网络加载跨度
        }
    }
    //更换期数
    @Override
    public void IgnoreChange(int position,int type) {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> IgnoreChange(position,type)));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initSort(scale_sort_red,scale_sort_blue,emerge_sort_red,
                emerge_sort_blue,scale_sort_condition,emerge_sort_condition);//初始化排序状态
        posDate=position;
        if(pos==2){
            LoadConditionData(10013,MapUtils.DATE_ARRAY[position],type);//请求网络加载跨度
        }else if(pos==4){
            LoadConditionData(10002,MapUtils.DATE_ARRAY[position],type);//请求网络加载和值
        }else if(pos==7){
            LoadConditionData(10027,MapUtils.DATE_ARRAY[position],type);//请求网络加载和值
        }

    }
    /**
     * 次数占比和欲出几率排序
     * @param type
     */
    public void sort(boolean type){
       //跨度  和值 排序
        sortCondition(type);
        adapter.notifyDataSetChanged();

    }
    private void controlHoscrollVisible(boolean isVisible) {
        //显示下方滑动布局
        ViewUtils.showViewsVisible(isVisible,findViewById(R.id.trend_view),findViewById(R.id.trend_submit),findViewById(R.id.trend_ignore_submit_layout));
    }

    public void sortCondition(boolean type){
        if(type){//次数占比排序
            if(scale_sort_condition){
                sortShowBigBiMaxs(data_condition,0);
            }else{//倒序
                sortShowBiMaxs(data_condition,0);
            }
            scale_sort_condition=!scale_sort_condition;
            emerge_sort_condition=true;//保证下次 点击欲出几率 是大到小
        }else{//欲出几率排序
            if(emerge_sort_condition){
                sortBigEmerges(data_condition,0);
            }else{//倒序
                sortEmerges(data_condition,0);
            }
            emerge_sort_condition=!emerge_sort_condition;
            scale_sort_condition=true;//保证下次 点击次数占比 是大到小
        }
    }

}
