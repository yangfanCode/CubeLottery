package com.cp2y.cube.fragment.chongqing5;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.adapter.TrendOddNumberAdapter;
import com.cp2y.cube.adapter.TrendOddPatternAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendP5BaseFragment;
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
 * 奇偶走势图
 */
public class CQ5JiouTrendFragment extends TrendP5BaseFragment {
    private View view_pattern;
    private View view_number;
    private View mRootView;
    private TabLayout tabLayout;
    private HVSRelativeLayout relativeLayout;
    private HVListView dataList;
    private TrendDateAdapter dateAdapter;
    private TrendOddPatternAdapter dataAdapter1;
    private TrendOddNumberAdapter dataAdapter2;
    private List<RadioGroup> list_pattern;
    private List<CheckBox> list_number;
    private List<String> list_pattern_data, list_num_check_data,list_number_data,list_pattern_check_data;
    private LinearLayout ll_pattern, ll_number;
    private TextView tv_submit;
    private String[] oddData = {"奇", "偶"};
    private String[] oddNumData = {"5:0", "4:1", "3:2", "2:3", "1:4", "0:5"};
    private int checkCount = 0;
    private boolean checkPattern = false;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private View view_jiou;
    private HScrollView HScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_cq5_jiou_trend, container, false);
        initSettingDialog(getString(R.string.odd_tips), MapUtils.D3_LOCATE_SUPPORT, new int[]{1});
        //默认不显示开奖号
        condition.setShowBaseNum(false);
        condition.setCalcBlue(false);
        setHasOptionsMenu(true);
        initView(mRootView);
        tabLayout = (TabLayout) mRootView.findViewById(R.id.tabs);
        TabLayout.Tab tab = tabLayout.newTab().setText("奇偶形态");
        tab.select();
        tabLayout.addTab(tab);
        tabLayout.addTab(tabLayout.newTab().setText("奇/偶个数"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                reloadView();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        relativeLayout = (HVSRelativeLayout) mRootView.findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) mRootView.findViewById(R.id.scroll_layout1);
        dataList = (HVListView) mRootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter1 = new TrendOddPatternAdapter(getContext(), condition, 4);
        dataAdapter2 = new TrendOddNumberAdapter(getContext(), condition, 4);
//        HScrollView scrollView = (HScrollView) mRootView.findViewById(R.id.scroll_layout4);
//        scrollView.setScrollViewListener((view, x, y, oldx, oldy) -> {
//            dataAdapter1.setXoffset(x);
//            dataAdapter1.notifyDataSetChanged();
//            dataAdapter2.setXoffset(x);
//            dataAdapter2.notifyDataSetChanged();
//        });
        reloadView();
        initLisenter();
        return mRootView;
    }

    private void initLisenter() {
        tv_submit.setOnClickListener((v) -> {
            //判断选择的页面
            if (tabLayout.getTabAt(0).isSelected()) {
                initDataPattern();
                if (list_pattern_check_data.size() == 5) {
                    if (CommonUtil.checkSame(list_pattern_data, list_pattern_check_data)) {
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                //保存过滤条件
                if (list_pattern_data != null && list_pattern_data.size() > 0) {
                    list_pattern_check_data.clear();
                    list_pattern_check_data.addAll(list_pattern_data);//检测重复集合
                    SingletonMapFilter.registerCQ5Service("7", list_pattern_data);
                }
            } else {
                initDataNumber();
                if(list_num_check_data.size()>0){
                    if(CommonUtil.checkSame(list_number_data,list_num_check_data)){
                        TipsToast.showTips("已存在过滤条件");
                        return;
                    }
                }
                if (list_number_data != null && list_number_data.size() > 0) {
                    list_num_check_data.clear();
                    list_num_check_data.addAll(list_number_data);
                    SingletonMapFilter.registerCQ5Service("8", list_number_data);
                }
            }
            TipsToast.showTips("已成功保存至过滤条件");
        });
        //遍历radiogroup集合
        for (int i = 0; i < list_pattern.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_pattern.get(i).getChildCount(); j++) {
                View child = list_pattern.get(i).getChildAt(j);
                if (child instanceof RadioButton) {
                    ((RadioButton) child).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                tv_submit.setEnabled(true);
                                tv_submit.setText("保存");
                                checkPattern = true;
                            }
                        }
                    });
                }
            }
        }
        for (int i = 0; i < list_number.size(); i++) {
            list_number.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //控制选择完毕,清空按钮变换
                    if (isChecked) {
                        checkCount++;
                        tv_submit.setEnabled(true);
                    } else {
                        checkCount--;
                    }
                    if (checkCount == 0) {
                        tv_submit.setEnabled(false);
                    }
                }
            });
        }

    }

    private void initView(View mRootView) {
        list_pattern_check_data=new ArrayList<>();
        list_num_check_data=new ArrayList<>();
        list_pattern = new ArrayList<>();
        list_number = new ArrayList<>();
        list_pattern_data = new ArrayList<>();
        list_number_data = new ArrayList<>();
        container = (FrameLayout) getActivity().findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView) getActivity().findViewById(R.id.AVLoadingIndicator);
        tv_submit = (TextView) mRootView.findViewById(R.id.trend_submit);
        HScrollView = (HScrollView) mRootView.findViewById(R.id.trend_submit_layout);
        view_jiou = (View) mRootView.findViewById(R.id.view_trend_jiou);
        for (int i = 0; i < 5; i++) {
            list_pattern_data.add("奇偶");
        }
        tv_submit.setEnabled(false);
    }

    private void reloadView() {
        LinearLayout titleExtLayout = (LinearLayout) mRootView.findViewById(R.id.trend_title_ext_layout);
        titleExtLayout.removeAllViews();
        HScrollView submitLayout = (HScrollView) mRootView.findViewById(R.id.trend_submit_layout);
        submitLayout.removeAllViews();
        int tag = tabLayout.getSelectedTabPosition();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if (tag == 0) {
            //标题下方文字
            titleExtLayout.addView(inflater.inflate(R.layout.p5_odd_even_pattern_title, null));
            view_pattern = inflater.inflate(R.layout.trend_p5_odd_pattern_select, null);
            submitLayout.addView(view_pattern);
            dataList.setAdapter(dataAdapter1);
            ll_pattern = (LinearLayout) view_pattern.findViewById(R.id.add_pattern_ll);
            //初始化radioGroup
            initRadioGroupPattern();
            //切换刷新按钮
            tv_submit.setEnabled(false);
            initLisenter();
        } else {
            titleExtLayout.addView(inflater.inflate(R.layout.p5_odd_even_number_title, null));
            view_number = inflater.inflate(R.layout.trend_p5_odd_number_select, null);
            submitLayout.addView(view_number);
            dataList.setAdapter(dataAdapter2);
            ll_number = (LinearLayout) view_number.findViewById(R.id.odd_number_ll);
            //初始化
            initRadioGroupNumber();
            //切换刷新按钮
            tv_submit.setEnabled(false);
            initLisenter();
        }
        resizeLayout();
    }

    //标题显示与隐藏
    private void resizeLayout() {
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_submit_tips), relativeLayout.findViewById(R.id.trend_title_hold));//开奖号码隐藏
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(tabLayout.getSelectedTabPosition() == 0 ? 470f : 710f) - DisplayUtil.dip2px(condition.isShowBaseNum() ? 0f : 70f), DisplayUtil.dip2px(30f * dataAdapter1.getCount()));//默认设置450dp
    }

    private void initRadioGroupNumber() {
        list_number.clear();
        for (int i = 0; i < ll_number.getChildCount(); i++) {
            View child = ll_number.getChildAt(i);
            if (child instanceof CheckBox) {
                list_number.add((CheckBox) child);
            }
        }
    }

    private void initRadioGroupPattern() {
        list_pattern.clear();
        for (int i = 1; i < ll_pattern.getChildCount(); i++) {
            View child = ll_pattern.getChildAt(i);
            if (child instanceof RadioGroup) {
                list_pattern.add((RadioGroup) child);
            }
        }
    }

    private void initDataPattern() {
        list_pattern_data.clear();
        for (int i = 0; i < 5; i++) {
            list_pattern_data.add("奇偶");
        }
        //遍历radiogroup集合
        for (int i = 0; i < list_pattern.size(); i++) {
            //遍历radiogroup里的radiobutton
            for (int j = 0; j < list_pattern.get(i).getChildCount(); j++) {
                View child = list_pattern.get(i).getChildAt(j);
                if (child instanceof RadioButton) {
                    //如果选择,更改数据
                    if (((RadioButton) list_pattern.get(i).getChildAt(j)).isChecked()) {
                        list_pattern_data.set(i, oddData[j]);
                    }
                }
            }
        }
    }

    private void initDataNumber() {
        list_number_data.clear();
        //遍历checkbox集合
        for (int i = 0; i < list_number.size(); i++) {
            if (list_number.get(i).isChecked()) {
                list_number_data.add(oddNumData[i]);
            } else {
                list_number_data.remove(oddNumData[i]);
            }
        }
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowBaseNum());
        adapter.setSelect(1, condition.isShowMiss());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowBaseNum(adapter.containSelect(0));//是否显示开奖号
        condition.setShowMiss(adapter.containSelect(1));
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
        dataAdapter1.initNums();
        dataAdapter2.initNums();
        tv_submit.setEnabled((checkCount > 0 || checkPattern) );
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueAnalyse(10089, "10019", condition.getPeriod(), condition.getWeekDay())
                    .doOnTerminate(() -> {
                        condition.setCurrentPeriod(condition.getPeriod());
                        condition.setCurrentWeekDay(condition.getWeekDay());
                    })
                    .subscribe(new SafeOnlyNextSubscriber<IssueAnalyseDataModel>() {
                        @Override
                        public void onNext(IssueAnalyseDataModel args) {
                            super.onNext(args);
                            List<IssueAnalyseDataModel.IssueAnalyse> data = args.getData();
                            if (data != null) {
                                AVLoading.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                Collections.reverse(data);
                                List<String> dates = new ArrayList<>();
                                for (IssueAnalyseDataModel.IssueAnalyse date : data) {
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
        ViewUtils.showViewsVisible(tv_submit.getVisibility() == View.VISIBLE ? false : true, tv_submit, HScrollView, view_jiou);
    }

}
