package com.cp2y.cube.activity.pushsingle;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.news.NewsActivity;
import com.cp2y.cube.activity.pushsingle.fragment.ActiveFragment;
import com.cp2y.cube.activity.pushsingle.fragment.AttentionFragment;
import com.cp2y.cube.activity.pushsingle.fragment.BangListFragment;
import com.cp2y.cube.activity.trends.TrendActivity;
import com.cp2y.cube.model.MessageEvent;
import com.cp2y.cube.utils.CommonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 推单动态
 */
public class PushSingleActivity extends BaseActivity {
    private RadioGroup app_push_rg;
    private RadioButton rb_push;
    private ImageView push,iv_add;
    private TabLayout tabLayout;
    private ActiveFragment activeFragment=new ActiveFragment();
    private AttentionFragment attentionFragment=new AttentionFragment();
    private BangListFragment bangListFragment=new BangListFragment();
    private List<Fragment> fragment=new ArrayList<Fragment>();
    private FragmentManager manager=getSupportFragmentManager();
    private int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_single);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)pos=bundle.getInt("pos",0);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initChange(pos);
        tabLayout.getTabAt(pos).select();//默认开启页面
    }

    private void initListener() {
        push.setOnClickListener((v -> {
            if(CommonUtil.isLogin()){
                startActivity(new Intent(PushSingleActivity.this,SelectSingleActivity.class));
            }else{
                intentLogin();
            }
        }));
        app_push_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Intent intent = new Intent();
                switch (checkedId){
                    case R.id.app_pushSingle_rb_main:
                        intent.setClass(PushSingleActivity.this,MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_pushSingle_rb_trend:
                        intent.setClass(PushSingleActivity.this,TrendActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                    case R.id.app_pushSingle_rb_news:
                        intent.setClass(PushSingleActivity.this,NewsActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        break;
                }

            }
        });
        iv_add.setOnClickListener((v -> showMoreWindow(v)));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pos=tab.getPosition();
                push.setVisibility(pos==2?View.GONE:View.VISIBLE);//隐藏编辑按钮
                initChange(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initChange(int pos){
        manager.beginTransaction().replace(R.id.push_single_containter,fragment.get(pos)).commit();
    }
    private void initView() {
        push = (ImageView) findViewById(R.id.push_single_button);
        iv_add = (ImageView) findViewById(R.id.app_selectSingle_add);//中间加号按钮
        app_push_rg = (RadioGroup) findViewById(R.id.app_pushSingle_rg);
        rb_push = (RadioButton) findViewById(R.id.app_pushSingle_rb_pushSingle);
        tabLayout = (TabLayout) findViewById(R.id.select_single_tablayout);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.addTab(tabLayout.newTab().setText("动态"));
        tabLayout.addTab(tabLayout.newTab().setText("关注"));
        tabLayout.addTab(tabLayout.newTab().setText("榜单"));
        fragment.add(activeFragment);
        fragment.add(attentionFragment);
        fragment.add(bangListFragment);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        rb_push.setChecked(true);
        if(fragment.get(pos) instanceof ActiveFragment){
            ((ActiveFragment)fragment.get(pos)).initData(true);
        }else if(fragment.get(pos) instanceof AttentionFragment){
            ((AttentionFragment)fragment.get(pos)).initData(true);
        }else if(fragment.get(pos) instanceof BangListFragment){
            ((BangListFragment)fragment.get(pos)).initData(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fragment.get(pos) instanceof ActiveFragment){
            ((ActiveFragment)fragment.get(pos)).setWarm();
        }else if(fragment.get(pos) instanceof AttentionFragment){
            ((AttentionFragment)fragment.get(pos)).setWarm();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(fragment.get(pos) instanceof ActiveFragment){
            ((ActiveFragment)fragment.get(pos)).setWarm();
        }else if(fragment.get(pos) instanceof AttentionFragment){
            ((AttentionFragment)fragment.get(pos)).setWarm();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
     public void onEventMainThread(MessageEvent event){
        if(event==null)return;
        if(event.type==MessageEvent.GUANZHU_RANK){
            tabLayout.getTabAt(event.position).select();
            initChange(event.position);
        }

     }
}
