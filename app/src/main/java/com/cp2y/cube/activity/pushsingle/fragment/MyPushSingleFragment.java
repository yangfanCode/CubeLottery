package com.cp2y.cube.activity.pushsingle.fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.adapter.MyPushSingleAdpater;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.PushSingleListModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class MyPushSingleFragment extends Fragment {


    private MaterialRefreshLayout refreshLayout;
    private ListView lv;
    private int lotteryId;
    private int totalPages=0,currPage=1;//总页数
    private MyPushSingleAdpater adpater;
    private AVLoadingIndicatorView AVLoading;

    public MyPushSingleFragment(int lotteryId) {
        this.lotteryId=lotteryId;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_my_push_single, container, false);
        initView(rootView);//初始化
        initListener();//监听
        initData();//请求网络
        return rootView;
    }

    private void initData() {
        currPage=1;
        NetHelper.LOTTERY_API.getPushSingleList(LoginSPUtils.getInt("id", 0),lotteryId,currPage,10)
                .doOnTerminate((() -> refreshLayout.finishRefresh()))
                .subscribe(new SafeOnlyNextSubscriber<PushSingleListModel>(){
            @Override
            public void onNext(PushSingleListModel args) {
                super.onNext(args);
                List<PushSingleListModel.Detail>list=args.getList();
                if(list!=null&&list.size()>0){
                    AVLoading.setVisibility(View.GONE);
                    totalPages=args.getPageSize();//总页数
                    if(lv.getHeaderViewsCount()==0){//加头部
                        View head=null;
                        if(lotteryId==0){//全部 头布局
                            head=LayoutInflater.from(getActivity()).inflate(R.layout.my_push_single_head_all,null);
                        }else{//其他彩种 头布局
                            head=LayoutInflater.from(getActivity()).inflate(R.layout.my_push_single_head_lottery,null);
                            TextView title= (TextView) head.findViewById(R.id.my_push_single_head_title);
                            TextView rate= (TextView) head.findViewById(R.id.my_push_single_head_rate);
                            title.setText(list.get(0).getLotteryName().concat("回报率:"));
                            rate.setText(args.getRate());
                        }
                        lv.addHeaderView(head);
                    }
                    adpater.LoadData(list);
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
    private void initListener() {
        refreshLayout.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                refreshLayout.setLoadMore(true);
                initData();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                super.onRefreshLoadMore(materialRefreshLayout);
                if(currPage<totalPages){
                    super.onRefreshLoadMore(materialRefreshLayout);
                    loadMoreData();
                }else{
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                    refreshLayout.setLoadMore(false);
                    if(lv.getFooterViewsCount()==0){
                        lv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                    }
                }
            }
        });
    }
    //上拉加载
    private void loadMoreData(){
        NetHelper.LOTTERY_API.getPushSingleList(LoginSPUtils.getInt("id", 0),lotteryId,++currPage,10)
                .doOnTerminate((() -> refreshLayout.finishRefreshLoadMore()))
                .subscribe(new SafeOnlyNextSubscriber<PushSingleListModel>(){
            @Override
            public void onNext(PushSingleListModel args) {
                super.onNext(args);
                List<PushSingleListModel.Detail>list=args.getList();
                if(list!=null&&list.size()>0){
                    adpater.reLoadData(list);
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
        refreshLayout = (MaterialRefreshLayout)rootView. findViewById(R.id.RefreshLayout);
        AVLoading= (AVLoadingIndicatorView) rootView.findViewById(R.id.AVLoadingIndicator);
        refreshLayout.setProgressColors(new int[]{ColorUtils.MID_BLUE});
        lv = (ListView)rootView. findViewById(R.id.my_push_lv);
        adpater=new MyPushSingleAdpater(getActivity());
        lv.setAdapter(adpater);
    }

}
