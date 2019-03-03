package com.cp2y.cube.fragment.lotto;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendLottoBaseFragment;
import com.cp2y.cube.fragment.doubleball.adapter.TrendSumMantissaAdapter;
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
 * 和尾数走势
 */
public class LottoSumMantissaTrendFragment extends TrendLottoBaseFragment {

    private View rootView;
    private List<String>list_data;
    private List<String>list_flag=new ArrayList<>();
    private HVSRelativeLayout relativeLayout;
    private TrendDateAdapter dateAdapter;
    private TrendSumMantissaAdapter dataAdapter;
    private TextView tv_submit;
    private LinearLayout ll;
    private List<CheckBox>list_check;
    private int checkCount=0;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private HScrollView HScrollView;
    private View view_span;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_summantissa_trend, container, false);
        initSettingDialog(getString(R.string.span_tips), MapUtils.LOTTO_ODD_SUPPORT, new int[]{1, 2}, 0);
        //默认不显示开奖号
        condition.setShowBaseNum(false);
        setHasOptionsMenu(true);
        initView(rootView);
        relativeLayout = (HVSRelativeLayout) rootView.findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        //奖期
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter = new TrendSumMantissaAdapter(getContext(),condition);//带含篮球adapter
        dataList.setAdapter(dataAdapter);
//        HScrollView scrollView = (HScrollView) rootView.findViewById(R.id.scroll_layout4);
//        scrollView.setScrollViewListener((view, x, y, oldx, oldy) -> {
//            dataAdapter.setXoffset(x);
//            dataAdapter.notifyDataSetChanged();
//        });
        initListener();
        resizeLayout();
        return rootView;
    }
    //点击控制开关
    private void initListener() {
        //提交
        tv_submit.setOnClickListener((v)->{
            if(list_data!=null&&list_data.size()>0){
                initData();
                if(CommonUtil.ListCheck(list_flag,list_data)){
                    TipsToast.showTips("已存在过滤条件");
                    return;
                }
            }
            initData();
            if(list_data!=null&&list_data.size()>0){
                list_flag.clear();
                list_flag.addAll(list_data);//判断是否相同标记
                SingletonMapFilter.registerLottoService("11", list_data);
            }
            TipsToast.showTips("已成功保存至过滤条件");
        });
        for (int i = 0; i < list_check.size(); i++) {
            list_check.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        checkCount++;
                        tv_submit.setEnabled(true);
                        tv_submit.setText("保存");
                    }else{
                        checkCount--;
                    }
                    if(checkCount==0){
                        tv_submit.setEnabled(false);
                        tv_submit.setText("条件");
                    }
                }
            });
        }
    }
    public void initData(){
        list_data.clear();
        for (int i = 0; i < list_check.size(); i++) {
            if(list_check.get(i).isChecked()){
                list_data.add(String.valueOf(i));
            }
        }
    }
    //初始化
    private void initView(View rootView) {
        list_data=new ArrayList<>();
        list_check=new ArrayList<>();
        ll = (LinearLayout)rootView. findViewById(R.id.span_num_ll);
        tv_submit = (TextView) rootView.findViewById(R.id.trend_submit);
        container = (FrameLayout)getActivity(). findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView)getActivity(). findViewById(R.id.AVLoadingIndicator);
        view_span = (View)rootView. findViewById(R.id.trend_view_span);
        HScrollView = (HScrollView)rootView. findViewById(R.id.trend_submit_layout);
        tv_submit.setEnabled(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child=ll.getChildAt(i);
            if(child instanceof CheckBox){
                list_check.add((CheckBox) child);
            }
        }
    }

    private void resizeLayout() {
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_submit_tips));
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(condition.isShowBaseNum()?492f:336f), DisplayUtil.dip2px(30f * dataAdapter.getCount()));//默认设置450dp
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowBaseNum());
        adapter.setSelect(1, condition.isCalcBlue());
        adapter.setSelect(2, condition.isShowMiss());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowBaseNum(adapter.containSelect(0));//是否显示开奖号
        condition.setCalcBlue(adapter.containSelect(1));//是否含蓝球计算
        condition.setShowMiss(adapter.containSelect(2));//是否显示遗漏
        //下方控制隐藏
        ViewUtils.showViewsVisible(condition.isCalcBlue(),tv_submit,HScrollView,view_span);
    }

    @Override
    protected void reverseData(){
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    protected void reloadData() {
        resizeLayout();
        dataAdapter.initNums();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueAnalyse(10088, "10027", condition.getPeriod(), condition.getWeekDay())
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
    public void onDoubleTaped() {
        closePop();
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_submit.getVisibility()==View.VISIBLE?false:true,tv_submit,HScrollView,view_span);
    }

}
