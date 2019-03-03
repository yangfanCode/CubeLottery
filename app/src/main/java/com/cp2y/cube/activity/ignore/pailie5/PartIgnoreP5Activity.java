package com.cp2y.cube.activity.ignore.pailie5;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
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
import com.cp2y.cube.activity.ignore.P5BaseActivity;
import com.cp2y.cube.activity.ignore.pailie5.adapter.P5PartConditionIgnoreAdapter;
import com.cp2y.cube.activity.ignore.pailie5.adapter.P5PartIgnoreAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.D3NumberMiss;
import com.cp2y.cube.model.IgnoreTrendNumModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * 分区形态遗漏activity  展示排列5,奇偶,除3余,号码遗漏 大小
 */
public class PartIgnoreP5Activity extends P5BaseActivity implements MyInterface.IgnoreDateChange {
    private List<String> list_divide_num, list_pattern_check_data, list_num_check_data;//保存集合
    private List<String> list_divide_pattern;//保存集合
    private List<String> list_odd_num;//保存集合
    private List<String> list_odd_pattern;//保存集合
    private List<String> list_big_num;//保存集合
    private List<String> list_big_pattern;//保存集合
    private List<List<D3NumberMiss.MissData>> data_condition_num;//数据
    private List<List<D3NumberMiss.MissData>> data_condition_special;//数据
    private List<List<IgnoreTrendNumModel.MissData>> data_condition_count;//数据
    private List<List<IgnoreTrendNumModel.MissData>> data_condition_pattern;//数据
    private int pos = -1;
    private TextView title, tv_submit, selectDate, ignore_title;
    private TabLayout tabLayout;
    private ListView lv;
    private P5PartIgnoreAdapter adapter;
    private P5PartConditionIgnoreAdapter adapter_condition;
    private HorizontalScrollView Hscrollview;
    private LinearLayout num_trend_layout;
    private String[] oddData = {"奇","偶"};
    private String[] bigData={"大","小"};
    private String[] oddNumData = {"5:0", "4:1", "3:2", "2:3", "1:4", "0:5"};
    private ImageView pop_iv, netOff;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout title_layout, scale_layout, probability_layout;
    private boolean scale_sort_condition_num = true;//个数出现次数条件排序
    private boolean emerge_sort_condition_num = true;//形态欲出几率条件排序
    private boolean scale_sort_condition_pattern = true;//个数出现次数条件排序
    private boolean emerge_sort_condition_pattern = true;//形态欲出几率条件排序
    private int posDate = 2;//记录期数
    private List<RadioGroup> list_divideNum_check = new ArrayList<>();//除3余个数
    private List<RadioGroup> list_dividePattern_check = new ArrayList<>();//除3余形态选择控件
    private List<RadioGroup> list_oddPattern_check = new ArrayList<>();//奇偶比形态选择控件
    private List<CheckBox> list_oddNum_check = new ArrayList<>();//奇偶比选择控件
    private List<RadioGroup> list_bigPattern_check=new ArrayList<>();//大小形态选择控件
    private List<CheckBox> list_bigNum_check=new ArrayList<>();//大小比选择控件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3d_part_ignore);
        setNavigationIcon(R.mipmap.back_chevron);//回退按钮
        setNavigationOnClickListener((v -> finish()));
        pos = getIntent().getIntExtra("pos", 0);
        initView();
        initData();//初始化静态布局
        initNetOff();//断网控制
        initListener();
    }

    private void initNetOff() {
        if (!CommonUtil.isNetworkConnected(this)) {//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
            AVLoading.setVisibility(View.GONE);
        } else {
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initNetData();
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
            if (list_divideNum_check != null && list_divideNum_check.size() > 0 && pos == 3 && tabLayout.getSelectedTabPosition() == 0) {
                //除3余个数保存
                initDivideNumber();
                if (list_num_check_data.size() > 0) {
                    if (CommonUtil.checkSame(list_divide_num, list_num_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                if (list_divide_num != null && list_divide_num.size() > 0) {
                    List<String> list_yu = new ArrayList<String>();
                    if (list_divide_num.size() == 1) {
                        list_yu.add(list_divide_num.get(0));
                        list_yu.add(list_divide_num.get(0));
                    } else {
                        list_yu.add(list_divide_num.get(0));
                        list_yu.add(list_divide_num.get(0));
                        list_yu.add(list_divide_num.get(1));
                        list_yu.add(list_divide_num.get(1));
                    }
                    list_num_check_data.clear();
                    list_num_check_data.addAll(list_divide_num);
                    SingletonMapFilter.registerP5Service("10", list_yu);
                }
            } else if (list_dividePattern_check != null && list_dividePattern_check.size() > 0 && pos == 3 && tabLayout.getSelectedTabPosition() == 1) {
                //除3余形态保存
                initDividePattern();
                if (list_pattern_check_data.size() == 5) {
                    if (CommonUtil.checkSame(list_divide_pattern, list_pattern_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if (list_divide_pattern != null && list_divide_pattern.size() > 0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_divide_pattern);//检测重复集合
                    SingletonMapFilter.registerP5Service("9", list_divide_pattern);
                }

            } else if (list_oddNum_check != null && list_oddNum_check.size() > 0 && pos == 5 && tabLayout.getSelectedTabPosition() == 0) {
                //奇偶比保存
                initOddNumber();
                if (list_num_check_data.size() > 0) {
                    if (CommonUtil.checkSame(list_odd_num, list_num_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                if (list_odd_num != null && list_odd_num.size() > 0) {
                    list_num_check_data.clear();
                    list_num_check_data.addAll(list_odd_num);
                    SingletonMapFilter.registerP5Service("8", list_odd_num);
                }
            } else if (list_oddPattern_check != null && list_oddPattern_check.size() > 0 && pos == 5 && tabLayout.getSelectedTabPosition() == 1) {
                //奇偶形态保存
                initOddPattern();
                if (list_pattern_check_data.size() == 5) {
                    if (CommonUtil.checkSame(list_odd_pattern, list_pattern_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if (list_odd_pattern != null && list_odd_pattern.size() > 0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_odd_pattern);//检测重复集合
                    SingletonMapFilter.registerP5Service("7", list_odd_pattern);
                }
            }else if(list_bigNum_check!=null&&list_bigNum_check.size()>0&&pos==6&&tabLayout.getSelectedTabPosition()==0){
                //大小比保存
                initBigNumber();
                if(list_num_check_data.size()>0){
                    if(CommonUtil.checkSame(list_big_num,list_num_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                if(list_big_num!=null&&list_big_num.size()>0) {
                    list_num_check_data.clear();
                    list_num_check_data.addAll(list_big_num);
                    SingletonMapFilter.registerP5Service("13", list_big_num);
                }
            }else if(list_bigPattern_check!=null&&list_bigPattern_check.size()>0&&pos==6&&tabLayout.getSelectedTabPosition()==1){
                //大小形态保存
                initBigPattern();
                if(list_pattern_check_data.size()==5){
                    if(CommonUtil.checkSame(list_big_pattern,list_pattern_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if(list_big_pattern!=null&&list_big_pattern.size()>0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_big_pattern);//检测重复集合
                    SingletonMapFilter.registerP5Service("12", list_big_pattern);
                }
            }
            TipsToast.showTips("已成功保存至过滤条件");
        });
        //两个排序监听
        scale_layout.setOnClickListener((v1 -> {//次数占比排序
            if (pos == 0) {
                sortNum(true);//排序方法
            } else {
                sortCondition(true);
            }
        }));
        probability_layout.setOnClickListener((v1 -> {//欲出几率排序
            if (pos == 0) {
                sortNum(false);//排序方法
            } else {
                sortCondition(false);
            }
        }));
        //下拉框期数监听
        selectDate.setOnClickListener((v -> {
            if (isPopShowing()) {
                closePop(pop_iv);
            } else {
                popWindow(pop_iv, selectDate, pos, tabLayout.getSelectedTabPosition(), posDate, 0);
            }
        }));
        //除3余个数监听-----------------------------
        if (list_divideNum_check != null && list_divideNum_check.size() > 0 && pos == 3) {
            setDivideNumListener(list_divideNum_check, tv_submit);
        } else if (list_oddNum_check != null && list_oddNum_check.size() > 0 && pos == 5) {//奇偶比监听
            setOddNumListener(list_oddNum_check, tv_submit);
        } else if(list_bigNum_check!=null&&list_bigNum_check.size()>0&&pos==6){
            setOddNumListener(list_bigNum_check,tv_submit);
        }
    }

    //tablayout切换重新加载
    private void reloadIgnoreData() {
        ignore_title.setText(tabLayout.getSelectedTabPosition() == 0 ? (pos == 0 ? "号码" : (pos == 3 ? "个数" : (pos==5?"奇偶比":"大小比"))) : (pos == 0 ? "号码" : "形态"));
        if (tabLayout.getSelectedTabPosition() == 0) {//个数
                if (data_condition_count != null && data_condition_count.size() > 0) {
                    addHscrollview(pos, true);
                    if (list_divideNum_check != null && list_divideNum_check.size() > 0 && pos == 3) {
                        setDivideNumListener(list_divideNum_check, tv_submit);
                    } else if (list_oddNum_check != null && list_oddNum_check.size() > 0 && pos == 5) {//奇偶比监听
                        setOddNumListener(list_oddNum_check, tv_submit);
                    } else if(list_bigNum_check != null && list_bigNum_check.size() > 0 && pos == 6){
                        setOddNumListener(list_bigNum_check, tv_submit);//大小比监听
                    }
                    adapter_condition.loadData(data_condition_count, pos, tabLayout.getSelectedTabPosition());
                } else {
                    AVLoading.setVisibility(View.GONE);
                    TipsToast.showTips("网络请求失败");
                }
        } else {
                if (data_condition_pattern != null && data_condition_pattern.size() > 0) {
                    addHscrollview(pos, false);//切换监听
                    if (list_oddPattern_check != null && list_oddPattern_check.size() > 0 && pos == 5) {
                        setDivideNumListener(list_oddPattern_check, tv_submit);//奇偶形态监听
                    }
                    if (list_dividePattern_check != null && list_dividePattern_check.size() > 0 && pos == 3) {
                        //除3余形态监听
                        setDivideNumListener(list_dividePattern_check, tv_submit);
                    }
                    if (list_bigPattern_check != null && list_bigPattern_check.size() > 0 && pos == 6) {
                        setDivideNumListener(list_bigPattern_check, tv_submit);//大小形态监听
                    }
                    adapter_condition.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
                } else {
                    AVLoading.setVisibility(View.GONE);
                    TipsToast.showTips("网络请求失败");
                }
        }
    }

    private void initData() {
        if (pos == 0) {//号码遗漏
            setNumIgnore();
            ignore_title.setText("号码");
            adapter = new P5PartIgnoreAdapter(this);
            lv.setAdapter(adapter);
        } else if (pos == 3) {//除3余
            setDivideIgnore();//设置标题
            ignore_title.setText("个数");
            adapter_condition = new P5PartConditionIgnoreAdapter(this);
            lv.setAdapter(adapter_condition);
        } else if (pos == 5) {//奇偶
            setOddIgnore();//设置标题
            ignore_title.setText("奇偶比");
            adapter_condition = new P5PartConditionIgnoreAdapter(this);
            lv.setAdapter(adapter_condition);
        } else if(pos==6){//大小
            setBigIgnore();//设置标题
            ignore_title.setText("大小比");
            adapter_condition = new P5PartConditionIgnoreAdapter(this);
            lv.setAdapter(adapter_condition);
        }
        if (pos != 0) {//号码遗漏
            addHscrollview(pos, true);//添加下方滑动布局
        }
    }

    private void initView() {
        title = (TextView) findViewById(R.id.toolbar_title);//普通标题
        ignore_title = (TextView) findViewById(R.id.ignore_nomal_title);//中间标记标题
        selectDate = (TextView) findViewById(R.id.selectNum_filter_ignore);//下拉框
        tv_submit = (TextView) findViewById(R.id.trend_submit);//提交按钮
        tabLayout = (TabLayout) findViewById(R.id.trend_ignore_tablayout);//tablayout标题
        lv = (ListView) findViewById(R.id.ignore_nomal_lv);//普通不分区listview
        Hscrollview = (HorizontalScrollView) findViewById(R.id.trend_ignore_submit_layout);//下方选号区域
        num_trend_layout = (LinearLayout) findViewById(R.id.num_trend_layout);//下方选号提示布局
        pop_iv = (ImageView) findViewById(R.id.ignore_xiala);//下拉三角
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        title_layout = (RelativeLayout) findViewById(R.id.ignore_nomal_layout);
        scale_layout = (RelativeLayout) findViewById(R.id.trend_ignore_scale_layout);//出现次数占比布局
        probability_layout = (RelativeLayout) findViewById(R.id.trend_ignore_probability_layout);//欲出几率布局
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        tv_submit.setEnabled(false);
        setIgnoreDateChange(this);//初始化接口
        data_condition_num = new ArrayList<>();
        data_condition_special = new ArrayList<>();
        data_condition_count = new ArrayList<>();//接收接口数据
        data_condition_pattern = new ArrayList<>();//接收接口数据
        list_divide_num = new ArrayList<>();//除3余个数保存集合
        list_divide_pattern = new ArrayList<>();//除3余形态保存集合
        list_odd_num = new ArrayList<>();//奇偶比存集合
        list_odd_pattern = new ArrayList<>();//奇偶形态保存集合
        list_big_num=new ArrayList<>();//大小比存集合
        list_big_pattern=new ArrayList<>();//大小形态保存集合
        list_pattern_check_data = new ArrayList<>();//重复保存集合
        list_num_check_data = new ArrayList<>();//重复保存集合
    }

    //设置号码选项卡标题
    public void setNumIgnore() {
        tabLayout.setVisibility(View.GONE);
        title.setVisibility(View.VISIBLE);
        title.setText("号码遗漏");
    }

    //设置除3选项卡标题
    public void setDivideIgnore() {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("除3余个数遗漏"));
        tabLayout.addTab(tabLayout.newTab().setText("除3余形态遗漏"));
    }

    //设置奇偶选项卡标题
    public void setOddIgnore() {
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("奇偶比遗漏"));
        tabLayout.addTab(tabLayout.newTab().setText("奇偶形态遗漏"));
    }
    //设置大小选项卡标题
    public void setBigIgnore(){
        tabLayout.removeAllTabs();
        tabLayout.addTab(tabLayout.newTab().setText("大小比遗漏"));
        tabLayout.addTab(tabLayout.newTab().setText("大小形态遗漏"));
    }

    //添加下方滑动布局_初始化控件 true:个数  false:形态
    public void addHscrollview(int pos, boolean type) {
        Hscrollview.removeAllViews();
        View view = null;
        if (pos == 3) {//除3余控件初始化
            list_divideNum_check.clear();
            list_dividePattern_check.clear();
            view = LayoutInflater.from(this).inflate(type == true ? R.layout.ignore_divide_p5_number_select : R.layout.ignore_divide_p5_pattern_select, null);
            LinearLayout layout = (LinearLayout) view.findViewById(type == true ? R.id.trend_divide_number_ll : R.id.trend_divide_pattern_ll);
            for (int i = 0; i < layout.getChildCount(); i++) {
                if (layout.getChildAt(i) instanceof RadioGroup) {
                    if (type) {
                        list_divideNum_check.add((RadioGroup) layout.getChildAt(i));
                    } else {
                        list_dividePattern_check.add((RadioGroup) layout.getChildAt(i));
                    }
                }
            }
        } else {//奇偶控件初始化
            list_oddNum_check.clear();
            list_oddPattern_check.clear();
            list_bigNum_check.clear();
            list_bigPattern_check.clear();
            if(type){
                view = LayoutInflater.from(this).inflate(R.layout.ignore_odd_p5_number_select , null);
                LinearLayout layout = (LinearLayout) view.findViewById( R.id.odd_number_ll);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    if (layout.getChildAt(i) instanceof CheckBox) {
                        if(pos==5){
                            list_oddNum_check.add((CheckBox) layout.getChildAt(i));
                        }else{
                            list_bigNum_check.add((CheckBox) layout.getChildAt(i));
                        }
                    }
                }
            }else{
                view = LayoutInflater.from(this).inflate(pos==5?R.layout.ignore_odd_p5_pattern_select:R.layout.ignore_big_p5_pattern_select , null);
                LinearLayout layout= (LinearLayout) view.findViewById(R.id.add_pattern_ll);
                for(int i=0;i<layout.getChildCount();i++){
                    if(layout.getChildAt(i) instanceof RadioGroup){
                        if(pos==5){
                            list_oddPattern_check.add((RadioGroup) layout.getChildAt(i));
                        }else{
                            list_bigPattern_check.add((RadioGroup) layout.getChildAt(i));
                        }
                    }
                }
            }


        }
        if (view != null) {
            Hscrollview.addView(view);
            tv_submit.setEnabled(false);//切换清空数据
        }
    }

    //由于数据不再一个接口 所以请求两次
    private void LoadCountData(int targetID, int date, int typeCount, int typePattern) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10004", typePattern, date, targetID).flatMap(IgnoreTrendNumModel -> {
            data_condition_pattern.clear();
            data_condition_pattern = IgnoreTrendNumModel.getMiss();
            return NetHelper.LOTTERY_API.getConditionMissTrend("10004", typeCount, date, targetID);
        }).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                controlHoscrollVisible(true);//显示下方滑动布局
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition_count.clear();
                data_condition_count = args.getMiss();
                adapter_condition.loadData(data_condition_count, pos, tabLayout.getSelectedTabPosition());
            }
        });
    }

    private void LoadPatternData(int targetID, int date, int typeCount, int typePattern) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10004", typeCount, date, targetID).flatMap(IgnoreTrendNumModel -> {
            data_condition_count.clear();
            data_condition_count = IgnoreTrendNumModel.getMiss();
            return NetHelper.LOTTERY_API.getConditionMissTrend("10004", typePattern, date, targetID);
        }).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                controlHoscrollVisible(true);//显示下方滑动布局
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition_pattern.clear();
                data_condition_pattern = args.getMiss();
                adapter_condition.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            }
        });
    }

    //除3余形态数据
    private void initDividePattern() {
        list_divide_pattern.clear();
        for (int i = 0; i < 5; i++) {
            list_divide_pattern.add("012");
        }
        //遍历radiogroup集合
        for (int i = 0; i < list_dividePattern_check.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_dividePattern_check.get(i).getChildCount(); j++) {
                View child = list_dividePattern_check.get(i).getChildAt(j);
                if (child instanceof RadioButton) {
                    //如果选择,更改数据
                    if (((RadioButton) list_dividePattern_check.get(i).getChildAt(j)).isChecked()) {
                        list_divide_pattern.set(i, j + "");
                    }
                }
            }
        }
    }

    //除3余个数数据
    private void initDivideNumber() {
        list_divide_num.clear();
        //遍历radiogroup集合
        for (int i = 0; i < list_divideNum_check.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_divideNum_check.get(i).getChildCount(); j++) {
                View child = list_divideNum_check.get(i).getChildAt(j);
                if (child instanceof RadioButton) {
                    //如果选择,更改数据
                    if (((RadioButton) list_divideNum_check.get(i).getChildAt(j)).isChecked()) {
                        list_divide_num.add(j + "");
                    }
                }
            }
        }
    }

    //奇偶形态
    private void initOddPattern() {
        list_odd_pattern.clear();
        for (int i = 0; i < 5; i++) {
            list_odd_pattern.add("奇偶");
        }
        //遍历radiogroup集合
        for (int i = 0; i < list_oddPattern_check.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_oddPattern_check.get(i).getChildCount(); j++) {
                View child=list_oddPattern_check.get(i).getChildAt(j);
                if(child instanceof RadioButton){
                    //如果选择,更改数据
                    if(((RadioButton)list_oddPattern_check.get(i).getChildAt(j)).isChecked()){
                        list_odd_pattern.set(i,oddData[j]);
                    }
                }
            }
        }
    }
    //奇偶比保存
    private void initOddNumber() {
        list_odd_num.clear();
        //遍历checkbox集合
        for (int i = 0; i < list_oddNum_check.size(); i++) {
            if (list_oddNum_check.get(i).isChecked()) {
                list_odd_num.add(oddNumData[i]);
            } else {
                list_odd_num.remove(oddNumData[i]);
            }
        }
    }
    //大小比保存
    private void initBigNumber() {
        list_big_num.clear();
        //遍历checkbox集合
        for (int i = 0; i < list_bigNum_check.size(); i++) {
            if(list_bigNum_check.get(i).isChecked()){
                list_big_num.add(oddNumData[i]);
            }else{
                list_big_num.remove(oddNumData[i]);
            }
        }
    }
    //大小形态
    private void initBigPattern() {
        list_big_pattern.clear();
        for (int i = 0; i < 5; i++) {
            list_big_pattern.add("大小");
        }
        //遍历radiogroup集合
        for (int i = 0; i < list_bigPattern_check.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_bigPattern_check.get(i).getChildCount(); j++) {
                View child=list_bigPattern_check.get(i).getChildAt(j);
                if(child instanceof RadioButton){
                    //如果选择,更改数据
                    if(((RadioButton)list_bigPattern_check.get(i).getChildAt(j)).isChecked()){
                        list_big_pattern.set(i,bigData[j]);
                    }
                }
            }
        }
    }
    //请求网络 默认数据
    public void initNetData() {
        if (pos == 0) {
            LoadNumData(100);//号码接口
        } else if (pos == 3) {//除3余
            LoadCountData(10022, 100, 1, 5);
        }else if(pos==6){//大小
            LoadCountData(10004, 100, 3, 5);
        } else {
            LoadCountData(10019, 100, 3, 5);
        }
    }

    //号码数据
    private void LoadNumData(int date) {
        NetHelper.LOTTERY_API.getNumberMissBy3D("10004", date).subscribe(new SafeOnlyNextSubscriber<D3NumberMiss>() {
            @Override
            public void onNext(D3NumberMiss args) {
                super.onNext(args);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition_num.clear();
                data_condition_pattern.clear();
                data_condition_num = args.getNum().getMiss();
                adapter.loadData(data_condition_num, pos);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }

    //更换期数
    @Override
    public void IgnoreChange(int position, int type) {
        if (!CommonUtil.isNetworkConnected(this)) {//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> IgnoreChange(position, type)));//点击加载
            AVLoading.setVisibility(View.GONE);
        } else {
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initSort(scale_sort_condition_num, emerge_sort_condition_num, scale_sort_condition_pattern, emerge_sort_condition_pattern);//初始化排序
        posDate = position;
        if (pos == 0) {//号码切换选项卡
            LoadNumData(MapUtils.DATE_ARRAY[position]);
        } else if (pos == 3) {//除3余
            if (tabLayout.getSelectedTabPosition() == 0) {//个数
                LoadCountData(10022, MapUtils.DATE_ARRAY[position], 1, 5);
            } else {
                LoadPatternData(10022, MapUtils.DATE_ARRAY[position], 1, 5);
            }
        } else if (pos == 5) {//奇偶切换选项卡
            if (tabLayout.getSelectedTabPosition() == 0) {//比值
                LoadCountData(10019, MapUtils.DATE_ARRAY[position], 3, 5);
            } else {
                LoadPatternData(10019, MapUtils.DATE_ARRAY[position], 3, 5);
            }
        } else if (pos == 6) {//大小切换选项卡
            if (tabLayout.getSelectedTabPosition() == 0) {//比值
                LoadCountData(10004, MapUtils.DATE_ARRAY[position], 3, 5);
            } else {
                LoadPatternData(10004, MapUtils.DATE_ARRAY[position], 3, 5);
            }
        }
    }

    /**
     * 次数占比和欲出几率排序
     *
     * @param type
     */
    public void sortCondition(boolean type) {//指标排序
        // 除3余排序 和奇偶形态排序
        if (pos == 3) {
            if (tabLayout.getTabAt(0).isSelected()) {//个数排序
                if (type) {//次数占比排序  3次
                    scaleSort(3, data_condition_count, 0);
                } else {//欲出几率排序
                    emergeSort(3, data_condition_count, 0);
                }
                adapter_condition.loadData(data_condition_count, pos, tabLayout.getSelectedTabPosition());
            } else {//形态排序
                if (type) {//次数占比排序  3次
                    scaleSort(5, data_condition_pattern, 1);
                } else {//欲出几率排序
                    emergeSort(5, data_condition_pattern, 1);
                }
                adapter_condition.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            }
        } else {
            //奇偶
            if (tabLayout.getTabAt(0).isSelected()) {//奇偶个数排序
                if (type) {//次数占比排序
                    scaleSort(1, data_condition_count, 0);
                } else {//欲出几率排序
                    emergeSort(1, data_condition_count, 0);
                }
                adapter_condition.loadData(data_condition_count, pos, tabLayout.getSelectedTabPosition());
            } else {
                if (type) {//次数占比排序
                    scaleSort(5, data_condition_pattern, 1);
                } else {//欲出几率排序
                    emergeSort(5, data_condition_pattern, 1);
                }
                adapter_condition.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            }
        }
    }

    //欲出几率排序次数
    private void emergeSort(int count, List<List<IgnoreTrendNumModel.MissData>> data, int num) {
        if (num == 0) {//个数和形态切换
            if (emerge_sort_condition_num) {
                setEmergeBigSorts(data, count);
            } else {//倒序
                setEmergeSmallSorts(data, count);
            }
            emerge_sort_condition_num = !emerge_sort_condition_num;
            scale_sort_condition_num = true;
        } else {
            if (emerge_sort_condition_pattern) {
                setEmergeBigSorts(data, count);
            } else {//倒序
                setEmergeSmallSorts(data, count);
            }
            emerge_sort_condition_pattern = !emerge_sort_condition_pattern;
            scale_sort_condition_pattern = true;
        }

    }

    //次数占比排序次数
    private void scaleSort(int count, List<List<IgnoreTrendNumModel.MissData>> data, int num) {
        if (num == 0) {//个数和形态切换
            if (scale_sort_condition_num) {
                setBiBigSorts(data, count);
            } else {//倒序
                setBiSmallSorts(data, count);
            }
            scale_sort_condition_num = !scale_sort_condition_num;
            emerge_sort_condition_num = true;
        } else {
            if (scale_sort_condition_pattern) {
                setBiBigSorts(data, count);
            } else {//倒序
                setBiSmallSorts(data, count);
            }
            scale_sort_condition_pattern = !scale_sort_condition_pattern;
            emerge_sort_condition_pattern = true;
        }
    }

    public void sortNum(boolean type) {//号码排序
        //基本号码排序
            if (type) {//次数占比排序  5次
                if (scale_sort_condition_num) {
                    setBiBigSort(data_condition_num, 5);
                } else {//倒序
                    setBiSmallSort(data_condition_num, 5);
                }
                scale_sort_condition_num = !scale_sort_condition_num;
                emerge_sort_condition_num = true;
            } else {//欲出几率排序
                if (emerge_sort_condition_num) {
                    setEmergeBigSort(data_condition_num, 5);
                } else {//倒序
                    setEmergeSmallSort(data_condition_num, 5);
                }
                emerge_sort_condition_num = !emerge_sort_condition_num;
                scale_sort_condition_num = true;
            }
            adapter.loadData(data_condition_num, pos);
    }

    private void controlHoscrollVisible(boolean isVisible) {
        //显示下方滑动布局
        ViewUtils.showViewsVisible(isVisible, findViewById(R.id.trend_view), findViewById(R.id.trend_submit), findViewById(R.id.trend_ignore_submit_layout));
    }
}
