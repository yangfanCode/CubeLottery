package com.cp2y.cube.activity.pushsingle.fragment;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.pushsingle.adapter.PerAttentionAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.SubscribeCancleDialog;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.AttentionFansListModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.MessageEvent;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 他人个人中心关注页面
 */
public class PersonalAttentionFragment extends Fragment implements MyInterface.SubscribePersonal{
    private int totalPage=0,currentPage=1;
    private ListView lv_child;
    private PullToRefreshListView lv;
    private PerAttentionAdapter adapter;
    private List<AttentionFansListModel.Detail>list;
    private int personalUserId=0;
    private SubscribeCancleDialog dialog=null;
    private AlertDialog alertDialog=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_personal_attention, container, false);
        if(getArguments()!=null){
            personalUserId=getArguments().getInt("personalUserId",0);
        }
        initView(rootView);
        initData();
        initListener();
        return rootView;
    }

    public static PersonalAttentionFragment newInstance(int param1) {
        PersonalAttentionFragment fragment = new PersonalAttentionFragment();
        Bundle args = new Bundle();
        args.putInt("personalUserId", param1);
        fragment.setArguments(args);
        return fragment;
    }
    private void initListener() {
        lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if (currentPage < totalPage) {
                    loadMoreData();
                } else {
                    ContextHelper.getApplication().runDelay(lv::onRefreshComplete, 100);
                    lv.setMode(PullToRefreshBase.Mode.DISABLED);
                    if(lv_child.getFooterViewsCount()==1){
                        lv_child.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                    }
                    //解决lv.setMode(PullToRefreshBase.Mode.DISABLED); setMode后listview滚动到第一条 setSelection无效bug
                    lv.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lv_child.setSelection(adapter.getCount()+1);
                        }
                    },50);

                }
            }
        });
    }

    public void initData() {
        currentPage=1;
        NetHelper.LOTTERY_API.getPersonalAttentionFansList(personalUserId,personalUserId,0,currentPage,10,LoginSPUtils.getInt("id", 0))
                .doOnTerminate(()->lv.onRefreshComplete())
                .subscribe(new SafeOnlyNextSubscriber<AttentionFansListModel>(){
            @Override
            public void onNext(AttentionFansListModel args) {
                super.onNext(args);
                if(args.getItem()!=null){
                    totalPage=args.getPageSize();
                    list.clear();
                    list.addAll(args.getItem());
                    adapter.loadData(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
    private void loadMoreData(){
        NetHelper.LOTTERY_API.getPersonalAttentionFansList(personalUserId,personalUserId,0,++currentPage,10,LoginSPUtils.getInt("id", 0))
                .doOnTerminate(()->lv.onRefreshComplete())
                .subscribe(new SafeOnlyNextSubscriber<AttentionFansListModel>(){
            @Override
            public void onNext(AttentionFansListModel args) {
                super.onNext(args);
                if(args.getItem()!=null&&args.getItem().size()>0){
                    list.addAll(args.getItem());
                    adapter.reloadData(args.getItem());
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
    private void initView(View rootView) {
        list=new ArrayList<>();
        lv = (PullToRefreshListView) rootView. findViewById(R.id.personal_attention_lv);
        lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv_child=lv.getRefreshableView();
        lv_child.setFooterDividersEnabled(false);
        adapter=new PerAttentionAdapter(getActivity());
        adapter.setSubscribePersonal(this);
        lv_child.setAdapter(adapter);
    }

    private void updateSingle(int position,int type) {
        int pos=position+1;//头布局
        /**第一个可见的位置**/
        int firstVisiblePosition = lv_child.getFirstVisiblePosition();
        /**最后一个可见的位置**/
        int lastVisiblePosition = lv_child.getLastVisiblePosition();

        /**在看见范围内才更新，不可见的滑动后自动会调用getView方法更新**/
        if (pos >= firstVisiblePosition && pos <= lastVisiblePosition) {
            /**获取指定位置view对象**/
            View view = lv_child.getChildAt(pos - firstVisiblePosition);
            ImageView button = (ImageView) view.findViewById(R.id.attention_fans_button);
            //头布局占以为 -1
            if (button != null)
                button.setImageResource(type==0?R.mipmap.xq_guanzhu:R.mipmap.xq_yiguanzhu);
        }
    }

    @Override
    public void subscribePersonal(int position, int type) {
        if(!CommonUtil.isNetworkConnected(getActivity())){
            TipsToast.showTips(getString(R.string.netOff));
            return;
        }
        if (CommonUtil.isLogin()) {
            if(type==0){//没有互相关注
                NetHelper.LOTTERY_API.doSubscribeUser(LoginSPUtils.getInt("id", 0),adapter.getItem(position).getSubscribeUserId())
                        .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                            @Override
                            public void onNext(FlagModel args) {
                                super.onNext(args);
                                if(args.getFlag()==1){
                                    AttentionFansListModel.Detail detail= adapter.getList().get(position);
                                    detail.setSubscribeType(1-type);//变更状态
                                    adapter.getList().set(position,detail);
                                    updateSingle(position,1-type);
                                    TipsToast.showTips(getString(R.string.sub_success));
                                    EventBus.getDefault().post(new MessageEvent(MessageEvent.PERSONAL_GUANZHU));
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                super.onError(e);
                                TipsToast.showTips(getString(R.string.serviceOff));
                            }
                        });
            }else{
                if(dialog==null){
                    dialog=new SubscribeCancleDialog(getActivity());
                }
                if(alertDialog==null){
                    alertDialog=dialog.getDialog();
                }
                alertDialog.show();
                dialog.getTvCancle().setOnClickListener((v -> alertDialog.dismiss()));
                dialog.getTvSubmit().setOnClickListener((v -> {
                    alertDialog.dismiss();
                    NetHelper.LOTTERY_API.cancleSubscribeUser(LoginSPUtils.getInt("id", 0),adapter.getItem(position).getSubscribeUserId(),adapter.getItem(position).getId())
                            .subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                                @Override
                                public void onNext(FlagModel args) {
                                    super.onNext(args);
                                    if(args.getFlag()==1){
                                        AttentionFansListModel.Detail detail= adapter.getList().get(position);
                                        detail.setSubscribeType(1-type);//变更状态
                                        adapter.getList().set(position,detail);
                                        updateSingle(position,1-type);
                                        TipsToast.showTips(getString(R.string.sub_cancle));
                                        EventBus.getDefault().post(new MessageEvent(MessageEvent.PERSONAL_QUXIAOGUANZHU));
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
            ((BaseActivity)getActivity()).intentLogin();//登录页面
        }
    }
    public PerAttentionAdapter getAdapter(){
      return adapter;
    };

}
