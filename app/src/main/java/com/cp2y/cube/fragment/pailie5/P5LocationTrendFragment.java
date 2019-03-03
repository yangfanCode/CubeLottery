package com.cp2y.cube.fragment.pailie5;


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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendP5BaseFragment;
import com.cp2y.cube.fragment.pailie5.adapter.P5TrendLocateAdapter;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.P5LotteryService;
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


/**
 * 定位走势
 */
public class P5LocationTrendFragment extends TrendP5BaseFragment {
    private boolean isFullScreen = false;//是否全屏状态
    //辅助标记
    private List<CheckBox> list_check_wan;
    private List<CheckBox> list_check_qian;
    private List<CheckBox> list_check_bai;
    private List<CheckBox> list_check_shi;
    private List<CheckBox> list_check_ge;
    private TrendDateAdapter dateAdapter;
    private P5TrendLocateAdapter dataAdapter;
    private HVSRelativeLayout relativeLayout;
    private LinearLayout numsLayout;
    private RelativeLayout tv_sunbmit;
    private LinearLayout layout;
    private MyGridView gv_wan, gv_qian, gv_bai, gv_shi, gv_ge;
    private List<String> list_wan_select, list_qian_select, list_bai_select, list_shi_select, list_ge_select;
    private ArrayAdapter<String> adapter_wan, adapter_qian, adapter_bai, adapter_shi, adapter_ge;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private HScrollView HScrollView;
    private View view_location;
    private View rootView;
    private int locateFlag = 0;
    private String[] tabTitle = {"万位", "千位", "百位", "十位", "个位"};
    private TextView submit_tv, submit_tv_tip;
    private HScrollView wan_ll, qian_ll, bai_ll, shi_ll, ge_ll;
    private int position = 0;
    private List<String> list = new ArrayList<>(); //总保存数据集合
    /**以下为控件管理数组**/
    private HScrollView[] selectLayout;//百位 十位....选号布局
    private List<CheckBox>[] checkArrays;//checkbox数组
    private ArrayAdapter<String>[] adapterArrays;//选号gv的adapter
    private List<String>[] selectNumArrays;//选好选择集合数组

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_p5_location_trend, container, false);
        initSettingDialog(getString(R.string.locate_tips), MapUtils.D3_LOCATE_SUPPORT, new int[]{0, 1});
        setHasOptionsMenu(true);
        initView(rootView);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        //标题栏
        for (int i = 0; i < 5; i++) {
            TextView text = new TextView(getContext());
            text.setGravity(Gravity.CENTER);
            text.setTextSize(14f);
            text.setText(tabTitle[i]);
            text.setTextColor(ColorUtils.NORMAL_RED);
            TabLayout.Tab tab = tabLayout.newTab().setCustomView(text).setTag(i);
            if (i == 0) tab.select();
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                int pos = tab.getPosition();
                dataAdapter.reloadType((int) tab.getTag());
                locateFlag = pos;
                reloadNums();
                if (!isFullScreen) {//全屏状态下执行
                    setSubmitLayoutChange(pos);//下方选号切换
                    setSubmitChangeView(pos);//切换控件初始化
                    setListener(pos);//切换控件监听
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        numsLayout = (LinearLayout) rootView.findViewById(R.id.trend_locate_nums);
        relativeLayout = (HVSRelativeLayout) rootView.findViewById(R.id.trend_layout);
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(370f), DisplayUtil.dip2px(300f));//默认设置450dp
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter = new P5TrendLocateAdapter(getContext(), condition);
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
            int selectCount=0;
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
               if(list_wan_select.size() == 1 &&list_qian_select.size() == 1 &&list_bai_select.size() == 1 && list_shi_select.size() == 1 && list_ge_select.size() == 1){
                   //判断相同号码
                   List<String> list_check = new ArrayList<String>();
                   list_check.addAll(list_wan_select);
                   list_check.addAll(list_qian_select);
                   list_check.addAll(list_bai_select);
                   list_check.addAll(list_shi_select);
                   list_check.addAll(list_ge_select);
                   if (list.size() > 0) {//保存过
                       if (list_check.get(0).equals(list.get(0)) && list_check.get(1).equals(list.get(1)) && list_check.get(2).equals(list.get(2))
                               && list_check.get(3).equals(list.get(3))&& list_check.get(4).equals(list.get(4))) {//判断重复
                           TipsToast.showTips("已存在号码库");
                           return;
                       }
                   }
                   list.clear();
                   list.addAll(list_wan_select);
                   list.addAll(list_qian_select);
                   list.addAll(list_bai_select);
                   list.addAll(list_shi_select);
                   list.addAll(list_ge_select);
                   selectCount = (list_wan_select.size()*list_qian_select.size()*list_bai_select.size() * list_shi_select.size() * list_ge_select.size());
                   getService(P5LotteryService.class).saveLotteryNumber(list,0,4,selectCount);
               }else{
                   //判断相同号码
                   List<String> list_check = new ArrayList<String>();
                   list_check.addAll(list_wan_select);
                   for (String num : list_qian_select) {
                       list_check.add((Integer.parseInt(num) + 50) + "");
                   }
                   for (String num : list_bai_select) {
                       list_check.add((Integer.parseInt(num) + 100) + "");
                   }
                   for (String num : list_shi_select) {
                       list_check.add((Integer.parseInt(num) + 150) + "");
                   }
                   for (String num : list_ge_select) {
                       list_check.add((Integer.parseInt(num) + 200) + "");
                   }
                   if (CommonUtil.ListCheck(list_check, list)) {
                       TipsToast.showTips("已存在号码库");
                       return;
                   }
                   list.clear();
                   list.addAll(list_wan_select);
                   for (String num : list_qian_select) {
                       list.add(Integer.parseInt(num) + 50 + "");
                   }
                   for (String num : list_bai_select) {
                       list.add(Integer.parseInt(num) + 100 + "");
                   }
                   for (String num : list_shi_select) {
                       list.add(Integer.parseInt(num) + 150 + "");
                   }
                   for (String num : list_ge_select) {
                       list.add((Integer.parseInt(num) + 200) + "");
                   }
                   CommonUtil.SortCollection(list);//排序
                   selectCount = (list_wan_select.size()*list_qian_select.size()*list_bai_select.size() * list_shi_select.size() * list_ge_select.size());
                   getService(P5LotteryService.class).saveLotteryNumber(list, 1,4,selectCount);
               }
            } else {
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        });
        setListener(0);//默认万位监听
    }


    private void initView(View rootView) {
        list_check_wan = new ArrayList<>();
        list_check_qian = new ArrayList<>();
        list_check_bai = new ArrayList<>();
        list_check_shi = new ArrayList<>();
        list_check_ge = new ArrayList<>();
        checkArrays=new List[]{list_check_wan,list_check_qian,list_check_bai,list_check_shi,list_check_ge};
        list_wan_select = new ArrayList<>();
        list_qian_select = new ArrayList<>();
        list_bai_select = new ArrayList<>();
        list_shi_select = new ArrayList<>();
        list_ge_select = new ArrayList<>();
        selectNumArrays=new List[]{list_wan_select,list_qian_select,list_bai_select,list_shi_select,list_ge_select};
        container = (FrameLayout) getActivity().findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView) getActivity().findViewById(R.id.AVLoadingIndicator);
        layout = (LinearLayout) rootView.findViewById(R.id.location_trend_layout);
        gv_wan = (MyGridView) rootView.findViewById(R.id.location_trend_gvWan);
        gv_qian = (MyGridView) rootView.findViewById(R.id.location_trend_gvQian);
        gv_bai = (MyGridView) rootView.findViewById(R.id.location_trend_gvBai);
        gv_shi = (MyGridView) rootView.findViewById(R.id.location_trend_gvShi);
        gv_ge = (MyGridView) rootView.findViewById(R.id.location_trend_gvGe);
        tv_sunbmit = (RelativeLayout) rootView.findViewById(R.id.trend_submit);
        wan_ll = (HScrollView) rootView.findViewById(R.id.p5_selectnum_wan);
        qian_ll = (HScrollView) rootView.findViewById(R.id.p5_selectnum_qian);
        bai_ll = (HScrollView) rootView.findViewById(R.id.p5_selectnum_bai);
        shi_ll = (HScrollView) rootView.findViewById(R.id.p5_selectnum_shi);
        ge_ll = (HScrollView) rootView.findViewById(R.id.p5_selectnum_ge);
        selectLayout=new HScrollView[]{wan_ll,qian_ll,bai_ll,shi_ll,ge_ll};//选号布局
        ViewUtils.showViewsVisible(false,qian_ll,bai_ll,shi_ll,ge_ll);//只显示万位选号
        tv_sunbmit.setEnabled(false);
        submit_tv = (TextView) rootView.findViewById(R.id.trend_submit_tv);
        submit_tv_tip = (TextView) rootView.findViewById(R.id.trend_submit_tip_tv);
        LinearLayout ll = (LinearLayout) wan_ll.findViewById(R.id.location_trend_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof CheckBox) {
                list_check_wan.add((CheckBox) child);
            }
        }//默认百位选号
        adapter_wan = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_wan_select);
        gv_wan.setAdapter(adapter_wan);
        adapter_qian = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_qian_select);
        gv_qian.setAdapter(adapter_qian);
        adapter_bai = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_bai_select);
        gv_bai.setAdapter(adapter_bai);
        adapter_shi = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_shi_select);
        gv_shi.setAdapter(adapter_shi);
        adapter_ge = new ArrayAdapter<String>(getActivity(), R.layout.item_trend_selectnum, R.id.item_trend_select_tv, list_ge_select);
        gv_ge.setAdapter(adapter_ge);
        adapterArrays=new ArrayAdapter[]{adapter_wan,adapter_qian,adapter_bai,adapter_shi,adapter_ge};
    }

    //标题下方号码
    private void reloadNums() {
        numsLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        for (int i = 0; i <= 9; i++) {
            TextView text = new TextView(ContextHelper.getLastActivity());
            text.setTextSize(12);
            text.setTextColor(ColorUtils.NORMAL_GRAY);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            text.setLayoutParams(params);
            text.setGravity(Gravity.CENTER);
            String numText = String.valueOf(i);
            text.setText(numText);
            numsLayout.addView(text, lp);
        }
        ViewGroup.LayoutParams lp1 = numsLayout.getLayoutParams();
        //设置tablayout下方号码布局宽度
        int width = 0, widthNum = 0;
        width = DisplayUtil.dip2px(30f * 10);
        lp1.width = width;
        numsLayout.setLayoutParams(lp1);
        //此处宽度根绝开奖号码设置
        widthNum = DisplayUtil.dip2px(condition.isShowBaseNum() ? (30f * 10 + 70f) : 30f * 10);
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_locate_lotteryNums), relativeLayout.findViewById(R.id.trend_locate_view)
                , relativeLayout.findViewById(R.id.location_trend_submit_tip_layout));
        relativeLayout.setHsvViewSize(widthNum, DisplayUtil.dip2px(30f * dataAdapter.getCount()));
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
        relativeLayout.keepOnHorizontal();//上下滑动一下 解决错乱bug
    }

    @Override
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    protected void reloadData() {
        reloadNums();
        dataAdapter.notifyDataSetChanged();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueNumber(10004, "10002", condition.getPeriod(), condition.getWeekDay(), 1)
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
        if (list_wan_select.size() > 0 ||list_qian_select.size() > 0 ||list_bai_select.size() > 0 || list_shi_select.size() > 0 || list_ge_select.size() > 0) {
            layout.setVisibility(layout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_sunbmit.getVisibility() == View.VISIBLE ? false : true, tv_sunbmit, view_location = (View) rootView.findViewById(R.id.trend_view_location)
                , relativeLayout.findViewById(R.id.location_trend_submit_tip_layout));
        HScrollView[] views={wan_ll,qian_ll,bai_ll,shi_ll,ge_ll};
        //隐藏选号
        views[position].setVisibility(tv_sunbmit.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
        isFullScreen = tv_sunbmit.getVisibility() == View.VISIBLE ? false : true;//全屏状态标记
    }
    //切换下方选号 提示
    public void setSubmitLayoutChange(int pos) {
        String[] tips={"(万位)","(千位)","(百位)","(十位)","(个位)"};
        submit_tv_tip.setText(tips[pos]);
        for(int i=0;i<selectLayout.length;i++){
            selectLayout[i].setVisibility(i==pos?View.VISIBLE:View.GONE);
        }
    }

    public void setSubmitChangeView(int pos) {
        list_check_wan.clear();
        list_check_qian.clear();
        list_check_bai.clear();
        list_check_shi.clear();
        list_check_ge.clear();
        LinearLayout ll = null;
        ll = (LinearLayout) selectLayout[pos].findViewById(R.id.location_trend_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof CheckBox) {
                checkArrays[pos].add((CheckBox) child);
            }
        }
    }

    public void setListener(int pos) {
        setSubmitChangeListener(pos, checkArrays[pos], adapterArrays[pos]);
    }

    public void setSubmitChangeListener(int pos, List<CheckBox> list, ArrayAdapter adapter) {
        //个位监听控制提交按钮
        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        layout.setVisibility(View.VISIBLE);
                        selectNumArrays[pos].add(String.valueOf(finalI));
                    } else {
                        selectNumArrays[pos].remove(String.valueOf(finalI));
                    }
                    //控制开关和 弹窗
                    int num_wan=selectNumArrays[0].size(),num_qian=selectNumArrays[1].size()
                            ,num_bai=selectNumArrays[2].size(),num_shi=selectNumArrays[3].size(),num_ge=selectNumArrays[4].size();
                    if (num_wan>0&&num_qian>0&&num_bai > 0 && num_shi > 0 && num_ge > 0) {
                        tv_sunbmit.setEnabled(true);
                        submit_tv.setText("保存");
                        submit_tv_tip.setVisibility(View.GONE);
                    } else if (num_wan==0&&num_qian==0&&num_bai == 0 && num_shi == 0 && num_ge == 0) {
                        layout.setVisibility(View.GONE);
                    } else {
                        tv_sunbmit.setEnabled(false);
                        submit_tv.setText("选号");
                        submit_tv_tip.setVisibility(View.VISIBLE);
                    }
                    //排序
                    CommonUtil.SortCollection(selectNumArrays[pos]);
                    //第一次点击就显示
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
