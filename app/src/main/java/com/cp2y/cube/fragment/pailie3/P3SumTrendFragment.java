package com.cp2y.cube.fragment.pailie3;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.D3TrendSumAdapter;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendD3BaseFragment;
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
 * 和值走势图
 */
public class P3SumTrendFragment extends TrendD3BaseFragment {
    private List<CheckBox>list_check;
    private View rootView;
    private List<String>list_flag=new ArrayList<>();
    private HVSRelativeLayout relativeLayout;
    private TrendDateAdapter dateAdapter;
    private D3TrendSumAdapter dataAdapter;
    private List<String>list_data;
    private TextView tv_submit;
    private RadioGroup rg_num;
    private int checkCount=0;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private LinearLayout ll;
    private String[] sum_data={"18-34","35-51","52-68","69-85","86-102","103-119","120-136","137-153","154-170","171-188"};
    private HScrollView HScrollView;
    private View view_sum;
    private String flag="";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_3d_sum_trend, container, false);
        initSettingDialog(getString(R.string.sum_tips), MapUtils.D3_LOCATE_SUPPORT, new int[]{1});
        //默认不显示开奖号
        condition.setShowBaseNum(false);
        setHasOptionsMenu(true);
        initView(rootView);
        relativeLayout = (HVSRelativeLayout) rootView.findViewById(R.id.trend_layout);
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter = new D3TrendSumAdapter(getContext(),condition,3);
        dataList.setAdapter(dataAdapter);
//        HScrollView scrollView = (HScrollView) rootView.findViewById(R.id.scroll_layout4);
//        scrollView.setScrollViewListener((view, x, y, oldx, oldy) -> {
//            dataAdapter.setXoffset(x);
//            dataAdapter.notifyDataSetChanged();
//        });
        resizeLayout();
        initListener();
        return rootView;
    }

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
                SingletonMapFilter.registerP3Service("1", list_data);
            }
            TipsToast.showTips("已成功保存至过滤条件");
        });
        //点击开关控制
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

    private void initView(View rootView) {
        list_data=new ArrayList<>();
        list_check=new ArrayList<>();
        ll = (LinearLayout)rootView. findViewById(R.id.sum_num_ll);
        container = (FrameLayout)getActivity(). findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView)getActivity(). findViewById(R.id.AVLoadingIndicator);
        tv_submit = (TextView)rootView. findViewById(R.id.trend_submit);
        HScrollView = (HScrollView)rootView. findViewById(R.id.trend_submit_layout);
        view_sum = (View)rootView. findViewById(R.id.trend_view_sum);
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
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(condition.isShowBaseNum()?946f:875f), DisplayUtil.dip2px(30f * dataAdapter.getCount()));//默认设置450dp
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
    }

    @Override
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    protected void reloadData() {
        resizeLayout();
        dataAdapter.initNums();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueAnalyse(10003, "10002", condition.getPeriod(), condition.getWeekDay())
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
        if(condition.isCalcBlue()) {
            ViewUtils.showViewsVisible(tv_submit.getVisibility() == View.VISIBLE ? false : true, tv_submit, HScrollView, view_sum);
        }
    }

}
