package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.h5trends.DF61TrendPicActivity;
import com.cp2y.cube.activity.h5trends.HD15xuan5TrendPicActivity;
import com.cp2y.cube.activity.h5trends.QiLeCaiTrendPicActivity;
import com.cp2y.cube.activity.h5trends.QiXingCaiTrendPicActivity;
import com.cp2y.cube.activity.ignore.doubleball.IgnoreActivity;
import com.cp2y.cube.activity.ignore.fucai3d.PartIgnore3DActivity;
import com.cp2y.cube.activity.ignore.lotto.IgnoreLottoActivity;
import com.cp2y.cube.activity.ignore.pailie3.PartIgnoreP3Activity;
import com.cp2y.cube.activity.ignore.pailie5.PartIgnoreP5Activity;
import com.cp2y.cube.activity.news.NewsActivity;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.activity.recognize.CameraActivity;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.activity.selectnums.Select3DNumActivity;
import com.cp2y.cube.activity.selectnums.SelectNumFilterActivity;
import com.cp2y.cube.activity.selectnums.SelectP3NumActivity;
import com.cp2y.cube.activity.selectnums.SelectP5NumActivity;
import com.cp2y.cube.activity.trends.D3TrendPicActivity;
import com.cp2y.cube.activity.trends.LottoTrendPicActivity;
import com.cp2y.cube.activity.trends.P3TrendPicActivity;
import com.cp2y.cube.activity.trends.P5TrendPicActivity;
import com.cp2y.cube.activity.trends.TrendPicActivity;
import com.cp2y.cube.adapter.MyOpenLotterySummaryAdapter;
import com.cp2y.cube.adapter.MyOpenLotterySummaryGvAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotterySummaryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenLotterySummaryActivity extends BaseActivity {
    private int pos=0;
    private TextView tv_lotteryName, tv_lotteryYear, tv_lotteryDate;
    private LinearLayout ll_num;
    private List<LotterySummaryModel.SummaryLottery.Summaryitems> list;
    private List<LotterySummaryModel.SummaryLottery> list_ball;
    private MyListView lv;
    private MyGridView gv;
    private int lottery_id;
    private String issueOrder,lottery_Name;
    private TextView title;
    private TextView tv_PullSum, tv_moneyTotal,tv_PullSum_title,tv_moneyTotal_title;
    private TextView btn_historyOpen;
    private AVLoadingIndicatorView AVLoading;
    private ScrollView scrollView;
    private int flag=0;
    private ImageView iv_back,iv_close,netOff;
    private Map<Integer,Integer> posMap=new HashMap<Integer,Integer>(){{//走势
        put(10002,0);
        put(10088,1);
        put(10001,2);
        put(10003,3);
        put(10004,4);
        put(10019,5);
        put(10083,6);
        put(10005,7);
        put(10100,8);
    }};
    private static Class[] TREND_ACTIVITY={TrendPicActivity.class,LottoTrendPicActivity.class,D3TrendPicActivity.class,P3TrendPicActivity.class,P5TrendPicActivity.class,HD15xuan5TrendPicActivity.class, QiLeCaiTrendPicActivity.class, QiXingCaiTrendPicActivity.class, DF61TrendPicActivity.class};
    private static Class[] SELECTNUM_ACTIVITY={SelectNumFilterActivity.class,LottoSelectNumActivity.class,Select3DNumActivity.class,SelectP3NumActivity.class,SelectP5NumActivity.class};
    private static Class[] IGNORE_ACTIVITY={IgnoreActivity.class,IgnoreLottoActivity.class, PartIgnore3DActivity.class, PartIgnoreP3Activity.class,PartIgnoreP5Activity.class};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_lottery_summary);
        lottery_id = getIntent().getIntExtra("lottery_id", 0);
        issueOrder = getIntent().getStringExtra("issueOrder");
        lottery_Name = getIntent().getStringExtra("lottery_Name");
        flag=getIntent().getIntExtra("flag",0);
        if(posMap.containsKey(lottery_id))pos=posMap.get(lottery_id);
        //初始化
        initView();
        initNetOff();//断网控制
        initListener();
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initData();
    }
    private void initListener() {
        btn_historyOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenLotterySummaryActivity.this, HistoryOpenLotteryactivity.class);
                intent.putExtra("lottery_id", lottery_id);
                intent.putExtra("lottery_Name",lottery_Name);
                startActivity(intent);
            }
        });
    }

    private void initData() {
        NetHelper.LOTTERY_API.lotterySummaryList(lottery_id, issueOrder).subscribe(new SafeOnlyNextSubscriber<LotterySummaryModel>() {
            @Override
            public void onNext(LotterySummaryModel args) {
                super.onNext(args);
                AVLoading.setVisibility(View.GONE);
                if(args.getDrawDetail()!=null) {
                    list_ball = args.getDrawDetail();
                    list = list_ball.get(0).getItems();
                    if (list_ball != null && list_ball.size() > 0) {
                        tv_lotteryYear.setText(list_ball.get(0).getIssue() + "期");
                        tv_lotteryDate.setText(list_ball.get(0).getDrawTime());
                        tv_lotteryName.setText(list_ball.get(0).getLotteryName());
                        tv_PullSum.setText(list_ball.get(0).getSaleTotal().equals("0") ? "--" : list_ball.get(0).getSaleTotal());
                        tv_moneyTotal.setText(list_ball.get(0).getRemainTotal().equals("0") ? "--" : list_ball.get(0).getRemainTotal());
                        scrollView.setVisibility(View.VISIBLE);
                        //动态创建红球
                        if (list_ball.get(0).getDrawRedNumber() != null) {
                            String[] lotteryRedName = list_ball.get(0).getDrawRedNumber().split(",");
                            for (int i = 0; i < lotteryRedName.length; i++) {
                                TextView tv_ball = (TextView) LayoutInflater.from(OpenLotterySummaryActivity.this).inflate(R.layout.open_summary_tvball, null).findViewById(R.id.openlettery_summary_tvball);
                                tv_ball.setTextSize(18);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, 0, 10, 0);//4个参数按顺序分别是左上右下
                                tv_ball.setLayoutParams(layoutParams);
                                tv_ball.setText(lotteryRedName[i]);
                                ll_num.addView(tv_ball);
                            }
                        }
                        //动态创建篮球
                        if (list_ball.get(0).getDrawBlueNumber() != null && !list_ball.get(0).getDrawBlueNumber().equals("false")) {
                            String[] lotteryBlueName = list_ball.get(0).getDrawBlueNumber().split(",");
                            for (int i = 0; i < lotteryBlueName.length; i++) {
                                TextView tv_ball = (TextView) LayoutInflater.from(OpenLotterySummaryActivity.this).inflate(R.layout.open_summary_tvball, null).findViewById(R.id.openlettery_summary_tvball);
                                tv_ball.setTextSize(18);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.setMargins(0, 0, 10, 0);//4个参数按顺序分别是左上右下
                                tv_ball.setLayoutParams(layoutParams);
                                tv_ball.setText(lotteryBlueName[i]);
                                tv_ball.setTextColor(ColorUtils.MID_BLUE);
                                ll_num.addView(tv_ball);
                            }
                        }
                        MyOpenLotterySummaryAdapter adapter = new MyOpenLotterySummaryAdapter(list, OpenLotterySummaryActivity.this);
                        lv.setAdapter(adapter);
                        initSupport(args.getEntrance());//功能区

                    } else {
                        TipsToast.showTips("暂无开奖数据");
                    }
                }else{
                    TipsToast.showTips("暂无开奖数据");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }
    //功能
    public void initSupport(List<String>list){
        MyOpenLotterySummaryGvAdapter adapter_gv = new MyOpenLotterySummaryGvAdapter(list, this, R.layout.item_openlottery_summary_gv);
        gv.setAdapter(adapter_gv);
        gv.setOnItemClickListener(((parent, view, position, id) -> {
            Intent intent = new Intent();
            switch (list.get(position)) {
                case "号码库"://号码库
                    boolean loginState = LoginSPUtils.isLogin();
                    if (loginState) {
                        intent.setClass(OpenLotterySummaryActivity.this, NumLibraryActivity.class);
                        intent.putExtra("flag", pos);
                        startActivity(intent);
                    }else{
                        intentLogin();//登录页面
                    }
                    break;
                case "走势"://走势
                    intent.setClass(OpenLotterySummaryActivity.this,TREND_ACTIVITY[pos]);
                    intent.putExtra("lotteryName",lottery_Name);
                    startActivity(intent);
                    break;
                case "遗漏"://遗漏
                    intent.setClass(OpenLotterySummaryActivity.this,IGNORE_ACTIVITY[pos]);
                    intent.putExtra("pos",0);
                    startActivity(intent);
                    break;
                case "过滤"://过滤
                    intent.setClass(OpenLotterySummaryActivity.this,SELECTNUM_ACTIVITY[pos]);
                    startActivity(intent);
                    break;
                case "扫一扫"://扫一扫 3d的资讯
                    intent.setClass(OpenLotterySummaryActivity.this,CameraActivity.class);
                    startActivity(intent);
                    break;
                case "预测"://资讯 3d的玩法
                    intent.setClass(OpenLotterySummaryActivity.this, NewsActivity.class);
                    startActivity(intent);
                    break;
                case "玩法"://写死玩法
                    if(lottery_id==10002||lottery_id==10088||lottery_id==10001||lottery_id==10003){
                        intent.setClass(OpenLotterySummaryActivity.this, DoubleBallRuleActivity.class);
                        intent.putExtra("lotteryId",lottery_id);
                    }else{//动态玩法
                        intent.setClass(OpenLotterySummaryActivity.this, RulesActivity.class);
                        intent.putExtra("lotteryId",lottery_id);
                    }
                    startActivity(intent);
                    break;
            }
        }));
    }

    private void initView() {
        tv_lotteryName = (TextView) findViewById(R.id.openlottery_summary_tv_lotteryName);
        tv_lotteryYear = (TextView) findViewById(R.id.openlottery_summary_tv_year);
        tv_lotteryDate = (TextView) findViewById(R.id.openlottery_summary_tv_date);
        ll_num = (LinearLayout) findViewById(R.id.openlettery_summary_num_ll);
        lv = (MyListView) findViewById(R.id.openlottery_summary_money_lv);
        gv = (MyGridView) findViewById(R.id.openlottery_summary_money_gv);
        title = (TextView) findViewById(R.id.toolbar_title_back_history);
        tv_PullSum = (TextView) findViewById(R.id.openlettery_summary_pullSum);
        tv_PullSum_title = (TextView) findViewById(R.id.openlettery_summary_tv_pullSum);
        tv_moneyTotal_title = (TextView) findViewById(R.id.openlettery_summary_tv_moneyTotal);
        tv_moneyTotal = (TextView) findViewById(R.id.openlottery_summary_moneyTotal);
        btn_historyOpen = (TextView) findViewById(R.id.toolbar_title_back_historyOpen);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        scrollView = (ScrollView) findViewById(R.id.ScrollLayout);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        iv_back = (ImageView) findViewById(R.id.history_back);
        iv_close = (ImageView) findViewById(R.id.history_close);
        title.setText(lottery_Name + "-开奖详情");
        list = new ArrayList<>();
        list_ball = new ArrayList<>();
        btn_historyOpen.setVisibility(flag==1?View.GONE:View.VISIBLE);
        iv_close.setVisibility(flag==1?View.VISIBLE:View.GONE);
        iv_close.setOnClickListener((v)->{startActivity(new Intent(this,OpenLotteryActivity.class));});
        iv_back.setOnClickListener((v)->finish());
    }


}
