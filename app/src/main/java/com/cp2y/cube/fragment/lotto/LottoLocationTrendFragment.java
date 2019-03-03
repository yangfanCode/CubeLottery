package com.cp2y.cube.fragment.lotto;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.adapter.LottoTrendLocateAdapter;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendLottoBaseFragment;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.LotteryService;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.HScrollView;
import com.cp2y.cube.widgets.HVListView;
import com.cp2y.cube.widgets.HVSRelativeLayout;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


/**
 * 定位走势
 */
public class LottoLocationTrendFragment extends TrendLottoBaseFragment {
    //辅助标记
    private List<CheckBox> list_check;
    private List<String> list_red;
    private List<String> list_blue;
    private List<String> list_total;
    private int num_red = 0;
    private int num_blue = 0;
    private TrendDateAdapter dateAdapter;
    private LottoTrendLocateAdapter dataAdapter;
    private HVSRelativeLayout relativeLayout;
    private LinearLayout numsLayout;
    private LinearLayout ll;
    private TextView tv_sunbmit;
    private LinearLayout layout;
    private MyGridView gv_red, gv_blue;
    private List<String> list_red_select, list_blue_select;
    private ArrayAdapter<String> adapter_red, adapter_blue;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private HScrollView HScrollView;
    private View view_location;
    private View rootView;
    private int locateFlag = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_lotto_location_trend, container, false);
        initSettingDialog(getString(R.string.locate_tips), MapUtils.LOCATE_SUPPORT, new int[]{0}, 0);
        setHasOptionsMenu(true);
        initView(rootView);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        //标题栏
        for (int i = 0; i < 7; i++) {
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setTextSize(14f);
            if (i < 5) {
                text.setText(String.format(Locale.CHINA, "前%d", i + 1));
                text.setTextColor(ColorUtils.NORMAL_RED);
                TabLayout.Tab tab = tabLayout.newTab().setCustomView(text).setTag(i);
                if (i == 0) tab.select();
                tabLayout.addTab(tab);
            } else {
                text.setText(String.format(Locale.CHINA, "后%d", i - 4));
                text.setTextColor(ColorUtils.NORMAL_BLUE);
                tabLayout.addTab(tabLayout.newTab().setCustomView(text).setTag(i));
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                dataAdapter.reloadType((int) tab.getTag());
                locateFlag = tab.getPosition();
                reloadNums();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
//                dataAdapter
            }
        });
        numsLayout = (LinearLayout) rootView.findViewById(R.id.trend_locate_nums);
        relativeLayout = (HVSRelativeLayout) rootView.findViewById(R.id.trend_layout);
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(450f), DisplayUtil.dip2px(0f));//默认设置450dp
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter = new LottoTrendLocateAdapter(getContext(), condition);
        dataList.setAdapter(dataAdapter);
