package com.cp2y.cube.activity.h5trends;

import android.app.AlertDialog;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Arrays;

/**
 * 特别号码走势图包含 福建22选5
 */
public class Fujian22xuan5TrendPicActivity extends BaseActivity {
    protected ConditionModel condition;
    private int[] select ;//默认选中值
    private AlertDialog settingDialog = null;
    private int posTrend;
    private String lotteryName;
    private ProgressBar progressBar;
    private PopupWindow popupWindow = null;
    private String[] title_array = {"号码走势", "定位走势", "跨度走势", "除3余走势", "和值走势", "奇偶走势", "大小走势", "和尾数走势"};
    private ImageView pop_iv;
    private ImageView netOff;
    private WebView web;
    private AVLoadingIndicatorView AVLoading;
    private int selectCount=1;//选择条件的个数
    private SparseIntArray targets=new SparseIntArray(){{put(2,10013);put(3,10022);put(4,10002);put(5,10019);put(6,10004);put(7,10027);}};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hd15xuan5_trend_pic);
        posTrend = getIntent().getIntExtra("pos", 0);
        lotteryName=getIntent().getStringExtra("lotteryName");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((view -> finish()));
        setMainTitle(lotteryName+"-"+title_array[posTrend]);
        findViewById(R.id.trend_ignore).setVisibility(View.GONE);
        condition= new ConditionModel();
        setDefaultSelect();//默认设置辅助功能
        RelativeLayout layout= (RelativeLayout) findViewById(R.id.trend_title_layout);
        layout.setOnClickListener((v1 -> showSubTitles()));
        pop_iv = (ImageView) findViewById(R.id.trend_pop_iv);
        netOff = (ImageView) findViewById(R.id.netOff);//断网
        web=(WebView)findViewById(R.id.trend_container) ;
        progressBar = (ProgressBar) findViewById(R.id.myProgressBar);
        initNetOff();
        selectGuide();
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(Fujian22xuan5TrendPicActivity.this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            web.setVisibility(View.GONE);
            TipsToast.showTips("请检查网络设置");
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            initData();
            web.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
    }

    //默认设置
    private void setDefaultSelect() {
        if(posTrend==0){//基本号码走势
            selectCount=2;
            select=new int[]{4,6};
            condition.setShowPartLine(true);//分区线
            condition.setShowMiss(true);//遗漏
        }else if(posTrend==1){//定位走势
            selectCount=3;
            select=new int[]{0,2,4};
            condition.setShowFoldLine(true);//折叠线
            condition.setShowPartLine(true);//分区线
            condition.setShowMiss(true);//遗漏
        }else if(posTrend==4){//指标走势 和值
            selectCount=1;
            select=new int[]{0};
            condition.setShowFoldLine(true);//折叠线
            condition.setShowMiss(false);//遗漏 不显示
            condition.setShowBarChart(false);//遗漏柱图不显示
        }else{
            selectCount=2;
            select=new int[]{0,3};
            condition.setShowFoldLine(true);//折叠线
            condition.setShowMiss(true);//遗漏
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_trend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (settingDialog == null) {//默认值
            AlertDialog.Builder builder = new AlertDialog.Builder(Fujian22xuan5TrendPicActivity.this);
            //获取自定义的VIew
            View trendNumView=LayoutInflater.from(Fujian22xuan5TrendPicActivity.this).inflate(R.layout.dialog_3d_numtrend, null);
            //添加自定义VIew
            builder.setView(trendNumView);
            settingDialog = builder.create();
            ImageView iv_support= (ImageView) trendNumView.findViewById(R.id.dialog_numTrend_ivSupport);
            TextView tv_tip= (TextView) trendNumView.findViewById(R.id.dialog_numTrend_shuoming);
            iv_support.setVisibility(View.GONE);
            tv_tip.setText("");
            int[] pos = new int[]{0};
            RadioGroup radioGroup = (RadioGroup) trendNumView.findViewById(R.id.dialog_numTrend_rg);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.trend_dialog_rg1:
                            pos[0] =0;
                            break;
                        case R.id.trend_dialog_rg2:
                            pos[0] =1;
                            break;
                        case R.id.trend_dialog_rg3:
                            pos[0] =2;
                            break;
                        case R.id.trend_dialog_rg4:
                            pos[0] =3;
                            break;
                        case R.id.trend_dialog_rg5:
                            pos[0] =4;
                            break;
                    }
                }
            });
            radioGroup.check(R.id.trend_dialog_rg1);
            RadioButton[] buttons = new RadioButton[] {
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_dateAll),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date1),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date2),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date3),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date4),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date5),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date6),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date7),
            };
            //初始值100
            final int[] pos2 = new int[]{0};
            buttons[0].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton , checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            pos2[0] =i;
                            continue;
                        }
                        button.setChecked(false);
                    }
                }
            };
            for (RadioButton button: buttons) {
                button.setOnCheckedChangeListener(listener);
            }
