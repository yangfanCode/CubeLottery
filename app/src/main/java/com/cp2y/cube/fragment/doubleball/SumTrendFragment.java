package com.cp2y.cube.fragment.doubleball;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.adapter.TrendSumAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendBaseFragment;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
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
public class SumTrendFragment extends TrendBaseFragment {
    private View rootView;
    private HVSRelativeLayout relativeLayout;
    private TrendDateAdapter dateAdapter;
    private TrendSumAdapter dataAdapter;
    private List<String>list_data;
    private TextView tv_submit;
    private RadioGroup rg_num;
    private String sum="";
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private String[] sum_data={"22-39","40-57","58-75","76-93","94-111","112-129","130-147","148-165","166-183","184-199"};
    private HScrollView HScrollView;
    private View view_sum;
    private String flag="";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sum_trend, container, false);
        initSettingDialog(getString(R.string.sum_tips), MapUtils.SUM_SUPPORT, new int[]{1},0);
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
        dataAdapter = new TrendSumAdapter(getContext(),condition,0);
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
        tv_submit.setOnClickListener((v -> {
            if(!TextUtils.isEmpty(sum)){
                initData();
                if(flag.equals(sum)){
                    TipsToast.showTips("已存在过滤条件");
                    return;
                }
            }
            initData();
            if(!TextUtils.isEmpty(sum)){
                //添加数据
                flag=sum;//判断重复标记
                list_data.clear();
                list_data.add(sum.substring(0,sum.indexOf("-")));
                list_data.add(sum.substring(sum.lastIndexOf("-")+1,sum.length()));
                SingletonMapFilter.registerService("1", list_data);
                TipsToast.showTips("已成功保存至过滤条件");
            }

        }));
        //点击开关控制
        for (int i = 0; i < rg_num.getChildCount(); i++) {
            View child=rg_num.getChildAt(i);
            if(child instanceof RadioButton){
                ((RadioButton) child).setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    if(isChecked){
                        tv_submit.setEnabled(true);
                        tv_submit.setText("保存");
                    }
                }));
            }
        }
    }

    private void initData() {
        sum="";
        for (int i = 0; i < rg_num.getChildCount(); i++) {
            View child=rg_num.getChildAt(i);
            if(child instanceof RadioButton){
                if(((RadioButton) child).isChecked()){
                    sum=sum_data[i];
                }
            }
        }
    }

    private void initView(View rootView) {
        list_data=new ArrayList<>();
        container = (FrameLayout)getActivity(). findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView)getActivity(). findViewById(R.id.AVLoadingIndicator);
        rg_num = (RadioGroup)rootView. findViewById(R.id.sum_rg);
        tv_submit = (TextView)rootView. findViewById(R.id.trend_submit);
        HScrollView = (HScrollView)rootView. findViewById(R.id.trend_submit_layout);
        view_sum = (View)rootView. findViewById(R.id.trend_view_sum);
        tv_submit.setEnabled(false);
    }

    private void resizeLayout() {
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_submit_tips));
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(condition.isShowBaseNum()?796f:640f), DisplayUtil.dip2px(30.1f * dataAdapter.getCount()-1));//默认设置450dp
    }

    @Override
    protected void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        adapter.setSelect(0, condition.isShowBaseNum());
        adapter.setSelect(1, condition.isCalcBlue());
    }

    @Override
    protected void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        condition.setShowBaseNum(adapter.containSelect(0));//是否显示开奖号
        condition.setCalcBlue(adapter.containSelect(1));//是否含蓝球计算
        //下方滑动隐藏
        ViewUtils.showViewsVisible(condition.isCalcBlue(),view_sum,tv_submit,HScrollView);
    }

    @Override
    protected void reverseData() {
        dateAdapter.reverseData();
        dataAdapter.reverseData();
    }

    @Override
    protected void reloadData() {
        resizeLayout();
        LinearLayout numLayout = (LinearLayout) rootView.findViewById(R.id.trend_num_layout);
        int[] sums = condition.isCalcBlue()?MapUtils.SUM_BLUE_NUMS:MapUtils.SUM_NORMAL_NUMS;
        for (int i = 0; i < numLayout.getChildCount(); i++) {
            TextView tv = (TextView) numLayout.getChildAt(i);
            tv.setText(String.valueOf(sums[i]));
        }
        dataAdapter.initNums();
        if (condition.getPeriod() != condition.getCurrentPeriod() || condition.getWeekDay() != condition.getCurrentWeekDay()) {//加载期数不相同时再加载
            NetHelper.LOTTERY_API.getIssueNumber(10002, "10002", condition.getPeriod(), condition.getWeekDay(), 0)
                    .doOnTerminate(()->{condition.setCurrentPeriod(condition.getPeriod());condition.setCurrentWeekDay(condition.getWeekDay());})
                    .subscribe(new SafeOnlyNextSubscriber<IssueNumberDataModel>(){
                        @Override
                        public void onNext(IssueNumberDataModel args) {
                            super.onNext(args);
                            List<IssueNumberDataModel.IssueNumber> data = args.getData();
                            if (data != null) {
                                AVLoading.setVisibility(View.GONE);
                                container.setVisibility(View.VISIBLE);
                                Collections.reverse(data);
                                List<String> dates = new ArrayList<>();
                                for (IssueNumberDataModel.IssueNumber date:data) {
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
