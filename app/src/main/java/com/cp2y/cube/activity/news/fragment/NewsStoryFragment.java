package com.cp2y.cube.activity.news.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.news.NewsSummaryActivity;
import com.cp2y.cube.activity.news.adapter.NewsAdapter;
import com.cp2y.cube.callback.ACacheInterface;
import com.cp2y.cube.custom.SingletonNewsGray;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ACacheHelper;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.NewsModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewsStoryFragment extends Fragment {
    private int totalPages=0,currPage=1;//总页数
    private AVLoadingIndicatorView AVLoading;
    private MaterialRefreshLayout MateriaRefresh;
    private NewsAdapter adapter;
    private ListView lv;
    private View head;
    private ImageView netOff;
    private List<NewsModel.News> list=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_layout, container, false);
        initView(view);
        initNetOff();
        initListener();
        return view;
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            if(!TextUtils.isEmpty(ACacheHelper.getAsString("NewsStory"))){
                showCacheData();//断网如果有缓存拿缓存数据
            }else {//没有缓存展示断网
                netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                netOff.setOnClickListener((v -> initNetOff()));//点击加载
                AVLoading.setVisibility(View.GONE);
            }
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initNet();
    }
    private void initNet() {
        currPage=1;
        NetHelper.LOTTERY_API.getNewsList("-1", LoginSPUtils.getInt("id",0),currPage,20,false,false)
                .doOnTerminate(() -> MateriaRefresh.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
                    @Override
                    public void onNext(NewsModel args) {
                        super.onNext(args);
                        NetDataTotal(args);
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);//转圈圈
                        TipsToast.showTips("请检查网络设置");
                    }
                });
        if(CommonUtil.isNetworkConnected(getActivity())){
            AcacheJson();//缓存
        }
    }

    private void initData(NewsModel args) {
        SimpleDraweeView icon_head= (SimpleDraweeView) head.findViewById(R.id.news_item_story_head);
        icon_head.setImageURI(args.getCidPicture());
        adapter=new NewsAdapter(getActivity());
        if(lv.getHeaderViewsCount()>0){
            lv.removeHeaderView(head);//刷新时刷新头布局
            lv.addHeaderView(head);
        }else{
            lv.addHeaderView(head);
        }
        lv.setAdapter(adapter);
    }

    private void initListener() {
        MateriaRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    MateriaRefresh.setLoadMore(true);
                    initNetOff();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    if(currPage<totalPages){
                        super.onRefreshLoadMore(materialRefreshLayout);
                        LoadMoreData();
                    }else{
                        ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                        MateriaRefresh.setLoadMore(false);
                        if(lv.getFooterViewsCount()==0){
                            lv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                        }
                    }
            }
        });
        lv.setOnItemClickListener(((parent, view, position, id) -> {
            if(position==0)return;
            if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
            Intent intent=new Intent(getActivity(), NewsSummaryActivity.class);
            intent.putExtra("id",list.get(position-1).getId());
            SingletonNewsGray.registerService(list.get(position-1).getId());
            startActivity(intent);
        }));
    }

    //第一次请求网络展示数据
    private void NetDataTotal(NewsModel args) {
        if(!TextUtils.isEmpty(args.getPageSize())){
            totalPages=Integer.parseInt(args.getPageSize());
        }
        if (args.getList() != null && args.getList().size() > 0) {
            list.clear();
            list.addAll(args.getList());
            initData(args);//头部数据
            adapter.LoadData(args.getList());
            lv.setVisibility(View.VISIBLE);
        }
        AVLoading.setVisibility(View.GONE);//转圈圈
    }

    //缓存json
    private void AcacheJson() {
        ACacheInterface.getNewLists service= ACacheHelper.RETROFIT.create(ACacheInterface.getNewLists.class);
        service.getNewList("-1", LoginSPUtils.getInt("id",0),0,20,false).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.e("yangfan", "onError: "+e);
            }
            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    ACacheHelper.put("NewsStory", responseBody.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 展示缓存数据
     */
    private void showCacheData(){
        String jsonStr=ACacheHelper.getAsString("NewsStory");
        NewsModel model=new Gson().fromJson(jsonStr,NewsModel.class);
        NetDataTotal(model);
    }


    //上拉加载
    private void LoadMoreData(){
//        if(!CommonUtil.isNetworkConnected(getActivity())){
//            TipsToast.showTips("请检查网络设置");
//            MateriaRefresh.finishRefreshLoadMore();
//            AVLoading.setVisibility(View.GONE);
//            netOff.setVisibility(View.VISIBLE);
//            netOff.setOnClickListener((v -> LoadMoreData()));
//            return;
//        }
        NetHelper.LOTTERY_API.getNewsList("-1", LoginSPUtils.getInt("id",0),++currPage,20,false,false)
                .doOnTerminate(() -> MateriaRefresh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
                    @Override
                    public void onNext(NewsModel args) {
                        super.onNext(args);
                        if (args.getList() != null && args.getList().size() > 0) {
                            netOff.setVisibility(View.GONE);
                            list.addAll(args.getList());
                            adapter.ReLoadData(args.getList());
                        }
                    }
                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        AVLoading.setVisibility(View.GONE);//转圈圈
                        MateriaRefresh.finishRefreshLoadMore();
                        TipsToast.showTips("请检查网络设置");
                    }
                });
    }
    private void initView(View view) {
        lv = (ListView)view. findViewById(R.id.news_lv);
        netOff = (ImageView)view. findViewById(R.id.netOff);
        head=LayoutInflater.from(getActivity()).inflate(R.layout.news_item_story_head,lv,false);
        AVLoading = (AVLoadingIndicatorView)view. findViewById(R.id.AVLoadingIndicator);
        MateriaRefresh = (MaterialRefreshLayout)view. findViewById(R.id.RefreshLayout);
        MateriaRefresh.setProgressColors(new int[]{ColorUtils.MID_BLUE});
    }

    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }
}
