package com.cp2y.cube.fragment.chongqing2;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendD3BaseFragment;
import com.cp2y.cube.fragment.chongqing2.adapter.CQ2TrendDivideNumberAdapter;
import com.cp2y.cube.fragment.chongqing2.adapter.CQ2TrendDividePatternAdapter;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IssueAnalyseDataModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.HScrollView;
import com.cp2y.cube.widgets.HVListView;
import com.cp2y.cube.widgets.HVSRelativeLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 除3余走势图
 */
public class CQ2Chu3yuTrendFragment extends TrendD3BaseFragment {
    private List<String> list_pattern_data,list_pattern_check_data;
    private List<String> list_number_data,list_number_check_data;
    private List<RadioGroup>list_pattern;
    private List<RadioGroup>list_number;
    private View mRootView;
    private View view_pattern;
    private View view_number;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private TabLayout tabLayout;
    private TrendDateAdapter dateAdapter;
    private HVSRelativeLayout relativeLayout;
    private HVListView dataList;
    private CQ2TrendDividePatternAdapter dataAdapter1;
    private CQ2TrendDivideNumberAdapter dataAdapter2;
    private LinearLayout ll_pattern,ll_number;
    private TextView tv_submit;
    private View view_trend;
    private HScrollView HScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_cq3_chu3yu_trend, container, false);
        initSettingDialog(getString(R.string.divide_tips), MapUtils.D3_LOCATE_SUPPORT, new int[]{1});
        condition.setShowBaseNum(false);
        condition.setCalcBlue(false);
        setHasOptionsMenu(true);
        initView(mRootView);
        tabLayout = (TabLayout) mRootView.findViewById(R.id.tabs);
        TabLayout.Tab tab = tabLayout.newTab().setText("除3余形态");
        tab.select();
        tabLayout.addTab(tab);
        tabLayout.addTab(tabLayout.newTab().setText("除3余0/1/2个数"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                reloadView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        relativeLayout = (HVSRelativeLayout) mRootView.findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) mRootView.findViewById(R.id.scroll_layout1);
        dataList = (HVListView) mRootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter1 = new CQ2TrendDividePatternAdapter(getContext(),condition);
        dataAdapter2 = new CQ2TrendDivideNumberAdapter(getContext(),condition,7);
//        HScrollView scrollView = (HScrollView) mRootView.findViewById(R.id.scroll_layout4);
//        scrollView.setScrollViewListener((view, x, y, oldx, oldy) -> {
//            dataAdapter1.setXoffset(x);
//            dataAdapter1.notifyDataSetChanged();
//            dataAdapter2.setXoffset(x);
//            dataAdapter2.notify DataSetChanged();
//        });
        reloadView();
        initListener();
        return mRootView;
    }

    private void initListener() {
        tv_submit.setOnClickListener((v)->{
             //判断选择的页面
            if(tabLayout.getTabAt(0).isSelected()){
                initDataPattern();
                if(list_pattern_check_data.size()==2){
                    if(CommonUtil.checkSame(list_pattern_data,list_pattern_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if(list_pattern_data!=null&&list_pattern_data.size()>0){
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_pattern_data);//检测重复集合
                    SingletonMapFilter.registerCQ2Service("9", list_pattern_data);
                }
            }else{
                initDataNumber();
                if(list_number_check_data.size()>0){
                    if(CommonUtil.checkSame(list_number_data,list_number_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                if(list_number_data!=null&&list_number_data.size()>0){
                    List<String>list_yu=new ArrayList<String>();
                    list_yu.add(list_number_data.get(0));
                    list_yu.add(list_number_data.get(0));
                    list_yu.add(list_number_data.get(1));
                    list_yu.add(list_number_data.get(1));
                    list_number_check_data.clear();
                    list_number_check_data.addAll(list_number_data);
                    SingletonMapFilter.registerCQ2Service("10", list_yu);
                }
            }
            TipsToast.showTips("已成功保存至过滤条件");
        });
        //监听除3余形态点击
        for (int i = 0; i < list_pattern.size(); i++) {
            for (int j = 0; j < list_pattern.get(i).getChildCount(); j++) {
                View child=list_pattern.get(i).getChildAt(j);
                if(child instanceof RadioButton){
                    ((RadioButton) child).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if(isChecked) {
                                tv_submit.setEnabled(true);
                                tv_submit.setText("保存");
                                }
                            }
                    });
                }
            }
        }
        //监听除3余个数点击
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

    private void initView(View view) {
        list_pattern_data=new ArrayList<>();
        list_number_check_data=new ArrayList<>();
        list_pattern_check_data=new ArrayList<>();
        list_number_data=new ArrayList<>();
        list_pattern=new ArrayList<>();
        list_number=new ArrayList<>();
        HScrollView = (HScrollView)mRootView. findViewById(R.id.trend_submit_layout);
        container = (FrameLayout)getActivity(). findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView)getActivity(). findViewById(R.id.AVLoadingIndicator);
        tv_submit = (TextView)view. findViewById(R.id.trend_submit);
        for (int i = 0; i < 2; i++) {
            list_pattern_data.add("012");
        }
        tv_submit.setEnabled(false);

    }
    public void initRadioGroupPattern(){
        list_pattern.clear();
        for (int i = 0; i < ll_pattern.getChildCount(); i++) {
            View child =  ll_pattern.getChildAt(i);
            if(child instanceof RadioGroup){
                list_pattern.add((RadioGroup) child);
            }
        }
    }
    public void initRadioGroupNumber(){
        list_number.clear();
        for (int i = 0; i < ll_number.getChildCount(); i++) {
            View child= ll_number.getChildAt(i);
            if(child instanceof RadioGroup){
                list_number.add((RadioGroup) child);
            }
        }
    }
    private void initDataPattern() {
        list_pattern_data.clear();
        for (int i = 0; i < 2; i++) {
            list_pattern_data.add("012");
        }
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_pattern.get(0).getChildCount(); j++) {
                View child=list_pattern.get(0).getChildAt(j);
                if(child instanceof RadioButton){
                    //如果选择,更改数据
                    RadioButton button= (RadioButton) child;
                    if(button.isChecked()){
                        String text=button.getText().toString();
                        list_pattern_data.set(0,text.substring(0,1));
                        list_pattern_data.set(1,text.substring(1,text.length()));
                    }
                }
            }
    }
    private void initDataNumber() {
        list_number_data.clear();
            for (int j = 0; j < list_number.get(0).getChildCount(); j++) {
                View child=list_number.get(0).getChildAt(j);
                if(child instanceof RadioButton){
                    //如果选择,更改数据
                    RadioButton button= (RadioButton) child;
                    if(button.isChecked()){
                        String text[]=button.getText().toString().split(":");
                        list_number_data.add(text[0]);
                        list_number_data.add(text[1]);
                    }
                }
            }
    }

    /**
     * Tab切换重新加载
     */
    private void reloadView() {
        LinearLayout hScrollView = (LinearLayout) mRootView.findViewById(R.id.trend_title_ext_layout);
        hScrollView.removeAllViews();
        HScrollView submitLayout = (HScrollView) mRootView.findViewById(R.id.trend_submit_layout);
        submitLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (tabLayout.getSelectedTabPosition() == 0) {//选择形态
            hScrollView.addView(inflater.inflate(R.layout.cq2_divide_three_pattern_title, null));
            view_pattern=inflater.inflate(R.layout.trend_cq2_divide_pattern_select, null);
            submitLayout.addView(view_pattern);
            dataList.setAdapter(dataAdapter1);
            ll_pattern = (LinearLayout) view_pattern.findViewById(R.id.trend_divide_pattern_ll);
            //初始化radioGroup
            initRadioGroupPattern();
            //切换重置数据
            tv_submit.setEnabled(false);
            initListener();
        } else {
            hScrollView.addView(inflater.inflate(R.layout.cq2_three_number_title, null));
            view_number=inflater.inflate(R.layout.trend_cq2_divide_number_select, null);
            submitLayout.addView(view_number);
            dataList.setAdapter(dataAdapter2);
            ll_number = (LinearLayout) view_number.findViewById(R.id.trend_divide_number_ll);
            //初始化radioGroup
            initRadioGroupNumber();
            //切换重置数据
            tv_submit.setEnabled(false);
            initListener();
        }
        resizeLayout();
    }

    private void resizeLayout() {
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title),relativeLayout.findViewById(R.id.trend_submit_tips),relativeLayout.findViewById(R.id.trend_title_hold));//开奖号码隐藏
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(tabLayout.getSelectedTabPosition() == 0 ?312f:690f) + DisplayUtil.dip2px(condition.isShowBaseNum()?70f:0f), DisplayUtil.dip2px(30f * dataAdapter1.getCount()));//默认设置450dp
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowBaseNum());
        adapter.setSelect(1, condition.isShowMiss());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowBaseNum(adapter.containSelect(0));//是否显示开奖号
        condition.setShowMiss(adapter.containSelect(1));//是否显示遗漏
        //滑动修复位置错乱
        relativeLayout.keepOnHorizontal();
    }

    @Override
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter1.reverseData();
        dataAdapter2.reverseData();
    }

    @Override
    protected void reloadData() {
        resizeLayout();
        dataAdapter1.notifyDataSetChanged();
        dataAdapter2.initNums();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueAnalyse(10095, "10022", condition.getPeriod(), condition.getWeekDay())
                    .doOnTerminate(()->{condition.setCurrentPeriod(condition.getPeriod());condition.setCurrentWeekDay(condition.getWeekDay());})
                    .subscribe(new SafeOnlyNextSubscriber<IssueAnalyseDataModel>(){
                        @Override
                        public void onNext(IssueAnalyseDataModel args) {
                            super.onNext(args);
                            List<IssueAnalyseDataModel.IssueAnalyse> data = args.getData();
                            if (data != null) {
                                AVLoading.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                Collections.reverse(data);
                                List<String> dates = new ArrayList<>();
                                for (IssueAnalyseDataModel.IssueAnalyse date:data) {
                                    dates.add(date.getIss());
                                }
                                dateAdapter.reloadData(dates);
                                dataAdapter1.reloadData(data);
                                dataAdapter2.reloadData(data);
                                resizeLayout();
                                relativeLayout.scrollBottom();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            AVLoading.setVisibility(View.GONE);
                            //TipsToast.showTips("请检查网络设置");
                        }
                    });
        }
    }

    @Override
    public void onDoubleTaped() {
        closePop();
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_submit.getVisibility() == View.VISIBLE ? false : true, tv_submit, HScrollView, view_trend);
    }
//    //断网
//    @Override
//    public void netOff() {
//
//    }
}
