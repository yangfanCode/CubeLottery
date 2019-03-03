package com.cp2y.cube.fragment;


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
import com.cp2y.cube.activity.FastLotteryHistoryActivity;
import com.cp2y.cube.adapter.FastHistoryAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryHistoryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FastLotteryHistoryFragment extends Fragment {
    private int totalPages=0,currPage=1;//总页数
    private ImageView netOff;
    private String dat=null;
    private boolean isQuick;
    private ListView lv;
    private MaterialRefreshLayout refrensh;
    private AVLoadingIndicatorView AVLoading;
    private List<LotteryHistoryModel.HistoryLottery> list;
    private FastHistoryAdapter adapter;
    private int lottery_id;

    public FastLotteryHistoryFragment(String dat,boolean isQuick) {
        this.dat=dat;
        this.isQuick=isQuick;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_fast_lottery_history, container, false);
        lottery_id=((FastLotteryHistoryActivity)getActivity()).getLotteryId();
        initView(rootView);//初始化
        initNetOff();//断网控制
        initListener();//监听
        return rootView;
    }

    private void initListener() {
        refrensh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refrensh.setLoadMore(true);
                initNetOff();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                if(currPage<totalPages){
                    super.onRefreshLoadMore(materialRefreshLayout);
                    LoadMoreData();
                }else{
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                    refrensh.setLoadMore(false);
                    if(lv.getFooterViewsCount()==0){
                        lv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                    }
                }
            }
        });
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
            AVLoading.setVisibility(View.GONE);
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);

        }
        initData();
    }

    private void initData() {
        currPage=1;
        NetHelper.LOTTERY_API.lotteryHistoryList(lottery_id, currPage, 20,dat).doOnTerminate(()->refrensh.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<LotteryHistoryModel>(){
                    @Override
                    public void onNext(LotteryHistoryModel args) {
                        super.onNext(args);
                        list.clear();
                        list.addAll(args.getList());
                        totalPages=args.getPageSize();
                        AVLoading.setVisibility(View.GONE);
                        if (list != null && list.size() > 0) {
                            netOff.setVisibility(View.GONE);
                            adapter.loadData(list);

                        } else {
                            refrensh.finishRefresh();
                            //TipsToast.showTips("无历史开奖");
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }
    //上拉加载
    private void LoadMoreData(){
        NetHelper.LOTTERY_API.lotteryHistoryList(lottery_id, ++currPage, 20,dat)
                .doOnTerminate(() -> refrensh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<LotteryHistoryModel>(){
                    @Override
                    public void onNext(LotteryHistoryModel args) {
                        super.onNext(args);
                        if (args.getList() != null && args.getList().size() > 0) {
                            netOff.setVisibility(View.GONE);
                            list.addAll(args.getList());
                            adapter.loadData(list);
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);//转圈圈
                        refrensh.finishRefreshLoadMore();
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }
    private void initView(View rootView) {
        lv = (ListView)rootView. findViewById(R.id.fast_history_lv);
        netOff = (ImageView) rootView.findViewById(R.id.netOff);//断网
        list=new ArrayList<>();
        refrensh = (MaterialRefreshLayout) rootView.findViewById(R.id.history_refrensh);
        AVLoading = (AVLoadingIndicatorView) rootView.findViewById(R.id.AVLoadingIndicator);
        if(lv.getHeaderViewsCount()==0)lv.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.fats_history_head,null));
        adapter = new FastHistoryAdapter(getActivity(),isQuick);
        lv.setAdapter(adapter);
    }

}
