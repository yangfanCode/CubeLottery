package com.cp2y.cube.fragment.lotto;

import android.os.Bundle;
import android.view.GestureDetector;
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
import com.cp2y.cube.adapter.LottoTrendBaseAdapter;
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
import com.cp2y.cube.util.CombineAlgorithm;
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

public class LottoNumTrendFragment extends TrendLottoBaseFragment {
    private List<String> list_red, list_blue, list_total, list_red_select, list_blue_select;
    private View rootView;
    private LinearLayout ll, layout;
    private ArrayAdapter<String> adapter_red, adapter_blue;
    private String PATH = ContextHelper.getApplication().getCacheDir() + "/CubeLottery/";
    private List<CheckBox> list_check;
    private TrendDateAdapter dateAdapter;
    private LottoTrendBaseAdapter dataAdapter;
    private HVSRelativeLayout relativeLayout;
    private AVLoadingIndicatorView AVLoading;
    private MyGridView gv_red, gv_blue;
    private FrameLayout container;
    private TextView tv_submit;
    private View view_num;
    private int num_red = 0;
    private int num_blue = 0;
    private HScrollView HScrollView;
    private GestureDetector mDetector;
    private long firstClick = 0;
    private long lastClick = 0;
    private int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_lotto_num, container, false);
        initSettingDialog(getString(R.string.num_tips), MapUtils.NUM_SUPPORT, new int[]{0}, 1);
        //设置菜单
        setHasOptionsMenu(true);
        initView(rootView);
        relativeLayout = (HVSRelativeLayout) rootView.findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        //左侧奖期数据
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        //右侧走势图
        dataAdapter = new LottoTrendBaseAdapter(getContext(), condition);
        dataList.setAdapter(dataAdapter);
        resizeLayout();
        initListener();
//        dataList.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        // 如果第二次点击 距离第一次点击时间过长 那么将第二次点击看为第一次点击
//                        if (firstClick != 0 && System.currentTimeMillis() - firstClick > 300) {
//                            count = 0;
//                        }
//                        count++;
//                        if (count == 1) {
//                            firstClick = System.currentTimeMillis();
//                        } else if (count == 2) {
//                            lastClick = System.currentTimeMillis();
//                            // 两次点击小于300ms 也就是连续点击
//                            if (lastClick - firstClick < 300) {// 判断是否是执行了双击事件
//                                TipsToast.showTips("aaaaaa");
//
//                            }
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        break;
//                }
//                return true;
//            }
//        });
        return rootView;
    }

    private void initListener() {
        tv_submit.setOnClickListener((v) -> {
            int selectCount=0;//注数
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                initData();
                if (list_red.size() + list_blue.size() == 7) {
                    //单式
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
                    selectCount = CombineAlgorithm.combination(list_red_select.size(), 5) * CombineAlgorithm.combination(list_blue_select.size(), 2);
                    //保存
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
                    //保存
                    Collections.sort(list_total, (s, t) -> {
                        int s1 = Integer.valueOf(s);
                        int s2 = Integer.valueOf(t);
                        if (s1 < s2) return -1;
                        if (s1 == s2) return 0;
                        return 1;
                    });
                    selectCount = CombineAlgorithm.combination(list_red_select.size(), 5) * CombineAlgorithm.combination(list_blue_select.size(), 2);
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
                            //红球
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
                        } else {
                            num_blue--;
                            //删除数据
                            list_blue_select.remove(CommonUtil.preZeroForBall(finalI - 34));
                        }
                    }

                    //控制开关和 弹窗
                    if (num_red > 4 && num_blue > 1 && num_red + num_blue > 6) {
                        tv_submit.setEnabled(true);
                        tv_submit.setText("保存");
                    } else if (num_red == 0 && num_blue == 0) {
                        layout.setVisibility(View.GONE);
                    } else {
                        tv_submit.setEnabled(false);
                        tv_submit.setText("选号");
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

    private void resizeLayout() {
        //隐藏开奖号码文字 选择号码保存文字
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_submit_tips));
        //整个宽 不显示开奖宽
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(condition.isShowBaseNum() ? 1655f : 1499f), DisplayUtil.dip2px(30.1f * dataAdapter.getCount() - 1));
    }

    private void initView(View rootView) {
        list_red = new ArrayList<>();
        list_blue = new ArrayList<>();
        list_total = new ArrayList<>();
        list_check = new ArrayList<>();
        list_red_select = new ArrayList<>();
        list_blue_select = new ArrayList<>();
        layout = (LinearLayout) rootView.findViewById(R.id.num_trend_layout);
        ll = (LinearLayout) rootView.findViewById(R.id.num_trend_ll);
        gv_red = (MyGridView) rootView.findViewById(R.id.num_trend_gvRed);
        gv_blue = (MyGridView) rootView.findViewById(R.id.num_trend_gvBlue);
        tv_submit = (TextView) rootView.findViewById(R.id.trend_submit);
        tv_submit.setEnabled(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof CheckBox) {
                list_check.add((CheckBox) child);
            }
        }
        AVLoading = (AVLoadingIndicatorView) getActivity().findViewById(R.id.AVLoadingIndicator);
        container = (FrameLayout) getActivity().findViewById(R.id.trend_container);
        adapter_red = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_blue = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_blue_select);
        gv_blue.setAdapter(adapter_blue);
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowBaseNum());
        adapter.setSelect(1, condition.isShowMutiNum());
        adapter.setSelect(2, condition.isShowConnectNum());
        adapter.setSelect(3, condition.isShowEdgeNum());
        adapter.setSelect(4, condition.isShowSerialNum());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowBaseNum(adapter.containSelect(0));//是否显示开奖号
        condition.setShowMutiNum(adapter.containSelect(1));
        condition.setShowConnectNum(adapter.containSelect(2));
        condition.setShowEdgeNum(adapter.containSelect(3));
        condition.setShowSerialNum(adapter.containSelect(4));
    }

    @Override
    protected void reloadData() {
        resizeLayout();
        dataAdapter.notifyDataSetChanged();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueNumber(10088, "10002", condition.getPeriod(), condition.getWeekDay(), 0)
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
                                if (isReverse()) {
                                    Collections.reverse(data);
                                }
                                List<String> dates = new ArrayList<>();
                                for (IssueNumberDataModel.IssueNumber date : data) {
                                    dates.add(date.getIss());
                                }
                                dateAdapter.reloadData(dates);
                                dataAdapter.reloadData(data);
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
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    public void onDoubleTaped() {
        //双击关闭popwindow
        closePop();
        //双击隐藏选号提示
        if (list_red_select.size() > 0 || list_blue_select.size() > 0) {
            layout.setVisibility(layout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_submit.getVisibility() == View.VISIBLE ? false : true, tv_submit, HScrollView = (HScrollView) rootView.findViewById(R.id.num_trend_HScroll), view_num = (View) rootView.findViewById(R.id.trend_view_num));
    }

}
