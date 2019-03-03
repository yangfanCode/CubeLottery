package com.cp2y.cube.fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.adapter.CustomTrendAdapter;
import com.cp2y.cube.custom.EnLargeGridView;
import com.cp2y.cube.custom.H5TrendSingletonMap;
import com.cp2y.cube.model.TrendModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class TrendFragment extends Fragment {
    private boolean isBranch;
    private boolean isDeveloped;
    private List<TrendModel.Detail> ssList;
    private CustomTrendAdapter adapter;
    private EnLargeGridView gv;
    private AnimationSet manimationSet;
    private int lotteryId;
    private String lotteryName="";
    private RelativeLayout noDevelop_layout;
    /**
     * 双色球,大乐透,华东15选5,七乐彩,浙江快乐彩,上海11选5,
     **/
    private List<Integer> doubleTrend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_number_r);add(R.mipmap.chart_button_location_r);add(R.mipmap.chart_button_span);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> douTrendID = new HashSet<Integer>() {{add(10002);add(10088);add(10019);add(10083);add(10169);add(10152);}};
    /**
     * 新疆18选7 25选7 35选7 浙江20选5 广东36选7 广东26选5 深圳风采35选7 辽宁35选7 安徽25选5 河南22选5 湖北30选5 福建22选5 福建31选7 福建36选7 黑龙江22选5,黑龙江36选7,广西快乐双彩,湖南幸运赛车组3
     */
    private List<Integer> xinJiangTrend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_number_r);add(R.mipmap.chart_button_location);add(R.mipmap.chart_button_span);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> xinJiangTrendID = new HashSet<Integer>(){{add(10078);add(10079);add(10080);add(10021);add(10022);add(10023);add(10082);add(10051);add(10062);add(10028);add(10031);add(10174);add(10065);add(10066);add(10067);add(10068);add(10069);add(10209);add(10157);}};
    /**
     * 天津快乐十分,新疆11选5前5,北京11选5前5,天津11选5前5,浙江11选5前5,广东11选5,山东11选5,辽宁11选5,湖北11选5,山西11选5,江西11选5,广西快乐十分,广东快乐十分,广西11选5,重庆快乐十分
     */
    private List<Integer> tianJinKLSFTrend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_number_r);add(R.mipmap.chart_button_location);add(R.mipmap.chart_button_span_r);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> tianJinKLSFTrendID = new HashSet<Integer>(){{add(10206);add(10180);add(10179);add(10177);add(10210);add(10138);add(10103);add(10183);add(10182);add(10178);add(10129);add(10132);add(10142);add(10181);add(10147);add(10149);add(10150);}};
    /**
     * 安徽快三,广西快三,内蒙快三
     **/
