package com.cp2y.cube.activity.pushsingle;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.pushsingle.fragment.PushSingleFragment;
import com.cp2y.cube.activity.pushsingle.fragment.SelectD3Fragment;
import com.cp2y.cube.activity.pushsingle.fragment.SelectDoubleFragment;
import com.cp2y.cube.activity.pushsingle.fragment.SelectLottoFragment;
import com.cp2y.cube.activity.pushsingle.fragment.SelectP3Fragment;
import com.cp2y.cube.activity.pushsingle.fragment.SelectP5Fragment;
import com.cp2y.cube.custom.NoDoubleClickListener;
import com.cp2y.cube.custom.SingletonSelectSingleCheck;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.SelectSingleTitleModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.widgets.NotLoginForOptional;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 选单界面
 */
public class SelectSingleActivity extends BaseActivity {
    private boolean isPush = false;
    private SelectDoubleFragment doubleFragment = new SelectDoubleFragment();
    private SelectLottoFragment lottoFragment = new SelectLottoFragment();
    private SelectD3Fragment d3Fragment = new SelectD3Fragment();
    private SelectP3Fragment p3Fragment = new SelectP3Fragment();
    private SelectP5Fragment p5Fragment = new SelectP5Fragment();
    private FragmentManager manager = getSupportFragmentManager();
    private PushSingleFragment pushFragment = new PushSingleFragment();
    private Toolbar selectBar, pushBar;
    private PopupWindow popupWindow = null;
    private RelativeLayout title_layout;
    private ImageView icon;
    private TextView title;
    private List<SelectSingleTitleModel.Detail> title_data = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private int posFragment = 0;//第几个页面
    private TextView tv_goPush, tv_Push;
    private NotLoginForOptional layView;
    private ImageView netOff;
    private Map<Fragment, Set<Integer>> goPushData = new HashMap<Fragment, Set<Integer>>() {{
        put(doubleFragment, SingletonSelectSingleCheck.getDoubleObj());
        put(lottoFragment, SingletonSelectSingleCheck.getLottoObj());
        put(d3Fragment, SingletonSelectSingleCheck.getD3Obj());
        put(p3Fragment, SingletonSelectSingleCheck.getP3Obj());
        put(p5Fragment, SingletonSelectSingleCheck.getP5Obj());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_single);
        initView();
        initNetOff();
        initListener();
    }

    private void initNetOff() {
        if (!CommonUtil.isNetworkConnected(this)) {//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            tv_goPush.setVisibility(View.GONE);//隐藏确定按钮
            title.setText("选单");
            title_layout.setVisibility(View.VISIBLE);//显示标题
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
            ((ImageView) title_layout.findViewById(R.id.toolbar_icon)).setVisibility(View.GONE );
        } else {
            netOff.setVisibility(View.GONE);
        }
        initData();
    }

