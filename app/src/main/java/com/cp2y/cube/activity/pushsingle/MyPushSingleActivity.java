package com.cp2y.cube.activity.pushsingle;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.activity.pushsingle.fragment.MyPushSingleFragment;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.PushSingleTitleModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.widgets.NotLoginForOptional;

import java.util.ArrayList;
import java.util.List;

public class MyPushSingleActivity extends BaseActivity {

    private ViewPager vp;
    private TabLayout tabLayout;
    private String[] title;
    private List<Fragment>list_fragment;
    private NotLoginForOptional layView;
    private ImageView netOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_push_single);
        setMainTitle("我的推单");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();//初始化
        initNetOff();//请求网络
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            netOff.setVisibility(View.GONE);
        }
        initData();
    }
    private void initData() {
        int id=LoginSPUtils.getInt("id", 0);
        NetHelper.LOTTERY_API.getPushSingleTitle(id).subscribe(new SafeOnlyNextSubscriber<PushSingleTitleModel>(){
            @Override
            public void onNext(PushSingleTitleModel args) {
                super.onNext(args);
                List<PushSingleTitleModel.Detail> list=args.getLotteryList();
                if(list!=null&&list.size()>0){
                    tabLayout.setVisibility(View.VISIBLE);//显示标题
                    title=new String[list.size()];
                    for (int i = 0; i < list.size(); i++) {
                        title[i]=list.get(i).getLotteryName();//标题数组
                        MyPushSingleFragment fragment=new MyPushSingleFragment(list.get(i).getLotteryID());
                        list_fragment.add(fragment);
                    }
                    ViewPagerAdapter adapter=new ViewPagerAdapter(getSupportFragmentManager(),list_fragment,title);
                    vp.setAdapter(adapter);
                }else{
                    layView.setVisibility(View.VISIBLE);
                    layView.setTypeEnum(NotLoginForOptional.TypeEnum.NO_PUSH);
                    layView.setBtnOnClickListener((v -> CommonUtil.openActicity(MyPushSingleActivity.this,SelectSingleActivity.class,null)));
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }


    private void initView() {
        netOff= (ImageView) findViewById(R.id.netOff);
        layView = (NotLoginForOptional) findViewById(R.id.lay_login_view);
        vp = (ViewPager) findViewById(R.id.main_home_vp);
        tabLayout = (TabLayout) findViewById(R.id.main_home_tablayout);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        list_fragment=new ArrayList<>();
    }
}