//    private List<Integer> k3Trend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_number_r);add(R.mipmap.chart_button_span_r);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
//    private Set<Integer> k3TrendID = new HashSet<Integer>() {{add(10165);add(10166);add(10151);}};
    /**
     * 福彩3D,排列3,排列5,重庆时时彩3星5星2星,北京3D,上海4D,新疆时时彩5星3星2星,新疆11选5前3前2,北京11选5前3前2,天津时时彩5星3星2星,天津11选5 前3前2, 浙江6+1 江苏7位数 浙江11选5 前3 前2 ,广东11选5 前3 前2,山东11选5 前3 前2,辽宁11选5 前3 前2,河北福彩数字5 河北福彩数字7,湖北11选5 前3 前2,黑龙江P62,黑龙江6+1,山西11选5 前3 前2,江西11选5 前3 前2,广西11选5 前3 前2,湖南幸运赛车 全部 前2
     **/
    private List<Integer> d3Trend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_location_r);add(R.mipmap.chart_button_swing);add(R.mipmap.chart_button_span_r);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> d3TrendID = new HashSet<Integer>() {{add(10001);add(10003);add(10004);add(10093);add(10089);add(10095);add(10143);add(10011);add(10200);add(10201);add(10202);add(10190);add(10191);add(10188);add(10189);add(10203);add(10204);add(10205);add(10184);add(10185);add(10020);add(10048);add(10211);add(10212);
        add(10140);add(10141);add(10105);add(10106);add(10196);add(10197);add(10030);add(10029);add(10194);add(10195);add(10070);add(10071);add(10186);add(10187);add(10130);add(10131);add(10192);add(10193);add(10155);add(10156);}};
    /**
     * 七星彩,东方6+1,上海时时乐,上海11选5前三位,上海11选5前两位
     **/
    private List<Integer> QiXingTrend = new ArrayList<Integer>() {{add(R.mipmap.chart_button_number_r);add(R.mipmap.chart_button_swing_r);add(R.mipmap.chart_button_span);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> QiXingTrendID = new HashSet<Integer>() {{add(10005);add(10100);add(10084);add(10153);add(10154);}};

    //湖北快3,江苏快3,安徽快三,广西快三,内蒙快三
    private List<Integer> kuai3Trend=new ArrayList<Integer>(){{add(R.mipmap.chart_button_location_r);add(R.mipmap.chart_button_type_r);add(R.mipmap.chart_button_zuhe_r);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_connect);add(R.mipmap.chart_button_span);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> kuai3TrendID=new HashSet<Integer>(){{add(10148);add(10198);add(10199);add(10165);add(10166);add(10151);}};

    //广东好彩一
    private List<Integer> haoCaiYiTrend=new ArrayList<Integer>(){{add(R.mipmap.chart_button_digit_r);add(R.mipmap.chart_button_animal);add(R.mipmap.chart_button_season);add(R.mipmap.chart_button_direction);}};
    private Set<Integer> haoCaiYiTrendID=new HashSet<Integer>(){{add(10123);}};

    //快乐扑克
    private List<Integer> KLPKTrend=new ArrayList<Integer>(){{add(R.mipmap.chart_button_location_r);add(R.mipmap.chart_button_type_r);add(R.mipmap.chart_button_sum_r);add(R.mipmap.chart_button_span);add(R.mipmap.chart_button_divide);add(R.mipmap.chart_button_odd_even);add(R.mipmap.chart_button_big);add(R.mipmap.chart_button_summantissa);}};
    private Set<Integer> KLPKTrendID=new HashSet<Integer>(){{add(10167);}};

    private RelativeLayout add_childType_layout;

    @SuppressLint("ValidFragment")
    public TrendFragment(int lotteryId, String lotteryName, boolean isDeveloped, boolean isBranch) {
        this.lotteryId = lotteryId;
        this.lotteryName=lotteryName;
        this.isDeveloped=isDeveloped;
        this.isBranch=isBranch;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_trend, container, false);
        initView(rootView);
        return rootView;
    }
    //分支数据
    public void setSsList(List<TrendModel.Detail> ssList){
        this.ssList=ssList;
    }

    private void initListener() {
        gv.setOnItemClickListener(((parent, view, position, id) -> {
            setScaleAnimation(view);
            Intent intent = new Intent(getActivity(), H5TrendSingletonMap.getH5Class(lotteryId));
            intent.putExtra("pos", position);
            intent.putExtra("lotteryName",lotteryName);
            startActivity(intent);
        }));

    }

    private void initData() {
        if (douTrendID.contains(lotteryId)) {
            adapter.loadData(doubleTrend);
        } else if (d3TrendID.contains(lotteryId)) {
            adapter.loadData(d3Trend);
        } else if (QiXingTrendID.contains(lotteryId)) {
            adapter.loadData(QiXingTrend);
        } else if(xinJiangTrendID.contains(lotteryId)) {
            adapter.loadData(xinJiangTrend);
        } else if(tianJinKLSFTrendID.contains(lotteryId)) {
            adapter.loadData(tianJinKLSFTrend);
        } else if(kuai3TrendID.contains(lotteryId)){
            adapter.loadData(kuai3Trend);
        } else if(haoCaiYiTrendID.contains(lotteryId)){
            adapter.loadData(haoCaiYiTrend);
        } else if(KLPKTrendID.contains(lotteryId)){
            adapter.loadData(KLPKTrend);
        }
    }

    private void initView(View rootView) {
        if (isDeveloped) {//已开发彩种
            gv = (EnLargeGridView) rootView.findViewById(R.id.trend_gv);
            gv.setLayoutAnimation(getAnimationController());
            gv.setSelector(new StateListDrawable());//不要选中时的背景效果
            add_childType_layout = (RelativeLayout) rootView.findViewById(R.id.trend_addChild_layout);
            if(isBranch){//有分支
                addChildTypeView();
                lotteryName=ssList.get(0).getLotteryName();//有分支的彩种默认名字
            }
            add_childType_layout.setVisibility(isBranch? View.VISIBLE : View.GONE);
            adapter = new CustomTrendAdapter(getActivity(), lotteryId);
            gv.setAdapter(adapter);
            initData();
            initListener();//监听
        } else {
            noDevelop_layout = (RelativeLayout) rootView.findViewById(R.id.noDevelop_layout);
            showNoDevelop();
        }

    }

    private void showNoDevelop() {
        noDevelop_layout.removeAllViews();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.nodevelop_layout, null);
        noDevelop_layout.addView(view);
        noDevelop_layout.setVisibility(View.VISIBLE);
        ((Button) view.findViewById(R.id.go_custom_btn)).setOnClickListener((v -> startActivity(new Intent(getActivity(), CustomProvinceActivity.class))));
    }

    //添加区分彩种选项radiobutton
    private void addChildTypeView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.shanghai_11xuan5_child_layout, null);
            ((RadioButton)view.findViewById(R.id.trend_cq_rb5x)).setText(ssList.get(0).getLotteryName());
            ((RadioButton)view.findViewById(R.id.trend_cq_rb3x)).setText(ssList.get(1).getLotteryName());
            ((RadioButton)view.findViewById(R.id.trend_cq_rb2x)).setText(ssList.get(2).getLotteryName());
        //选项监听
        if(view==null)return;
        RadioGroup cq_rg = (RadioGroup) view.findViewById(R.id.trend_cq_rg);
        cq_rg.setOnCheckedChangeListener(((group, checkedId) -> {
            gv.setLayoutAnimation(getAnimationController());
            switch (checkedId) {
                case R.id.trend_cq_rb2x:
                    lotteryId=ssList.get(2).getLotteryID();
                    lotteryName=ssList.get(2).getLotteryName();
                    break;
                case R.id.trend_cq_rb3x:
                    lotteryId = ssList.get(1).getLotteryID();
                    lotteryName=ssList.get(1).getLotteryName();
                    break;
                case R.id.trend_cq_rb5x:
                    lotteryId = ssList.get(0).getLotteryID();
                    lotteryName=ssList.get(0).getLotteryName();
                    break;
            }
            initData();
        }));
        add_childType_layout.addView(view);
    }

    //放大动画
    public void setScaleAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(true);
        if (manimationSet != null && manimationSet != animationSet) {
            //manimationSet.setFillAfter(false);
            manimationSet.cancel();
        }
        // ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2f, 1.0f,
        // 2f, 1, 0.5f, 1, 0.5f);
        Animation scaleAnimation = AnimationUtils.loadAnimation(getActivity(),
                R.anim.save_num);
        animationSet.addAnimation(scaleAnimation);
        //animationSet.setFillAfter(true);//动画终止时停留在最后一帧
        view.startAnimation(animationSet);
        manimationSet = animationSet;
    }


    /**
     * Layout动画
     * gridview下落动画
     *
     * @return
     */
    protected LayoutAnimationController getAnimationController() {
        int duration = 100;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }
}
