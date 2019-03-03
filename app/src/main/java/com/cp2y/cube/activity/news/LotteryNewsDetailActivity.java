package com.cp2y.cube.activity.news;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
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
import com.cp2y.cube.widgets.HVListView;
import com.cp2y.cube.widgets.listener.ScrollViewListener;
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

public class LotteryNewsDetailActivity extends BaseActivity {
    private int flag_pos=-1;//判断彩种 新闻类别
    private int totalPages=0,currPage=1;//总页数
    private List<NewsModel.News> list=new ArrayList<>();
    private View head;//头布局
    private HVListView lv;
    private RelativeLayout title_layout;
    private NewsAdapter adapter;
    private AVLoadingIndicatorView AVLoading;
    private ImageView iv_back;
    private MaterialRefreshLayout MateriaRefresh;
    private TextView title;
    private String[] titleArrays={"新闻资讯","预测分析","技巧大全"};
    private String[] Cid={"21","22","25","40","41","44","26","27","30","45","46","49"};
    private int[] titleColor={ColorUtils.PINK,ColorUtils.BLUEPURPLE,ColorUtils.KHAKI};
    private int[] Imags={R.mipmap.bg_xinwenzixun,R.mipmap.bg_yucefenxi,R.mipmap.bg_jiqiaodaquan};
    private ImageView netOff;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lottery_news_detail);
        flag_pos=getIntent().getIntExtra("pos",0);//双色球0,1,2 大乐透3,4,5,福彩3D6,7,8，排列３９，１０，１１
        initView();
        initNetOff();
        initListener();
    }
    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(LotteryNewsDetailActivity.this)){//断网机制
            if(!TextUtils.isEmpty(ACacheHelper.getAsString("NewsDetail".concat(Cid[flag_pos])))){
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
        lv.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
                int t=lv.getListScrollY();
                titleAnim(t);
            }
        });

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
                    RefreshLoadMore();
                }else{
                    ContextHelper.getApplication().runDelay(materialRefreshLayout::finishRefreshLoadMore, 200);
                    MateriaRefresh.setLoadMore(false);
                    if(lv.getFooterViewsCount()==0){
                        View view=LayoutInflater.from(LotteryNewsDetailActivity.this).inflate(R.layout.numlibrary_long_foot,null);
//                        if(adapter.getCount()<6){
//                            LinearLayout ll= (LinearLayout) view.findViewById(R.id.foot_long_ll);
//                            int[] height={550,600,750,800,950};
//                            LinearLayout.LayoutParams params= (LinearLayout.LayoutParams) ll.getLayoutParams();
//                            params.height=height[height.length-adapter.getCount()];
//                            ll.setLayoutParams(params);
//                        }
                        lv.addFooterView(view);
                    }
                }

            }
        });
        lv.setOnItemClickListener(((parent, view, position, id) -> {
            if(position==0)return;
            if(lv.getFooterViewsCount()==1&&position==lv.getLastVisiblePosition())return;//点击尾部局无效
            Intent intent=new Intent(LotteryNewsDetailActivity.this,NewsSummaryActivity.class);
            intent.putExtra("id",list.get(position-1).getId());
            SingletonNewsGray.registerService(list.get(position-1).getId());
            startActivity(intent);
        }));
    }

    private void initNet() {
        NetHelper.LOTTERY_API.getNewsList(Cid[flag_pos], LoginSPUtils.getInt("id",0),currPage,20,false,false)
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
            }
        });
        AcacheJson();//缓存
    }
    //第一次请求网络展示数据
    private void NetDataTotal(NewsModel args) {
        if(!TextUtils.isEmpty(args.getPageSize())){
            totalPages=Integer.parseInt(args.getPageSize());
        }
        if (args.getList() != null && args.getList().size() > 0) {
            initData(args);//头部数据
            list.clear();
            list.addAll(args.getList());//数据源 点击
            adapter.LoadData(args.getList());
            lv.setVisibility(View.VISIBLE);
        }
        AVLoading.setVisibility(View.GONE);//转圈圈
    }
    //缓存json
    private void AcacheJson() {
        ACacheInterface.getNewLists service= ACacheHelper.RETROFIT.create(ACacheInterface.getNewLists.class);
        service.getNewList(Cid[flag_pos], LoginSPUtils.getInt("id",0),1,20,false).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe(new Subscriber<ResponseBody>() {
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
                    ACacheHelper.put("NewsDetail".concat(Cid[flag_pos]), responseBody.string());
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
        String jsonStr=ACacheHelper.getAsString("NewsDetail".concat(Cid[flag_pos]));
        NewsModel model=new Gson().fromJson(jsonStr,NewsModel.class);
        NetDataTotal(model);
    }
    //上拉加载
    private void RefreshLoadMore(){
        NetHelper.LOTTERY_API.getNewsList(Cid[flag_pos], LoginSPUtils.getInt("id",0),++currPage,20,false,false)
                .doOnTerminate(() -> MateriaRefresh.finishRefreshLoadMore())
                .subscribe(new SafeOnlyNextSubscriber<NewsModel>(){
            @Override
            public void onNext(NewsModel args) {
                super.onNext(args);
                if (args.getList() != null && args.getList().size() > 0) {
                    list.addAll(args.getList());
                    adapter.ReLoadData(args.getList());
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
    private void initData(NewsModel args) {
        SimpleDraweeView icon= (SimpleDraweeView) head.findViewById(R.id.item_lottery_detail_head_iv);
        //icon.setImageURI(args.getCidPicture());//头部图片
        icon.setImageResource(Imags[flag_pos%3]);
        title_layout.setBackgroundColor(titleColor[flag_pos%3]);
        title.setText(titleArrays[flag_pos%3]);
        setSystemBarColor(titleColor[flag_pos%3],false);//状态栏颜色
        adapter=new NewsAdapter(this);
        if(lv.getHeaderViewsCount()!=0){
            lv.removeHeaderView(head);//刷新时刷新头布局
            lv.addHeaderView(head);
        }else{
            lv.addHeaderView(head);
        }
        lv.setAdapter(adapter);
    }

    private void initView() {
        netOff = (ImageView)findViewById(R.id.netOff);
        MateriaRefresh = (MaterialRefreshLayout)findViewById(R.id.RefreshLayout);
        MateriaRefresh.setProgressColors(new int[]{ColorUtils.MID_BLUE});
        lv = (HVListView) findViewById(R.id.Lottery_news_detail_lv);
        head= LayoutInflater.from(this).inflate(R.layout.item_lottery_detail_head,lv,false);
        ImageView head_back= (ImageView) head.findViewById(R.id.news_Lottery_detail_back);
        head_back.setOnClickListener((v1 -> finish()));
        title_layout = (RelativeLayout) findViewById(R.id.news_Lottery_detail_title_layout);
        title_layout.setAlpha(0f);
        AVLoading = (AVLoadingIndicatorView)findViewById(R.id.AVLoadingIndicator);
        iv_back = (ImageView) findViewById(R.id.news_Lottery_detail_back);
        iv_back.setOnClickListener((v -> finish()));
        title = (TextView) findViewById(R.id.news_Lottery_detail_title);
    }
    public  void titleAnim(int y){//800解决快速滑动惯性问题
        if (y < 800) {//y指scrollview滑出屏幕的距离300范围内调整标题透明度滑出300不处理
            float alpha = ((float) y) / 200;
            title_layout.setAlpha(alpha);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }
}
