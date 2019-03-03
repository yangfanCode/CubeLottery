package com.cp2y.cube.activity.ignore.doubleball;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.ignore.DoubleBaseActivity;
import com.cp2y.cube.adapter.NomalIgnoreAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IgnoreTrendNumModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 普通不分区形态遗漏activity  展示双色球,跨度,基本号红篮球,和值遗漏,和尾数
 */
public class IgnoreActivity extends DoubleBaseActivity implements MyInterface.IgnoreDateChange,MyInterface.IgnoreIsBlue{
    private List<String> list_red;//保存集合
    private List<String> list_blue;//保存集合
    private List<String> list_total;//保存集合
    private List<String> list_span;//保存集合
    private List<String> list_span_flag;//重复保存保存集合
    private List<String> list_sumMantissa;//保存集合
    private List<String> list_sumMantissa_flag;//重复保存保存集合
    private List<String> list_sum;//和值数据保存到集合
    private List<List<IgnoreTrendNumModel.MissData>> data_blue_num;//蓝球数据
    private List<List<IgnoreTrendNumModel.MissData>> data_red_num;//红球数据
    private List<List<IgnoreTrendNumModel.MissData>> data_condition;//条件数据
    private String sum="";//和值赋值
    private String sum_flag="";//重复保存保存
    private int pos=-1;
    private TextView title,tv_submit,selectDate,ignore_title;
    private TabLayout tabLayout;
    private ListView lv;
    private NomalIgnoreAdapter adapter;
    private HorizontalScrollView Hscrollview;
    private List<String> list_red_select, list_blue_select;
    private List<CheckBox>list_double_num_check=new ArrayList<>();
    private List<CheckBox>list_double_span_check=new ArrayList<>();
    private List<CheckBox>list_double_sumMantissa_check=new ArrayList<>();
    private ArrayAdapter<String> adapter_red, adapter_blue;
    private RadioGroup sum_rg;//和值单选
    private LinearLayout num_trend_layout;
    private MyGridView gv_red,gv_blue;
    private String[] sum_data={"22-39","40-57","58-75","76-93","94-111","112-129","130-147","148-165","166-183","184-199"};
    private ImageView pop_iv,netOff;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout title_layout;
    private boolean scale_sort_red=true;//出现次数排序标记
    private boolean scale_sort_blue=true;
    private boolean emerge_sort_red=true;//出现次数排序标记
    private boolean emerge_sort_blue=true;
    private boolean scale_sort_condition=true;//出现次数条件排序
    private boolean emerge_sort_condition=true;//欲出几率条件排序
    private RelativeLayout probability_layout,scale_layout;
    private int posDate=2;//记录期数
    private boolean isBlue=true;//记录是否含篮球

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignore);
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
        initNetdata();
    }

    private void initListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                reloadIgnoreData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tv_submit.setOnClickListener((v) -> {
            if(list_double_num_check!=null&&list_double_num_check.size()>0&&pos==0){
                boolean loginState = LoginSPUtils.isLogin();
                if (loginState) {
                    initSubmitData();
                    saveNum(list_red,list_blue,0);//双色球基本号保存
                }else{
                    intentLogin();//登录页面
                }
            }else if(list_double_span_check!=null&&list_double_span_check.size()>0&&pos==2){
                //跨度条件保存
                if(list_span!=null&&list_span.size()>0){
                    initSpanData();
                    if(CommonUtil.ListCheck(list_span_flag,list_span)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSpanData();
                saveSpan(list_span,list_span_flag,0);
            }else if(list_double_sumMantissa_check!=null&&list_double_sumMantissa_check.size()>0&&pos==7){
                //和尾数条件保存
                if(list_sumMantissa!=null&&list_sumMantissa.size()>0){
                    initSumMantissaData();
                    if(CommonUtil.ListCheck(list_sumMantissa_flag,list_sumMantissa)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSumMantissaData();
                saveSumMantissan(list_sumMantissa,list_sumMantissa_flag,0);
            }else{//和值保存条件
                if(!TextUtils.isEmpty(sum)){
                    initSumData();
                    if(sum_flag.equals(sum)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                initSumData();
                if(!TextUtils.isEmpty(sum)){
                    //添加数据
                    sum_flag=sum;//判断重复标记
                    list_sum.clear();
                    list_sum.add(sum.substring(0,sum.indexOf("-")));
                    list_sum.add(sum.substring(sum.lastIndexOf("-")+1,sum.length()));
                    SingletonMapFilter.registerService(1 + "", list_sum);
                    TipsToast.showTips("已成功保存至过滤条件");
                }
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
                popWindow(pop_iv,selectDate,pos,tabLayout.getSelectedTabPosition(),isBlue,posDate,0);
            }
        }));
        //选双色球基本号监听
        if(list_double_num_check!=null&&list_double_num_check.size()>0&&pos==0){
            setDoubleCheckListener(list_double_num_check,tv_submit,num_trend_layout,adapter_red,adapter_blue,list_red_select,list_blue_select);
        }else if(list_double_span_check!=null&&list_double_span_check.size()>0&&pos==2){//跨度选择条件监听
            //跨度选择监听
            setSpanCheckListener(list_double_span_check,tv_submit);
        }else if(sum_rg!=null){//和值选择监听
            setSumSelectListener(sum_rg,tv_submit);
        }else if(list_double_sumMantissa_check!=null&&list_double_sumMantissa_check.size()>0&&pos==7){//和尾数选择条件监听
            //和尾数选择监听
            setSpanCheckListener(list_double_sumMantissa_check,tv_submit);
        }
    }
    //tablayout切换重新加载 红篮球
    private void reloadIgnoreData() {
        if(tabLayout.getSelectedTabPosition()==0){//红球
            adapter.reloadData(data_red_num,tabLayout.getSelectedTabPosition());
        }else{//蓝球
            adapter.reloadData(data_blue_num,tabLayout.getSelectedTabPosition());
        }

    }

    private void initData() {
        if(pos==0){//普通红篮球
            setRedBlueIgnore();//设置标题
            tv_submit.setText("选号");
            ignore_title.setText("号码");
        }else if(pos==2){//跨度
            setNomalTitle("跨度遗漏");//设置标题
            tv_submit.setText("条件");
            ignore_title.setText("跨度");
        }else if(pos==4){//和值
            setNomalTitle("和值遗漏");//设置标题
            tv_submit.setText("条件");
            ignore_title.setText("和值");
        }else if(pos==7){
            setNomalTitle("和尾数遗漏");//设置标题
            tv_submit.setText("条件");
            ignore_title.setText("和尾");
        }
        addHscrollview(pos);//添加下方滑动布局
        adapter=new NomalIgnoreAdapter(this);
        lv.setAdapter(adapter);
        //下方选号适配器
        adapter_red = new ArrayAdapter<String>(this, R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_blue = new ArrayAdapter<String>(this, R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_blue_select);
        gv_blue.setAdapter(adapter_blue);
    }
    private void initView() {
        title = (TextView) findViewById(R.id.toolbar_title);//普通标题
        ignore_title = (TextView) findViewById(R.id.ignore_nomal_title);//中间标记标题
        selectDate = (TextView) findViewById(R.id.selectNum_filter_ignore);//下拉框
        tv_submit = (TextView) findViewById(R.id.trend_submit);//普通标题
        tabLayout = (TabLayout) findViewById(R.id.trend_ignore_tablayout);//tablayout标题
        lv = (ListView) findViewById(R.id.ignore_nomal_lv);//普通不分区listview
        Hscrollview = (HorizontalScrollView) findViewById(R.id.trend_ignore_submit_layout);//下方选号区域
        num_trend_layout = (LinearLayout) findViewById(R.id.num_trend_layout);//下方选号提示布局
        gv_red = (MyGridView)findViewById(R.id.num_trend_gvRed);
        gv_blue = (MyGridView)findViewById(R.id.num_trend_gvBlue);
        pop_iv = (ImageView) findViewById(R.id.ignore_xiala);//下拉三角
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        title_layout = (RelativeLayout) findViewById(R.id.ignore_nomal_layout);
        scale_layout = (RelativeLayout) findViewById(R.id.trend_ignore_scale_layout);//出现次数占比布局
        probability_layout = (RelativeLayout) findViewById(R.id.trend_ignore_probability_layout);//欲出几率布局
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        tv_submit.setEnabled(false);
        setIgnoreDateChange(this);//初始化接口
        setIgnoreCalcBlue(this);//是否含篮球接口初始化
        data_red_num=new ArrayList<>();//接收接口号码数据
        data_blue_num=new ArrayList<>();
        data_condition=new ArrayList<>();//接口条件数据
        list_red_select=new ArrayList<>();//红球选择集合带0
        list_blue_select=new ArrayList<>();//蓝球选择集合带0
        list_red=new ArrayList<>();//红球保存集合
        list_blue=new ArrayList<>();//蓝球保存集合
        list_total=new ArrayList<>();//总保存集合
        list_span=new ArrayList<>();//跨度保存集合
        list_span_flag=new ArrayList<>();//跨度重复保存集合
        list_sumMantissa=new ArrayList<>();//和尾数保存集合
        list_sumMantissa_flag=new ArrayList<>();//和尾数重复保存集合
        list_sum=new ArrayList<>();//和值保存集合
    }
    //设置选项卡标题
    public void setRedBlueIgnore(){
        tabLayout.setVisibility(View.VISIBLE);//切换标题
        title.setVisibility(View.GONE);//切换标题
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("红球遗漏"));
        tabLayout.addTab(tabLayout.newTab().setText("蓝球遗漏"));
    }
    //设置普通标题
    public void setNomalTitle(String str){
        tabLayout.setVisibility(View.GONE);//切换标题
        title.setVisibility(View.VISIBLE);//切换标题
        title.setText(str);
    }
    //加载数据(第一次进入)
    public void LoadRedData(int type,int date){
        //请求网络 下载红篮球数据
        NetHelper.LOTTERY_API.getMissTrend("10002",type,date).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                controlHoscrollVisible(true);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_red_num.clear();
                data_red_num=args.getMiss();
                adapter.loadData(data_red_num,pos);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }

    private void controlHoscrollVisible(boolean isVisible) {
        //显示下方滑动布局
        ViewUtils.showViewsVisible(isVisible,findViewById(R.id.trend_view),findViewById(R.id.trend_submit),findViewById(R.id.trend_ignore_submit_layout));
    }

    //蓝球数据只下载不展示
    private void ReLoadRedData(int type,int date) {
        NetHelper.LOTTERY_API.getMissTrend("10002",type,date).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                data_red_num.clear();
                data_red_num=args.getMiss();
            }
        });
    }
    //蓝球数据只下载不展示
    private void LoadBlueData(int type,int date) {
        NetHelper.LOTTERY_API.getMissTrend("10002",type,date).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                data_blue_num.clear();
                data_blue_num=args.getMiss();
            }
        });
    }
    //切换期数重新请求蓝球数据
    private void ReLoadBlueData(int type,int date) {
        NetHelper.LOTTERY_API.getMissTrend("10002",type,date).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                controlHoscrollVisible(true);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_blue_num.clear();
                data_blue_num=args.getMiss();
                adapter.reloadData(data_blue_num,tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }
    //和值  跨度  和尾数
    private void LoadConditionData(int targetID,int date,int type){
        NetHelper.LOTTERY_API.getConditionMissTrend("10002",type,date,targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>(){
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                AVLoading.setVisibility(View.GONE);
                controlHoscrollVisible(isBlue);
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
        if(pos==4){//和值
            view=LayoutInflater.from(this).inflate(R.layout.ignore_sum_scroll,null);
            sum_rg= (RadioGroup) view.findViewById(R.id.sum_rg);
        }else{//跨度和基本号 和尾数
            view= LayoutInflater.from(this).inflate(pos==0?R.layout.ignore_selectnum_scroll:(pos==2?R.layout.ignore_span_scroll:R.layout.ignore_d3_span_scroll),null);
            //初始化获取控件
            LinearLayout layout= (LinearLayout) view.findViewById(pos==0?R.id.num_trend_ll:R.id.span_num_ll);
            for(int i=0;i<layout.getChildCount();i++){
                if(layout.getChildAt(i) instanceof CheckBox){
                    if(pos==0){
                        list_double_num_check.add((CheckBox) layout.getChildAt(i));//基本号
                    }else if(pos==2){
                        list_double_span_check.add((CheckBox) layout.getChildAt(i));//跨度
                    }else{
                        list_double_sumMantissa_check.add((CheckBox) layout.getChildAt(i));//和尾数
                    }
                }
            }
        }
        if(view!=null){
            Hscrollview.addView(view);
        }
    }
    //号码数据
    public void initSubmitData(){
        list_red.clear();
        list_blue.clear();
        for (int i = 0; i < list_double_num_check.size(); i++) {
            if (i < 33) {
                if (list_double_num_check.get(i).isChecked()) {
                    list_red.add((i + 1) + "");
                }
            } else {
                if (list_double_num_check.get(i).isChecked()) {
                    list_blue.add((i - 32) + "");
                }
            }
        }
    }
    //跨度数据
    public void initSpanData(){
        list_span.clear();
        for (int i = 0; i < list_double_span_check.size(); i++) {
            if(list_double_span_check.get(i).isChecked()){
                list_span.add(i+6+"");
            }
        }
    }
    //和尾数数据
    public void initSumMantissaData(){
        list_sumMantissa.clear();
        for (int i = 0; i < list_double_sumMantissa_check.size(); i++) {
            if(list_double_sumMantissa_check.get(i).isChecked()){
                list_sumMantissa.add(i+"");
            }
        }
    }
    //和值数据
    public void initSumData(){
        sum="";
        for(int i=0;i<sum_rg.getChildCount();i++){
            if(sum_rg.getChildAt(i) instanceof RadioButton){
                if(((RadioButton) sum_rg.getChildAt(i)).isChecked()){
                    sum=sum_data[i];
                }
            }
        }
    }
    //请求网络 默认数据
    public void initNetdata(){
        if(pos==0){
            LoadRedData(3,100);//请求红球网络加载数据 刷新
            LoadBlueData(4,100);//请求蓝球网络加载数据 刷新
        }else if(pos==4){
            LoadConditionData(10002,100,2);//请求网络加载和值
        }else if(pos==2){
            LoadConditionData(10013,100,2);//请求网络加载跨度
        }else if(pos==7){
            LoadConditionData(10027,100,2);//请求网络加载和尾数
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
        if(pos==0){//红篮球更改期数
            if(tabLayout.getSelectedTabPosition()==0){
                LoadRedData(3,MapUtils.DATE_ARRAY[position]);//加载两个数据 展示一个
                LoadBlueData(4,MapUtils.DATE_ARRAY[position]);
            }else{
                ReLoadBlueData(4,MapUtils.DATE_ARRAY[position]);
                ReLoadRedData(3,MapUtils.DATE_ARRAY[position]);
            }
        }else if(pos==4){
            LoadConditionData(10002,MapUtils.DATE_ARRAY[position],type);//请求网络加载和值
        }else if(pos==2) {
            LoadConditionData(10013, MapUtils.DATE_ARRAY[position], type);//请求网络加载跨度
        }else if(pos==7){
            LoadConditionData(10027, MapUtils.DATE_ARRAY[position], type);//请求网络加载和尾数
        }

    }
    /**
     * 次数占比和欲出几率排序
     * @param type
     */
    public void sort(boolean type){
        if(pos==0){//红篮球排序
            if(tabLayout.getSelectedTabPosition()==0){//红球排序
                if(type){//次数占比排序
                    if(scale_sort_red){
                        sortShowBigBiMax(data_red_num,0);
                    }else{//倒序
                        sortShowBiMax(data_red_num,0);
                    }
                    scale_sort_red=!scale_sort_red;
                    emerge_sort_red=true;//保证 欲出几率 下次为大到小
                }else{//欲出几率排序
                    if(emerge_sort_red){
                        sortBigEmerge(data_red_num,0);
                    }else{//倒序
                        sortEmerge(data_red_num,0);
                    }
                    emerge_sort_red=!emerge_sort_red;
                    scale_sort_red=true;//保证 次数占比 下次为大到小
                }
            }else{//蓝球排序
                if(type){//次数占比排序
                    if(scale_sort_blue){
                        sortShowBigBiMax(data_blue_num,0);
                    }else{//倒序
                        sortShowBiMax(data_blue_num,0);
                    }
                    scale_sort_blue=!scale_sort_blue;
                    emerge_sort_blue=true;//保证 欲出几率 下次为大到小
                }else{//欲出几率排序
                    if(emerge_sort_blue){
                        sortBigEmerge(data_blue_num,0);
                    }else{//倒序
                        sortEmerge(data_blue_num,0);
                    }
                    emerge_sort_blue=!emerge_sort_blue;
                    scale_sort_blue=true;//保证 次数占比 下次为大到小
                }
            }
            adapter.notifyDataSetChanged();
        }else {//跨度  和值 和尾数排序
            sortCondition(type);
            adapter.notifyDataSetChanged();
        }
    }
    //处理是否含篮球
    @Override
    public void isCalcBlue(boolean calcBlue,int position) {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> isCalcBlue(calcBlue,position)));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        //下方滑动显示
        controlHoscrollVisible(calcBlue);
        initSort(scale_sort_red,scale_sort_blue,emerge_sort_red,
                emerge_sort_blue,scale_sort_condition,emerge_sort_condition);//初始化排序状态
        isBlue=calcBlue;
        //跨度和值  是否含篮球
        if(pos==4){
            LoadConditionData(10002,MapUtils.DATE_ARRAY[position],calcBlue?2:1);//请求网络加载和值
        }else if(pos==2){
            LoadConditionData(10013,MapUtils.DATE_ARRAY[position],calcBlue?2:1);//请求网络加载跨度
        }else if(pos==7){
            LoadConditionData(10027,MapUtils.DATE_ARRAY[position],calcBlue?2:1);//请求网络加载和尾数
        }
    }
    public void sortCondition(boolean type){
        if(type){//次数占比排序
            if(scale_sort_condition){
                sortShowBigBiMax(data_condition,0);
            }else{//倒序
                sortShowBiMax(data_condition,0);
            }
            scale_sort_condition=!scale_sort_condition;
            emerge_sort_condition=true;//保证下次 点击欲出几率 是大到小
        }else{//欲出几率排序
            if(emerge_sort_condition){
                sortBigEmerge(data_condition,0);
            }else{//倒序
                sortEmerge(data_condition,0);
            }
            emerge_sort_condition=!emerge_sort_condition;
            scale_sort_condition=true;//保证下次 点击次数占比 是大到小
        }
    }
}
