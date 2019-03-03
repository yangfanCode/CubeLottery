package com.cp2y.cube.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.activity.recognize.CameraActivity;
import com.cp2y.cube.adapter.MyLeftMenuGridViewAdapter;
import com.cp2y.cube.custom.ClosePop;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.MoreWindow;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.GuideDialog;
import com.cp2y.cube.helper.AndroidMHelper;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.IssueModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.services.Service;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.GuideSharedPreferences;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.NavigationUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.tencent.stat.StatService;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import rx.Subscription;

/**
 * Created by js on 2016/11/29.
 */
public class BaseActivity extends AppCompatActivity {
    private ClosePop closePop;
    private DrawerLayout drawer=null;
    private GuideSharedPreferences gsp=null;
    private GuideDialog  dialog=null;
    private AlertDialog dialog6=null,dialog1=null;
    private MoreWindow mMoreWindow;
    private SimpleDraweeView loginIcon;//用户头像
    private TextView loginName;//用户昵称
    public int[] defaultId={10002,10088,10001,10003};//默认四个
    public String[] defaultName={"双色球","超级大乐透","福彩3D","排列3"};//默认四个
    public String customLottery = "10002,10088,10001,10003";//默认上传
    private List<CustomModel.Detail>list_custom=new ArrayList<>();//定制结果
    private List<Subscription> mSubscription = new ArrayList<>();//订阅管理器
    /**
     * 导航栏
     */
    private Toolbar mToolbar = null;
    public void setClosePop(ClosePop closePop){
        this.closePop=closePop;
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayUtil.SCALE=getResources().getDisplayMetrics().density;
        ContextHelper.addActivity(this);
        ContextHelper.setLastActivity(this);
        dialog=new GuideDialog(BaseActivity.this);
        setSystemBarColor(ColorUtils.GRAY_BLUE,true);//状态栏颜色
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        if(!loginState){//启动页和引导页不同步
            if(!(BaseActivity.this instanceof SplashActivity)&&!(BaseActivity.this instanceof WelcomeActivity)) {
                CustomLotteryList.synchronizedData();//同步本地定制数据到内存
            }
        }
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle("");//隐藏中间的文字
            setSupportActionBar(mToolbar);
        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null && mToolbar != null) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();
            NavigationView nav= (NavigationView) findViewById(R.id.nav_view);
            View headerLayout = nav.getHeaderView(0);//登录头像布局
            loginIcon= (SimpleDraweeView) headerLayout.findViewById(R.id.WeChat_Login_icon);
            loginName= (TextView) headerLayout.findViewById(R.id.WeChat_Login_name);
            loginIcon.setOnClickListener((v2 -> {
                boolean loginState= LoginSPUtils.isLogin();//获取登录状态
                if(loginState){
                    return;//登录状态点击无效
                }else{
                    intentLogin();//登录页面
                    //overridePendingTransition(android.R.anim.fade_in,R.anim.activity_close);
                }
            }));
            //侧滑菜单彩种展示
            MyGridView my_lottery_gv = (MyGridView) findViewById(R.id.app_left_btn_lottery_gv);
            MyLeftMenuGridViewAdapter adapter = new MyLeftMenuGridViewAdapter(BaseActivity.this);
            my_lottery_gv.setAdapter(adapter);
            my_lottery_gv.setOnItemClickListener(((parent, view2, position, id) -> {
                if(list_custom.get(position).getLotteryID()==10000){//更多
                    startActivity(new Intent(BaseActivity.this,CustomProvinceActivity.class));
                }else{
                    LeftMenuIntent(list_custom.get(position).getLotteryID(),list_custom.get(position).getLotteryName());
                }
            }));
            //侧滑菜单点击事件
            //Button btn_main = (Button) findViewById(R.id.app_left_btn_main);
            TextView btn_service = (TextView) findViewById(R.id.app_left_btn_myservice);
            TextView btn_hotLine = (TextView) findViewById(R.id.app_left_btn_hotLine);
            TextView btn_lottery = (TextView) findViewById(R.id.app_left_btn_lottery);
            TextView btn_numLibrary = (TextView) findViewById(R.id.app_left_btn_mynumlibrary);
            TextView btn_share = (TextView) findViewById(R.id.app_left_btn_share);
            TextView btn_set = (TextView) findViewById(R.id.app_left_btn_setting);
            TextView btn_checkLottery = (TextView) findViewById(R.id.app_left_btn_checklottery);
//            btn_main.setOnClickListener(view1 -> {
//                if (drawer.isDrawerOpen(GravityCompat.START)) {
//                    if (!(BaseActivity.this instanceof MainActivity)) {
//                        Intent intent=new Intent(BaseActivity.this, MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                    } else {
//                        drawer.closeDrawer(GravityCompat.START);
//                    }
//                }
//            });
            //热线
            btn_hotLine.setOnClickListener((v3 -> startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-666-7575")))));
            btn_lottery.setOnClickListener((v2 -> {
                Intent intent=new Intent(BaseActivity.this,CustomProvinceActivity.class);
                intent.putExtra("isCustom",false);
                startActivity(intent);
            }));
            btn_checkLottery.setOnClickListener((v1 -> {startActivity(new Intent(BaseActivity.this, CameraActivity.class));}));
            btn_numLibrary.setOnClickListener(view -> {
                boolean loginState= LoginSPUtils.isLogin();
                if(loginState) {
                    if (drawer.isDrawerOpen(GravityCompat.START)) {
                        //多个号码库,点击
                        if (BaseActivity.this instanceof NumLibraryActivity) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            Intent intent = new Intent(BaseActivity.this, NumLibraryActivity.class);
                            startActivity(intent);//不传flag 默认-1
                        }
                    }
                }else{
                    intentLogin();//登录页面
                }
            });
            btn_service.setOnClickListener((v -> {
                if (NavigationUtil.isAvilible(BaseActivity.this, "com.tencent.mobileqq")){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+"619334324"+"&version=1")));
                }else{
                    Toast.makeText(this,"请安装QQ",Toast.LENGTH_SHORT).show();
                }
            }));
            btn_share.setOnClickListener((v -> {//6.4.3友盟
                UMImage image = new UMImage(BaseActivity.this, R.mipmap.logo108_108);
                UMWeb web = new UMWeb(NetConst.APP_SHARE_URL);
                web.setTitle("推荐一个超好用的彩票工具");//标题
                web.setThumb(image);  //缩略图
                web.setDescription(getString(R.string.share_CubeLottery));
                ShareBoardConfig config = new ShareBoardConfig();
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);
                config.setCancelButtonVisibility(false);//取消按钮
                config.setIndicatorVisibility(false);//小白点
                new ShareAction(BaseActivity.this)
                        .withMedia(web)
                        .setDisplayList(SHARE_MEDIA.QQ,SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener).open(config);
            }));
            btn_set.setOnClickListener((v -> startActivity(new Intent(BaseActivity.this,SettingActivity.class))));
            drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
               //开启侧拉关闭号码库popwindow
                    if(BaseActivity.this instanceof NumLibraryActivity){
                        closePop.closePop();
                    }
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    setDialog();//新手引导
                    setLoginInformation();//登录信息
                    refrenshCustom(adapter,my_lottery_gv);//刷新
                }

                @Override
                public void onDrawerClosed(View drawerView) {

                }

                @Override
                public void onDrawerStateChanged(int newState) {

                }
            });
        }
    }
    public <T extends Service> T getService(Class<T> tClass) {
        return Service.getService(tClass);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        ContextHelper.addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);//友盟统计
        StatService.onResume(this);//腾讯统计
        ContextHelper.setLastActivity(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);//友盟统计
        StatService.onPause(this);//腾讯统计
        //释放掉没有取消订阅的对象
        for (Subscription sub: mSubscription) {
            if (sub != null) sub.unsubscribe();
        }
    }

    /**添加到订阅管理器中**/
    public void addSubscription(Subscription subscription) {
        mSubscription.add(subscription);
    }

    /**移除订阅**/
    public void removeSubScription(Subscription subscription) {
        subscription.unsubscribe();
        mSubscription.remove(subscription);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ContextHelper.removeActivity(getClass());
    }

    /**
     * 设置导航标题
     * @param title
     */
    public void setNavTitle(String title) {
        try {
            TextView tv = (TextView) mToolbar.findViewById(R.id.nav_title);
            tv.setText(title);
            tv.setVisibility(View.VISIBLE);//显示导航文字
            mToolbar.setNavigationIcon(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置导航标题
     * @param res
     */
    public void setNavTitle(int res) {
        try {
            TextView tv = (TextView) mToolbar.findViewById(R.id.nav_title);
            tv.setText(res);
            tv.setVisibility(View.VISIBLE);//显示导航文字
            mToolbar.setNavigationIcon(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置导航标题点击事件
     * @param listener
     */
    public void setNavTitleClickListener(View.OnClickListener listener) {
        try {
            mToolbar.findViewById(R.id.nav_title).setOnClickListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置主标题
     * @param title
     */
    public void setMainTitle(String title) {
        try {
            TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            tv.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置标题点击事件
     * @param listener
     */
    public void setTitleClickListener(View.OnClickListener listener) {
        try {
            TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            tv.setOnClickListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取ToolBar
     * @return
     */
    public Toolbar getToolBar() {
        return mToolbar;
    }

    /**
     * 设置主标题
     * @param res
     */
    public void setMainTitle(int res) {
        try {
            TextView tv = (TextView) mToolbar.findViewById(R.id.toolbar_title);
            tv.setText(res);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置导航图标
     * @param res
     */
    public void setNavigationIcon(int res) {
        try {
            mToolbar.setNavigationIcon(res);
            mToolbar.findViewById(R.id.nav_title).setVisibility(View.GONE);//隐藏导航文字
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置导航点击事件
     * @param listener
     */
    public void setNavigationOnClickListener(View.OnClickListener listener) {
        try {
            mToolbar.setNavigationOnClickListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置菜单点击事件
     * @param listener
     */
    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener listener) {
        try {
            mToolbar.setOnMenuItemClickListener(listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 弹出新手引导dialog保存判断
     * @param saveKey 保存的key
     * @param resmipmapId 图片资源
     */
    public void setGuideDialog(String saveKey, int resmipmapId){
        gsp=new GuideSharedPreferences();
        if(gsp.takeSharedPreferences(this,saveKey))return;
        GuideDialog dialog=new GuideDialog(BaseActivity.this);
        View view=LayoutInflater.from(BaseActivity.this).inflate(R.layout.app_guide,null);
        dialog.setContentView(view);
        ImageView iv= (ImageView) view.findViewById(R.id.guide_iv);
        iv.setBackgroundResource(resmipmapId);
        iv.setOnClickListener((v)->dialog.dismiss());
        dialog.show();
        gsp.saveSharedPreferences(this,"已启动",saveKey);
    }

    /**弹出多张新手引导dialog保存判断
     *
     * @param saveKey
     * @param pos 0
     * @param resmipmapIds
     */
    public void setMoreGuideDialog(List<String> saveKey,int pos,Integer...resmipmapIds){
        gsp=new GuideSharedPreferences();
        for(int i=0;i<saveKey.size();i++){
            if(!gsp.takeSharedPreferences(this,saveKey.get(i))){
                break;//如果有没展示的引导图就跳出循环展示图片
            }else{
                if(i==saveKey.size()-1){
                    return;//如果都没有未展示的直接跳出方法
                }else{
                    continue;//否则就continue
                }
            }
        }
            View view=LayoutInflater.from(BaseActivity.this).inflate(R.layout.app_guide,null);
            dialog.setContentView(view);
            ImageView iv= (ImageView) view.findViewById(R.id.guide_iv);
            iv.setBackgroundResource(resmipmapIds[pos]);//默认第一张
            if(pos!=resmipmapIds.length-1){
                gsp.saveSharedPreferences(this,"已启动",saveKey.get(pos));
                final int finalPos = ++pos;
                iv.setOnClickListener((v)->{
                    dialog.dismiss();
                    setMoreGuideDialog(saveKey, finalPos,resmipmapIds);//递归点击之后继续开启
                });
            }else{
                gsp.saveSharedPreferences(this,"已启动",saveKey.get(saveKey.size()-1));
                iv.setOnClickListener((v)->{
                    dialog.dismiss();//最后一张dismiss
                });
            }
            dialog.show();
    }
    public void setDrawerClose(){
        if(drawer!=null){
            drawer.closeDrawers();
        }
    }

    public  UMShareListener umShareListener = new UMShareListener() {


        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA share_media) {

        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

    }
    private void setDialog() {
        if(BaseActivity.this instanceof MainActivity){
            boolean isCustom=((MainActivity)BaseActivity.this).getisCustom();
            setGuideDialog("main_custom",isCustom?R.mipmap.custom_guide1:R.mipmap.custom_guide2);
        }
    }
    /**
     * 首页侧拉菜单跳转
     * @param LotteryId
     */
    public void LeftMenuIntent(int LotteryId,String lotteryName){
        if(!CommonUtil.isNetworkConnected(this)){
            TipsToast.showTips("请检查网络设置");
            return;
        }
        NetHelper.LOTTERY_API.getNewDrawIssue(String.valueOf(LotteryId)).subscribe(new SafeOnlyNextSubscriber<IssueModel>(){
            @Override
            public void onNext(IssueModel args) {
                super.onNext(args);
                String Issu=args.getIssue();
                Intent intent=new Intent();
                if(!TextUtils.isEmpty(Issu)){
                    boolean isDetail=args.isDetail();
                    boolean isQuick=args.isQuick();
                    if(isDetail){
                        intent.setClass(BaseActivity.this, OpenLotterySummaryActivity.class);
                        intent.putExtra("issueOrder",Issu);
                    }else{
                        intent.setClass(BaseActivity.this,FastLotteryHistoryActivity.class);
                        intent.putExtra("isQuick",isQuick);
                    }
                    intent.putExtra("lottery_id",LotteryId);
                    intent.putExtra("lottery_Name",lotteryName);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
    //禁止修改APP内部文字大小
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics() );
        return res;

    }

    public void showMoreWindow(View view) {
        if(!CommonUtil.isNetworkConnected(this)){//断网机制
            TipsToast.showTips("请检查网络设置");
            return;
        }
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view);
    }
    public void closeMoreWindow(){
        if(mMoreWindow!=null){
            mMoreWindow.dismiss();
        }
    }
    //修改状态栏颜色和图标颜色
    public void setSystemBarColor(int colorId,boolean isDark){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){//5.0
            Window window = getWindow();
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(colorId);
            //修改系统图标颜色
            new AndroidMHelper().setStatusBarLightMode(this, isDark);
        }
    }

    /**
     * 登录信息展示刷新
     */
    public void setLoginInformation(){
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        String iconurl= LoginSPUtils.getString("iconurl","");
        String name= LoginSPUtils.getString("name","");
        if(loginState){
            if(!TextUtils.isEmpty(iconurl)){
                loginIcon.setImageURI(iconurl);//头像
            }
            loginName.setText(name);
        }else{
            //退出登录状态
            loginIcon.setImageResource(R.mipmap.btn_avatar);
            loginName.setText("点击登录");
        }
    }

    /**
     * 定制结果刷新
     */
    public void refrenshCustom(MyLeftMenuGridViewAdapter adapter,MyGridView my_lottery_gv){
        if(!CommonUtil.isNetworkConnected(BaseActivity.this)) {//断网机制
            my_lottery_gv.setVisibility(View.GONE);
        }else {
            my_lottery_gv.setVisibility(View.VISIBLE);
            boolean loginState = LoginSPUtils.isLogin();//获取登录状态
            list_custom.clear();
            if (!loginState) {//未登录
                list_custom.addAll(initCustomResultData(true));
                adapter.loadData(list_custom);
            } else {
                NetHelper.USER_API.getLoginCustom(LoginSPUtils.getInt("id", 0)).subscribe(new SafeOnlyNextSubscriber<CustomModel>() {
                    @Override
                    public void onNext(CustomModel args) {
                        super.onNext(args);
                        if (args.getLotteryCustom() != null && args.getLotteryCustom().size() > 0) {
                            list_custom.addAll(args.getLotteryCustom());
                            CustomLotteryList.synchronizedLoginData(list_custom);//同步登陆数据
                            if (list_custom.size() < 6) {
                                CustomModel.Detail detail = new CustomModel.Detail(10000, "更多");
                                list_custom.add(detail);
                            }
                            adapter.loadData(list_custom);//刷新数据
                        }
                    }
                });
            }
        }
    };

    //展示选6弹窗
    public void showDialog6(){
        if(dialog6==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.custom_lottery_full, null);
            builder.setView(view);
            builder.setCancelable(false);
            view.findViewById(R.id.custom_dialog_btn).setOnClickListener((v -> {dialog6.dismiss();}));
            dialog6=builder.show();
        }else{
            dialog6.show();
        }
    }
    //展示选1弹窗
    public void showDialog1(){
        if(dialog1==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            View view = LayoutInflater.from(BaseActivity.this).inflate(R.layout.custom_lottery_less, null);
            builder.setView(view);
            builder.setCancelable(false);
            view.findViewById(R.id.custom_dialog_btn).setOnClickListener((v -> {dialog1.dismiss();}));
            dialog1=builder.show();
        }else{
            dialog1.show();
        }
    }
    //未登录定制数据排序
    /**list为空定制都为未开发,
     *
     * @param developTotal
     * @param isTrend 走势不需要区分三星五星
     * @return
     */
    public List<CustomModel.Detail> initCustomData(Set<Integer> developTotal,boolean isTrend){
        List<CustomModel.Detail> list = new ArrayList<>();//排序后集合
        //取本地数据
        String customLottery = CommonSPUtils.getString("customLottery");
        if (!TextUtils.isEmpty(customLottery)) {//有本地数据
            boolean isDevelop = false;//是否定制已开发彩种
            List<CustomModel.Detail> develop = new ArrayList<>();//已开发集合
            List<CustomModel.Detail> no_Develop = new ArrayList<>();//未开发集合
            String[] customs = customLottery.split(",");
            for (String str : customs) {
                int key = Integer.valueOf(str.substring(0, str.indexOf("_")));
                String value = str.substring(str.indexOf("_") + 1, str.length());
                CustomModel.Detail detail = new CustomModel.Detail(key, value);
                if (developTotal.contains(key)) {//已开发
                    isDevelop = true;//有已开发彩种
                    develop.add(detail);
                    if(!isTrend){//过滤区分三星五星,走势在里面区分
                        if(key==10089){//定制重庆时时彩 造数据
                            CustomModel.Detail detailCQ1 = new CustomModel.Detail(10093, "重庆时时彩(三星)");
                            develop.add(detailCQ1);
                            CustomModel.Detail detailCQ2 = new CustomModel.Detail(10095, "重庆时时彩(二星)");
                            develop.add(detailCQ2);
                        }
                    }
                } else {//未开发
                    no_Develop.add(detail);
                }
            }
            if (isDevelop) {//有开发
                list.addAll(develop);
                list.addAll(no_Develop);
            }
        } else {//未登录无本地数据  默认四个
            for (int i = 0; i < defaultId.length; i++) {
                CustomModel.Detail detail = new CustomModel.Detail(defaultId[i], defaultName[i]);
                list.add(detail);
            }
        }
        return list;
    }

    /**
     * 未登录定制结果不排序
     * @return
     * 是否显示更多
     */
    public List<CustomModel.Detail> initCustomResultData(boolean isShowMore) {
        List<CustomModel.Detail> list = new ArrayList<>();//不排序集合
        //未登录
        String custom = CommonSPUtils.getString("customLottery");
        if (!TextUtils.isEmpty(custom)) {//读取定制数据
            String[] customs = custom.split(",");
            for (String str : customs) {
                int key = Integer.valueOf(str.substring(0, str.indexOf("_")));
                String value = str.substring(str.indexOf("_") + 1, str.length());
                CustomModel.Detail detail = new CustomModel.Detail(key, value);
                list.add(detail);
            }
            if(isShowMore&&list.size() < 6){
                CustomModel.Detail detail = new CustomModel.Detail(10000, "更多");
                list.add(detail);//更多标识
            }
        } else {//无定制数据默认
            for (int i = 0; i < defaultId.length; i++) {
                CustomModel.Detail detail = new CustomModel.Detail(defaultId[i], defaultName[i]);
                list.add(detail);
            }
        }
        return list;
    }
    //登录结果排序
    public List<CustomModel.Detail> initLoginCustomData(Set<Integer> developTotal,List<CustomModel.Detail> data){
        List<CustomModel.Detail> list = new ArrayList<>();//排序后集合
        boolean isDevelop = false;//是否定制已开发彩种
        List<CustomModel.Detail> develop = new ArrayList<>();//已开发集合
        List<CustomModel.Detail> no_Develop = new ArrayList<>();//未开发集合
        for(int i=0,size=data.size();i<size;i++){
            int key=data.get(i).getLotteryID();
            String value=data.get(i).getLotteryName();
            CustomModel.Detail detail = new CustomModel.Detail(key, value);
            if (developTotal.contains(key)) {//已开发
                isDevelop = true;//有已开发彩种
                develop.add(detail);
            } else {//未开发
                no_Develop.add(detail);
            }
        }
        if (isDevelop) {//有开发
            list.addAll(develop);
            list.addAll(no_Develop);
        }
        return list;
    }
    public void intentLogin(){
        Intent intent=new Intent();
        if(NavigationUtil.isAvilible(BaseActivity.this,"com.tencent.mm")){
            intent.setClass(BaseActivity.this,WeChatLoginActivity.class);
        }else{
            intent.setClass(BaseActivity.this,PhoneLoginActivity.class);
        }
        startActivity(intent);
    }
}
