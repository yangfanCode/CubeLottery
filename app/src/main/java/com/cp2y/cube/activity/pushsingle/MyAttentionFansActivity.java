package com.cp2y.cube.activity.pushsingle;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.activity.pushsingle.fragment.MyAttentionFragment;
import com.cp2y.cube.activity.pushsingle.fragment.MyFansFragment;
import com.cp2y.cube.model.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MyAttentionFansActivity extends BaseActivity {
    private TabLayout tabLayout;
    private ViewPager vp;
    private int pos;
    private MyAttentionFragment attentionFragment=new MyAttentionFragment();
    private MyFansFragment fansFragment=new MyFansFragment();

    //我的关注粉丝页面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attention_fans);
        EventBus.getDefault().register(this);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        pos=getIntent().getIntExtra("pos",0);
        initView();
        initListener();
    }

    private void initListener() {

    }

    private void initView() {
        tabLayout = (TabLayout) findViewById(R.id.my_attention_fans_tablayout);
        vp = (ViewPager) findViewById(R.id.main_home_vp);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        List<Fragment> list=new ArrayList<>();
        String title[]={"我的关注","我的粉丝"};
        tabLayout.setupWithViewPager(vp);
        list.add(attentionFragment);
        list.add(fansFragment);
        ViewPagerAdapter vpAdapter=new ViewPagerAdapter(getSupportFragmentManager(),list,title);
        vp.setAdapter(vpAdapter);
        tabLayout.getTabAt(pos).select();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event){
        //点击关注取消应该刷新对应列表
        if(event==null)return;
        if(event.type==MessageEvent.PERSONAL_GUANZHU||event.type==MessageEvent.PERSONAL_QUXIAOGUANZHU){
            attentionFragment.initData();//刷新关注数
            fansFragment.initData();//刷新粉丝数
        }
    }
}
