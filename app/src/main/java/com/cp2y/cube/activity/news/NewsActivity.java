package com.cp2y.cube.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.activity.news.fragment.News3DFragment;
import com.cp2y.cube.activity.news.fragment.NewsDoubleFragment;
import com.cp2y.cube.activity.news.fragment.NewsIndustryFragment;
import com.cp2y.cube.activity.news.fragment.NewsLottoFragment;
import com.cp2y.cube.activity.news.fragment.NewsP3Fragment;
import com.cp2y.cube.activity.news.fragment.NewsP5Fragment;
import com.cp2y.cube.activity.news.fragment.NewsStoryFragment;
import com.cp2y.cube.activity.news.fragment.NewsSupportFragment;
import com.cp2y.cube.activity.pushsingle.PushSingleActivity;
import com.cp2y.cube.activity.trends.TrendActivity;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends BaseActivity {
    private List<Fragment>list;//fragment数据
    private Fragment[]fragments={new NewsSupportFragment(),new NewsDoubleFragment(),new NewsLottoFragment(),new News3DFragment(),new NewsP3Fragment(),new NewsP5Fragment(),new NewsIndustryFragment(),
            new NewsStoryFragment()};
    private TabLayout tabLayout;
    private String[]tabs={"推荐","双色球","大乐透","福彩3D","排列3","排列5","行业动态","彩民故事"};
    private ViewPager vp;
    private ViewPagerAdapter adapter;
    private RadioGroup rg;
    private ImageView center_add;
    private int pos=0;//vp位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setMainTitle("资讯");
        initView();//初始化
        initData();//数据
        initListener();//监听
    }

    private void initListener() {
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent intent = new Intent();
                switch (checkedId){
                    case R.id.app_news_rb_main:
                        intent.setClass(NewsActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_news_rb_trend:
                        intent.setClass(NewsActivity.this, TrendActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_news_rb_openLottery:
                        intent.setClass(NewsActivity.this, PushSingleActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                }
            }
        });
        center_add.setOnClickListener((v -> showMoreWindow(v)));
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {pos=position;}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void initData() {
        for (int i = 0; i < fragments.length; i++) {//fragment数据
            list.add(fragments[i]);
        }
        tabLayout.setupWithViewPager(vp);//与viewpager关联
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//平均分配
        vp.setOffscreenPageLimit(0);
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),list,tabs);
        vp.setAdapter(adapter);//适配器
    }
    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.app_news_tablayout);
        vp = (ViewPager) findViewById(R.id.app_news_vp);
        rg = (RadioGroup) findViewById(R.id.app_news_rg);
        center_add = (ImageView) findViewById(R.id.app_news_add);
        list=new ArrayList<>();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //跳转到H5页面返回后重新开启
        closeMoreWindow();//关闭过滤选号
        setDrawerClose();//关闭侧拉
        if(adapter.getItem(pos)instanceof NewsSupportFragment){
            NewsSupportFragment fragment= (NewsSupportFragment) list.get(0);
            fragment.startHandler();
        }
    }
}
