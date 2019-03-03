package com.cp2y.cube.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.activity.selectnums.SelectNumFilterActivity;
import com.cp2y.cube.activity.trends.TrendActivity;
import com.cp2y.cube.adapter.MyOtherTrendFuCaiAdapter;
import com.cp2y.cube.adapter.MyOtherTrendSportAdapter;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.MyGridView;

import java.util.Arrays;

public class FilterActivity extends BaseActivity implements ClosePop{

    private MyGridView myFilter_gv;
    private MyGridView welfareLottery_gv;
    private MyGridView sportLottery_gv;
    private String[] myFilter = {"双色球-胆拖", "双色球-普通"};
    //"大乐透-普通","大乐透-胆拖","3D-普通","3D-胆拖"
    private String[] filter_fucai = {"双色球", "福彩3D", "七乐彩", "华东15选5"};
    private String[] filter_ticai = {"大乐透", "浙江6+1", "排列5", "排列3", "浙江20选5", "七星彩"};
    private RadioButton rb_filter;
    private RadioGroup rg_filter;
    private String type[] = {"双色球", "福彩3D", "七乐彩", "15选5"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        setMainTitle("过滤缩水");
        setClosePop(this);
        welfareLottery_gv = (MyGridView) findViewById(R.id.app_filter_welfareLottery_gv);
        sportLottery_gv = (MyGridView) findViewById(R.id.app_filter_sportLottery_gv);
        rb_filter = (RadioButton) findViewById(R.id.app_filter_rb_filter);
        rg_filter = (RadioGroup) findViewById(R.id.app_filter_rg);
        initData();
        initListener();
    }

    private void initListener() {
        rg_filter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent intent = new Intent();
                switch (checkedId) {
                    case R.id.app_filter_rb_openLottery:
                        intent.setClass(FilterActivity.this, OpenLotteryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_filter_rb_trend:
                        intent.setClass(FilterActivity.this, TrendActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                }
            }
        });
        welfareLottery_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(FilterActivity.this, SelectNumFilterActivity.class);
                    startActivity(intent);
                }
            }
        });
        sportLottery_gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent = new Intent(FilterActivity.this, LottoSelectNumActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void initData() {
        MyOtherTrendFuCaiAdapter adapter_fucai = new MyOtherTrendFuCaiAdapter(Arrays.asList(filter_fucai), this, R.layout.item_othertrend_gv);
        welfareLottery_gv.setAdapter(adapter_fucai);
        //设置无点击效果
        welfareLottery_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
        //体彩
        MyOtherTrendSportAdapter adapter_ticai = new MyOtherTrendSportAdapter(Arrays.asList(filter_ticai), this, R.layout.item_othertrend_gv);
        sportLottery_gv.setAdapter(adapter_ticai);
        sportLottery_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    //返回键
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rb_filter.setChecked(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //设置无动画
        overridePendingTransition(0,0);
    }

    @Override
    public void closePop() {

    }
}
