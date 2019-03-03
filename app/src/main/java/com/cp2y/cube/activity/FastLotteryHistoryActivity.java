package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.fragment.FastLotteryHistoryFragment;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 无开奖详情的历史开奖页面
 */

public class FastLotteryHistoryActivity extends BaseActivity {
    private List<Fragment> list;//fragment数据
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private String[]tabs=new String[7];//标题数组
    private ViewPager vp;
    private int lottery_id;
    private String lottery_Name;
    private boolean isQuick=false;//是否快开彩
    private TextView rules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fast_lottery_history);
        lottery_id = getIntent().getIntExtra("lottery_id", 0);
        lottery_Name = getIntent().getStringExtra("lottery_Name");
        isQuick=getIntent().getBooleanExtra("isQuick",false);
        setMainTitle(lottery_Name+"-历史开奖");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();//初始化
        initData();//加载数据
        initListener();//监听
    }

    private void initListener() {
        rules.setOnClickListener((v -> {
            Intent intent=new Intent(FastLotteryHistoryActivity.this,RulesActivity.class);
            intent.putExtra("lotteryId",lottery_id);
            startActivity(intent);
        }));
    }

    //初始化数据
    private void initData() {
        if(isQuick){//快开彩
            tabs[0]="今天";
            tabs[1]="昨天";
            tabs[2]="前天";
            tabs[3]=cutDateStr(3);
            tabs[4]=cutDateStr(4);
            tabs[5]=cutDateStr(5);
            tabs[6]=cutDateStr(6);
            for(int i=0;i<7;i++){
                FastLotteryHistoryFragment fragment=new FastLotteryHistoryFragment(CommonUtil.getRageDate(i,"yyyy-MM-dd"),isQuick);
                list.add(fragment);
            }
            tabLayout.setupWithViewPager(vp);//与viewpager关联
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//平均分配
            vp.setOffscreenPageLimit(0);
        }else{//非快开彩
            FastLotteryHistoryFragment fragment=new FastLotteryHistoryFragment("",isQuick);
            list.add(fragment);
        }
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),list,tabs);
        vp.setAdapter(adapter);//适配器
    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.fastHistory_tablayout);
        tabLayout.setVisibility(isQuick? View.VISIBLE:View.GONE);//快开彩隐藏
        rules = (TextView) findViewById(R.id.toolbar_text);
        vp = (ViewPager) findViewById(R.id.fastHistory_vp);
        rules.setText("玩法");
        list=new ArrayList<>();
    }

    public int getLotteryId(){
        return lottery_id;
    }

    //日期格式
    public String cutDateStr(int date){
        String str=CommonUtil.getRageDate(date,"MM/dd");
        String m=str.substring(0,str.indexOf("/"));
        String d=str.substring(str.indexOf("/")+1,str.length());
        return String.valueOf(Integer.parseInt(m)).concat("/").concat(String.valueOf(Integer.parseInt(d)));
    }
}
