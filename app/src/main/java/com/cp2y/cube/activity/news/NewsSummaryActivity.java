package com.cp2y.cube.activity.news;

import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.NewsDetailModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

//资讯详情页面
public class NewsSummaryActivity extends BaseActivity {

    private WebView web;
    private String id="",url="";
    private ProgressBar progressBar;
    private String shareTitle="";
    private String content="";
    private ImageView iv;
    private RelativeLayout failed_layout;
    private boolean isViewPagerPlay=false;//是否从轮播图点进来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_summary);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        setMainTitle("详情");
        if(!TextUtils.isEmpty(getIntent().getStringExtra("id"))){
            id=getIntent().getStringExtra("id");
            isViewPagerPlay=false;
        }
        if(!TextUtils.isEmpty(getIntent().getStringExtra("url"))){//轮播图点击 加载全地址
            url=getIntent().getStringExtra("url");
            isViewPagerPlay=true;
        }
        initView();
        initNet();
        initData();

    }
    public void NetFailed(){
        failed_layout.setVisibility(View.VISIBLE);
        iv.setOnClickListener((v -> {
            initNet();
            initData();
        }));
    }
    private void initData() {
        //重新设置
        web.getSettings().setDefaultTextEncodingName("UTF-8");
        web.getSettings().setJavaScriptEnabled(true);
        web.getSettings().setDomStorageEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        web.getSettings().setDatabaseEnabled(true);
        web.getSettings().setAllowFileAccess(true);
        web.getSettings().setAppCacheEnabled(true);
        web.getSettings().setAllowUniversalAccessFromFileURLs(true);

        //web.getSettings().setSupportZoom(true);//缩放
        //web.getSettings().setBuiltInZoomControls(true);//显示控制器
//        web.getSettings().setUseWideViewPort(true);
////自适应屏幕
//        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        web.getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == progressBar.getVisibility()) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    progressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);

            }
        });
        //webview点击相关阅读 刷新url
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                view.loadUrl(url);
                return true;//true自己处理跳转 刷新url false webview处理跳转
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(error.getPrimaryError() == android.net.http.SslError.SSL_INVALID ){// 校验过程遇到了bug
                    handler.proceed();
                }else{
                    handler.cancel();
                }  //接受信任所有网站的证书
            }
        });
        if(isViewPagerPlay){
            web.loadUrl(url);
        }else{
            web.loadUrl(NetConst.NEWS_DETAIL.concat(id));
        }

    }

    private void initView() {
        web = (WebView) findViewById(R.id.news_detail_web);
        progressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        iv= (ImageView) findViewById(R.id.news_summary_fail_iv);//断网界面
        failed_layout = (RelativeLayout) findViewById(R.id.news_summary_fail_layout);
    }

    private void initNet() {
        if(!CommonUtil.isNetworkConnected(this)){
            TipsToast.showTips("请检查网络设置");
            NetFailed();//断网显示失败图片
            return;
        }
        failed_layout.setVisibility(View.GONE);
        NetHelper.LOTTERY_API.getNewsDetailList(id).subscribe(new SafeOnlyNextSubscriber<NewsDetailModel>(){
            @Override
            public void onNext(NewsDetailModel args) {
                super.onNext(args);
                if(args.getTitle()!=null){
                    shareTitle=args.getTitle();
                    content=args.getContent();
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_news_detail,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        UMImage image = new UMImage(NewsSummaryActivity.this, R.mipmap.logo108_108);
        UMWeb web = new UMWeb(NetConst.NEWS_DETAIL.concat(id));
        web.setTitle(shareTitle);//标题
        web.setThumb(image);  //缩略图
        web.setDescription(content);
        ShareBoardConfig config = new ShareBoardConfig();
        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
        config.setCancelButtonVisibility(false);
        config.setIndicatorVisibility(false);
        new ShareAction(NewsSummaryActivity.this)
                .withMedia(web)
                .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(umShareListener).open(config);
        return false;
    }
}