//        HScrollView scrollView = (HScrollView) rootView.findViewById(R.id.scroll_layout4);
//        scrollView.setScrollViewListener((view, x, y, oldx, oldy) -> {
//            dataAdapter.setXoffset(x);
//            dataAdapter.notifyDataSetChanged();
//        });
        initListener();
        return rootView;
    }

    private void initListener() {
        //提交保存
        tv_sunbmit.setOnClickListener((v) -> {
            int selectCount=0;//注数
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                initData();
                //单式
                if (list_red.size() + list_blue.size() == 7) {
                    //判断相同号码
                    List<String> list_check = new ArrayList<String>();
                    list_check.addAll(list_red);
                    list_check.addAll(list_blue);
                    if (CommonUtil.ListCheck(list_check, list_total)) {
                        TipsToast.showTips("已存在号码库");
                        return;
                    }
                    //红球排序
                    CommonUtil.SortCollection(list_red);
                    list_total.clear();
                    list_total.addAll(list_red);
                    list_total.addAll(list_blue);
                    selectCount = CombineAlgorithm.combination(list_red.size(), 5) * CombineAlgorithm.combination(list_blue.size(), 2);
                    getService(LotteryService.class).saveLotteryNumber(list_total, 0, 1,selectCount);
                } else if (list_red.size() + list_blue.size() > 7) {
                    //复式
                    //判断相同号码
                    List<String> list_check = new ArrayList<String>();
                    list_check.addAll(list_red);
                    list_check.addAll(list_blue);
                    if (CommonUtil.ListCheck(list_check, list_total)) {
                        TipsToast.showTips("已存在号码库");
                        return;
                    }
                    list_total.clear();
                    list_total.addAll(list_red);
                    //蓝球加50
                    for (int i = 0; i < list_blue.size(); i++) {
                        list_total.add((Integer.parseInt(list_blue.get(i)) + 50) + "");
                    }
                    selectCount = CombineAlgorithm.combination(list_red.size(), 5) * CombineAlgorithm.combination(list_blue.size(), 2);
                    getService(LotteryService.class).saveLotteryNumber(list_total, 1, 1,selectCount);
                }
            } else {
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        });
        //监听控制提交按钮
        for (int i = 0; i < list_check.size(); i++) {
            final int finalI = i;
            list_check.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        layout.setVisibility(View.VISIBLE);
                        if (finalI < 35) {
//                            if(list_red_select.size()>=16){
//                                list_check.get(finalI).setChecked(false);
//                                TipsToast.showTips("红球最多选16个,蓝球12个");
//                                return;
//                            }
                            num_red++;
                            list_red_select.add(CommonUtil.preZeroForBall(finalI + 1));

                        } else {
                            //蓝球
//                            if(list_blue_select.size()>=12){
//                                list_check.get(finalI).setChecked(false);
//                                TipsToast.showTips(getActivity(),"红球最多选16个,蓝球12个");
//                                return;
//                            }
                            num_blue++;
                            list_blue_select.add(CommonUtil.preZeroForBall(finalI - 34));
                        }
                    } else {
                        //取消选中
                        if (finalI < 35) {
                            num_red--;
                            //删除数据
                            list_red_select.remove(CommonUtil.preZeroForBall(finalI + 1));
                            adapter_red.notifyDataSetChanged();
                        } else {
                            num_blue--;
                            //删除数据
                            list_blue_select.remove(CommonUtil.preZeroForBall(finalI - 34));

                        }
                    }
                    //控制开关和 弹窗
                    if (num_red > 4 && num_blue > 1 && num_red + num_blue > 6) {
                        tv_sunbmit.setEnabled(true);
                        tv_sunbmit.setText("保存");
                    } else if (num_red == 0 && num_blue == 0) {
                        layout.setVisibility(View.GONE);
                    } else {
                        tv_sunbmit.setEnabled(false);
                        tv_sunbmit.setText("选号");
                    }
                    //排序
                    CommonUtil.SortCollection(list_red_select);
                    CommonUtil.SortCollection(list_blue_select);
                    //第一次点击就显示
                    adapter_red.notifyDataSetChanged();
                    adapter_blue.notifyDataSetChanged();
                }
            });
        }
    }

    //添加数据
    private void initData() {
        list_red.clear();
        list_blue.clear();
        for (int i = 0; i < list_check.size(); i++) {
            if (i < 35) {
                if (list_check.get(i).isChecked()) {
                    list_red.add((i + 1) + "");
                }
            } else {
                if (list_check.get(i).isChecked()) {
                    list_blue.add((i - 34) + "");
                }
            }
        }
    }

    private void initView(View rootView) {
        list_red = new ArrayList<>();
        list_blue = new ArrayList<>();
        list_total = new ArrayList<>();
        list_check = new ArrayList<>();
        list_red_select = new ArrayList<>();
        list_blue_select = new ArrayList<>();
        container = (FrameLayout) getActivity().findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView) getActivity().findViewById(R.id.AVLoadingIndicator);
        layout = (LinearLayout) rootView.findViewById(R.id.location_trend_layout);
        gv_red = (MyGridView) rootView.findViewById(R.id.location_trend_gvRed);
        gv_blue = (MyGridView) rootView.findViewById(R.id.location_trend_gvBlue);
        ll = (LinearLayout) rootView.findViewById(R.id.location_trend_ll);
        tv_sunbmit = (TextView) rootView.findViewById(R.id.trend_submit);
        tv_sunbmit.setEnabled(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof CheckBox) {
                list_check.add((CheckBox) child);
            }
        }
        adapter_red = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_blue = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_blue_select);
        gv_blue.setAdapter(adapter_blue);
    }

    //标题下方号码
    private void reloadNums() {
        Range numRange = dataAdapter.getNumRange();
        numsLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        for (int i = numRange.start; i <= numRange.end; i++) {
            TextView text = new TextView(ContextHelper.getLastActivity());
            text.setTextSize(12);
            text.setTextColor(ColorUtils.NORMAL_GRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            text.setLayoutParams(params);
            text.setGravity(Gravity.CENTER);
            String numText = "0" + String.valueOf(i);
            text.setText(numText.substring(numText.length() - 2, numText.length()));
            numsLayout.addView(text, lp);
        }
        ViewGroup.LayoutParams lp1 = numsLayout.getLayoutParams();
        //设置tablayout下方号码布局宽度
        int width = 0, widthNum = 0;
        if (locateFlag == 5 || locateFlag == 6) {//后区部分宽度增加,增加view占位补全背景线
            width = DisplayUtil.dip2px(30f * (numRange.end - numRange.start + 1) + 180f);
            TextView text = new TextView(ContextHelper.getLastActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 6.0f);
            text.setLayoutParams(params);
            numsLayout.addView(text);
        } else {
            width = DisplayUtil.dip2px(30f * (numRange.end - numRange.start + 1));
        }
        lp1.width = width;
        numsLayout.setLayoutParams(lp1);
        widthNum = DisplayUtil.dip2px(30f * (numRange.end - numRange.start + 1));
        relativeLayout.setHsvViewSize(widthNum, DisplayUtil.dip2px(30.1f * dataAdapter.getCount() - 1));
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowMiss());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowMiss(adapter.containSelect(0));
    }

    @Override
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    protected void reloadData() {
        dataAdapter.notifyDataSetChanged();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueNumber(10088, "10002", condition.getPeriod(), condition.getWeekDay(), 2)
                    .flatMap(issueNumberDataModel1 -> NetHelper.LOTTERY_API.getIssueNumber(10088, "10002", condition.getPeriod(), condition.getWeekDay(), 0)
                            .map(issueNumberDataModel2 -> {
                                List<IssueNumberDataModel.IssueNumber> data1 = issueNumberDataModel1.getData();
                                List<IssueNumberDataModel.IssueNumber> data2 = issueNumberDataModel2.getData();
                                for (int i = 0; i < data2.size(); i++) {
                                    IssueNumberDataModel.IssueNumber number1 = data1.get(i);
                                    IssueNumberDataModel.IssueNumber number2 = data2.get(i);
                                    List<Integer> blueMiss = number2.getMs().get(1);
                                    number2.getMs().clear();
                                    number2.getMs().addAll(number1.getMs());
                                    number2.getMs().add(blueMiss);
                                }
                                return issueNumberDataModel2;
                            }))
                    .doOnTerminate(() -> {
                        condition.setCurrentPeriod(condition.getPeriod());
                        condition.setCurrentWeekDay(condition.getWeekDay());
                    })
                    .subscribe(new SafeOnlyNextSubscriber<IssueNumberDataModel>() {
                        @Override
                        public void onNext(IssueNumberDataModel args) {
                            super.onNext(args);
                            List<IssueNumberDataModel.IssueNumber> data = args.getData();
                            if (data != null) {
                                AVLoading.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                Collections.reverse(data);
                                List<String> dates = new ArrayList<>();
                                for (IssueNumberDataModel.IssueNumber date : data) {
                                    dates.add(date.getIss());
                                }
                                dateAdapter.reloadData(dates);
                                dataAdapter.reloadData(data);
                                reloadNums();
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
        //双击关闭pop
        closePop();
        //双击关闭选号提示
        if (list_red_select.size() > 0 || list_blue_select.size() > 0) {
            layout.setVisibility(layout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_sunbmit.getVisibility() == View.VISIBLE ? false : true, tv_sunbmit, HScrollView = (HScrollView) rootView.findViewById(R.id.location_trend_HScroll), view_location = (View) rootView.findViewById(R.id.trend_view_location));
    }

}
