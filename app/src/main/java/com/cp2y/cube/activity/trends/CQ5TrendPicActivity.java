package com.cp2y.cube.activity.trends;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
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
import com.cp2y.cube.activity.ignore.chongqing5.IgnoreCQ5Activity;
import com.cp2y.cube.activity.ignore.chongqing5.PartIgnoreCQ5Activity;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendP5BaseFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5BigTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5Chu3yuTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5JiouTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5LocationTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5SpanTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5SumMantissaTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5SumTrendFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5SwingTrendFragment;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.CommonService;
import com.cp2y.cube.utils.CommonUtil;

public class CQ5TrendPicActivity extends BaseActivity {
    private AlertDialog dialog=null;
    private int pos;
    private String lotteryName;
    private PopupWindow popupWindow = null;
    private String[] title_array = {"定位走势", "振幅走势", "跨度走势", "除3余走势", "和值走势", "奇偶走势", "大小走势", "和尾数走势"};
    private static Class[] TREND_FRAGMENTS = {CQ5LocationTrendFragment.class, CQ5SwingTrendFragment.class, CQ5SpanTrendFragment.class, CQ5Chu3yuTrendFragment.class, CQ5SumTrendFragment.class, CQ5JiouTrendFragment.class, CQ5BigTrendFragment.class, CQ5SumMantissaTrendFragment.class};
    /**
     * 走势图
     **/
    private GestureDetectorCompat mDetector;
    private ImageView pop_iv,ignore_iv;
    private ImageView netOff;

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
                        isShowSystemStatusBar(bar.getVisibility()==View.VISIBLE?true:false);
                        TrendP5BaseFragment fragment = (TrendP5BaseFragment) getSupportFragmentManager().findFragmentByTag("TrendFragment");
                        fragment.onDoubleTaped();
                    }
                });
                return true;
            }

        });
        setContentView(R.layout.activity_p5_trend_pic);
        pos = getIntent().getIntExtra("pos", 0);
        lotteryName=getIntent().getStringExtra("lotteryName");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((view -> finish()));
        setMainTitle(lotteryName+"-"+title_array[pos]);
        RelativeLayout layout= (RelativeLayout) findViewById(R.id.trend_title_layout);
        layout.setOnClickListener((v1 -> showSubTitles()));
        pop_iv = (ImageView) findViewById(R.id.trend_pop_iv);
        ignore_iv = (ImageView) findViewById(R.id.trend_ignore);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        ignore_iv.setImageResource(pos == 1 ? R.mipmap.icon_shuoming_weight : R.mipmap.icon_yilou);
        ignore_iv.setOnClickListener((v -> ignoreListener()));
        initChange(pos);
        selectGuide(pos);
    }
    private void ignoreListener() {
        Intent intent = new Intent();
        if (pos == 2 ||  pos == 4 ||  pos == 7) {
            intent.setClass(CQ5TrendPicActivity.this, IgnoreCQ5Activity.class);
            intent.putExtra("pos", pos);
            startActivity(intent);
        } else if(pos==1){
            setTipDialog();
        }else{
            intent.setClass(CQ5TrendPicActivity.this, PartIgnoreCQ5Activity.class);
            intent.putExtra("pos", pos);
            startActivity(intent);
        }
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
        pos=position;
        try {
            if(!CommonUtil.isNetworkConnected(this)){//断网机制
                TipsToast.showTips("请检查网络设置");
                netOff.setVisibility(View.VISIBLE);
                netOff.setOnClickListener((v -> initChange(position)));//点击加载
            }else{
                netOff.setVisibility(View.GONE);
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.trend_container, (Fragment) TREND_FRAGMENTS[position].newInstance(), "TrendFragment").commit();
            ignore_iv.setImageResource(pos == 1 ? R.mipmap.icon_shuoming_weight : R.mipmap.icon_yilou);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //新手引导 只留保存
    public void selectGuide(int position){
        //如果第一次入口为号码走势和定位走势只展示走势设置新手引导和双击
        if(position==0||position==1&& GuideFlag.isFirst_Guide){
            //setMoreGuideDialog(Arrays.asList(new String[]{"trendSet","trendDoubleClick"}),0,new Integer[]{R.mipmap.trend_set_guide,R.mipmap.trend_doubleclick_guide});
        }else if(position!=0&&position!=1&&GuideFlag.isFirst_Guide){
            //如果第一次入口为过滤条件则展示三张引导
            //setMoreGuideDialog(Arrays.asList(new String[]{"trendSet","trendConditionSave","trendDoubleClick"}),0,new Integer[]{R.mipmap.trend_set_guide,R.mipmap.trend_save_condition_guide,R.mipmap.trend_doubleclick_guide});
            setGuideDialog("trendConditionSave",R.mipmap.trend_save_condition_guide);
        }else{
            setGuideDialog("trendConditionSave",R.mipmap.trend_save_condition_guide);
        }
        GuideFlag.isFirst_Guide=false;
    }
    //弹窗处理
    public void showSubTitles() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(CQ5TrendPicActivity.this)
                    .inflate(R.layout.trend_3dpop, null);
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
                            setMainTitle(lotteryName+"-"+title_array[i]);
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
        if(popupWindow==null)return;
        popupWindow.dismiss();
    }
    //显示与隐藏系统状态栏
    public void isShowSystemStatusBar(boolean isShow){
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

    public void setTipDialog(){
        if(dialog==null){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            View view=LayoutInflater.from(this).inflate(R.layout.d3_swing_dialog,null);
            builder.setView(view);
            builder.setCancelable(false);
            view.findViewById(R.id.swing_dialog_btn).setOnClickListener((v -> {dialog.dismiss();}));
            dialog=builder.show();
        }else{
            dialog.show();
        }

    }
}
