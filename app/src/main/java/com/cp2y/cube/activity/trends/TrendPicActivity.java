package com.cp2y.cube.activity.trends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.ignore.doubleball.IgnoreActivity;
import com.cp2y.cube.activity.ignore.doubleball.PartIgnoreActivity;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendBaseFragment;
import com.cp2y.cube.fragment.doubleball.BigTrendFragment;
import com.cp2y.cube.fragment.doubleball.Chu3yuTrendFragment;
import com.cp2y.cube.fragment.doubleball.JiouTrendFragment;
import com.cp2y.cube.fragment.doubleball.LocationTrendFragment;
import com.cp2y.cube.fragment.doubleball.NumTrendFragment;
import com.cp2y.cube.fragment.doubleball.SpanTrendFragment;
import com.cp2y.cube.fragment.doubleball.SumMantissaTrendFragment;
import com.cp2y.cube.fragment.doubleball.SumTrendFragment;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.CommonService;
import com.cp2y.cube.utils.CommonUtil;

import java.util.HashSet;
import java.util.Set;

public class TrendPicActivity extends BaseActivity {
    private String lotteryName;
    private int pos;
    private ImageView netOff;
    private PopupWindow popupWindow = null;
    private String[] title_array = {"号码走势", "定位走势", "跨度走势", "除3余走势", "和值走势", "奇偶走势", "大小走势", "和尾数走势"};
    private static Class[] TREND_FRAGMENTS = {NumTrendFragment.class, LocationTrendFragment.class, SpanTrendFragment.class, Chu3yuTrendFragment.class, SumTrendFragment.class, JiouTrendFragment.class, BigTrendFragment.class, SumMantissaTrendFragment.class};
    /**
     * 走势图
     **/
    private GestureDetectorCompat mDetector;
    private ImageView pop_iv;
    private ImageView ignore_iv;
    private Set<Integer> noIgnore = new HashSet<Integer>() {{
        add(1);
    }};//不显示遗漏的走势

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        mDetector = new GestureDetectorCompat(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                getService(CommonService.class).runDelay(150).subscribe(new SafeOnlyNextSubscriber<Long>() {
                    @Override
                    public void onNext(Long args) {
                        super.onNext(args);
                        Toolbar bar = getToolBar();
                        bar.setVisibility(bar.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                        //系统状态栏
                        isShowSystemStatusBar(bar.getVisibility() == View.VISIBLE ? true : false);
                        TrendBaseFragment fragment = (TrendBaseFragment) getSupportFragmentManager().findFragmentByTag("TrendFragment");
                        fragment.onDoubleTaped();
                    }
                });
                return true;
            }

        });
        setContentView(R.layout.activity_trend_pic);
        pos = getIntent().getIntExtra("pos", 0);
        lotteryName = getIntent().getStringExtra("lotteryName");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((view -> finish()));
        setMainTitle(lotteryName + "-" + title_array[pos]);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.trend_title_layout);
        layout.setOnClickListener((v1 -> showSubTitles()));
        pop_iv = (ImageView) findViewById(R.id.trend_pop_iv);
        ignore_iv = (ImageView) findViewById(R.id.trend_ignore);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        ignore_iv.setVisibility(noIgnore.contains(pos) ? View.GONE : View.VISIBLE);//遗漏隐藏
        ignore_iv.setOnClickListener((v -> ignoreListener()));
        initChange(pos);
        selectGuide(pos);
    }

    private void ignoreListener() {
        Intent intent = new Intent();
        if (pos == 3 || pos == 5 || pos == 6) {
            intent.setClass(TrendPicActivity.this, PartIgnoreActivity.class);
        } else {
            intent.setClass(TrendPicActivity.this, IgnoreActivity.class);
        }
        intent.putExtra("pos", pos);
        startActivity(intent);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_trend, menu);
        return true;
    }

    private void initChange(int position) {
        //切换显示转圈
        findViewById(R.id.trend_container).setVisibility(View.GONE);
        findViewById(R.id.AVLoadingIndicator).setVisibility(View.VISIBLE);
        pos = position;
        try {
            if (!CommonUtil.isNetworkConnected(this)) {//断网机制
                netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                netOff.setOnClickListener((v -> initChange(position)));//点击加载
            } else {
                netOff.setVisibility(View.GONE);
            }
            ignore_iv.setVisibility(noIgnore.contains(pos) ? View.GONE : View.VISIBLE);//定位走势隐藏
            getSupportFragmentManager().beginTransaction().replace(R.id.trend_container, (Fragment) TREND_FRAGMENTS[position].newInstance(), "TrendFragment").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    //新手引导
//    public void selectGuide(int position) {
//        //如果第一次入口为号码走势和定位走势只展示走势设置新手引导和双击  遗漏
//        if (position == 0 || position == 1 && GuideFlag.isFirst_Guide) {
//            if(position==0){
//                setMoreGuideDialog(Arrays.asList(new String[]{"trendSet", "trendDoubleClick","ignoreIcon"}), 0, new Integer[]{R.mipmap.trend_set_guide, R.mipmap.trend_doubleclick_guide,R.mipmap.tip_ignore_icon});
//            }else{//定位走势不显示遗漏
//                setMoreGuideDialog(Arrays.asList(new String[]{"trendSet", "trendDoubleClick"}), 0, new Integer[]{R.mipmap.trend_set_guide, R.mipmap.trend_doubleclick_guide});
//            }
//        } else if (position != 0 && position != 1 && GuideFlag.isFirst_Guide) {
//            //如果第一次入口为过滤条件则展示四张引导
//            setMoreGuideDialog(Arrays.asList(new String[]{"trendSet", "trendConditionSave", "trendDoubleClick","ignoreIcon"}), 0, new Integer[]{R.mipmap.trend_set_guide, R.mipmap.trend_save_condition_guide, R.mipmap.trend_doubleclick_guide,R.mipmap.tip_ignore_icon});
//        } else {//第一次进入号码走势定位走势第二次进入展示保存
//            setGuideDialog("trendConditionSave", R.mipmap.trend_save_condition_guide);
//        }
//        GuideFlag.isFirst_Guide = false;
//    }
//新手引导 只留保存
    public void selectGuide(int position) {
        //如果第一次入口为号码走势和定位走势只展示走势设置新手引导和双击
        if (position == 0 || position == 1 && GuideFlag.isFirst_Guide) {
            //setMoreGuideDialog(Arrays.asList(new String[]{"trendSet","trendDoubleClick"}),0,new Integer[]{R.mipmap.trend_set_guide,R.mipmap.trend_doubleclick_guide});
        } else if (position != 0 && position != 1 && GuideFlag.isFirst_Guide) {
            //如果第一次入口为过滤条件则展示三张引导
            //setMoreGuideDialog(Arrays.asList(new String[]{"trendSet","trendConditionSave","trendDoubleClick"}),0,new Integer[]{R.mipmap.trend_set_guide,R.mipmap.trend_save_condition_guide,R.mipmap.trend_doubleclick_guide});
            setGuideDialog("trendConditionSave", R.mipmap.trend_save_condition_guide);
        } else {
            setGuideDialog("trendConditionSave", R.mipmap.trend_save_condition_guide);
        }
        GuideFlag.isFirst_Guide = false;
    }


    //弹窗处理
    public void showSubTitles() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(TrendPicActivity.this)
                    .inflate(R.layout.trendpop, null);
            RadioButton[] buttons = new RadioButton[]{
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbNum),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbDingwei),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbKuadu),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbChu3yu),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbSum),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbJiou),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbDaXiao),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbSumMantissa)
            };
            buttons[pos].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton, checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            initChange(i);
                            //切换判断新手引导
                            selectGuide(i);
                            setMainTitle(lotteryName + "-" + title_array[i]);
                            showSubTitles();
                            continue;
                        }
                        button.setChecked(false);
                    }
                }
            };
            for (RadioButton button : buttons) {
                button.setOnCheckedChangeListener(listener);
            }
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            //popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setContentView(contentView);
        }
        if (popupWindow.isShowing()) {
            pop_iv.setImageResource(R.mipmap.icon_xiaola);
            popupWindow.dismiss();
        } else {
            //展示在toolbar下方
            pop_iv.setImageResource(R.mipmap.icon_xiala_top);
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
            //popupWindow.showAtLocation(getToolBar(), Gravity.TOP, 0, 250);
        }
    }

    public void ClosePop() {
        if (popupWindow == null) return;
        popupWindow.dismiss();
    }

    //显示与隐藏系统状态栏
    public void isShowSystemStatusBar(boolean isShow) {
        if (!isShow) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

}
