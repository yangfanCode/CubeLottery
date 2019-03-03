package com.cp2y.cube.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.RulesAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.RulesModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;

/**
 * 未开发彩种玩法
 */
public class RulesActivity extends BaseActivity {
    private RulesAdapter adapter;
    private int lotteryId;
    private ListView lv;
    private TextView time,rate,rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        setMainTitle("玩法介绍");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        lotteryId=getIntent().getIntExtra("lotteryId",0);
        initView();
        initData();
    }

    private void initData() {
        NetHelper.LOTTERY_API.getRuleLottery(lotteryId).subscribe(new SafeOnlyNextSubscriber<RulesModel>() {
            @Override
            public void onNext(RulesModel args) {
                super.onNext(args);
                int flag=args.getFlag();
                time.setText(!args.getDrawTime().equals("false")?args.getDrawTime():"");
                rule.setText(!args.getRule().equals("false")?args.getRule():"");
                rate.setText(!args.getReMoneyRate().equals("false")?args.getReMoneyRate():"");
                if(flag==1){
                    if(args.getWinConditions()!=null&&args.getWinConditions().size()>0){
                        adapter.loadData(args.getWinConditions());
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.rules_lv);
        View head= LayoutInflater.from(RulesActivity.this).inflate(R.layout.rules_head,lv,false);
        lv.addHeaderView(head);
        time = (TextView) head.findViewById(R.id.rule_open_time);
        rule = (TextView) head.findViewById(R.id.rule_tv);
        rate = (TextView) head.findViewById(R.id.rule_rate);
        adapter=new RulesAdapter(RulesActivity.this);
        lv.setAdapter(adapter);
    }
}
