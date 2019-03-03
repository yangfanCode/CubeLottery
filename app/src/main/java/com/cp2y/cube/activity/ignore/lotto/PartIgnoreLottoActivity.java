package com.cp2y.cube.activity.ignore.lotto;

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
import com.cp2y.cube.activity.ignore.DoubleBaseActivity;
import com.cp2y.cube.adapter.PartIgnoreAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonMapFilter;
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
 * 分区形态遗漏activity  展示大乐透,奇偶,大小,除3余遗漏
 */
public class PartIgnoreLottoActivity extends DoubleBaseActivity implements MyInterface.IgnoreDateChange, MyInterface.IgnoreIsBlue {
    private List<String> list_divide_num, list_num_check_data, list_pattern_check_data;//保存集合
    private List<String> list_divide_pattern;//保存集合
    private List<String> list_odd_num;//保存集合
    private List<String> list_odd_pattern;//保存集合
    private List<String> list_big_num;//保存集合
    private List<String> list_big_pattern;//保存集合
    private int pos = -1;
    private TextView title, tv_submit, selectDate, ignore_title;
    private TabLayout tabLayout;
    private ListView lv;
    private PartIgnoreAdapter adapter;
    private List<String> list;
    private HorizontalScrollView Hscrollview;
    private List<List<IgnoreTrendNumModel.MissData>> data_condition_num;//数据
    private List<List<IgnoreTrendNumModel.MissData>> data_condition_pattern;//数据
    private LinearLayout num_trend_layout;
    private String[] oddData = {"奇", "偶"};
    private String[] bigData={"大","小"};
    private String[] oddNumData = {"7:0", "6:1", "5:2", "4:3", "3:4", "2:5", "1:6", "0:7"};
    private ImageView pop_iv,netOff;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout title_layout, scale_layout, probability_layout;
    private int type = 0;//记录是否含篮球
    private boolean scale_sort_condition_num = true;//个数出现次数条件排序
    private boolean emerge_sort_condition_num = true;//形态欲出几率条件排序
    private boolean scale_sort_condition_pattern = true;//个数出现次数条件排序
    private boolean emerge_sort_condition_pattern = true;//形态欲出几率条件排序
    private List<RadioGroup> list_divideNum_check = new ArrayList<>();//除3余个数
    private List<RadioGroup> list_dividePattern_check = new ArrayList<>();//除3余形态选择控件
    private List<RadioGroup> list_oddPattern_check = new ArrayList<>();//奇偶比形态选择控件
    private List<CheckBox> list_oddNum_check = new ArrayList<>();//奇偶比选择控件
    private List<RadioGroup> list_bigPattern_check=new ArrayList<>();//大小形态选择控件
    private List<CheckBox> list_bigNum_check=new ArrayList<>();//大小比选择控件
    private int posDate = 2;//记录期数
    private boolean isBlue = true;//记录是否含篮球

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_ignore);
        setNavigationIcon(R.mipmap.back_chevron);//回退按钮
        setNavigationOnClickListener((v -> finish()));
        pos = getIntent().getIntExtra("pos", 0);
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
                    SingletonMapFilter.registerLottoService("10", list_yu);
                }
            } else if (list_dividePattern_check != null && list_dividePattern_check.size() > 0 && pos == 3 && tabLayout.getSelectedTabPosition() == 1) {
                //除3余形态保存
                initDividePattern();
                if (list_pattern_check_data.size() == 7) {
                    if (CommonUtil.checkSame(list_divide_pattern, list_pattern_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if (list_divide_pattern != null && list_divide_pattern.size() > 0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_divide_pattern);//检测重复集合
                    SingletonMapFilter.registerLottoService("9", list_divide_pattern);
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
                    SingletonMapFilter.registerLottoService("8", list_odd_num);
                }
            } else if (list_oddPattern_check != null && list_oddPattern_check.size() > 0 && pos == 5 && tabLayout.getSelectedTabPosition() == 1) {
                //奇偶形态保存
                initOddPattern();
                if (list_pattern_check_data.size() == 7) {
                    if (CommonUtil.checkSame(list_odd_pattern, list_pattern_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if (list_odd_pattern != null && list_odd_pattern.size() > 0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_odd_pattern);//检测重复集合
                    SingletonMapFilter.registerLottoService("7", list_odd_pattern);
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
                    SingletonMapFilter.registerLottoService("13", list_big_num);
                }
            }else if(list_bigPattern_check!=null&&list_bigPattern_check.size()>0&&pos==6&&tabLayout.getSelectedTabPosition()==1){
                //大小形态保存
                initBigPattern();
                if(list_pattern_check_data.size()==7){
                    if(CommonUtil.checkSame(list_big_pattern,list_pattern_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if(list_big_pattern!=null&&list_big_pattern.size()>0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_big_pattern);//检测重复集合
                    SingletonMapFilter.registerLottoService("12", list_big_pattern);
                }
            }
            TipsToast.showTips("已成功保存至过滤条件");
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
            if (isPopShowing()) {
                closePop(pop_iv);
            } else {
                popWindow(pop_iv, selectDate, pos, tabLayout.getSelectedTabPosition(), isBlue, posDate, 1);
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
        ignore_title.setText(tabLayout.getSelectedTabPosition()==0?(pos==3?"个数":(pos==5?"奇偶比":"大小比")):"形态");
        if (tabLayout.getSelectedTabPosition() == 0) {//个数
            if (data_condition_num != null && data_condition_num.size() > 0) {
                addHscrollview(pos, true);
                if (list_divideNum_check != null && list_divideNum_check.size() > 0 && pos == 3) {
                    setDivideNumListener(list_divideNum_check, tv_submit);
                } else if (list_oddNum_check != null && list_oddNum_check.size() > 0 && pos == 5) {//奇偶比监听
                    setOddNumListener(list_oddNum_check, tv_submit);
                }else if(list_bigNum_check != null && list_bigNum_check.size() > 0 && pos == 6){
                    setOddNumListener(list_bigNum_check, tv_submit);//大小比监听
                }
                adapter.loadData(data_condition_num, pos, tabLayout.getSelectedTabPosition());
            } else {
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        } else {//形态
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
                adapter.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            } else {
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        }
    }

    private void initData() {
        if (pos == 3) {//除3余
            setDivideIgnore();//设置标题
            ignore_title.setText(tabLayout.getSelectedTabPosition() == 0 ? "个数" : "形态");
        } else if (pos == 5) {//奇偶
            setOddIgnore();//设置标题
            ignore_title.setText(tabLayout.getSelectedTabPosition() == 0 ? "奇偶比" : "形态");
        } else if(pos==6){//大小
            setBigIgnore();//设置标题
            ignore_title.setText("大小比");
        }
        addHscrollview(pos, true);//添加下方滑动布局
        adapter = new PartIgnoreAdapter(this, 1);
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
        num_trend_layout = (LinearLayout) findViewById(R.id.num_trend_layout);//下方选号提示布局
        pop_iv = (ImageView) findViewById(R.id.ignore_xiala);//下拉三角
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        title_layout = (RelativeLayout) findViewById(R.id.ignore_nomal_layout);
        scale_layout = (RelativeLayout) findViewById(R.id.trend_ignore_scale_layout);//出现次数占比布局
        probability_layout = (RelativeLayout) findViewById(R.id.trend_ignore_probability_layout);//欲出几率布局
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        data_condition_num = new ArrayList<>();//数据集合
        data_condition_pattern = new ArrayList<>();//数据集合
        tv_submit.setEnabled(false);
        setIgnoreDateChange(this);//初始化接口
        setIgnoreCalcBlue(this);
        list = new ArrayList<>();//数据
        list_divide_num = new ArrayList<>();//除3余个数保存集合
        list_divide_pattern = new ArrayList<>();//除3余形态保存集合
        list_odd_num = new ArrayList<>();//奇偶比存集合
        list_odd_pattern = new ArrayList<>();//奇偶形态保存集合
        list_big_num=new ArrayList<>();//大小比存集合
        list_big_pattern=new ArrayList<>();//大小形态保存集合
        list_pattern_check_data = new ArrayList<>();//重复保存集合
        list_num_check_data = new ArrayList<>();//重复保存集合
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

    private void controlHoscrollVisible(boolean isVisible) {
        //显示下方滑动布局
        ViewUtils.showViewsVisible(isVisible, findViewById(R.id.trend_view), findViewById(R.id.trend_submit), findViewById(R.id.trend_ignore_submit_layout));
    }

    //加载个数数据
    private void LoadNumData(int targetID, int date, int type) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10088", type, date, targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                //下方滑动显示
                controlHoscrollVisible(isBlue);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition_num.clear();
                data_condition_num = args.getMiss();
                adapter.loadData(data_condition_num, pos, tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }

    //加载形态数据
    private void LoadPatternData(int targetID, int date, int type) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10088", type, date, targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                //下方滑动显示
                controlHoscrollVisible(isBlue);
                AVLoading.setVisibility(View.GONE);
                title_layout.setVisibility(View.VISIBLE);
                data_condition_pattern.clear();
                data_condition_pattern = args.getMiss();
                if ((pos == 3 || pos == 5||pos==6) && tabLayout.getSelectedTabPosition() == 1 && type == 5) {
                    data_condition_pattern.remove(data_condition_pattern.size() - 1);//不含蓝球删除最后2条
                    data_condition_pattern.remove(data_condition_pattern.size() - 1);//不含蓝球删除最后2条
                }
                adapter.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("网络请求失败");
            }
        });
    }

    //加载个数数据
    private void ReLoadNumData(int targetID, int date, int type) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10088", type, date, targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                data_condition_num.clear();
                data_condition_num = args.getMiss();
            }
        });
    }

    //加载形态数据
    private void ReLoadPatternData(int targetID, int date, int type) {
        NetHelper.LOTTERY_API.getConditionMissTrend("10088", type, date, targetID).subscribe(new SafeOnlyNextSubscriber<IgnoreTrendNumModel>() {
            @Override
            public void onNext(IgnoreTrendNumModel args) {
                super.onNext(args);
                data_condition_pattern.clear();
                data_condition_pattern = args.getMiss();
                //从个数页面 选择不含蓝球 删除
                if ((pos == 3 || pos == 5||pos==6) && tabLayout.getSelectedTabPosition() == 0 && type == 5) {
                    data_condition_pattern.remove(data_condition_pattern.size() - 1);//不含蓝球删除最后2条
                    data_condition_pattern.remove(data_condition_pattern.size() - 1);//不含蓝球删除最后2条
                }
            }
        });
    }

    //添加下方滑动布局_初始化控件 true:个数  false:形态
    public void addHscrollview(int pos, boolean type) {
        Hscrollview.removeAllViews();
        View view = null;
        if (pos == 3) {//除3余控件初始化
            list_divideNum_check.clear();
            list_dividePattern_check.clear();
            view = LayoutInflater.from(this).inflate(type == true ? R.layout.ignore_divide_number_select : R.layout.ignore_lotto_divide_pattern_select, null);
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
        } else {//奇偶大小控件初始化
            list_oddNum_check.clear();
            list_oddPattern_check.clear();
            list_bigNum_check.clear();
            list_bigPattern_check.clear();
            if (type) {
                view = LayoutInflater.from(this).inflate(R.layout.ignore_odd_number_select, null);
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.odd_number_ll);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    if (layout.getChildAt(i) instanceof CheckBox) {
                        if(pos==5){
                            list_oddNum_check.add((CheckBox) layout.getChildAt(i));
                        }else{
                            list_bigNum_check.add((CheckBox) layout.getChildAt(i));
                        }
                    }
                }
            } else {
                view = LayoutInflater.from(this).inflate(pos==5?R.layout.ignore_lotto_odd_pattern_select:R.layout.ignore_lotto_big_pattern_select, null);
                LinearLayout layout = (LinearLayout) view.findViewById(R.id.add_pattern_ll);
                for (int i = 0; i < layout.getChildCount(); i++) {
                    if (layout.getChildAt(i) instanceof RadioGroup) {
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

    //除3余形态数据
    private void initDividePattern() {
        list_divide_pattern.clear();
        for (int i = 0; i < 7; i++) {
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
        for (int i = 0; i < 7; i++) {
            list_odd_pattern.add("奇偶");
        }
        //遍历radiogroup集合
        for (int i = 0; i < list_oddPattern_check.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_oddPattern_check.get(i).getChildCount(); j++) {
                View child = list_oddPattern_check.get(i).getChildAt(j);
                if (child instanceof RadioButton) {
                    //如果选择,更改数据
                    if (((RadioButton) list_oddPattern_check.get(i).getChildAt(j)).isChecked()) {
                        list_odd_pattern.set(i, oddData[j]);
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
        for (int i = 0; i < 7; i++) {
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
    //更换期数
    @Override
    public void IgnoreChange(int position, int type) {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> IgnoreChange(position,type)));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initSort(scale_sort_condition_num, emerge_sort_condition_num, scale_sort_condition_pattern, emerge_sort_condition_pattern);//初始化排序
        posDate = position;
        if (pos == 3) {//除3余切换选项卡
            if (tabLayout.getSelectedTabPosition() == 0) {//个数
                LoadNumData(10022, MapUtils.DATE_ARRAY[position], type);
                ReLoadPatternData(10022, MapUtils.DATE_ARRAY[position], type == 2 ? 6 : 5);//个数形态type不同
            } else {
                LoadPatternData(10022, MapUtils.DATE_ARRAY[position], type);
                ReLoadNumData(10022, MapUtils.DATE_ARRAY[position], type == 6 ? 2 : 1);
            }
        } else if (pos == 5) {//奇偶切换选项卡
            if (tabLayout.getSelectedTabPosition() == 0) {//个数
                LoadNumData(10019, MapUtils.DATE_ARRAY[position], type);
                ReLoadPatternData(10019, MapUtils.DATE_ARRAY[position], type == 4 ? 6 : 5);//个数形态type不同
            } else {
                LoadPatternData(10019, MapUtils.DATE_ARRAY[position], type);
                ReLoadNumData(10019, MapUtils.DATE_ARRAY[position], type == 6 ? 4 : 3);
            }
        }else if(pos==6){//大小
            if(tabLayout.getSelectedTabPosition()==0){//个数
                LoadNumData(10004,MapUtils.DATE_ARRAY[position],type);
                ReLoadPatternData(10004,MapUtils.DATE_ARRAY[position],type==4?6:5);//个数形态type不同
            }else{
                LoadPatternData(10004,MapUtils.DATE_ARRAY[position],type);
                ReLoadNumData(10004,MapUtils.DATE_ARRAY[position],type==6?4:3);
            }
        }
    }

    /**
     * 次数占比和欲出几率排序
     *
     * @param type
     */
    public void sort(boolean type) {
        //排序 除3余排序 和奇偶形态排序
        if (pos == 3 || (pos == 5 && tabLayout.getSelectedTabPosition() == 1)||(pos==6&&tabLayout.getSelectedTabPosition()==1)) {
            if (tabLayout.getSelectedTabPosition() == 0) {//除3余个数排序
                if (type) {//次数占比排序  3次
                    if (scale_sort_condition_num) {
                        setBiBigSort(data_condition_num, 3);
                    } else {//倒序
                        setBiSmallSort(data_condition_num, 3);
                    }
                    scale_sort_condition_num = !scale_sort_condition_num;
                    emerge_sort_condition_num = true;
                } else {//欲出几率排序
                    if (emerge_sort_condition_num) {
                        setEmergeBigSort(data_condition_num, 3);
                    } else {//倒序
                        setEmergeSmallSort(data_condition_num, 3);
                    }
                    emerge_sort_condition_num = !emerge_sort_condition_num;
                    scale_sort_condition_num = true;
                }
                adapter.loadData(data_condition_num, pos, tabLayout.getSelectedTabPosition());
            } else {//形态排序-7次
                if (type) {//次数占比排序
                    if (scale_sort_condition_pattern) {
                        setBiBigSort(data_condition_pattern, isBlue ? 7 : 5);
                    } else {//倒序
                        setBiSmallSort(data_condition_pattern, isBlue ? 7 : 5);
                    }
                    scale_sort_condition_pattern = !scale_sort_condition_pattern;
                    emerge_sort_condition_pattern = true;
                } else {//欲出几率排序
                    if (emerge_sort_condition_pattern) {
                        setEmergeBigSort(data_condition_pattern, isBlue ? 7 : 5);
                    } else {//倒序
                        setEmergeSmallSort(data_condition_pattern, isBlue ? 7 : 5);
                    }
                    emerge_sort_condition_pattern = !emerge_sort_condition_pattern;
                    scale_sort_condition_pattern = true;
                }
                adapter.loadData(data_condition_pattern, pos, tabLayout.getSelectedTabPosition());
            }
        } else {
            //奇偶比
            if (type) {//次数占比排序
                if (scale_sort_condition_num) {
                    sortShowBigBiMax(data_condition_num, 0);
                } else {//倒序
                    sortShowBiMax(data_condition_num, 0);
                }
                scale_sort_condition_num = !scale_sort_condition_num;
                emerge_sort_condition_num = true;
            } else {//欲出几率排序
                if (emerge_sort_condition_num) {
                    sortBigEmerge(data_condition_num, 0);
                } else {//倒序
                    sortEmerge(data_condition_num, 0);
                }
                emerge_sort_condition_num = !emerge_sort_condition_num;
                scale_sort_condition_num = true;
            }
            adapter.loadData(data_condition_num, pos, tabLayout.getSelectedTabPosition());
        }

    }

    @Override
    public void isCalcBlue(boolean calcBlue, int position) {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> isCalcBlue(calcBlue,position)));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        controlHoscrollVisible(calcBlue);
        initSort(scale_sort_condition_num, emerge_sort_condition_num, scale_sort_condition_pattern, emerge_sort_condition_pattern);//初始化排序
        isBlue = calcBlue;
        if (pos == 3) {//除3余
            if (tabLayout.getSelectedTabPosition() == 0) {//个数
                LoadNumData(10022, MapUtils.DATE_ARRAY[position], calcBlue ? 2 : 1);
                ReLoadPatternData(10022, MapUtils.DATE_ARRAY[position], calcBlue ? 6 : 5);//个数形态type不同
            } else {
                LoadPatternData(10022, MapUtils.DATE_ARRAY[position], calcBlue ? 6 : 5);
                ReLoadNumData(10022, MapUtils.DATE_ARRAY[position], calcBlue ? 2 : 1);
            }
        } else  if(pos==5){//奇偶
            if (tabLayout.getSelectedTabPosition() == 0) {//个数
                LoadNumData(10019, MapUtils.DATE_ARRAY[position], calcBlue ? 4 : 3);
                ReLoadPatternData(10019, MapUtils.DATE_ARRAY[position], calcBlue ? 6 : 5);//个数形态type不同
            } else {
                LoadPatternData(10019, MapUtils.DATE_ARRAY[position], calcBlue ? 6 : 5);
                ReLoadNumData(10019, MapUtils.DATE_ARRAY[position], calcBlue ? 4 : 3);
            }
        }else if(pos==6){//大小
            if(tabLayout.getSelectedTabPosition()==0){//个数
                LoadNumData(10004,MapUtils.DATE_ARRAY[position],calcBlue?4:3);
                ReLoadPatternData(10004,MapUtils.DATE_ARRAY[position],calcBlue?6:5);//个数形态type不同
            }else{
                LoadPatternData(10004,MapUtils.DATE_ARRAY[position],calcBlue?6:5);
                ReLoadNumData(10004,MapUtils.DATE_ARRAY[position],calcBlue?4:3);
            }
        }
    }

    //请求网络 默认数据
    public void initNetData() {
        if (pos == 3) {//除3余
            LoadNumData(10022, 100, 2);//默认展示比值含篮球
            ReLoadPatternData(10022, 100, 6);
        }else if(pos==6){//大小
            LoadNumData(10004,100,4);//默认展示比值含篮球
            ReLoadPatternData(10004,100,6);
        } else {
            LoadNumData(10019, 100, 4);//默认展示比值含篮球
            ReLoadPatternData(10019, 100, 6);
        }
    }
}
