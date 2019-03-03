package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.news.NewsActivity;
import com.cp2y.cube.activity.trends.TrendActivity;
import com.cp2y.cube.adapter.MyOpenLotteryAdapter;
import com.cp2y.cube.callback.ACacheInterface;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ACacheHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryDrawModel;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpenLotteryActivity extends BaseActivity implements ClosePop {
    private List<LotteryDrawModel.DrawLottery> list;
    private ListView lv;
    private RadioGroup openlottery_rg;
    private RadioButton rb_openLottery;
    private AVLoadingIndicatorView AVLoading;
    private SwipeRefreshLayout swipLayout;
    private ImageView center_add, netOff;
    private MyOpenLotteryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lottery);
        setMainTitle("开奖公告");
        setClosePop(this);
        list = new ArrayList<>();
        center_add = (ImageView) findViewById(R.id.app_openLottery_add);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        swipLayout = (SwipeRefreshLayout) findViewById(R.id.swipRefreshLayout);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        openlottery_rg = (RadioGroup) findViewById(R.id.app_openlottery_rg);
        lv = (ListView) findViewById(R.id.app_openlottery_lv);
        View footView = LayoutInflater.from(OpenLotteryActivity.this).inflate(R.layout.openlottery_custom_more, null);
        lv.addFooterView(footView);
        adapter = new MyOpenLotteryAdapter(OpenLotteryActivity.this);
        lv.setAdapter(adapter);
        rb_openLottery = (RadioButton) findViewById(R.id.app_openttery_rb_openLottery);
        swipLayout.setColorSchemeColors(ColorUtils.MID_BLUE);
        //添加数据
        initNetOff();
        //点击监听
        initListener();
    }

    private void initNetOff() {
        if (!CommonUtil.isNetworkConnected(this)) {//断网机制
            if (!TextUtils.isEmpty(ACacheHelper.getAsString("lotteryDrawList"))) {
                showCacheData();//断网如果有缓存拿缓存数据
            } else {//没有缓存展示断网
                netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                netOff.setOnClickListener((v -> initNetOff()));//点击加载
                AVLoading.setVisibility(View.GONE);
            }
        } else {
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);

        }
        initData();
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (lv.getFooterViewsCount() == 1 && position == (adapter.getCount())) {//点击定制更多
                    startActivity(new Intent(OpenLotteryActivity.this, CustomProvinceActivity.class));
                } else {
                    Intent intent = new Intent();
                    if (list.get(position).isDetail()) {//有开奖详情
                        intent.setClass(OpenLotteryActivity.this, OpenLotterySummaryActivity.class);
                        intent.putExtra("issueOrder", list.get(position).getIssue());
                    } else {//无开奖详情
                        intent.setClass(OpenLotteryActivity.this, FastLotteryHistoryActivity.class);
                        intent.putExtra("isQuick", list.get(position).isQuick());
                    }
                    intent.putExtra("lottery_id", list.get(position).getLotteryId());
                    intent.putExtra("lottery_Name", list.get(position).getLotteryName());
                    startActivity(intent);
                }
            }
        });
        openlottery_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent intent = new Intent();
                switch (checkedId) {
                    case R.id.app_openttery_rb_main:
                        intent.setClass(OpenLotteryActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.app_openttery_rb_trend:
                        intent.setClass(OpenLotteryActivity.this, TrendActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.app_openttery_rb_news:
                        intent.setClass(OpenLotteryActivity.this, NewsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                        break;

                }
            }
        });
        swipLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData();
            }
        });
        center_add.setOnClickListener((v -> showMoreWindow(v)));
    }

    private void initData() {
        boolean isLogin = LoginSPUtils.isLogin();
        String lottery = "";
        if (!isLogin) {
            String customLottery = CommonSPUtils.getString("customLottery");
            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(customLottery)) {
                String[] custom = customLottery.split(",");
                for (int i = 0; i < custom.length; i++) {
                    String str = custom[i];
                    int key = Integer.valueOf(str.substring(0, str.indexOf("_")));
                    if (i == 0) {
                        stringBuilder.append(String.valueOf(key));
                    } else {
                        stringBuilder.append(",").append(String.valueOf(key));
                    }
                }
            }
            lottery = stringBuilder.toString();
        }
        NetHelper.LOTTERY_API.lotteryDrawList(lottery, LoginSPUtils.getInt("id", 0)).subscribe(new SafeOnlyNextSubscriber<LotteryDrawModel>() {
            @Override
            public void onNext(LotteryDrawModel args) {
                super.onNext(args);
                list.clear();
                list = args.getDrawList();
                if (swipLayout.isRefreshing()) {
                    swipLayout.setRefreshing(false);
                }
                adapter.loadData(list);
                AVLoading.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                swipLayout.setRefreshing(false);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("请检查网络设置");
            }
        });
        AcacheJson(isLogin, lottery);//缓存
    }

    //缓存json数据
    private void AcacheJson(boolean isLogin, String lottery) {
        ApiHelper.HttpParam param = new ApiHelper.HttpParam();
        if (isLogin == false) {
            param.put("lotteryIds", lottery);
        } else {
            param.put("userID", LoginSPUtils.getInt("id", 0));
        }
        ACacheInterface.lotteryDrawList service = ACacheHelper.RETROFIT.create(ACacheInterface.lotteryDrawList.class);
        service.lotteryDrawList(param).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Log.e("yangfan", "onError: " + e);
                swipLayout.setRefreshing(false);
                TipsToast.showTips("请检查网络设置");
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String json = responseBody.string();
                    ACacheHelper.put("lotteryDrawList", json);//存json
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 展示缓存数据
     */
    private void showCacheData() {
        String jsonStr = ACacheHelper.getAsString("lotteryDrawList");
        LotteryDrawModel model = new Gson().fromJson(jsonStr, LotteryDrawModel.class);
        list = model.getDrawList();
        if (swipLayout.isRefreshing()) {
            swipLayout.setRefreshing(false);
        }
        AVLoading.setVisibility(View.GONE);
        adapter.loadData(list);
        lv.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rb_openLottery.setChecked(true);
        closeMoreWindow();//关闭过滤选号
        setDrawerClose();//关闭侧拉
        initData();//刷新数据
    }

    @Override
    protected void onPause() {
        super.onPause();
        //设置无动画
        overridePendingTransition(0, 0);
    }

    @Override
    public void closePop() {

    }
}
