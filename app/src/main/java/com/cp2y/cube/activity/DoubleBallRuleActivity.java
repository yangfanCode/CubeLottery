package com.cp2y.cube.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;

public class DoubleBallRuleActivity extends BaseActivity {
    private int Lottery_id=0;
    private TextView rule_open_time,rule_tv,rule_rate;
    private ImageView iv_rule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_ball_rule);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        setMainTitle("玩法介绍");
        Lottery_id=getIntent().getIntExtra("lotteryId",Lottery_id);
        initView();
        initData();
    }

    private void initView() {
        rule_open_time = (TextView) findViewById(R.id.rule_open_time);
        rule_tv = (TextView) findViewById(R.id.rule_tv);
        rule_rate = (TextView) findViewById(R.id.rule_rate);
        iv_rule = (ImageView) findViewById(R.id.iv_rule);
    }

    private void initData() {
        switch (Lottery_id){
            case 10002:
                break;
            case 10088:
                rule_open_time.setText("每周一、三、六20:00截止销售，20:30开奖");
                rule_tv.setText("5个前区+2个后区=1注（2元），追加投注：1元");
                rule_rate.setText("销售总额的50%");
                iv_rule.setImageResource(R.mipmap.lotto_rule);
                break;
            case 10001:
                rule_open_time.setText("每日21:20开奖");
                rule_tv.setText("000-999中的一个3位数=1注（2元）");
                rule_rate.setText("销售总额的53%");
                iv_rule.setImageResource(R.mipmap.d3_rule);
                break;
            case 10003:
                rule_open_time.setText("每日20:30开奖");
                rule_tv.setText("000-999中的一个3位数=1注（2元）");
                rule_rate.setText("销售总额的53%");
                iv_rule.setImageResource(R.mipmap.d3_rule);
                break;
        }
    }
}
