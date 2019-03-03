package com.cp2y.cube.activity.news.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.news.NewsSummaryActivity;
import com.cp2y.cube.activity.news.adapter.NewsSupportAdapter;
import com.cp2y.cube.activity.news.adapter.ViewPagerPlayAdapter;
import com.cp2y.cube.activity.news.custom.ViewPagerScroller;
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
import com.cp2y.cube.utils.DisplayUtil;
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

public class NewsSupportFragment extends Fragment {
    private List<String> ImageUrl;//图片地址
    private List<String> ImageIntent;//图片跳转地址
    private List<NewsModel.News>list;
    private int totalPages=0,currPage=1;//总页数
    private List<SimpleDraweeView>list_head;//头部图片集合
    private NewsSupportAdapter adapter;//新闻列表adapter
    private ViewPagerPlayAdapter head_adapter;//头布局轮播
    private ListView lv;
    private View head;
    private ViewPager head_vp;//头布局viewpager
    private ImageView[] ivs;//控制器数组
    private int imgCount=0;//轮播图数量
    private Handler handler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            head_vp.setCurrentItem(head_vp.getCurrentItem()+1);
            handler.sendEmptyMessageDelayed(1, 3000);
        };
    };
    private AVLoadingIndicatorView AVLoading;
    private MaterialRefreshLayout materialRefresh;
    private ImageView netOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_support, container, false);
        //初始化
        initView(view);
        initNetOff();//请求网络
        initListener();//viewopager滚动监听
        return view;
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            if(!TextUtils.isEmpty(ACacheHelper.getAsString("NewsSupport"))){
                showCacheData();//断网如果有缓存拿缓存数据
            }else{//没有缓存展示断网
                AVLoading();
                netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                netOff.setOnClickListener((v -> initNetOff()));//点击加载
            }
        }else{
            AVLoading.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        initNet();
    }

    //第一次请求1页20条数据展示viewpager
    private void initNet() {
        NetHelper.LOTTERY_API.getNewsList("3,6,-1,88,4,7", LoginSPUtils.getInt("id",0),currPage,20,false,false)
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
            @Override
            public void onNext(NewsModel args) {
                super.onNext(args);
                NetDataTotal(args);
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("请检查网络设置");
            }
        });
        if(CommonUtil.isNetworkConnected(getActivity())){
            AcacheJson();//缓存
        }
    }
    //第一次请求网络展示数据
    private void NetDataTotal(NewsModel args) {
        if(!"-1".equals(args.getHomePicture())){//有图
            ImageUrl.clear();
            ImageIntent.clear();
            String[] imgSummary=args.getHomePicture().split(";");
            imgCount=imgSummary.length;//图片轮播数量
            for (int i = 0; i < imgCount; i++) {//图片地址 和跳转地址
                ImageUrl.add(imgSummary[i].split(",")[0]);
                ImageIntent.add(imgSummary[i].split(",")[1]);
            }
            list_head.clear();//清除数据
            initSimleDraweeView();//创建网络加载imageview
            if(imgCount>0){//至少一张图片
                setImage();//多张数据
            }
        }else{//无图
            adapter=new NewsSupportAdapter(getActivity());
            lv.addHeaderView(head);
            lv.setAdapter(adapter);
            AVLoading();//转圈圈 显示标题
        }
        if(args.getList()!=null){
            totalPages=Integer.parseInt(args.getPageSize());//总页数
            list.clear();
            list=args.getList();
            adapter.LoadData(list);
        }
    }

    //缓存json
    private void AcacheJson() {
        ACacheInterface.getNewLists service=ACacheHelper.RETROFIT.create(ACacheInterface.getNewLists.class);
        service.getNewList("3,6,-1,88,4,7",LoginSPUtils.getInt("id",0),1,20,false).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
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
                    ACacheHelper.put("NewsSupport", responseBody.string());
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
        String jsonStr=ACacheHelper.getAsString("NewsSupport");
        NewsModel model=new Gson().fromJson(jsonStr,NewsModel.class);
        NetDataTotal(model);
    }

    private void initSimleDraweeView() {
        //轮播图不缓存
//        ImagePipeline imagePipeline = Fresco.getImagePipeline();
//        imagePipeline.clearCaches();
        for(int i=0;i<imgCount;i++){//添加数据根据接口 添加网络图片
            SimpleDraweeView iv=new SimpleDraweeView(getActivity());
            LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            iv.setLayoutParams(params);
            //请求网络图片展示到ImageView
            iv.setImageURI(ImageUrl.get(i));//fresco加载网络图片
            list_head.add(iv);
        }
    }

    private void AVLoading() {
        AVLoading.setVisibility(View.GONE);
        getActivity().findViewById(R.id.app_news_tablayout).setVisibility(View.VISIBLE);
    }

    private void initListener() {
        head_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(ivs!=null&&ivs.length>0){
                    for(int i=0;i<imgCount;i++){
                        ivs[i].setImageResource(R.mipmap.icon_white);
                    }
                    ivs[position%ImageIntent.size()].setImageResource(R.mipmap.icon_pre);
                }
            }
            @Override
            public void onPageSelected(int position) {}
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        materialRefresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                    materialRefresh.setLoadMore(true);
                    downRefresh();
            }
            @Override
            public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
                    if (currPage < totalPages) {
                        super.onRefreshLoadMore(materialRefreshLayout);
                        refreshData();
                    } else {
                        ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                        materialRefresh.setLoadMore(false);
                        if(lv.getFooterViewsCount()==0){
                            lv.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.numlibrary_foot,null));
                        }
                    }
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
                Intent intent=new Intent(getActivity(),NewsSummaryActivity.class);
                intent.putExtra("id",list.get(position-1).getId());
                SingletonNewsGray.registerService(list.get(position-1).getId());
                startActivity(intent);
            }
        });
    }
    //刷新上拉加载数据
    public void refreshData(){
//        if(!CommonUtil.isNetworkConnected(getActivity())){
//            TipsToast.showTips("请检查网络设置");
//            materialRefresh.finishRefreshLoadMore();
//            AVLoading.setVisibility(View.GONE);
//            netOff.setVisibility(View.VISIBLE);
//            netOff.setOnClickListener((v -> refreshData()));
//            return;
//        }
        NetHelper.LOTTERY_API.getNewsList("3,6,-1,88,4,7",LoginSPUtils.getInt("id",0),++currPage,20,false,false)
                .doOnTerminate(() -> materialRefresh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
            @Override
            public void onNext(NewsModel args) {
                super.onNext(args);
                if(args.getList()!=null&&args.getList().size()>0){
                    netOff.setVisibility(View.GONE);
                    list.addAll(args.getList());
                    adapter.ReLoadData(args.getList());
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);//转圈圈
                materialRefresh.finishRefreshLoadMore();
                TipsToast.showTips("请检查网络设置");
            }
        });
    }
    //下拉刷新
    public void downRefresh(){
//        if(!CommonUtil.isNetworkConnected(getActivity())){
//            TipsToast.showTips("请检查网络设置");
//            materialRefresh.finishRefresh();
//            AVLoading.setVisibility(View.GONE);
//            netOff.setVisibility(View.VISIBLE);
//            netOff.setOnClickListener((v -> downRefresh()));
//            return;
//        }
        currPage=1;
        NetHelper.LOTTERY_API.getNewsList("3,6,-1,88,4,7,4",LoginSPUtils.getInt("id",0),currPage,20,false,false)
                .doOnTerminate(() -> materialRefresh.finishRefresh())
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
                    @Override
                    public void onNext(NewsModel args) {
                        super.onNext(args);
                        netOff.setVisibility(View.GONE);
                            if (args.getList() != null && args.getList().size() > 0) {
                                list.clear();
                                list.addAll(args.getList());
                                adapter.LoadData(list);
                            }
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
    //控制器数据
    private void initIcon() {
        ivs=new ImageView[imgCount];
        LinearLayout layout=(LinearLayout)head. findViewById(R.id.news_support_head_ll);
        for(int i=0;i<imgCount;i++){
            ivs[i]=new ImageView(getActivity());
            LayoutParams params=new LayoutParams(DisplayUtil.dip2px(20),DisplayUtil.dip2px(2));
            params.setMargins(0, 0, DisplayUtil.dip2px(10), 0);
            ivs[i].setLayoutParams(params);
            ivs[i].setImageResource(R.mipmap.icon_white);
            layout.addView(ivs[i]);
        }
        ivs[0].setImageResource(R.mipmap.icon_pre);
    }

    private void initData() {
        ViewPagerScroller scroller = new ViewPagerScroller(getActivity());
        scroller.setScrollDuration(1000);
        scroller.initViewPagerScroll(head_vp);//设置viewpager自动轮播滚动过度速度1秒
        head_vp.setOffscreenPageLimit(imgCount-1);//全部缓存
        head_adapter=new ViewPagerPlayAdapter(list_head,ImageIntent,getActivity());
        head_vp.setAdapter(head_adapter);//头部viewpager数据
        adapter=new NewsSupportAdapter(getActivity());
        lv.addHeaderView(head);
        lv.setAdapter(adapter);
    }

    private void initView(View view) {
        ImageUrl=new ArrayList<>();//图片地址
        ImageIntent=new ArrayList<>();//图片跳转地址
        list=new ArrayList<>();//数据源
        list_head=new ArrayList<>();
        netOff = (ImageView)view. findViewById(R.id.netOff);
        lv = (ListView)view. findViewById(R.id.news_support_lv);
        head=LayoutInflater.from(getActivity()).inflate(R.layout.news_support_head,null);
        head_vp= (ViewPager) head.findViewById(R.id.news_support_head_vp);
        AVLoading = (AVLoadingIndicatorView) view.findViewById(R.id.AVLoadingIndicator);
        materialRefresh = (MaterialRefreshLayout)view. findViewById(R.id.RefreshLayout);
        materialRefresh.setProgressColors(new int[]{ColorUtils.MID_BLUE});
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeMessages(1);//清空handler
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
    //重新开启
    public void startHandler(){
        handler.sendEmptyMessageDelayed(1, 3000);//开启轮播
    }

    //轮播图滑动空白bug
    public void setImage(){
        if(imgCount==1){//1张图不开启轮播 不加载控制器
            initData();//展示viewpager数据
            AVLoading();//转圈圈 显示标题
        }else{
            initSimleDraweeView();
            setAdapterData();
            head_vp.setCurrentItem(Integer.MAX_VALUE/2-Integer.MAX_VALUE/2%list_head.size());//设置初始position为最大值中间并且从0位置开始
            handler.sendEmptyMessageDelayed(1, 3000);//开启轮播
        }
    }
    //展示数据
    private void setAdapterData() {
        initIcon();//控制器
        initData();//展示viewpager数据
        AVLoading();//转圈圈 显示标题
    }

}