    private void initListener() {
        title_layout.setOnClickListener((v -> {
            if (fragments.size() == 1) return;
            int size = title_data.size();
            if (popupWindow == null) {
                showSubTitles(size);
                icon.setImageResource(R.mipmap.icon_xiala_top);
            } else {
                if (popupWindow.isShowing()) {
                    icon.setImageResource(R.mipmap.icon_xiaola);
                    popupWindow.dismiss();
                } else {
                    icon.setImageResource(R.mipmap.icon_xiala_top);
                    showSubTitles(size);
                }
            }
        }));
        tv_goPush.setOnClickListener((v -> {
            for (Iterator<Map.Entry<Fragment, Set<Integer>>> iterator = goPushData.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<Fragment, Set<Integer>> entry = iterator.next();
                Fragment fragment = entry.getKey();
                Set<Integer> set = entry.getValue();
                if (fragment.getClass().equals(fragments.get(posFragment).getClass())) {
                    if (set.size() > 0) {
                        gotoConditionPage();
                    } else {
                        TipsToast.showTips("请先选单");
                    }
                    break;
                }
            }
        }));
        tv_Push.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                int id = LoginSPUtils.getInt("id", 0);
                String issue = getIssue();
                String numlib = getSelectSingleData().split("#")[0];
                String show = getSelectSingleData().split("#")[1];
                int lotteryId = getLotteryId();
                String title = getInputTitle();
                NetHelper.LOTTERY_API.pushSingle(id, issue, lotteryId, numlib, show, title, 0, 0)
                        .subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
                            @Override
                            public void onNext(FlagModel args) {
                                super.onNext(args);
                                TipsToast.showTips(args.getFlag() == 1 ? "发布成功" : args.getMessage());
                                pushFragment.closeKeyBoard();//关闭软键盘
                                ContextHelper.getApplication().runDelay(SelectSingleActivity.this::finish, 2000);
                            }

                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                TipsToast.showTips("请检查网路设置");
                            }
                        });
            }
        });
    }

    private void initData() {
        NetHelper.LOTTERY_API.getSelectSingleTitle(LoginSPUtils.getInt("id", 0)).subscribe(new SafeOnlyNextSubscriber<SelectSingleTitleModel>() {
            @Override
            public void onNext(SelectSingleTitleModel args) {
                super.onNext(args);
                title_data = args.getPushSingleLottery();
                if (title_data != null && title_data.size() > 0) {
                    layView.setVisibility(View.GONE);
                    tv_goPush.setVisibility(View.VISIBLE);//确定按钮
                    for (int i = 0; i < title_data.size(); i++) {
                        if (title_data.get(i).getLotteryID() == 10002) {
                            fragments.add(doubleFragment);
                        } else if (title_data.get(i).getLotteryID() == 10088) {
                            fragments.add(lottoFragment);
                        } else if (title_data.get(i).getLotteryID() == 10001) {
                            fragments.add(d3Fragment);
                        } else if (title_data.get(i).getLotteryID() == 10003) {
                            fragments.add(p3Fragment);
                        } else if (title_data.get(i).getLotteryID() == 10004) {
                            fragments.add(p5Fragment);
                        }
                    }
                    initFargments();//展示数据
                } else {//无数据
                    tv_goPush.setVisibility(View.GONE);//隐藏确定按钮
                    layView.setVisibility(View.VISIBLE);
                    layView.setTypeEnum(NotLoginForOptional.TypeEnum.NO_SELECTPUSH);
                    title.setText("选单");
                    title_layout.setVisibility(View.VISIBLE);//显示标题
                    ((ImageView) title_layout.findViewById(R.id.toolbar_icon)).setVisibility(View.GONE );
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //第一次初始化
    private void initFargments() {
        FragmentTransaction transaction = manager.beginTransaction();
        for (int i = 0; i < fragments.size(); i++) {//切换show hide fragment
            transaction.add(R.id.select_single_container, fragments.get(i));
            if (i != 0) {
                transaction.hide(fragments.get(i));
            }
        }
        transaction.add(R.id.select_single_container, pushFragment).hide(pushFragment);
        transaction.commit();
        title.setText(title_data.get(0).getLotteryName() + "-选单");
        title_layout.setVisibility(View.VISIBLE);//显示标题
        ((ImageView) title_layout.findViewById(R.id.toolbar_icon)).setVisibility(fragments.size() == 1 ? View.GONE : View.VISIBLE);
    }

    //切换号码库
    private void initChange(int position) {
        try {
            manager.beginTransaction().show(fragments.get(position)).hide(fragments.get(posFragment)).commit();
            //manager.beginTransaction().replace(R.id.select_single_container,fragments.get(position),"LibraryTrend").commit();
            title.setText(title_data.get(Integer.valueOf(position)).getLotteryName() + "-选单");
            if (popupWindow != null) {
                popupWindow.dismiss();
                icon.setImageResource(R.mipmap.icon_xiaola);
            }
            posFragment = position;//赋值
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSubTitles(int size) {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(SelectSingleActivity.this)
                    .inflate(R.layout.numlibrary_pop, null);
            FlowLayout ll = (FlowLayout) contentView.findViewById(R.id.num_library_pop_ll);
            ll.removeAllViews();
            RadioButton[] buttons = new RadioButton[size];
            for (int i = 0; i < size; i++) {
                RadioButton button = (RadioButton) LayoutInflater.from(this).inflate(R.layout.numlibrary_radiobutton, ll, false);
                button.setText(title_data.get(i).getLotteryName());
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
            //默认选择的button
            final int[] index = {0};
            buttons[index[0]].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton, checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            //切换显示转圈
                            initChange(i);
                            index[0] = i;
                            //showSubTitles(icon,i);
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
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
        } else {
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
        }
    }

    private void initView() {
        netOff = (ImageView) findViewById(R.id.netOff);
        layView = (NotLoginForOptional) findViewById(R.id.lay_login_view);
        title_layout = (RelativeLayout) findViewById(R.id.toolbar_numlibrary_layout);
        tv_goPush = (TextView) findViewById(R.id.selectsingle_goPush);
        tv_Push = (TextView) findViewById(R.id.push_singlt_tv);
        icon = (ImageView) findViewById(R.id.toolbar_icon);
        title = (TextView) findViewById(R.id.toolbar_title);
        selectBar = getToolBar();
        selectBar.setNavigationIcon(R.mipmap.back_chevron);
        selectBar.setNavigationOnClickListener(v -> onBackPressed());
        pushBar = (Toolbar) findViewById(R.id.toolbar_pushsingle);
        pushBar.setNavigationIcon(R.mipmap.back_chevron);
    }

    public void gotoConditionPage() {
        if (popupWindow != null) popupWindow.dismiss();
        manager.beginTransaction().show(pushFragment).hide(fragments.get(posFragment)).commit();
        isPush = true;
        pushFragment.openKeyBoard();//打开软键盘
        pushFragment.setLotteryName();//设置彩种名称
        switchToolBar();
    }

    /**
     * 切换ToolBar
     */
    private void switchToolBar() {
        pushBar.setVisibility(isPush ? View.VISIBLE : View.GONE);
        selectBar.setVisibility(isPush ? View.GONE : View.VISIBLE);
        setSupportActionBar(isPush ? pushBar : selectBar);
        selectBar.setNavigationOnClickListener(view2 -> onBackPressed());
        pushBar.setNavigationOnClickListener(view1 -> onBackPressed());
        pushBar.setTitle("推单");
    }

    @Override
    public void onBackPressed() {
        if (isPush) {
            pushFragment.closeKeyBoard();
            manager.beginTransaction().hide(pushFragment).show(fragments.get(posFragment)).commit();
            isPush = false;
            switchToolBar();
        } else {
            finish();
        }
    }

    //更改推单
    public void editData() {
        onBackPressed();//返回
    }

    //返回彩种名称
    public String getLotteryName() {
        return title_data.get(posFragment).getLotteryName();
    }

    //获取选单数据
    public String getSelectSingleData() {
        if (fragments.get(posFragment) instanceof SelectDoubleFragment) {
            return ((SelectDoubleFragment) fragments.get(posFragment)).getSelectSingleData();
        } else if (fragments.get(posFragment) instanceof SelectLottoFragment) {
            return ((SelectLottoFragment) fragments.get(posFragment)).getSelectSingleData();
        } else if (fragments.get(posFragment) instanceof SelectD3Fragment) {
            return ((SelectD3Fragment) fragments.get(posFragment)).getSelectSingleData();
        } else if (fragments.get(posFragment) instanceof SelectP3Fragment) {
            return ((SelectP3Fragment) fragments.get(posFragment)).getSelectSingleData();
        } else if (fragments.get(posFragment) instanceof SelectP5Fragment) {
            return ((SelectP5Fragment) fragments.get(posFragment)).getSelectSingleData();
        } else {
            return "";
        }
    }


    //获得奖期
    public String getIssue() {
        if (fragments.get(posFragment) instanceof SelectDoubleFragment) {
            return ((SelectDoubleFragment) fragments.get(posFragment)).getIssue();
        } else if (fragments.get(posFragment) instanceof SelectLottoFragment) {
            return ((SelectLottoFragment) fragments.get(posFragment)).getIssue();
        } else if (fragments.get(posFragment) instanceof SelectD3Fragment) {
            return ((SelectD3Fragment) fragments.get(posFragment)).getIssue();
        } else if (fragments.get(posFragment) instanceof SelectP3Fragment) {
            return ((SelectP3Fragment) fragments.get(posFragment)).getIssue();
        } else if (fragments.get(posFragment) instanceof SelectP5Fragment) {
            return ((SelectP5Fragment) fragments.get(posFragment)).getIssue();
        } else {
            return "";
        }
    }

    //获得彩种id
    public int getLotteryId() {
        if (fragments.get(posFragment) instanceof SelectDoubleFragment) {
            return ((SelectDoubleFragment) fragments.get(posFragment)).getLotteryId();
        } else if (fragments.get(posFragment) instanceof SelectLottoFragment) {
            return ((SelectLottoFragment) fragments.get(posFragment)).getLotteryId();
        } else if (fragments.get(posFragment) instanceof SelectD3Fragment) {
            return ((SelectD3Fragment) fragments.get(posFragment)).getLotteryId();
        } else if (fragments.get(posFragment) instanceof SelectP3Fragment) {
            return ((SelectP3Fragment) fragments.get(posFragment)).getLotteryId();
        } else if (fragments.get(posFragment) instanceof SelectP5Fragment) {
            return ((SelectP5Fragment) fragments.get(posFragment)).getLotteryId();
        } else {
            return 0;
        }
    }

    //获得输入内容
    public String getInputTitle() {
        String title = pushFragment.getTitle();
        if (!TextUtils.isEmpty(title)) {
            return title;
        } else {
            return "";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonSelectSingleCheck.clearAll();//清除数据
    }
}
