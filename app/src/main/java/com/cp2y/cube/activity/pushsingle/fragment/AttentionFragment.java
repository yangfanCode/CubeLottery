package com.cp2y.cube.activity.pushsingle.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.pushsingle.PushSingleChangeSummaryActivity;
import com.cp2y.cube.activity.pushsingle.PushSingleSummaryActivity;
import com.cp2y.cube.activity.pushsingle.adapter.ActiveAdpater;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.ActiveModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.MessageEvent;
import com.cp2y.cube.model.PushSingleWarmModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.widgets.NotLoginForOptional;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AttentionFragment extends Fragment {
    private int totalPages = 0, currPage = 1;
    private ListView lv;
    private MaterialRefreshLayout refreshLayout;
    private ActiveAdpater adapter;
    private AVLoadingIndicatorView AVLoading;
    private View view_warm;
    private List<ActiveModel.Detail> list = new ArrayList<>();//数据
    private NotLoginForOptional layView;
    private ImageView netOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_attention, container, false);
        initView(rootView);
        initNetOff();
        initListener();
        return rootView;
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            netOff.setVisibility(View.GONE);
        }
        initData(true);
    }

    private void initListener() {
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshLayout.setLoadMore(true);
                initData(false);
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if (currPage < totalPages) {
                    super.onRefreshLoadMore(materialRefreshLayout);
                    loadMoreData();
                } else {
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                    refreshLayout.setLoadMore(false);
                    if (lv.getFooterViewsCount() == 0) {
                        lv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot, null));
                    }
                }
            }
        });
        lv.setOnItemClickListener(((parent, view, position, id) -> {
            boolean isShowWarm = lv.getHeaderViewsCount() == 1;
            if (isShowWarm && position == 0) return;
            Intent intent = new Intent();
            int type = list.get(position).getType();
            intent.setClass(getActivity(), type == 0 ? PushSingleSummaryActivity.class : PushSingleChangeSummaryActivity.class);
            intent.putExtra("pushSingleID", list.get(isShowWarm ? position + 1 : position).getiD());
            startActivity(intent);
        }));
    }

    public void initData(boolean isShow) {
        boolean loginState = LoginSPUtils.isLogin();
        if(loginState){
            currPage = 1;
            AVLoading.setVisibility(isShow?View.VISIBLE:View.GONE);
            NetHelper.LOTTERY_API.pushSingleAttention(LoginSPUtils.getInt("id", 0),currPage, 10)
                    .doOnTerminate((() -> refreshLayout.finishRefresh()))
                    .subscribe(new SafeOnlyNextSubscriber<ActiveModel>() {
                        @Override
                        public void onNext(ActiveModel args) {
                            super.onNext(args);
                            if (args.getList() != null && args.getList().size() > 0) {
                                layView.setVisibility(View.GONE);
                                list.clear();
                                list.addAll(args.getList());
                                totalPages = args.getPageSize();
                                adapter.loadData(list);
                                if (lv.getHeaderViewsCount() == 0) {
                                    IsShowWarm();
                                } else {
                                    setWarm();
                                }
                            }else{
                                layView.setVisibility(View.VISIBLE);
                                layView.setTypeEnum(NotLoginForOptional.TypeEnum.NO_GUANZHU);
                                layView.setBtnOnClickListener((v -> EventBus.getDefault().post(new MessageEvent(MessageEvent.GUANZHU_RANK,2))));
                            }

                        }

                        @Override
                        public void onError(Throwable e) {
                            super.onError(e);
                            TipsToast.showTips("请检查网络设置");
                        }

                        @Override
                        public void onCompleted() {
                            super.onCompleted();
                            AVLoading.setVisibility(View.GONE);
                            lv.setSelection(0);//回到初始位置
                        }
                    });
        }else{
            layView.setVisibility(View.VISIBLE);
            layView.setTypeEnum(NotLoginForOptional.TypeEnum.NOT_LOGIN);
            layView.setBtnOnClickListener((v -> ((BaseActivity)getActivity()).intentLogin()));
        }
    }

    /**
     * 是否展示推荐
     */
    private void IsShowWarm() {
        NetHelper.LOTTERY_API.getPushSingleWarm(LoginSPUtils.getInt("id", 0)).subscribe(new SafeOnlyNextSubscriber<PushSingleWarmModel>() {
            @Override
            public void onNext(PushSingleWarmModel args) {
                super.onNext(args);
                boolean warm = args.isWarm();
                if (warm) {
                    lv.addHeaderView(view_warm);
                    lv.setSelection(1);
                    lv.smoothScrollToPositionFromTop(0, 0, 5000);//规定时间内滚动
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    //设置初始值
    public void setWarm() {
        NetHelper.LOTTERY_API.setPushSingleWarm(LoginSPUtils.getInt("id", 0)).subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if (args.getFlag() == 1) {
                    if (lv.getHeaderViewsCount() == 1) {
                        //lv.smoothScrollToPositionFromTop(1,0,5000);//规定时间内滚动
                        lv.removeHeaderView(view_warm);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void loadMoreData() {
        NetHelper.LOTTERY_API.pushSingleAttention(LoginSPUtils.getInt("id", 0),++currPage, 10)
                .doOnTerminate((() -> refreshLayout.finishRefreshLoadMore()))
                .subscribe(new SafeOnlyNextSubscriber<ActiveModel>() {
                    @Override
                    public void onNext(ActiveModel args) {
                        super.onNext(args);
                        if (args.getList() != null && args.getList().size() > 0) {
                            list.addAll(args.getList());
                            adapter.reLoadData(args.getList());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }

    private void initView(View rootView) {
        layView = (NotLoginForOptional)rootView. findViewById(R.id.lay_login_view);
        netOff = (ImageView) rootView.findViewById(R.id.netOff);
        refreshLayout = (MaterialRefreshLayout) rootView.findViewById(R.id.RefreshLayout);
        AVLoading = (AVLoadingIndicatorView) rootView.findViewById(R.id.AVLoadingIndicator);
        view_warm = LayoutInflater.from(getActivity()).inflate(R.layout.push_single_warm, lv, false);
        lv = (ListView) rootView.findViewById(R.id.my_attention_lv);
        refreshLayout.setProgressColors(new int[]{ColorUtils.MID_BLUE});
        adapter = new ActiveAdpater(getActivity());
        lv.setAdapter(adapter);
    }

}
