package com.cp2y.cube.activity.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.activity.CustomUploadActivity;
import com.cp2y.cube.activity.MapActivity;
import com.cp2y.cube.activity.PoiDetailActivity;
import com.cp2y.cube.activity.SettingActivity;
import com.cp2y.cube.activity.calculate.CalculateActivity;
import com.cp2y.cube.activity.multicalc.MultiCalcActivity;
import com.cp2y.cube.activity.news.NewsActivity;
import com.cp2y.cube.activity.news.custom.MyScrollView;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.activity.pushsingle.MyAttentionFansActivity;
import com.cp2y.cube.activity.pushsingle.MyPushSingleActivity;
import com.cp2y.cube.activity.pushsingle.PushSingleActivity;
import com.cp2y.cube.activity.trends.TrendActivity;
import com.cp2y.cube.adapter.MainHomeLotteryAdapter;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.helper.ProvinceHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.MainHomeModel;
import com.cp2y.cube.model.VersionControlModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.utils.LogUtils;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.NavigationUtil;
import com.cp2y.cube.utils.PermissionsChecker;
import com.cp2y.cube.versioncheck.utils.ApkDownloadTools;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class MainActivity extends BaseActivity implements ClosePop {
    private PoiSearch mPoiSearch;
    //定位对象
    public LocationClient locationClient = null;
    private boolean shouldRequest = false;//是否有权限
    private AlertDialog dialog = null;
    private ImageView iv_add, iv_user_add, iv_goCustom;
    private String address = "";
    private boolean isCustom;
    private TextView lotteryShop_name1, lotteryShop_name2, lotteryShop_name3, lotteryShop_diatance1, lotteryShop_diatance2, lotteryShop_diatance3,
            lotteryShop_address1, lotteryShop_address2, lotteryShop_address3, login_name, tv_push, tv_attention, tv_fans;
    private LinearLayout main_lotteryShop_ll, main_usetInfomation_ll;
    private AVLoadingIndicatorView AVLoading;
    private MainHomeLotteryAdapter adapter;
    private MyListView lv;
    private RelativeLayout main_myCustom_layout;
    private PopupWindow popupWindow;
    private MyScrollView main_scrollView;
    private RelativeLayout more;
    private SimpleDraweeView user_icon;
    private RelativeLayout lotteryShop_layou1, lotteryShop_layou2, lotteryShop_layou3;
    private double center_latitude;
    private double center_longitude;
    private String province = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.init(MainActivity.this);//判断包是debug还是release
        if (LogUtils.APP_DBG) MobclickAgent.setDebugMode(true);//友盟true为debug
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        setSystemBarColor(ColorUtils.MAIN_BLUE, false);//状态栏颜色
        //获取地图控件引用
        setClosePop(this);
        //初始化
        initView();
        initListener();
        checkPermisson();//判断权限
        LocationGPS();//开启定位
        initData();//内容主体数据
        isCustom = getIntent().getBooleanExtra("isCustom", true);
//        //展示2张引导弹窗
//        setMoreGuideDialog(Arrays.asList(new String[]{"main_map","main_scan"}),0,
//                new Integer[]{R.mipmap.main_map_guide,R.mipmap.guide_scan});
        //展示1张引导弹窗
        //setGuideDialog("main_map", R.mipmap.main_map_guide);
        VersionControl();
        updatePushCustom();//更新推送设置
//        com.cp2y.cube.dialog.AlertDialog dialog=new com.cp2y.cube.dialog.AlertDialog(this);
//        dialog.setMaxHeight(1000);
//        dialog.show();
    }

    //请求数据
    private void initData() {
        AVLoading.setVisibility(View.VISIBLE);
        boolean isLogin = LoginSPUtils.isLogin();
        String lottery = "";
        if (!isLogin) {//未登录
            login_name.setText("一键登录");
            login_name.setBackgroundResource(R.drawable.app_main_login);
            user_icon.setImageResource(R.mipmap.btn_avatar);
            String customLottery = CommonSPUtils.getString("customLottery");
            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(customLottery)) {
                String[] custom = customLottery.split(",");
                for (int i = 0; i < custom.length; i++) {
                    String str = custom[i];
                    int key = Integer.valueOf(str.substring(0, str.indexOf("_")));
                    if (i == 0) {
                        stringBuilder.append(String.valueOf(key));
                    } else {
                        stringBuilder.append(",").append(String.valueOf(key));
                    }
                }
            }
            lottery = stringBuilder.toString();
        }
        int id = LoginSPUtils.getInt("id", 0);
        NetHelper.LOTTERY_API.getHomePage(lottery, id).subscribe(new SafeOnlyNextSubscriber<MainHomeModel>() {
            @Override
            public void onNext(MainHomeModel args) {
                super.onNext(args);
                //个人信息状态栏
                main_usetInfomation_ll.setVisibility(isLogin ? View.VISIBLE : View.GONE);
                if (isLogin) {//登陆后个人信息
                    if (args.getUser() != null) {
                        login_name.setText(args.getUser().getName());
                        login_name.setBackground(null);
                        user_icon.setImageURI(args.getUser().getUrl());
                        tv_push.setText("推单 " + args.getUser().getPushSingleNum());
                        tv_attention.setText("关注 " + args.getUser().getAttentionNum());
                        tv_fans.setText("粉丝 " + args.getUser().getFansNum());
                    }
                }
                List<MainHomeModel.Detail> list = args.getList();
                adapter.loadData(list);
                main_myCustom_layout.setVisibility(View.VISIBLE);
                AVLoading.setVisibility(View.GONE);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                AVLoading.setVisibility(View.GONE);
                TipsToast.showTips("请检查网络设置");
            }
        });
        updatePushCustom();//更新推送设置
    }

    private void initView() {
        iv_add = (ImageView) findViewById(R.id.app_add);//中间加号按钮
        iv_user_add = (ImageView) findViewById(R.id.main_user_add);//个人主页加号按钮
        iv_goCustom = (ImageView) findViewById(R.id.main_home_goCustom);//个人主页加号按钮
        user_icon = (SimpleDraweeView) findViewById(R.id.main_user_icon);
        lotteryShop_name1 = (TextView) findViewById(R.id.main_lotteryShop_name1);
        lotteryShop_name2 = (TextView) findViewById(R.id.main_lotteryShop_name2);
        lotteryShop_name3 = (TextView) findViewById(R.id.main_lotteryShop_name3);
        lotteryShop_diatance1 = (TextView) findViewById(R.id.main_lotteryShop_diatance1);
        lotteryShop_diatance2 = (TextView) findViewById(R.id.main_lotteryShop_diatance2);
        lotteryShop_diatance3 = (TextView) findViewById(R.id.main_lotteryShop_diatance3);
        lotteryShop_address1 = (TextView) findViewById(R.id.main_lotteryShop_address1);
        lotteryShop_address2 = (TextView) findViewById(R.id.main_lotteryShop_address2);
        lotteryShop_address3 = (TextView) findViewById(R.id.main_lotteryShop_address3);
        lotteryShop_layou1 = (RelativeLayout) findViewById(R.id.main_lotteryShop_layout1);
        lotteryShop_layou2 = (RelativeLayout) findViewById(R.id.main_lotteryShop_layout2);
        lotteryShop_layou3 = (RelativeLayout) findViewById(R.id.main_lotteryShop_layout3);
        more = (RelativeLayout) findViewById(R.id.main_lotteryShop_more_layout);
        tv_push = (TextView) findViewById(R.id.main_login_push);
        tv_attention = (TextView) findViewById(R.id.main_login_attention);
        tv_fans = (TextView) findViewById(R.id.main_login_fans);
        login_name = (TextView) findViewById(R.id.main_user_name);
        main_lotteryShop_ll = (LinearLayout) findViewById(R.id.main_lotteryShop_ll);
        main_usetInfomation_ll = (LinearLayout) findViewById(R.id.main_usetInfomation_ll);
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        main_myCustom_layout = (RelativeLayout) findViewById(R.id.main_myCustom_layout);
        lv = (MyListView) findViewById(R.id.main_myDetail_lv);
        main_scrollView = (MyScrollView) findViewById(R.id.main_scrollView);
        adapter = new MainHomeLotteryAdapter(this);
        lv.setAdapter(adapter);
    }

    //获得是否定制
    public boolean getisCustom() {
        return isCustom;
    }

    private void initListener() {
        Intent intent = new Intent();
        RadioButton rb_trend = (RadioButton) findViewById(R.id.app_trend);
        RadioButton rb_pushSingle = (RadioButton) findViewById(R.id.app_btn_openLottery);
        RadioButton rb_news = (RadioButton) findViewById(R.id.app_btn_news);
        rb_trend.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                intent.setClass(MainActivity.this, TrendActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }));
        rb_pushSingle.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                intent.setClass(MainActivity.this, PushSingleActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }));
        rb_news.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                intent.setClass(MainActivity.this, NewsActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            }
        }));
        iv_add.setOnClickListener((v -> showMoreWindow(v)));
        login_name.setOnClickListener((v -> {
            boolean loginState = LoginSPUtils.isLogin();//获取登录状态
            if (loginState) {
                return;//登录状态点击无效
            } else {
                intentLogin();//登录页面
                //overridePendingTransition(android.R.anim.fade_in,R.anim.activity_close);
            }
        }));
        iv_user_add.setOnClickListener((v -> {
            if (isPopShowing()) {
                closeUserPop();
            } else {
                popWindow();
            }
        }));
        iv_goCustom.setOnClickListener((v -> startActivity(new Intent(MainActivity.this, CustomProvinceActivity.class))));
        more.setOnClickListener((v -> startActivity(new Intent(MainActivity.this, MapActivity.class))));
        tv_push.setOnClickListener((v -> startActivity(new Intent(MainActivity.this, MyPushSingleActivity.class))));
        tv_attention.setOnClickListener((v -> {
            Intent intent1 = new Intent(MainActivity.this, MyAttentionFansActivity.class);
            intent1.putExtra("pos", 0);
            startActivity(intent1);
        }));
        tv_fans.setOnClickListener((v -> {
            Intent intent1 = new Intent(MainActivity.this, MyAttentionFansActivity.class);
            intent1.putExtra("pos", 1);
            startActivity(intent1);
        }));
    }

    public boolean isPopShowing() {
        if (popupWindow == null) return false;
        if (popupWindow.isShowing()) {
            return true;
        } else {
            return false;
        }
    }

    public void closeUserPop() {
        if (popupWindow == null) return;
        popupWindow.dismiss();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);//后台运行
    }

    private void LocationGPS() {
        if (!shouldRequest) {
            TipsToast.showTips("请开启定位权限");
            return;
        }
        if (locationClient == null) {
            locationClient = new LocationClient(MainActivity.this);
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            option.setCoorType("bd09ll");
            //可选，默认gcj02，设置返回的定位结果坐标系
            option.setScanSpan(0);
            //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            option.setIsNeedAddress(true);
            //可选，设置是否需要地址信息，默认不需要
            option.setOpenGps(true);
            //可选，默认false,设置是否使用gps
            //是否设置手机 机头方向
            option.setNeedDeviceDirect(true);
            locationClient.setLocOption(option);
            locationClient.registerLocationListener(bdLocation -> {
                ProvinceHelper.setProvince(bdLocation.getProvince());//省份数据处理
                address = bdLocation.getCity();
                LatLng latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                center_latitude = latLng.latitude;//中心点坐标
                center_longitude = latLng.longitude;
                province = bdLocation.getProvince();//省份
                PoiSearch(latLng);
            });
            locationClient.start();//开启
        }
        locationClient.requestLocation();//发起请求
    }

    //判断定位权限
    public void checkPermisson() {
        PermissionsChecker checker = new PermissionsChecker(MainActivity.this);
        boolean permission = checker.lacksPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission) {//缺少定位权限
            shouldRequest = false;
        } else {//有定位权限
            shouldRequest = true;
        }
    }

    //POI检索方法
    public void PoiSearch(LatLng latLng) {
        if (mPoiSearch == null) {
            mPoiSearch = PoiSearch.newInstance();
            //Poi搜索的监听
            mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                @Override
                public void onGetPoiResult(PoiResult poiResult) {
                    //获取Poi所有节点
                    List<PoiInfo> list = poiResult.getAllPoi();
                    //Poi搜索的结果
                    if (list != null && list.size() > 0) {
                        //如果结果大于0
                        lotteryShop_name1.setText("1." + list.get(0).name);
                        lotteryShop_name2.setText("2." + list.get(1).name);
                        lotteryShop_name3.setText("3." + list.get(2).name);
                        lotteryShop_address1.setText(list.get(0).address);
                        lotteryShop_address2.setText(list.get(1).address);
                        lotteryShop_address3.setText(list.get(2).address);
                        int distance1 = (int) DistanceUtil.getDistance(latLng, list.get(0).location);
                        int distance2 = (int) DistanceUtil.getDistance(latLng, list.get(1).location);
                        int distance3 = (int) DistanceUtil.getDistance(latLng, list.get(2).location);
                        lotteryShop_diatance1.setText(distance1 > 1000 ? CommonUtil.changeDouble((double) distance1 / 1000) + "公里" : distance1 + "米");
                        lotteryShop_diatance2.setText(distance2 > 1000 ? CommonUtil.changeDouble((double) distance2 / 1000) + "公里" : distance2 + "米");
                        lotteryShop_diatance3.setText(distance3 > 1000 ? CommonUtil.changeDouble((double) distance3 / 1000) + "公里" : distance3 + "米");
                        main_lotteryShop_ll.setVisibility(View.VISIBLE);
                        lotteryShop_layou1.setOnClickListener((v -> intentPoiDetail(list, 0, distance1)));
                        lotteryShop_layou2.setOnClickListener((v -> intentPoiDetail(list, 1, distance2)));
                        lotteryShop_layou3.setOnClickListener((v -> intentPoiDetail(list, 2, distance3)));
                    } else {
                        //TipsToast.showTips("您附近5公里范围内，暂无彩票站点");
                    }

                }

                @Override
                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                }

                @Override
                public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
                    //获取室内的结果
                }
            });
        }
        if (!TextUtils.isEmpty(address)) {
            PoiNearbySearchOption nearbySearchOption = new PoiNearbySearchOption();
            nearbySearchOption.keyword("彩票");// 关键字
            nearbySearchOption.pageCapacity(3);// 默认每页10条
            nearbySearchOption.location(latLng);//设置中心点
            nearbySearchOption.radius(5000);
            // 发起检索请求
            mPoiSearch.searchNearby(nearbySearchOption);
        }
    }

    //跳转到poi详情页面
    private void intentPoiDetail(List<PoiInfo> list, int pos, int distance) {
        //得到结果的详情
        Intent intent = new Intent(MainActivity.this, PoiDetailActivity.class);
        intent.putExtra("end", list.get(pos).address);
        intent.putExtra("province", list.get(pos));
        intent.putExtra("center_latitude", center_latitude);
        intent.putExtra("center_longitude", center_longitude);
        intent.putExtra("end_latitude", list.get(pos).location.latitude);
        intent.putExtra("end_longitude", list.get(pos).location.longitude);
        intent.putExtra("PoiName", list.get(pos).name);
        intent.putExtra("phone", list.get(pos).phoneNum);
        intent.putExtra("distance", distance);
        startActivity(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            RadioButton rb_main = (RadioButton) findViewById(R.id.app_btn_openMain);
            rb_main.setChecked(true);
            closeMoreWindow();//关闭过滤选号
            closeUserPop();//关闭个人信息加号
            initData();//刷新数据
            LocationGPS();//开启定位
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("请开启权限");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void closePop() {

    }


    public void VersionControl() {
        NetHelper.LOTTERY_API.versionControl(0).subscribe(new SafeOnlyNextSubscriber<VersionControlModel>() {
            @Override
            public void onNext(VersionControlModel args) {
                super.onNext(args);
                //服务器获取版本号大于app版本号  args.getItem().getCurrentVersionid()>CommonUtil.getVersionName(MainActivity.this)
                if (args.getItem().getCurrentVersionid() > CommonUtil.getVersionId(MainActivity.this)) {
//                    if (dialog == null) {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.app_update_dialog, null);
//                        builder.setView(view);
//                        builder.setCancelable(false);
//                        Button NegativeButton = (Button) view.findViewById(R.id.NegativeButton);
//                        Button PositiveButton = (Button) view.findViewById(R.id.PositiveButton);
//                        TextView message = (TextView) view.findViewById(R.id.app_update_message);
//                        TextView title = (TextView) view.findViewById(R.id.app_update_title);
//                        message.setText(args.getItem().getDesc());
//                        title.setText(args.getItem().getCurrentVersiontitle());
//                        PositiveButton.setOnClickListener((v -> {
//                            try {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(args.getItem().getUrl()));
//                                startActivity(intent);
//                            } catch (Exception e) {
//                                TipsToast.showTips("浏览器打开失败");
//                            }
//                        }));
//                        NegativeButton.setOnClickListener((v -> dialog.dismiss()));
//                        dialog = builder.show();
//                        dialog.show();
//                    } else {
//                        dialog.show();
//                    }
                    Log.e("log", args.getItem().getUrl());
                    //开启下载弹窗
                    ApkDownloadTools apkDownloadTools = new ApkDownloadTools(MainActivity.this);
                    apkDownloadTools.setShowToast(false);
                    apkDownloadTools.showUpdateDialog(args, true);
                } else {
                    //TipsToast.showTips("已是最新版本");
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }

    //下拉popwindow
    public void popWindow() {
        View contentView = LayoutInflater.from(this)
                .inflate(R.layout.ignore_main_menu_pop, null);
        LinearLayout ll = (LinearLayout) contentView.findViewById(R.id.main_menu_pop_ll);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View v = ll.getChildAt(i);
            if (v instanceof LinearLayout) {
                LinearLayout ll_child = (LinearLayout) v;
                final int finalI = i;
                ll_child.setOnClickListener((v1 -> {
                    if (finalI == 0) {//号码库
                        boolean loginState = LoginSPUtils.isLogin();
                        if (loginState) {
                            startActivity(new Intent(MainActivity.this, NumLibraryActivity.class));//不传flag 默认-1
                        } else {
                            intentLogin();//登录页面
                        }
                    } else if (finalI == 1) {//计奖器
                        CommonUtil.openActicity(MainActivity.this, CalculateActivity.class, null);
                    } else if (finalI == 2) {//倍投器
                        CommonUtil.openActicity(MainActivity.this, MultiCalcActivity.class, null);
                    } else if (finalI == 3) {//投注站地图
                        startActivity(new Intent(MainActivity.this, MapActivity.class));
                    } else if (finalI == 4) {//分享
                        UMImage image = new UMImage(MainActivity.this, R.mipmap.logo108_108);
                        UMWeb web = new UMWeb(NetConst.APP_SHARE_URL);
                        web.setTitle("推荐一个超好用的彩票工具");//标题
                        web.setThumb(image);  //缩略图
                        web.setDescription(getString(R.string.share_CubeLottery));
                        ShareBoardConfig config = new ShareBoardConfig();
                        config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
                        config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
                        config.setCancelButtonVisibility(false);//取消按钮
                        config.setIndicatorVisibility(false);//小白点
                        new ShareAction(MainActivity.this)
                                .withMedia(web)
                                .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                                .setCallback(umShareListener).open(config);
                    } else if (finalI == 5) {//客服
                        if (NavigationUtil.isAvilible(MainActivity.this, "com.tencent.mobileqq")) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=" + "619334324" + "&version=1")));
                        } else {
                            Toast.makeText(this, "请安装QQ", Toast.LENGTH_SHORT).show();
                        }
                    } else if (finalI == 6) {//设置
                        startActivity(new Intent(MainActivity.this, SettingActivity.class));
                    }
                }));
            }
        }
        popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        //popupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
        popupWindow.setOutsideTouchable(true);//点击其他区域关闭
        popupWindow.setContentView(contentView);
        //展示在toolbar下方
        popupWindow.showAsDropDown(iv_user_add, 0, 0);
        //popupWindow.showAtLocation(getToolBar(), Gravity.TOP, 0, 250);
    }

    private void updatePushCustom() {
        NetHelper.LOTTERY_API.updateCustomPush(CommonUtil.getUserId(), 0).subscribe(new SafeOnlyNextSubscriber<FlagModel>() {
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if (args.getFlag() == 1)
                    LogUtil.LogE(CustomUploadActivity.class, "updatePushCustom");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
}
