package com.cp2y.cube.activity.pushsingle.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.adapter.MyPushSingleAdpater;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.PushSingleListModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 他人中心推单页面
 */
public class PersonalPushSingleFragment extends Fragment {

    private int totalPages = 0, currPage = 1;
    private PullToRefreshListView lv;
    private ListView lv_child;
    private MyPushSingleAdpater adapter;
    private int personalUserId=0;
    private List<PushSingleListModel.Detail> list = new ArrayList<>();//数据
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_personal_push_single, container, false);
        if(getArguments()!=null){
            personalUserId=getArguments().getInt("personalUserId",0);
        }
        initView(rootView);
        initData();
        initListener();
        return rootView;
    }
    public static PersonalPushSingleFragment newInstance(int param1) {
        PersonalPushSingleFragment fragment = new PersonalPushSingleFragment();
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
                if (currPage < totalPages) {
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

    private void initData() {
        currPage = 1;
        NetHelper.LOTTERY_API.getPushSingleList(personalUserId,0,currPage,10)
                .doOnTerminate((() -> lv.onRefreshComplete()))
                .subscribe(new SafeOnlyNextSubscriber<PushSingleListModel>(){
                    @Override
                    public void onNext(PushSingleListModel args) {
                        super.onNext(args);
                        if (args.getList() != null && args.getList().size() > 0) {
                            list.clear();
                            list.addAll(args.getList());
                            totalPages = args.getPageSize();
                            adapter.LoadData(list);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }
    private void loadMoreData() {
        NetHelper.LOTTERY_API.getPushSingleList(personalUserId,0,++currPage,10)
                .doOnTerminate((() -> lv.onRefreshComplete()))
                .subscribe(new SafeOnlyNextSubscriber<PushSingleListModel>(){
                    @Override
                    public void onNext(PushSingleListModel args) {
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
        lv= (PullToRefreshListView) rootView.findViewById(R.id.personal_pushSingle_lv);
        lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lv_child=lv.getRefreshableView();
        lv_child.setFooterDividersEnabled(false);
        adapter = new MyPushSingleAdpater(getActivity());
        lv_child.setAdapter(adapter);
    }

}
