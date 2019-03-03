package com.cp2y.cube.activity.news.fragment;

//行业动态
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
import com.cp2y.cube.activity.news.adapter.NewsIndustryAdapter;
import com.cp2y.cube.callback.ACacheInterface;
import com.cp2y.cube.custom.SingletonNewsGray;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ACacheHelper;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.NewsIndustryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewsIndustryFragment extends Fragment {
    private int YaoWenCount=0;//要闻个数
    private int totalPages=0,currPage=1;//总页数
    private ListView lv;
    private ImageView netOff;
    private MaterialRefreshLayout MateriaRefresh;
    private AVLoadingIndicatorView AVLoading;
    private boolean isShowImportNews =false;//是否有要闻
    private List<NewsIndustryModel.News> list;
    private NewsIndustryAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_news_layout, container, false);
        initView(view);
        initData();
        initNetOff();
        initListener();
        return view;
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            if(!TextUtils.isEmpty(ACacheHelper.getAsString("NewsIndustry"))){
                showCacheData();//断网如果有缓存拿缓存数据
            }else{//没有缓存展示断网
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
    private void initListener() {
        MateriaRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    MateriaRefresh.setLoadMore(true);
                    list.clear();
                    initNetOff();
            }

            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    if(currPage<totalPages){
                        super.onRefreshLoadMore(materialRefreshLayout);
                        RefreshLoadMore();
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
            if(isShowImportNews){
                if(position==0||position==YaoWenCount+1)return;
            }else{
                if(position==0)return;
            }
            if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
            Intent intent=new Intent(getActivity(), NewsSummaryActivity.class);
            intent.putExtra("id",list.get(position).getId());
            SingletonNewsGray.registerService(list.get(position).getId());
            startActivity(intent);
        }));
    }

    private void initNet() {
        currPage=1;
        //请求要闻接口
        NetHelper.LOTTERY_API.getNewsIndustryList("88", LoginSPUtils.getInt("id",0),currPage,20)
                .doOnTerminate(() -> MateriaRefresh.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<NewsIndustryModel>(){
                    @Override
                    public void onNext(NewsIndustryModel args) {
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
        AcacheJson();//缓存
    }

    //第一次请求网络展示数据
    private void NetDataTotal(NewsIndustryModel args) {
        if(!TextUtils.isEmpty(args.getPageSize())){
            totalPages=Integer.parseInt(args.getPageSize());
        }
        if(args.getListYW().size()>0){//有要闻数据
            isShowImportNews=true;
            //要闻数据
            list.add(null);//标题数据
            list.addAll(args.getListYW());
            YaoWenCount=args.getListYW().size();
            adapter.setIsShowImportNews(isShowImportNews);
            adapter.setYWCount(YaoWenCount);
        }
        if(args.getListDT()!=null&&args.getListDT().size()>0){//动态数据
            list.add(null);//标题数据
            list.addAll(args.getListDT());//动态数据
            adapter.LoadData(list);
            lv.setVisibility(View.VISIBLE);
        }
        AVLoading.setVisibility(View.GONE);//转圈圈
    }

    //缓存json
    private void AcacheJson() {
        ACacheInterface.getNewsIndustryList service= ACacheHelper.RETROFIT.create(ACacheInterface.getNewsIndustryList.class);
        service.getNewsIndustryList("88", CommonUtil.getIMEI(),0,1,20).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
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
                    ACacheHelper.put("NewsIndustry", responseBody.string());
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
        String jsonStr=ACacheHelper.getAsString("NewsIndustry");
        NewsIndustryModel model=new Gson().fromJson(jsonStr,NewsIndustryModel.class);
        NetDataTotal(model);
    }

    //上拉加载更多
    public void RefreshLoadMore(){
        if(!CommonUtil.isNetworkConnected(getActivity())){
            TipsToast.showTips("请检查网络设置");
            MateriaRefresh.finishRefreshLoadMore();
            AVLoading.setVisibility(View.GONE);
            netOff.setVisibility(View.VISIBLE);
            netOff.setOnClickListener((v -> RefreshLoadMore()));
            return;
        }
        NetHelper.LOTTERY_API.getNewsIndustryList("88",LoginSPUtils.getInt("id",0),++currPage,20)
                .doOnTerminate(() -> MateriaRefresh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<NewsIndustryModel>(){
                    @Override
                    public void onNext(NewsIndustryModel args) {
                        super.onNext(args);
                        if(args.getListDT()!=null&&args.getListDT().size()>0){//动态数据
                            netOff.setVisibility(View.GONE);
                            list.addAll(args.getListDT());//点击数据源
                            adapter.ReLoadData(args.getListDT());
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
    private void initData() {
        adapter=new NewsIndustryAdapter(getActivity());
        lv.setAdapter(adapter);
    }

    private void initView(View view) {
        list=new ArrayList<>();
        netOff = (ImageView)view. findViewById(R.id.netOff);
        lv = (ListView)view. findViewById(R.id.news_lv);
        MateriaRefresh = (MaterialRefreshLayout)view. findViewById(R.id.RefreshLayout);
        MateriaRefresh.setProgressColors(new int[]{ColorUtils.MID_BLUE});
        AVLoading = (AVLoadingIndicatorView)view. findViewById(R.id.AVLoadingIndicator);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }
}
