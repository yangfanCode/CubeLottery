package com.cp2y.cube.activity.pushsingle;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.news.adapter.ViewPagerAdapter;
import com.cp2y.cube.activity.pushsingle.fragment.PersonalAttentionFragment;
import com.cp2y.cube.activity.pushsingle.fragment.PersonalFansFragment;
import com.cp2y.cube.activity.pushsingle.fragment.PersonalPushSingleFragment;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.SubscribeCancleDialog;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.MessageEvent;
import com.cp2y.cube.model.PersonalModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * 他人中心页面
 */
public class PersonalActivity extends BaseActivity {

    private SimpleDraweeView iv_icon;
    private TextView tv_attention;
    private TextView tv_name;
    private TextView tv_message;
    private TabLayout tabLayout;
    private ViewPager vp;
    private int otherUserId = 0;//他人userId
    private int type=0;//关注状态
    private int id=0;//item id
    private SubscribeCancleDialog dialog = null;
    private AlertDialog alertDialog = null;
    private String tab1="推单 ";
    private String tab2="关注 ";
    private String tab3="粉丝 ";
    private ViewPagerAdapter adapter;
    private ImageView netOff;
    private String attentionNum;//关注数
    private String fansNum;//粉丝数
    private List<Fragment> list;//fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_personal);
        setMainTitle("个人主页");//名称
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        otherUserId = getIntent().getIntExtra("otherUserId", 0);
        initView();
        initNetOff();
        initListener();
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
    private void initListener() {
        tv_attention.setOnClickListener((v -> {
            if(!CommonUtil.isNetworkConnected(PersonalActivity.this)){
                TipsToast.showTips(getString(R.string.netOff));
                return;
            }
            if (CommonUtil.isLogin()) {
                if(type==0){//没有互相关注
                    NetHelper.LOTTERY_API.doSubscribeUser(LoginSPUtils.getInt("id", 0),otherUserId)
                            .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                                @Override
                                public void onNext(FlagModel args) {
                                    super.onNext(args);
                                    if(args.getFlag()==1){
                                        type=1-type;
                                        tv_attention.setBackgroundResource(type==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
                                        TipsToast.showTips(getString(R.string.sub_success));
                                        fansNum=String.valueOf(Integer.valueOf(fansNum)+1);
                                        tab3="粉丝 ".concat(fansNum);
                                        tabLayout.getTabAt(2).setText(tab3);//粉丝数加1
                                        ((PersonalFansFragment)list.get(2)).initData();//刷新粉丝数
                                    }
                                }
                                @Override
                                public void onError(Throwable e) {
                                    super.onError(e);
                                    TipsToast.showTips(getString(R.string.serviceOff));
                                }
                            });
                }else{
                    if (dialog == null) {
                        dialog = new SubscribeCancleDialog(PersonalActivity.this);
                    }
                    if (alertDialog == null) {
                        alertDialog = dialog.getDialog();
                    }
                    alertDialog.show();
                    dialog.getTvCancle().setOnClickListener((v1 -> alertDialog.dismiss()));
                    dialog.getTvSubmit().setOnClickListener((v1 -> {
                        alertDialog.dismiss();
                        NetHelper.LOTTERY_API.cancleSubscribeUser(LoginSPUtils.getInt("id", 0),otherUserId,id)
                                .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                                    @Override
                                    public void onNext(FlagModel args) {
                                        super.onNext(args);
                                        if(args.getFlag()==1){
                                            type=1-type;
                                            tv_attention.setBackgroundResource(type==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
                                            TipsToast.showTips(getString(R.string.sub_cancle));
                                            fansNum=String.valueOf(Integer.valueOf(fansNum)-1);
                                            tab3="粉丝 ".concat(fansNum);
                                            tabLayout.getTabAt(2).setText(tab3);//粉丝数减1
                                            ((PersonalFansFragment)list.get(2)).initData();//刷新粉丝数
                                        }
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        super.onError(e);
                                        TipsToast.showTips(getString(R.string.serviceOff));
                                    }
                                });
                    }));
                }
            }else{
                intentLogin();
            }
        }));
    }

    private void initData() {
        NetHelper.LOTTERY_API.personalDetail(otherUserId,LoginSPUtils.getInt("id", 0))
                .subscribe(new SafeOnlyNextSubscriber<PersonalModel>() {
                    @Override
                    public void onNext(PersonalModel args) {
                        super.onNext(args);
                        if(args.flag==1){
                            PersonalModel.Detail detail=args.user;
                            iv_icon.setImageURI(detail.url);
                            tv_name.setText(detail.name);
                            tv_message.setText(detail.interest);
                            type=detail.subscribeType;
                            id=detail.iD;
                            tv_attention.setBackgroundResource(type==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
                            attentionNum=detail.attentionNum;
                            fansNum=detail.fansNum;
                            tab1="推单 ".concat(detail.pushSingleNum);
                            tab2="关注 ".concat(detail.attentionNum);
                            tab3="粉丝 ".concat(detail.fansNum);
                            tabLayout.getTabAt(0).setText(tab1);
                            tabLayout.getTabAt(1).setText(tab2);
                            tabLayout.getTabAt(2).setText(tab3);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    private void initView() {
        netOff = (ImageView) findViewById(R.id.netOff);
        iv_icon = (SimpleDraweeView) findViewById(R.id.personal_ivIcon);
        tv_attention = (TextView) findViewById(R.id.personal_tvAttention);
        tv_name = (TextView) findViewById(R.id.personal_tvName);
        tv_message = (TextView) findViewById(R.id.personal_tvMessage);
        tabLayout = (TabLayout) findViewById(R.id.personal_tabLayout);
        tv_attention.setVisibility(otherUserId==LoginSPUtils.getInt("id", 0)? View.GONE:View.VISIBLE);//自己隐藏
        vp = (ViewPager) findViewById(R.id.personal_vp);
        tabLayout.setupWithViewPager(vp);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        list=new ArrayList<>();
        list.add(PersonalPushSingleFragment.newInstance(otherUserId));
        list.add(PersonalAttentionFragment.newInstance(otherUserId));
        list.add(PersonalFansFragment.newInstance(otherUserId));
        adapter=new ViewPagerAdapter(getSupportFragmentManager(),list,new String[]{tab1,tab2,tab3});
        vp.setAdapter(adapter);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event){
        //点击头像应该刷新用户
        //点击关注取消应该刷新对应列表
        if(event==null)return;
        if(otherUserId==CommonUtil.getUserId()){
           //自己主页
            if(event.type==MessageEvent.PERSONAL_GUANZHU){
                attentionNum=String.valueOf(Integer.valueOf(attentionNum)+1);
                tab2="关注 ".concat(attentionNum);
                tabLayout.getTabAt(1).setText(tab2);
                ((PersonalAttentionFragment)list.get(1)).initData();//刷新关注数
                ((PersonalFansFragment)list.get(2)).initData();//刷新粉丝数
            }else if(event.type==MessageEvent.PERSONAL_QUXIAOGUANZHU){
                attentionNum=String.valueOf(Integer.valueOf(attentionNum)-1);
                tab2="关注 ".concat(attentionNum);
                tabLayout.getTabAt(1).setText(tab2);
                ((PersonalAttentionFragment)list.get(1)).initData();//刷新关注数
                ((PersonalFansFragment)list.get(2)).initData();//刷新粉丝数
            }
        }else{//他人主页
            if(event.type==MessageEvent.PERSONAL_GUANZHU||event.type==MessageEvent.PERSONAL_QUXIAOGUANZHU){
                ((PersonalAttentionFragment)list.get(1)).initData();//刷新关注数
                ((PersonalFansFragment)list.get(2)).initData();//刷新粉丝数
            }
        }
        if(event.type==MessageEvent.PERSONAL_OPENACT){//点击头像刷新页面
            otherUserId=event.position;
            initData();
            list=new ArrayList<>();
            list.add(PersonalPushSingleFragment.newInstance(otherUserId));
            list.add(PersonalAttentionFragment.newInstance(otherUserId));
            list.add(PersonalFansFragment.newInstance(otherUserId));
            adapter=new ViewPagerAdapter(getSupportFragmentManager(),list,new String[]{tab1,tab2,tab3});
            vp.setAdapter(adapter);
        }
    }
}