//            RadioGroup weeks = (RadioGroup) trendNumView.findViewById(R.id.dialog_numTrend_WeekCount_rg);
//            for (int i = 0; i < weeks.getChildCount(); i++) {
//                weeks.getChildAt(i).setId(R.id.startId + i);
//            }
//            weeks.check(R.id.startId);
            //点击弹出说明
            iv_support.setOnClickListener(v ->{
                Object tag = v.getTag();
                tv_tip.setVisibility(tag == null ? View.VISIBLE : View.GONE);
                iv_support.setBackgroundResource(tag == null ? R.mipmap.icon_shuoming_pre : R.mipmap.icon_shuoming);
                tag = tag == null ? "": null;
                v.setTag(tag);
            });
            MyGridView gv_support= (MyGridView) trendNumView.findViewById(R.id.dialog_numTrend_suopprt_gv);
            MyTrendDialogSupportAdapter adapter = new MyTrendDialogSupportAdapter(Arrays.asList(setConditionData()),Fujian22xuan5TrendPicActivity.this,R.layout.item_trendsupport);
            for (int s:select) {
                adapter.addSelect(s);
            }
            gv_support.setAdapter(adapter);
            //辅助线点击事件
            gv_support.setOnItemClickListener((parent, view, position, id) ->adapter.replaceSelect(position));
            //点击说明隐藏
            tv_tip.setOnClickListener(v -> iv_support.performClick());
            trendNumView.findViewById(R.id.NegativeButton).setOnClickListener(v -> settingDialog.dismiss());//取消
            trendNumView.findViewById(R.id.PositiveButton).setOnClickListener(v -> {//点击确定的时候改变筛选条件
                condition.setPeriodH5(MapUtils.TREND_PERIOD[pos[0]]);
                condition.setWeekDay(MapUtils.D3_TREND_WEEK[pos2[0]]);
                onConditionChanged(adapter);
                reloadData();
                settingDialog.dismiss();
            });//确定
        } else {//非默认,根据当前条件加载
            RadioGroup radioGroup = (RadioGroup) settingDialog.findViewById(R.id.dialog_numTrend_rg);
            ((RadioButton)radioGroup.getChildAt(MapUtils.TREND_PERIOD_MAP.get(condition.getPeriodH5()))).setChecked(true);
            RadioButton[] buttons = new RadioButton[] {
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_dateAll),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date1),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date2),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date3),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date4),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date5),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date6),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date7),
            };
            buttons[MapUtils.TREND_D3_WEEK_MAP.get(condition.getWeekDay())].setChecked(true);
            MyGridView gv_support = (MyGridView) settingDialog.findViewById(R.id.dialog_numTrend_suopprt_gv);
            MyTrendDialogSupportAdapter adapter = (MyTrendDialogSupportAdapter) gv_support.getAdapter();
            onBeforeSettingShow(adapter);
            adapter.notifyDataSetChanged();
        }
        settingDialog.show();
        return true;
    }
    //更换条件更换走势图
    private void reloadData(){
        if(!CommonUtil.isNetworkConnected(Fujian22xuan5TrendPicActivity.this)){//断网机制
            netOff.setVisibility(View.VISIBLE);
            TipsToast.showTips("请检查网络设置");
            web.setVisibility(View.GONE);
            netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            web.setVisibility(View.VISIBLE);
            netOff.setVisibility(View.GONE);
        }
        web.loadUrl(getURL(posTrend));
    }
    //第一次加载设置
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

        web.getSettings().setSupportZoom(true);//缩放
        web.getSettings().setBuiltInZoomControls(true);
        web.getSettings().setDisplayZoomControls(false);//不显示控制器
        //自适应屏幕
