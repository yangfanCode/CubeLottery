package com.cp2y.cube.activity.trends;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.news.NewsActivity;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.activity.pushsingle.PushSingleActivity;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.TrendFragment;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.TrendModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TrendActivity extends BaseActivity implements ClosePop{
    private RadioGroup app_trend_rg;
    private RadioButton rb_trend;
    private ImageView center_add;
    private boolean loginState=false;
    private RelativeLayout noDevelop_layout;
    private ViewPager vp;
    private TabLayout tabLayout;
    private AVLoadingIndicatorView AVLoading;
    private RelativeLayout trend_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trend);
        setMainTitle("走势图表");
        setClosePop(this);
        initView();
        initData();
        //点击事件
        initListener();
    }

    private void initData() {
        List<Fragment>list=new ArrayList<>();
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        noDevelop_layout.setVisibility(View.GONE);//先隐藏未开发布局
        if(!loginState){//未登录
            AVLoading.setVisibility(View.GONE);
            String custom = CommonSPUtils.getString("customLottery");
            if (!TextUtils.isEmpty(custom)) {
                StringBuilder stringBuilder = new StringBuilder();
                String[] customs = custom.split(",");
                for (int i = 0, size = customs.length; i < size; i++) {
                    int key = Integer.valueOf(customs[i].substring(0, customs[i].indexOf("_")));
                    stringBuilder.append(i == 0 ? String.valueOf(key) : String.valueOf("," + String.valueOf(key)));
                }
                String customLottery = stringBuilder.toString();
                NetHelper.LOTTERY_API.getTrendHome(0,customLottery, NetConst.VERSION_CONTROL_ID).subscribe(new SafeOnlyNextSubscriber<TrendModel>(){
                    @Override
                    public void onNext(TrendModel args) {
                        super.onNext(args);
                        showTrendData(args, list);
                    }
                });
            }
        }else{//已登录
            NetHelper.LOTTERY_API.getTrendHome(LoginSPUtils.getInt("id",0),"",NetConst.VERSION_CONTROL_ID).subscribe(new SafeOnlyNextSubscriber<TrendModel>(){
                @Override
                public void onNext(TrendModel args) {
                    super.onNext(args);
                    showTrendData(args, list);
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    AVLoading.setVisibility(View.GONE);
                    TipsToast.showTips("请检查网络设置");
                }
            });
        }
    }

    /**
     * 走势数据 登陆未登录
     * @param args
     * @param list fragment
     */
    private void showTrendData(TrendModel args, List<Fragment> list) {
        if(args.getAndroidtrendList()!=null){
            Map<Integer,TrendModel.Detail> data=args.getAndroidtrendList();
            AVLoading.setVisibility(View.GONE);
            CustomLotteryList.synchronizedTrendLoginData(data);//同步登陆数据
            if(args.isHaveDeveloped()){//有已开发
                String[]tabs=new String[data.size()];//标题数组
                int i=0;
                for(Iterator<Map.Entry<Integer,TrendModel.Detail>> it = data.entrySet().iterator(); it.hasNext(); i++){
                    Map.Entry<Integer,TrendModel.Detail>entry=it.next();
                    int key=entry.getKey();
                    TrendModel.Detail detail=entry.getValue();
                    tabs[i]=detail.getLotteryName();
                    TrendFragment fragment=new TrendFragment(key,detail.getLotteryName(),detail.isDeveloped(),detail.isBranch());
                    if(detail.isBranch())fragment.setSsList(detail.getSsList());
                    list.add(fragment);
                }
                ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(),list,tabs);
                vp.setAdapter(adapter);//适配器
                trend_layout.setVisibility(View.VISIBLE);
            }else{
                showNoDevelop();
            }
        }
    }

    private void showNoDevelop() {
        noDevelop_layout.removeAllViews();
        View view= LayoutInflater.from(TrendActivity.this).inflate(R.layout.nodevelop_layout,null);
        noDevelop_layout.addView(view);
        noDevelop_layout.setVisibility(View.VISIBLE);
        ((Button)view.findViewById(R.id.go_custom_btn)).setOnClickListener((v -> startActivity(new Intent(TrendActivity.this,CustomProvinceActivity.class))));
    }

    private void initView() {
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        trend_layout = (RelativeLayout) findViewById(R.id.trend_layout);
        loginState = LoginSPUtils.isLogin();
        noDevelop_layout = (RelativeLayout) findViewById(R.id.noDevelop_layout);
        rb_trend = (RadioButton) findViewById(R.id.app_trend_rb_trend);
        center_add = (ImageView) findViewById(R.id.app_trend_add);
        app_trend_rg = (RadioGroup) findViewById(R.id.app_trend_rg);
        vp = (ViewPager) findViewById(R.id.trend_vp);
        tabLayout = (TabLayout) findViewById(R.id.trend_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(vp);
        vp.setOffscreenPageLimit(0);
    }

    private void initListener() {
        app_trend_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent intent = new Intent();
                switch (checkedId){
                    case R.id.app_trend_rb_main:
                        intent.setClass(TrendActivity.this,MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_trend_rb_openLottery:
                        intent.setClass(TrendActivity.this,PushSingleActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_trend_rb_news:
                        intent.setClass(TrendActivity.this,NewsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                }

            }
        });
        //加号
        center_add.setOnClickListener((v1 -> showMoreWindow(v1)));

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //shouMyTrend();
        //adapter.notifyDataSetChanged();
        rb_trend.setChecked(true);
        boolean isEdited= GuideFlag.isEdited;
        if(isEdited){
            GuideFlag.isEdited=false;//定制标记恢复初始状态
            initData();//编辑过刷新数据
        }
        closeMoreWindow();//关闭过滤选号
        setDrawerClose();//关闭侧拉
    }
    @Override
    protected void onPause() {
        super.onPause();
        //设置无动画
        overridePendingTransition(0,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GuideFlag.isEdited=false;//定制标记恢复初始状态
    }

    @Override
    public void closePop() {

    }
    public void setTrendLayoutVisible(){
        trend_layout.setVisibility(View.VISIBLE);
    }
}
