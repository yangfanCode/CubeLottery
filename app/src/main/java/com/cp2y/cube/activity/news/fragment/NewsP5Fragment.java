package com.cp2y.cube.activity.news.fragment;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.news.LotteryNewsDetailActivity;
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

public class NewsP5Fragment extends NewsBaseFragment {
    private int totalPages=0,currPage=1;//总页数
    private List<NewsModel.News> list=new ArrayList<>();
    private NewsAdapter adapter;
    private ListView lv;
    private ImageView netOff;
    private View lottery_head;//头布局
    private AVLoadingIndicatorView AVLoading;
    private MaterialRefreshLayout MateriaRefresh;
    private RelativeLayout head_layout;
    private Button btn_news,btn_prodiect,btn_skill;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_news_layout, container, false);
        //初始化
        initView(view);
        initData();
        initNetOff();
        initListener();
        return view;
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(getActivity())){//断网机制
            if(!TextUtils.isEmpty(ACacheHelper.getAsString("NewsP5"))){
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
            if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
            Intent intent=new Intent(getActivity(), NewsSummaryActivity.class);
            intent.putExtra("id",list.get(position-1).getId());
            SingletonNewsGray.registerService(list.get(position-1).getId());
            startActivity(intent);
        }));
        btn_news.setOnClickListener((v -> IntentDetail(9)));
        btn_prodiect.setOnClickListener((v -> IntentDetail(10)));
        btn_skill.setOnClickListener((v -> IntentDetail(11)));
    }

    //跳转到新闻列表
    private void IntentDetail(int pos) {
        Intent intent=new Intent(getActivity(), LotteryNewsDetailActivity.class);
        intent.putExtra("pos",pos);
        startActivity(intent);
    }

    private void initNet() {
        currPage=1;
        NetHelper.LOTTERY_API.getNewsList("7", LoginSPUtils.getInt("id",0),currPage,20,false,true)
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
    //第一次请求网络展示数据
    private void NetDataTotal(NewsModel args) {
        if(!TextUtils.isEmpty(args.getPageSize())){
            totalPages=Integer.parseInt(args.getPageSize());
        }
        if (args.getList() != null && args.getList().size() > 0) {
            setHead(args);//头部数据
            list.clear();
            list.addAll(args.getList());
            adapter.LoadData(args.getList());
            lv.setVisibility(View.VISIBLE);
        }
        AVLoading.setVisibility(View.GONE);//转圈圈
    }

    //缓存json
    private void AcacheJson() {
        ACacheInterface.getNewLists service= ACacheHelper.RETROFIT.create(ACacheInterface.getNewLists.class);
        service.getNewList("7", LoginSPUtils.getInt("id",0),0,20,true).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
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
                    ACacheHelper.put("NewsP5", responseBody.string());
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
        String jsonStr=ACacheHelper.getAsString("NewsP5");
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
        NetHelper.LOTTERY_API.getNewsList("7", LoginSPUtils.getInt("id",0),++currPage,20,false,true)
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
    private void initData() {
        adapter=new NewsAdapter(getActivity());
        lv.addHeaderView(lottery_head);
        lv.setAdapter(adapter);
    }

    private void initView(View view) {
        lv = (ListView)view. findViewById(R.id.news_lv);
        netOff = (ImageView)view. findViewById(R.id.netOff);
        lottery_head=LayoutInflater.from(getActivity()).inflate(R.layout.news_lottery_head,null);
        head_layout = (RelativeLayout)lottery_head. findViewById(R.id.news_lottery_head_layout);
        AVLoading = (AVLoadingIndicatorView)view. findViewById(R.id.AVLoadingIndicator);
        MateriaRefresh = (MaterialRefreshLayout)view. findViewById(R.id.RefreshLayout);
        MateriaRefresh.setProgressColors(new int[]{ColorUtils.MID_BLUE});
        btn_news = (Button)lottery_head. findViewById(R.id.news_lottery_btnNews);
        btn_prodiect = (Button)lottery_head. findViewById(R.id.news_lottery_btnPridiect);
        btn_skill = (Button)lottery_head. findViewById(R.id.news_lottery_btnSkill);
    }
    //设置头布局数据
    public void setHead(NewsModel args){
        head_layout.removeAllViews();
        Typeface face = Typeface.createFromAsset (getActivity().getAssets() , "fonts/futura_condensed.ttf" );
        if("-1".equals(args.getPrice())){//未中奖
            View nowinView=LayoutInflater.from(getActivity()).inflate(R.layout.news_p5_head_nowin,null);
            head_layout.addView(nowinView);
            LinearLayout draw_ll= (LinearLayout) nowinView.findViewById(R.id.news_double_head_drawNum_ll);
            SimpleDraweeView iv= (SimpleDraweeView) nowinView.findViewById(R.id.news_double_head_nowin_iv);
//            iv.setImageURI(args.getCidPicture());//fresco加载网络背景图片
            iv.setImageResource(R.mipmap.bg_kaijianghaoma);
            TextView tv_issue= (TextView) nowinView.findViewById(R.id.news_double_head_issue);//奖期
            TextView tv_buyTotal= (TextView) nowinView.findViewById(R.id.news_double_tv_buyTotal);//投注总额
            TextView tv_remainTotal= (TextView) nowinView.findViewById(R.id.news_double_tv_remainTotal);//奖池累积
            tv_issue.setText(args.getIssue());//奖期
            tv_buyTotal.setTypeface(face);//字体
            tv_buyTotal.setText(("-1".equals(args.getBuyTotal())||("0".equals(args.getBuyTotal()))?"--":args.getBuyTotal()));//投注总额
            tv_remainTotal.setTypeface(face);
            tv_remainTotal.setText(("-1".equals(args.getRemainTotal())||("0".equals(args.getRemainTotal()))?"--":args.getRemainTotal()));//奖池累积
            //开奖号码
            String[] red=args.getDrawNumber().split(",");//红球
            for(int i=0;i<draw_ll.getChildCount();i++){
                View child=draw_ll.getChildAt(i);
                if(child instanceof TextView){
                    ((TextView)child).setTypeface(face);
                    ((TextView)child).setText(red[i]+" ");
                }
            }
        }else{//中奖
            View winView=LayoutInflater.from(getActivity()).inflate(R.layout.news_p5_head_win,null);
            head_layout.addView(winView);
            LinearLayout draw_ll= (LinearLayout) winView.findViewById(R.id.news_double_head_drawNum_ll);
            SimpleDraweeView iv= (SimpleDraweeView) winView.findViewById(R.id.news_double_head_win_iv);
//            iv.setImageURI(args.getCidPicture());//fresco加载网络背景图片
            iv.setImageResource(R.mipmap.bg_haomakuzhongjiang);
            TextView tv_issue= (TextView) winView.findViewById(R.id.news_double_head_issue);//奖期
            TextView tv_issue2= (TextView) winView.findViewById(R.id.news_double_head_issue2);//奖期
            TextView tv_noteNum= (TextView) winView.findViewById(R.id.news_double_head_noteNum);//注数
            TextView tv_price= (TextView) winView.findViewById(R.id.news_double_head_price);//金额
            TextView count= (TextView) winView.findViewById(R.id.news_double_head_win_count);//文字注
            TextView yuan= (TextView) winView.findViewById(R.id.news_double_head_win_yuan);//文字元
            tv_issue.setText(args.getIssue());//奖期
            tv_issue2.setText(args.getIssue());//奖期
            tv_noteNum.setTypeface(face);//字体
            tv_price.setTypeface(face);//字体
            if(args.getNoteNum()>=10000){//变单位
                tv_noteNum.setText(setLargeCount(args.getNoteNum()));//注数
                count.setText("万注");
            }else{
                tv_noteNum.setText(String.valueOf(args.getNoteNum()));//注数
                count.setText("注");
            }
            if(Long.parseLong(args.getPrice())>100000000){//变单位
                tv_price.setText(setLargeMoney(Long.parseLong(args.getPrice())));//金额
                yuan.setText("亿元");
            }else{
                tv_price.setText(args.getPrice());//金额
                yuan.setText("元");
            }
            //开奖号码
            String[] red=args.getDrawNumber().split(",");//红球
            for(int i=0;i<draw_ll.getChildCount();i++){
                View child=draw_ll.getChildAt(i);
                if(child instanceof TextView){
                    ((TextView)child).setTypeface(face);
                    ((TextView)child).setText(red[i]+" ");
                }
            }
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if(adapter!=null) {
            adapter.notifyDataSetChanged();
        }
    }
}
