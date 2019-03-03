package com.cp2y.cube.fragment.pailie3;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
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
import com.cp2y.cube.adapter.D3TrendLocateAdapter;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.adapter.TrendDateAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendD3BaseFragment;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.IssueNumberDataModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.D3LotteryService;
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
public class P3SwingTrendFragment extends TrendD3BaseFragment {
    private boolean isFullScreen=false;//是否全屏状态
    //辅助标记
    private List<CheckBox>list_check_bai;
    private List<CheckBox>list_check_shi;
    private List<CheckBox>list_check_ge;
    private List<String>list_red;
    private List<String>list_blue;
    private List<String>list_total;
    private int num_bai=0;
    private int num_shi=0;
    private int num_ge=0;
    private TrendDateAdapter dateAdapter;
    private D3TrendLocateAdapter dataAdapter;
    private HVSRelativeLayout relativeLayout;
    private LinearLayout numsLayout;
    private RelativeLayout tv_sunbmit;
    private LinearLayout layout;
    private MyGridView gv_bai,gv_shi,gv_ge;
    private List<String>list_bai_select,list_shi_select,list_ge_select;
    private ArrayAdapter<String> adapter_bai,adapter_shi,adapter_ge;
    private AVLoadingIndicatorView AVLoading;
    private FrameLayout container;
    private HScrollView HScrollView;
    private View view_location;
    private View rootView;
    private int locateFlag=0;
    private String[] tabTitle={"百位","十位","个位"};
    private TextView submit_tv,submit_tv_tip;
    private HScrollView bai_ll,shi_ll,ge_ll;
    private int position=0;
    private AlertDialog dialog=null;
    private List<String> list = new ArrayList<>(); //总保存数据集合

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_d3_location_trend, container, false);
        initSettingDialog(getString(R.string.locate_tips), MapUtils.D3_LOCATE_SUPPORT, new int[]{0,1});
        setHasOptionsMenu(true);
        initView(rootView);
        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        //标题栏
        for (int i = 0; i < 3; i++) {
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
                position=tab.getPosition();//双击
                int pos=tab.getPosition();
                dataAdapter.reloadType((int) tab.getTag());
                locateFlag=pos;
                reloadNums();
                if(!isFullScreen){//全屏状态下执行
                    setSubmitLayoutChange(pos);//下方选号切换
                    setSubmitChangeView(pos);//切换控件初始化
                    setListener(pos);//切换控件监听
                }
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
        relativeLayout.setHsvViewSize(DisplayUtil.dip2px(520f), DisplayUtil.dip2px(0f));//默认设置450dp
        HVListView dateList = (HVListView) rootView.findViewById(R.id.scroll_layout1);
        HVListView dataList = (HVListView) rootView.findViewById(R.id.scroll_list);
        relativeLayout.setDateList(dateList);
        relativeLayout.setDataList(dataList);
        dateAdapter = new TrendDateAdapter(getContext());
        dateList.setAdapter(dateAdapter);
        dataAdapter = new D3TrendLocateAdapter(getContext(), condition);
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
        tv_sunbmit.setOnClickListener((v)->{
            int selectCount=0;
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                //定位
                //判断相同号码
                List<String> list_check = new ArrayList<String>();
                list_check.addAll(list_bai_select);
                //检验蓝球特殊标记 加50
                for (String num : list_shi_select) {
                    list_check.add((Integer.parseInt(num) + 50) + "");
                }
                for (String num : list_ge_select) {
                    list_check.add((Integer.parseInt(num) + 100) + "");
                }
                if (CommonUtil.ListCheck(list_check, list)) {
                    TipsToast.showTips("已存在号码库");
                    return;
                }
                list.clear();
                list.addAll(list_bai_select);
                //十位特殊标记 加50
                for (String num : list_shi_select) {
                    list.add(Integer.parseInt(num) + 50 + "");
                }//个位特殊标记 加100
                for (String num : list_ge_select) {
                    list.add((Integer.parseInt(num) + 100) + "");
                }
                CommonUtil.SortCollection(list);//排序
                selectCount = (list_bai_select.size() * list_shi_select.size() * list_ge_select.size());
                getService(D3LotteryService.class).saveLotteryNumber(list, 1, 3,selectCount);
            }else{
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        });
        setListener(0);//默认百位监听
    }


    private void initView(View rootView) {
        list_red=new ArrayList<>();
        list_blue=new ArrayList<>();
        list_total=new ArrayList<>();
        list_check_bai=new ArrayList<>();
        list_check_shi=new ArrayList<>();
        list_check_ge=new ArrayList<>();
        list_bai_select=new ArrayList<>();
        list_shi_select=new ArrayList<>();
        list_ge_select=new ArrayList<>();
        container = (FrameLayout)getActivity(). findViewById(R.id.trend_container);
        AVLoading = (AVLoadingIndicatorView)getActivity(). findViewById(R.id.AVLoadingIndicator);
        layout = (LinearLayout)rootView. findViewById(R.id.location_trend_layout);
        gv_bai = (MyGridView)rootView. findViewById(R.id.location_trend_gvBai);
        gv_shi = (MyGridView)rootView. findViewById(R.id.location_trend_gvShi);
        gv_ge = (MyGridView)rootView. findViewById(R.id.location_trend_gvGe);
        tv_sunbmit = (RelativeLayout)rootView. findViewById(R.id.trend_submit);
        bai_ll = (HScrollView)rootView. findViewById(R.id.d3_selectnum_bai);
        shi_ll = (HScrollView)rootView. findViewById(R.id.d3_selectnum_shi);
        ge_ll = (HScrollView)rootView. findViewById(R.id.d3_selectnum_ge);
        shi_ll.setVisibility(View.GONE);
        ge_ll.setVisibility(View.GONE);
        tv_sunbmit.setEnabled(false);
        submit_tv = (TextView)rootView. findViewById(R.id.trend_submit_tv);
        submit_tv_tip = (TextView)rootView. findViewById(R.id.trend_submit_tip_tv);
        LinearLayout ll= (LinearLayout) bai_ll.findViewById(R.id.location_trend_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child=ll.getChildAt(i);
            if(child instanceof CheckBox){
                list_check_bai.add((CheckBox) child);
            }
        }//默认百位选号
        adapter_bai =new ArrayAdapter<String>(getActivity(),R.layout.item_trend_selectnum,R.id.item_trend_select_tv,list_bai_select);
        gv_bai.setAdapter(adapter_bai);
        adapter_shi=new ArrayAdapter<String>(getActivity(),R.layout.item_trend_selectnum,R.id.item_trend_select_tv,list_shi_select);
        gv_shi.setAdapter(adapter_shi);
        adapter_ge=new ArrayAdapter<String>(getActivity(),R.layout.item_trend_selectnum,R.id.item_trend_select_tv,list_ge_select);
        gv_ge.setAdapter(adapter_ge);
    }
    //标题下方号码
    private void reloadNums() {
        numsLayout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.weight = 1;
        lp.gravity = Gravity.CENTER;
        for (int i = 0; i <= 9 ; i++) {
            TextView text = new TextView(ContextHelper.getLastActivity());
            text.setTextSize(12);
            text.setTextColor(ColorUtils.NORMAL_GRAY);
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT,1.0f);
            text.setLayoutParams(params);
            text.setGravity(Gravity.CENTER);
            String numText = String.valueOf(i);
            text.setText(numText);
            numsLayout.addView(text, lp);
        }
        ViewGroup.LayoutParams lp1 = numsLayout.getLayoutParams();
        //设置tablayout下方号码布局宽度
        int width=0,widthNum=0;
        width = DisplayUtil.dip2px(30f*10);
        lp1.width = width;
        numsLayout.setLayoutParams(lp1);
        //此处宽度根绝开奖号码设置
        widthNum=DisplayUtil.dip2px(condition.isShowBaseNum()?(30f*10+69f):30f*10);
        ViewUtils.showViewsVisible(condition.isShowBaseNum(), relativeLayout.findViewById(R.id.trend_number_title), relativeLayout.findViewById(R.id.trend_locate_lotteryNums),relativeLayout.findViewById(R.id.trend_locate_view)
                ,relativeLayout.findViewById(R.id.location_trend_submit_tip_layout));
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
            NetHelper.LOTTERY_API.getIssueNumber(10003, "10002", condition.getPeriod(), condition.getWeekDay(), 3)
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
        if(list_bai_select.size()>0||list_shi_select.size()>0||list_ge_select.size()>0) {
            layout.setVisibility(layout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        }
        relativeLayout.keepOnHorizontal();
        //双击隐藏选号条件
        ViewUtils.showViewsVisible(tv_sunbmit.getVisibility()==View.VISIBLE?false:true,tv_sunbmit,view_location = (View)rootView. findViewById(R.id.trend_view_location)
                ,relativeLayout.findViewById(R.id.location_trend_submit_tip_layout));
        if(position==0){//隐藏选号
            bai_ll.setVisibility(tv_sunbmit.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        }else if(position==1){
            shi_ll.setVisibility(tv_sunbmit.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        }else{
            ge_ll.setVisibility(tv_sunbmit.getVisibility()==View.VISIBLE?View.VISIBLE:View.GONE);
        }
        isFullScreen=tv_sunbmit.getVisibility()==View.VISIBLE?false:true;//全屏状态标记
    }
    public void setSubmitLayoutChange(int pos){
        if(pos==0){//百位
            submit_tv_tip.setText("(百位)");
            bai_ll.setVisibility(View.VISIBLE);
            shi_ll.setVisibility(View.GONE);
            ge_ll.setVisibility(View.GONE);
        }else if(pos==1){
            submit_tv_tip.setText("(十位)");
            bai_ll.setVisibility(View.GONE);
            shi_ll.setVisibility(View.VISIBLE);
            ge_ll.setVisibility(View.GONE);
        }else{
            submit_tv_tip.setText("(个位)");
            bai_ll.setVisibility(View.GONE);
            shi_ll.setVisibility(View.GONE);
            ge_ll.setVisibility(View.VISIBLE);
        }
    }

    public void setSubmitChangeView(int pos){
        list_check_bai.clear();
        list_check_shi.clear();
        list_check_ge.clear();
        LinearLayout ll=null;
        if(pos==0){
            ll= (LinearLayout) bai_ll.findViewById(R.id.location_trend_ll);
        }else if(pos==1){
            ll= (LinearLayout) shi_ll.findViewById(R.id.location_trend_ll);
        }else{
            ll= (LinearLayout) ge_ll.findViewById(R.id.location_trend_ll);
        }
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child=ll.getChildAt(i);
            if(child instanceof CheckBox){
                if(pos==0){
                    list_check_bai.add((CheckBox) child);
                }else if(pos==1){
                    list_check_shi.add((CheckBox) child);
                }else{
                    list_check_ge.add((CheckBox) child);
                }
            }
        }
    }
    public void setListener(int pos){
        if(pos==0){
            setSubmitChangeListener(pos,list_check_bai,list_bai_select,adapter_bai);
        }else if(pos==1){
            setSubmitChangeListener(pos,list_check_shi,list_shi_select,adapter_shi);
        }else{
            setSubmitChangeListener(pos,list_check_ge,list_ge_select,adapter_ge);
        }
    }

    public void setSubmitChangeListener(int pos,List<CheckBox> list,List<String> select,ArrayAdapter adapter){
        //个位监听控制提交按钮
        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;
            list.get(i).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        layout.setVisibility(View.VISIBLE);
                        select.add(String.valueOf(finalI));
                        if(pos==0){
                            num_bai++;
                        }else if(pos==1){
                            num_shi++;
                        }else{
                            num_ge++;
                        }
                    } else {
                        select.remove(String.valueOf(finalI));
                        if(pos==0){
                            num_bai--;
                        }else if(pos==1){
                            num_shi--;
                        }else{
                            num_ge--;
                        }
                    }
                    //控制开关和 弹窗
                    if (num_bai > 0 && num_shi > 0 && num_ge > 0) {
                        tv_sunbmit.setEnabled(true);
                        submit_tv.setText("保存");
                        submit_tv_tip.setVisibility(View.GONE);
                    } else if (num_bai == 0 && num_shi == 0 && num_ge == 0) {
                        layout.setVisibility(View.GONE);
                    } else {
                        tv_sunbmit.setEnabled(false);
                        submit_tv.setText("选号");
                        submit_tv_tip.setVisibility(View.VISIBLE);
                    }
                    //排序
                    CommonUtil.SortCollection(select);
                    //第一次点击就显示
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }
}