//        web.getSettings().setUseWideViewPort(true);
//        web.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        web.getSettings().setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            web.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        web.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    web.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.INVISIBLE);
                } else {
                    web.setVisibility(View.GONE);
                    if (View.INVISIBLE == progressBar.getVisibility()) {
                        //progressBar.setVisibility(View.VISIBLE);
                    }
                    //progressBar.setProgress(newProgress);
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
                return true;
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if(error.getPrimaryError() == SslError.SSL_INVALID ){// 校验过程遇到了bug
                    handler.proceed();
                }else{
                    handler.cancel();
                }  //接受信任所有网站的证书
            }
        });
        web.loadUrl(getURL(posTrend));
    }
    //新手引导
    public void selectGuide(){
        if(GuideFlag.isFirst_Guide_H5){
            setGuideDialog("trendSet",R.mipmap.trend_set_guide);
            GuideFlag.isFirst_Guide_H5=false;
        }
    }

    //弹窗处理
    public void showSubTitles() {
        if (popupWindow == null) {
            View contentView = LayoutInflater.from(Fujian22xuan5TrendPicActivity.this)
                    .inflate(R.layout.trend_15xuan5pop, null);
            RadioButton[] buttons = new RadioButton[]{
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbNum),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbDingwei),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbKuadu),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbChu3yu),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbSum),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbJiou),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbDaXiao),
                    (RadioButton) contentView.findViewById(R.id.trend_pop_rbSumMantissa)
            };
            buttons[posTrend].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton, checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            initCondition(i);//初始化设置 改变postrend
                            reloadData();
                            //切换判断新手引导
                            selectGuide();
                            setMainTitle(lotteryName+"-"+title_array[i]);
                            showSubTitles();
                            continue;
                        }
                        button.setChecked(false);
                    }
                }
            };
            for (RadioButton button : buttons) {
                button.setOnCheckedChangeListener(listener);
            }
            popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            //popupWindow.setAnimationStyle(R.style.popwin_anim_style);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setContentView(contentView);
        }
        if (popupWindow.isShowing()) {
            pop_iv.setImageResource(R.mipmap.icon_xiaola);
            popupWindow.dismiss();
        } else {
            //展示在toolbar下方
            pop_iv.setImageResource(R.mipmap.icon_xiala_top);
            popupWindow.showAsDropDown(getToolBar(), 0, 0);
            //popupWindow.showAtLocation(getToolBar(), Gravity.TOP, 0, 250);
        }
    }

    public void ClosePop() {
        if(popupWindow==null)return;
        popupWindow.dismiss();
    }
    //改变设置
    public void onConditionChanged(MyTrendDialogSupportAdapter adapter) {
        if(posTrend==0){
            condition.setShowMutiNum(adapter.containSelect(0));//重号
            condition.setShowConnectNum(adapter.containSelect(1));//连号
            condition.setShowEdgeNum(adapter.containSelect(2));//边号
            condition.setShowSerialNum(adapter.containSelect(3));//串号
            condition.setShowPartLine(adapter.containSelect(4));//分区线
            condition.setShowBurstLine(adapter.containSelect(5));//分段线
            condition.setShowMiss(adapter.containSelect(6));//折叠线
            condition.setShowBarChart(adapter.containSelect(7));//柱图
        }else if(posTrend==1){
            condition.setShowFoldLine(adapter.containSelect(0));//折叠线
            condition.setShowAvgLine(adapter.containSelect(1));//平均线
            condition.setShowPartLine(adapter.containSelect(2));//分区线
            condition.setShowBurstLine(adapter.containSelect(3));//分段线
            condition.setShowMiss(adapter.containSelect(4));//遗漏
            condition.setShowBarChart(adapter.containSelect(5));//遗漏柱图
        }else if(posTrend==4){//指标
            condition.setShowFoldLine(adapter.containSelect(0));//折叠线
            condition.setShowAvgLine(adapter.containSelect(1));//平均线
            condition.setShowBurstLine(adapter.containSelect(2));//分段线
        }else{
            condition.setShowFoldLine(adapter.containSelect(0));//折叠线
            condition.setShowAvgLine(adapter.containSelect(1));//平均线
            condition.setShowBurstLine(adapter.containSelect(2));//分段线
            condition.setShowMiss(adapter.containSelect(3));//遗漏
            condition.setShowBarChart(adapter.containSelect(4));//遗漏柱图
        }
        selectCount=adapter.getSelectCount();//选择设置的个数
    }
    //设置之前显示
    public void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter) {
        if(posTrend==0){
            adapter.setSelect(0, condition.isShowMutiNum());
            adapter.setSelect(1, condition.isShowConnectNum());
            adapter.setSelect(2, condition.isShowEdgeNum());
            adapter.setSelect(3, condition.isShowSerialNum());
            adapter.setSelect(4, condition.isShowPartLine());
            adapter.setSelect(5, condition.isShowBurstLine());
            adapter.setSelect(6, condition.isShowMiss());
            adapter.setSelect(7, condition.isShowBarChart());
        }else if(posTrend==1){
            adapter.setSelect(0, condition.isShowFoldLine());
            adapter.setSelect(1, condition.isShowAvgLine());
            adapter.setSelect(2, condition.isShowPartLine());
            adapter.setSelect(3, condition.isShowBurstLine());
            adapter.setSelect(4, condition.isShowMiss());
            adapter.setSelect(5, condition.isShowBarChart());
        }else if(posTrend==4){//和值
            adapter.setSelect(0, condition.isShowFoldLine());
            adapter.setSelect(1, condition.isShowAvgLine());
            adapter.setSelect(2, condition.isShowBurstLine());
        }else{
            adapter.setSelect(0, condition.isShowFoldLine());
            adapter.setSelect(1, condition.isShowAvgLine());
            adapter.setSelect(2, condition.isShowBurstLine());
            adapter.setSelect(3, condition.isShowMiss());
            adapter.setSelect(4, condition.isShowBarChart());
        }
    }
    public String getURL(int position){
        StringBuilder stringBuilder=new StringBuilder();
        if(position==0){
            stringBuilder.append(NetConst.dynamicBaseUrlForH5()).append("/chartH5View?lotteryId=10065").append("&functionMenu=111").append("&show="+condition.getPeriodH5()+"&week="+condition.getWeekDay());
            if(selectCount>0) {//设置了辅助
                stringBuilder.append("&operlist=");
                    if (condition.isShowMutiNum()) {//重号
                        stringBuilder.append("|chong");
                    }
                    if (condition.isShowConnectNum()) {//连号
                        //+"&operlist=chong|lian|bian|chuan|fqx|fdx|yl|ylz"
                        stringBuilder.append("|lian");
                    }
                    if (condition.isShowEdgeNum()) {//边号
                        stringBuilder.append("|bian");
                    }
                    if (condition.isShowSerialNum()) {//串号
                        stringBuilder.append("|chuan");
                    }
                    if (condition.isShowPartLine()) {//分区线
                        stringBuilder.append("|fqx");
                    }
                    if (condition.isShowBurstLine()) {//分段线
                        stringBuilder.append("|fdx");
                    }
                    if (condition.isShowMiss()) {//遗漏
                        stringBuilder.append("|yl");
                    }
                    if (condition.isShowBarChart()) {//柱图
                        stringBuilder.append("|ylz");
                    }
            }
        }else if(position==1){//定位走势
            stringBuilder.append(NetConst.dynamicBaseUrlForH5()).append("/chartH5View?lotteryId=10065").append("&functionMenu=112").append("&show="+condition.getPeriodH5()+"&week="+condition.getWeekDay());
            if(selectCount>0) {//设置了辅助
                stringBuilder.append("&operlist=");
                    if(condition.isShowFoldLine()){//折叠线
                        stringBuilder.append("|zdx");
                    }
                    if(condition.isShowAvgLine()){//平均线
                        stringBuilder.append("|pjx");
                    }
                    if(condition.isShowPartLine()){//分区线
                        stringBuilder.append("|fqx");
                    }
                    if(condition.isShowBurstLine()){//分段线
                        stringBuilder.append("|fdx");
                    }
                    if(condition.isShowMiss()){//遗漏
                        stringBuilder.append("|yl");
                    }
                    if(condition.isShowBarChart()){//柱图
                        stringBuilder.append("|ylz");
                    }
                }
        }else{//指标走势
            stringBuilder.append(NetConst.dynamicBaseUrlForH5()).append("/chartH5TargetView?lotteryId=10065").append("&targetId="+targets.get(posTrend)).append("&show="+condition.getPeriodH5()+"&week="+condition.getWeekDay());
            if(selectCount>0) {//设置了辅助
                stringBuilder.append("&operlist=");
                    if(condition.isShowFoldLine()){//折叠线
                        stringBuilder.append("|zdx");
                    }
                    if(condition.isShowAvgLine()){//平均线
                        stringBuilder.append("|pjx");
                    }
                    if(condition.isShowBurstLine()){//分段线
                        stringBuilder.append("|fdx");
                    }
                    if(condition.isShowMiss()){//遗漏
                        stringBuilder.append("|yl");
                    }
                    if(condition.isShowBarChart()){//柱图
                        stringBuilder.append("|ylz");
                    }
                }
        }
        return stringBuilder.toString();
    }
    public String[] setConditionData(){
        if(posTrend==0){//号码
            return MapUtils.HD15X5_NUM_SUPPORT;
        }else if(posTrend==1){//定位
            return MapUtils.HD15X5_LOCATION_SUPPORT;
        }else if(posTrend==4){//指标
            return MapUtils.HD15X5_SUM_SUPPORT;
        }else{
            return MapUtils.HD15X5_TARGET_SUPPORT;
        }
    }
    //初始化设置
    public void initCondition(int pos){
        condition= new ConditionModel();//初始化数据
        settingDialog=null;
        posTrend=pos;//改变页面位置
        setDefaultSelect();//初始化设置
    }
}
