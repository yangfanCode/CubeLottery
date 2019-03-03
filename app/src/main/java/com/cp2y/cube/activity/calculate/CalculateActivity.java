package com.cp2y.cube.activity.calculate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.calculate.fragment.d3.D3CalcFragment;
import com.cp2y.cube.activity.calculate.fragment.doubleball.DoubleBallCalcFragment;
import com.cp2y.cube.activity.calculate.fragment.lotto.LottoCalcFragment;
import com.cp2y.cube.activity.calculate.fragment.p3.P3CalcFragment;
import com.cp2y.cube.activity.calculate.fragment.p5.P5CalcFragment;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonMap3D;
import com.cp2y.cube.custom.SingletonMapLotto;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.custom.SingletonMapP3;
import com.cp2y.cube.custom.SingletonMapP5;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CalcLotteryIssueModel;
import com.cp2y.cube.model.CalcLotteryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.wheel.WheelFactory;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;
import com.wang.avi.AVLoadingIndicatorView;
import com.yangfan.widget.CustomDialog;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.OptionPicker;

public class CalculateActivity extends BaseActivity {
    private MyInterface.SetCalcIssue calcIssue;
    private PopupWindow popupWindow = null;
    private TextView tvDate, tvTitle;
    private ImageView pop_iv;
    private FragmentManager fm;
    private int pos;
    private Toolbar calcBar, normalBar;
    private List<Fragment> trend_fragments;
    private List<String> titles = new ArrayList<>();//title集合
    private List<Integer> lotteryIds = new ArrayList<>();
    private List<String> lotteryIssues = new ArrayList<>();//奖期集合
    private int wheelIndex = 0;
    private String issue = "";//奖期
    private Map<Integer, Fragment> map = new HashMap<Integer, Fragment>() {{
        put(10002, DoubleBallCalcFragment.newInstance());
        put(10088, LottoCalcFragment.newInstance());
        put(10001, D3CalcFragment.newInstance());
        put(10003, P3CalcFragment.newInstance());
        put(10004, P5CalcFragment.newInstance());
    }};
    private Map<Integer, Map<String, List<String>>> singletonMap = new HashMap<Integer, Map<String, List<String>>>() {{
        put(10002, SingletonMapNomal.getMap());
        put(10088, SingletonMapLotto.getMap());
        put(10001, SingletonMap3D.getMap());
        put(10003, SingletonMapP3.getMap());
        put(10004, SingletonMapP5.getMap());
    }};
    private AVLoadingIndicatorView AVLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        setMainTitle("计算奖金");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();
        initData();
        initListener();
        switchToolBar(false);
    }

    public void setCalcIssue(MyInterface.SetCalcIssue calcIssue) {
        this.calcIssue = calcIssue;
    }

    private void initListener() {
        tvDate.setOnClickListener((v -> {
            OptionPicker picker = WheelFactory.generateWheelWithNumbers(CalculateActivity.this, new WheelConfig(null, WheelConfig.LINE_CONFIG, lotteryIssues));
            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int index, String item) {
                    if (issue.equals(item) || singletonMap.get(lotteryIds.get(pos)).size() == 0) {//相同奖期 未选号码
                        wheelIndex = index;
                        tvDate.setText(item);
                        issue = item;
                        calcIssue.setCalcIssue(item);
                    } else {//不同奖期
                        CustomDialog.Builder builder = new CustomDialog.Builder(CalculateActivity.this);
                        builder.setMessage(String.format(getString(R.string.calc_lottery_tip), issue));
                        builder.setPositiveButton("确定", (dialog1, which) -> {
                            clearNum(0);//清空所选号码
                            wheelIndex = index;
                            tvDate.setText(item);
                            issue = item;
                            calcIssue.setCalcIssue(item);
                            dialog1.dismiss();
                        });
                        builder.setNegativeButton("取消", (dialog1, which) -> {
                            dialog1.dismiss();
                        });
                        CustomDialog dialog = builder.create();
                        builder.getPositiveButton().setTextColor(ColorUtils.MID_BLUE);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                }
            });
            picker.setSelectedIndex(wheelIndex);
            picker.show();
        }));
    }

    private void initData() {//请求彩种接口
        if (!CommonUtil.isNetworkConnected(CalculateActivity.this)) {//断网机制
            tvTitle.setText("计奖器");
            AVLoading.setVisibility(View.GONE);
            pop_iv.setVisibility(View.GONE);
            return;
        }
        boolean isLogin = CommonUtil.isLogin();
        pop_iv.setVisibility(View.VISIBLE);
        NetHelper.LOTTERY_API.getCalcLottery(CommonUtil.getUserId(), isLogin ? "" : CommonUtil.getNoLoginLotteryId())
                .flatMap(calcLotteryModel -> {
                    AVLoading.setVisibility(View.GONE);
                    if (calcLotteryModel.flag == 1) {
                        if (calcLotteryModel.redeemLottery != null && calcLotteryModel.redeemLottery.size() > 0) {
                            lotteryIds.clear();
                            List<CalcLotteryModel.Detail> list = calcLotteryModel.redeemLottery;
                            for (int i = 0; i < calcLotteryModel.redeemLottery.size(); i++) {
                                titles.add(list.get(i).lotteryName);
                                int lotteryId = list.get(i).lotteryID;
                                lotteryIds.add(lotteryId);
                                trend_fragments.add(map.get(lotteryId));//添加彩种页面
                            }
                            tvTitle.setText(list.get(0).lotteryName.concat("-计奖器"));//默认标题
                            initChange(0);
                        }
                    }
                    return NetHelper.LOTTERY_API.getCalcLotteryIssue(lotteryIds.get(0));
                }).subscribe(new SafeOnlyNextSubscriber<CalcLotteryIssueModel>() {
            @Override
            public void onNext(CalcLotteryIssueModel args) {
                super.onNext(args);
                List<String> issues = args.hisDrawIssue;
                if (issues != null && issues.size() > 0) {
                    tvDate.setText(issues.get(0));//默认奖期
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

//        NetHelper.LOTTERY_API.getCalcLottery(CommonUtil.getUserId(), isLogin ? "" : CommonUtil.getNoLoginLotteryId())
//                .subscribe(new SafeOnlyNextSubscriber<CalcLotteryModel>() {
//                    @Override
//                    public void onNext(CalcLotteryModel args) {
//                        super.onNext(args);
//                        if (args.flag == 1) {
//                            if (args.redeemLottery != null && args.redeemLottery.size() > 0) {
//                                List<CalcLotteryModel.Detail> list = args.redeemLottery;
//                                for (int i = 0; i < args.redeemLottery.size(); i++) {
//                                    titles.add(list.get(i).lotteryName);
//                                    int lotteryId = list.get(i).lotteryID;
//                                    trend_fragments.add(map.get(lotteryId));//添加彩种页面
//                                }
//                                tvTitle.setText(list.get(0).lotteryName.concat("-计奖器"));//默认标题
//                                initChange(0);
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                    }
//                });

    }

    private void initChange(int position) {
        //切换显示转圈
//        AVLoading.setVisibility(View.VISIBLE);
        pos = position;
        try {
            if (!CommonUtil.isNetworkConnected(this)) {//断网机制
                //netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                //netOff.setOnClickListener((v -> initChange(position)));//点击加载
            } else {
                //netOff.setVisibility(View.GONE);
            }
            fm.beginTransaction().replace(R.id.calculate_container, (Fragment) trend_fragments.get(position), "ClacFragment").commit();
            tvTitle.setText(titles.get(position).concat("-计奖器"));//默认标题
            wheelIndex=0;//重置
            if (popupWindow != null) {
                popupWindow.dismiss();
                pop_iv.setImageResource(R.mipmap.icon_xiaola);
            }
            initChangeIssue(position);//更换奖期
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //切换彩种初始化奖期
    private void initChangeIssue(int position) {
        NetHelper.LOTTERY_API.getCalcLotteryIssue(lotteryIds.get(position))
                .subscribe(new SafeOnlyNextSubscriber<CalcLotteryIssueModel>() {
                    @Override
                    public void onNext(CalcLotteryIssueModel args) {
                        super.onNext(args);
//                        AVLoading.setVisibility(View.GONE);
                        List<String> issues = args.hisDrawIssue;
                        if (issues != null && issues.size() > 0) {
                            lotteryIssues.clear();
                            lotteryIssues.addAll(issues);
                            tvDate.setText(issues.get(0));//默认奖期
                            calcIssue.setCalcIssue(issues.get(0));
                            issue = issues.get(0);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);
                    }
                });
    }

    private void initView() {
        normalBar = getToolBar();
        fm = getSupportFragmentManager();
        trend_fragments = new ArrayList<>();
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.trend_title_layout);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        calcBar = (Toolbar) findViewById(R.id.toolbar_calc);//计奖器选号标题
        tvDate = (TextView) findViewById(R.id.calculate_tvDate);
        tvTitle = (TextView) layout.findViewById(R.id.toolbar_title);
        calcBar.setNavigationIcon(R.mipmap.back_chevron);
        calcBar.setNavigationOnClickListener((v -> finish()));
        pop_iv = (ImageView) findViewById(R.id.trend_pop_iv);
        layout.setOnClickListener((v1 -> {
            if (!CommonUtil.isNetworkConnected(CalculateActivity.this)) return;
            if (popupWindow == null) {
                showSubTitles(titles.size());
                pop_iv.setImageResource(R.mipmap.icon_xiala_top);
            } else {
                if (popupWindow.isShowing()) {
                    pop_iv.setImageResource(R.mipmap.icon_xiaola);
                    popupWindow.dismiss();
                } else {
                    pop_iv.setImageResource(R.mipmap.icon_xiala_top);
                    showSubTitles(titles.size());
                }
            }
        }));
    }

    //弹窗处理
    public void showSubTitles(int size) {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(CalculateActivity.this)
                    .inflate(R.layout.numlibrary_pop, null);
            FlowLayout ll = (FlowLayout) contentView.findViewById(R.id.num_library_pop_ll);
            View view = contentView.findViewById(R.id.half_tanslate_view);
            ll.removeAllViews();
            RadioButton[] buttons = new RadioButton[size];
            for (int i = 0; i < size; i++) {
                RadioButton button = (RadioButton) LayoutInflater.from(this).inflate(R.layout.numlibrary_radiobutton, ll, false);
                button.setText(titles.get(i));
                buttons[i] = button;
                int width = getResources().getDisplayMetrics().widthPixels;
                if (size < 4) {//前3个数据动态设置宽度
                    FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams((width / size), DisplayUtil.dip2px(55f));
                    button.setLayoutParams(lp);
                } else {//大于等于个宽度固定
                    FlowLayout.LayoutParams lp = new FlowLayout.LayoutParams((width / 3), DisplayUtil.dip2px(55f));
                    button.setLayoutParams(lp);
                }
                ll.addView(button);
            }
            //默认选择的button 根据标题拿titles里的位置
            String s = tvTitle.getText().toString();
            String t = s.substring(0, s.indexOf("-"));
            int index = titles.indexOf(t);
            buttons[index].setChecked(true);
//            CompoundButton.OnClickListener listener = v -> {
//                for (int i = 0; i < buttons.length; i++) {
//                    int id = lotteryIds.get(i);
//                    //从已选彩种切换到
//                    if (id != 10002 && SingletonMapNomal.getMapSize() > 0 || id != 10088 && SingletonMapLotto.getMapSize() > 0
//                            || id != 10001 && SingletonMapLotto.getMapSize() > 0 || id != 10003 && SingletonMapLotto.getMapSize() > 0
//                            || id != 10004 && SingletonMapLotto.getMapSize() > 0) {
//                        if (dialog1 == null) {
//                            CustomDialog.Builder builder = new CustomDialog.Builder(CalculateActivity.this);
//                            builder.setMessage(String.format(getString(R.string.calc_issue_tip), t));
//                            builder.setPositiveButton("确定", (dialog1, which) -> {
//                                clearNum(id);//清空所选号码
//                            });
//                            builder.setNegativeButton("取消", (dialog1, which) -> {
//                                dialog1.dismiss();
//                            });
//                            dialog1 = builder.create();
//                            dialog1.setCancelable(false);
//                        }
//                        dialog1.show();
//                    }
//                    RadioButton button = buttons[i];
//                    if (v == button) {
//
//                        //切换显示转圈
//                        initChange(i);
//                        button.setChecked(true);
//                        continue;
//                    }
//                    button.setChecked(false);
//                }
//            };
//            CompoundButton.OnCheckedChangeListener listener = (compoundButton, checked) -> {
//                if (checked) {
//                    for (int i = 0; i < buttons.length; i++) {
//                        RadioButton button = buttons[i];
//                        if (compoundButton == button) {
//
//                            //切换显示转圈
//                            initChange(i);
//                            //showSubTitles(icon,i);
//                            continue;
//                        }
//                        button.setChecked(false);
//                    }
//                }
//            };
            view.setOnClickListener((v -> {
                pop_iv.setImageResource(R.mipmap.icon_xiaola);
                popupWindow.dismiss();
            }));
            for (int i = 0; i < buttons.length; i++) {
                RadioButton button = buttons[i];
                int finalI = i;
                button.setOnClickListener(v -> {
                    button.setChecked(false);//初始false
                    int id = lotteryIds.get(finalI);
                    //从已选彩种切换到其他
                    if (id != 10002 && SingletonMapNomal.getMapSize() > 0 || id != 10088 && SingletonMapLotto.getMapSize() > 0
                            || id != 10001 && SingletonMap3D.getMapSize() > 0 || id != 10003 && SingletonMapP3.getMapSize() > 0
                            || id != 10004 && SingletonMapP5.getMapSize() > 0) {

                        CustomDialog.Builder builder = new CustomDialog.Builder(CalculateActivity.this);
                        builder.setMessage(String.format(getString(R.string.calc_issue_tip), titles.get(pos)));
                        builder.setPositiveButton("确定", (dialog1, which) -> {
                            clearNum(lotteryIds.get(pos));//清空所选号码
                            initChange(finalI);
                            for (int j = 0; j < buttons.length; j++) {
                                buttons[j].setChecked(false);
                            }
                            button.setChecked(true);
                            dialog1.dismiss();
                        });
                        builder.setNegativeButton("取消", (dialog1, which) -> {
                            dialog1.dismiss();
                        });
                        CustomDialog dialog1 = builder.create();
                        builder.getPositiveButton().setTextColor(ColorUtils.MID_BLUE);
                        dialog1.setCancelable(false);
                        dialog1.show();
                    } else {
                        initChange(finalI);
                        for (int j = 0; j < buttons.length; j++) {
                            buttons[j].setChecked(false);
                        }
                        button.setChecked(true);
                    }
                });
            }
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            //popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setContentView(contentView);
            popupWindow.showAsDropDown(calcBar, 0, 0);
        } else {
            popupWindow.showAsDropDown(calcBar, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁清空数据 初始化
        clearNum(0);
    }

    private void clearNum(int lotteryId) {
        if (lotteryId == 0) {//全删
            int lotterys[] = {10002, 10088, 10001, 10003, 10004};
            for (int i = 0; i < lotterys.length; i++) {
                clearNum(lotterys[i]);
            }
        } else if (lotteryId == 10002) {
            SingletonMapNomal.getMap().clear();
            SingletonMapNomal.sign_sort = 1;
        } else if (lotteryId == 10088) {
            SingletonMapLotto.getMap().clear();
            SingletonMapLotto.sign_sort = 1;
        } else if (lotteryId == 10001) {
            SingletonMap3D.getMap().clear();
            SingletonMap3D.sign_sort = 1;
        } else if (lotteryId == 10003) {
            SingletonMapP3.getMap().clear();
            SingletonMapP3.sign_sort = 1;
        } else if (lotteryId == 10004) {
            SingletonMapP5.getMap().clear();
            SingletonMapP5.sign_sort = 1;
        }
    }

    /**
     * 切换ToolBar
     */
    public void switchToolBar(boolean isCondition) {
        normalBar.setVisibility(isCondition ? View.VISIBLE : View.GONE);
        calcBar.setVisibility(isCondition ? View.GONE : View.VISIBLE);
        setSupportActionBar(isCondition ? normalBar : calcBar);
        calcBar.setNavigationOnClickListener(view1 -> onBackPressed());
        normalBar.setNavigationOnClickListener(view2 -> onBackPressed());
    }

    public void ClosePop() {
        if (popupWindow == null) return;
        popupWindow.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (trend_fragments == null || trend_fragments.size() == 0) {
            finish();
            return;
        }
        Fragment fragment = trend_fragments.get(pos);
        if (fragment instanceof DoubleBallCalcFragment) {
            ((DoubleBallCalcFragment) fragment).onBackPressed();//返回键监听
        } else if (fragment instanceof LottoCalcFragment) {
            ((LottoCalcFragment) fragment).onBackPressed();//返回键监听
        } else if (fragment instanceof D3CalcFragment) {
            ((D3CalcFragment) fragment).onBackPressed();//返回键监听
        } else if (fragment instanceof P3CalcFragment) {
            ((P3CalcFragment) fragment).onBackPressed();//返回键监听
        } else if (fragment instanceof P5CalcFragment) {
            ((P5CalcFragment) fragment).onBackPressed();//返回键监听
        }
    }

    public void finishAct() {
        finish();
    }
}
