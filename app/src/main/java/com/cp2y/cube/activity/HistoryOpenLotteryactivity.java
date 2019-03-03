package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyHistoryOpenLotteryAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryHistoryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class HistoryOpenLotteryactivity extends BaseActivity {

    private ListView lv;
    private int lottery_id;
    private String issueOrder,lottery_Name;
    private TextView title;
    private List<LotteryHistoryModel.HistoryLottery> list;
    private LinearLayout ll;
    private int currPage=1, totalPage=1;
    private MaterialRefreshLayout refrensh;
    private ImageView netOff;
    private MyHistoryOpenLotteryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_ball_history);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v) -> finish());
        lottery_id = getIntent().getIntExtra("lottery_id", 0);
        lottery_Name=getIntent().getStringExtra("lottery_Name");
        initView();
        //填充数据
        initData();
        //点击事件
        initListener();
    }

    private void initListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(HistoryOpenLotteryactivity.this, OpenLotterySummaryActivity.class);
                issueOrder = list.get(position).getIssue();
                intent.putExtra("flag", 1);
                intent.putExtra("lottery_id", lottery_id);
                intent.putExtra("lottery_Name",lottery_Name);
                intent.putExtra("issueOrder", issueOrder);
                startActivity(intent);
            }
        });
        refrensh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                initData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (currPage < totalPage) {
                    super.onRefreshLoadMore(materialRefreshLayout);
                    RefrenshData();
                } else {
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                }
            }
        });
    }

    private void initData() {
        if(!CommonUtil.isNetworkConnected(this)){//断网
            TipsToast.showTips("请检查网络设置");
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> initData()));
            return;
        }
        NetHelper.LOTTERY_API.lotteryHistoryList(lottery_id, 1, 20,"")
                .doOnTerminate(() -> refrensh.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<LotteryHistoryModel>() {
                    @Override
                    public void onNext(LotteryHistoryModel args) {
                        super.onNext(args);
                        list = args.getList();
                        if (list != null && list.size() > 0) {
                            netOff.setVisibility(View.GONE);
                            lottery_id = args.getLotteryId();
                            totalPage=args.getPageSize();
                            adapter = new MyHistoryOpenLotteryAdapter(list, HistoryOpenLotteryactivity.this, args);
                            lv.setAdapter(adapter);
                        } else {
                            refrensh.finishRefresh();
                            TipsToast.showTips("请检查网络设置");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        refrensh.finishRefresh();
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }

    public void RefrenshData() {
        NetHelper.LOTTERY_API.lotteryHistoryList(lottery_id, ++currPage, 20,"")
                .doOnTerminate(() -> refrensh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<LotteryHistoryModel>() {
            @Override
            public void onNext(LotteryHistoryModel args) {
                super.onNext(args);
                List<LotteryHistoryModel.HistoryLottery> list_refrensh = args.getList();
                if (list_refrensh != null && list_refrensh.size() > 0) {
                    list.addAll(list_refrensh);
                    adapter.notifyDataSetChanged();
                } else {
                    TipsToast.showTips("请检查网络设置");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                refrensh.finishRefreshLoadMore();
                TipsToast.showTips("请检查网络设置");
            }
        });
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.openlottery_history_lv);
        list = new ArrayList<>();
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        title = (TextView) findViewById(R.id.toolbar_title);
        title.setText(lottery_Name + "-历史开奖");
        ll = (LinearLayout) findViewById(R.id.openlottery_history_tv_ll);
        refrensh = (MaterialRefreshLayout) findViewById(R.id.history_refrensh);
    }
}
