package com.cp2y.cube.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.adapter.CustomLotteryProvinceAdapter;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.DragGridViewContainer;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.custom.draggridview.DragAdapter;
import com.cp2y.cube.custom.draggridview.DragGridView;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.helper.ProvinceHelper;
import com.cp2y.cube.model.CustomLotteryProvinceModel;
import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.PermissionsChecker;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomProvinceActivity extends CustomUploadActivity {
    private DragGridView drag_gv;
    private TextView tv_province,tv_11xuan5,tv_Ssc,tv_K3,tv_Klsf,tv_Other;
    private MyGridView gv_province;
    private String province="";
    private CustomLotteryProvinceAdapter adapter;
    private List<CustomLotteryProvinceModel.LotteryDetail>list_province;//省份
    private DragGridViewContainer drag_containter;
    private boolean shouldRequest = false;//是否有权限
    private ScrollView scrollview;
    private TextView tv_finish;
    private DragAdapter dragAdapter;
    private ImageView netOff;
    private boolean isCustom=false;//true是从启动定制进入,false从侧拉菜单进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_province);
        setMainTitle("定制彩种");
        setNavigationIcon(R.mipmap.back_chevron);
        isCustom=getIntent().getBooleanExtra("isCustom",false);
        province=ProvinceHelper.getProvince();
        setNavigationOnClickListener((v -> backListener()));
        initView();//初始化
        initNetOff();//断网控制
        initListener();//监听
        updatePushCustom();//更新推送设置
    }

    private void initNetOff() {
        if(!CommonUtil.isNetworkConnected(CustomProvinceActivity.this)){//断网机制
                netOff.setVisibility(View.VISIBLE);
                TipsToast.showTips("请检查网络设置");
                netOff.setOnClickListener((v -> initNetOff()));//点击加载
        }else{
            netOff.setVisibility(View.GONE);
        }
        initData();
    }
    //判断定位权限
    public void checkPermisson(){
        PermissionsChecker checker=new PermissionsChecker(CustomProvinceActivity.this);
        boolean permission = checker.lacksPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (!permission&&CommonUtil.hasGPSDevice()) {//不缺少定位权限,且开启GPS
            shouldRequest=true;
        }else {
            shouldRequest=false;
        }

    }
    //返回键
    private void backListener() {
        if(isCustom){
            Intent intent=new Intent(CustomProvinceActivity.this, MainActivity.class);
            intent.putExtra("isCustom",true);
            startActivity(intent);
        }else{
            boolean loginState= LoginSPUtils.isLogin();//获取登录状态
            if(loginState) upLoadCustom();
            ContextHelper.getApplication().runDelay(this::finish,200);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backListener();
    }

    private void initData() {
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        List<HashMap<String, Object>> list=new ArrayList<>();
        if(!loginState){
            String customLottery= CommonSPUtils.getString("customLottery");
            //boolean isVisible=!TextUtils.isEmpty(customLottery);
            //定制结果 显示与隐藏
            //ViewUtils.showViewsVisible(isVisible,findViewById(R.id.view1),findViewById(R.id.custom_select_show_layout));
            list.addAll(getFilesCustom(customLottery));
            dragAdapter.reLoadData(list);//显示定制结果,展示数据
        }else{
            NetHelper.USER_API.getLoginCustom(LoginSPUtils.getInt("id",0)).subscribe(new SafeOnlyNextSubscriber<CustomModel>(){
                @Override
                public void onNext(CustomModel args) {
                    super.onNext(args);
                    if(args.getLotteryCustom()!=null&&args.getLotteryCustom().size()>0){
                        //ViewUtils.showViewsVisible(true,findViewById(R.id.view1),findViewById(R.id.custom_select_show_layout));
                        CustomLotteryList.synchronizedLoginData(args.getLotteryCustom());//同步登陆数据
                        list.addAll(getLoginCustom(args.getLotteryCustom()));
                        dragAdapter.reLoadData(list);//显示定制结果,展示数据
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    TipsToast.showTips("请检查网络设置");
                }
            });
        }
        initNet();//请求省份
    }


    private void initListener() {
        tv_finish.setOnClickListener((v1 -> {
            boolean loginState= LoginSPUtils.isLogin();//获取登录状态
            if(!loginState){
                dragAdapter.setFinish();
            }else{
                upLoadCustom();//登陆上传
                dragAdapter.setFinish();
            }
        }));//完成模式
        gv_province.setOnItemClickListener(((parent, view, position, id) -> gotoProvinceDetail(list_province.get(position).getId(),list_province.get(position).getProvince())));
        tv_province.setOnClickListener((v -> {
            if(!TextUtils.isEmpty(province))gotoProvinceDetail(Integer.valueOf(ProvinceHelper.getService(province)),province);
        }));
        tv_11xuan5.setOnClickListener((v -> gotoLotteryDetail(2,"各省11选5")));
        tv_Ssc.setOnClickListener((v -> gotoLotteryDetail(3,"各省时时彩")));
        tv_K3.setOnClickListener((v -> gotoLotteryDetail(4,"各省快三")));
        tv_Klsf.setOnClickListener((v -> gotoLotteryDetail(5,"各省快乐十分")));
        tv_Other.setOnClickListener((v -> gotoLotteryDetail(6,"其他高频")));
    }


    private void gotoProvinceDetail(int provinceId,String provinceName) {
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        if(loginState) upLoadCustom();
        Intent intent=new Intent(CustomProvinceActivity.this,CustomProvinceDetailActivity.class);
        intent.putExtra("provinceId",provinceId);
        intent.putExtra("provinceName",provinceName);
        startActivity(intent);
    }
    private void gotoLotteryDetail(int lotteryType,String lotteryTypeName) {
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        if(loginState) upLoadCustom();
        Intent intent=new Intent(CustomProvinceActivity.this,CustomProvinceDetailActivity.class);
        intent.putExtra("lotteryType",lotteryType);
        intent.putExtra("lotteryDetail",true);
        intent.putExtra("provinceName",lotteryTypeName);
        startActivity(intent);
    }

    private void initNet() {
        NetHelper.LOTTERY_API.CustomLotteryProvince().subscribe(new SafeOnlyNextSubscriber<CustomLotteryProvinceModel>(){
            @Override
            public void onNext(CustomLotteryProvinceModel args) {
                super.onNext(args);
                list_province=args.getprovinceList();
                adapter.loadData(list_province);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void initView() {
        drag_containter = (DragGridViewContainer) findViewById(R.id.drag_containter);
        scrollview = (ScrollView) findViewById(R.id.scrollView);
        netOff = (ImageView) findViewById(R.id.netOff);
        drag_gv = (DragGridView) findViewById(R.id.custom_lottery_dragGridView);
        tv_province = (TextView) findViewById(R.id.custom_lottery_Province);
        gv_province = (MyGridView) findViewById(R.id.custom_lottery_gvProvince);
        tv_11xuan5 = (TextView) findViewById(R.id.custom_lottery_tv11xuan5);
        tv_Ssc = (TextView) findViewById(R.id.custom_lottery_tvSsc);
        tv_K3 = (TextView) findViewById(R.id.custom_lottery_tvK3);
        tv_Klsf = (TextView) findViewById(R.id.custom_lottery_tvKlsf);
        tv_Other = (TextView) findViewById(R.id.custom_lottery_tvOther);
        tv_finish = (TextView) findViewById(R.id.custom_tv_finish);
        drag_gv.setSelector(android.R.color.transparent);
        //drag_gv.setIsShowBitMap(false);//设置拖拽不显示镜像
        drag_containter.setScrollView(scrollview);
        tv_province.setText(province);
        adapter=new CustomLotteryProvinceAdapter(CustomProvinceActivity.this);
        gv_province.setAdapter(adapter);
        dragAdapter=new DragAdapter(CustomProvinceActivity.this,drag_gv);//定制
        drag_gv.setAdapter(dragAdapter);
        checkPermisson();//检查权限
        ViewUtils.showViewsVisible(shouldRequest,findViewById(R.id.custom_lottery_tvLocation),tv_province);
    }




    //读取本地数据
    private List<HashMap<String, Object>> getFilesCustom(String customLottery) {
        List<HashMap<String, Object>> list=new ArrayList<>();
        if(!TextUtils.isEmpty(customLottery)) {
            String[] customs = customLottery.split(",");
            for (String str : customs) {
                int key = Integer.valueOf(str.substring(0, str.indexOf("_")));
                String value = str.substring(str.indexOf("_") + 1, str.length());
                HashMap<String, Object> map = new HashMap<>();
                map.put("lotteryId", key);
                map.put("lotteryName", value);
                list.add(map);
            }
        }else{//默认4个
            for(int i=0,length=defaultId.length;i<length;i++){
                int key=defaultId[i];
                String value=defaultName[i];
                HashMap<String, Object> map = new HashMap<>();
                map.put("lotteryId", key);
                map.put("lotteryName", value);
                list.add(map);
            }
        }
        return list;
    }
    //处理登陆后的定制数据
    public List<HashMap<String, Object>> getLoginCustom(List<CustomModel.Detail> data){
        List<HashMap<String, Object>> list=new ArrayList<>();
        if(data!=null&&data.size()>0){
            for(int i=0,size=data.size();i<size;i++){
                int key=data.get(i).getLotteryID();
                String value=data.get(i).getLotteryName();
                HashMap<String, Object> map = new HashMap<>();
                map.put("lotteryId", key);
                map.put("lotteryName", value);
                list.add(map);
            }
        }
        return list;
    }

    //完成显示与隐藏
    public void setFinishVisible(boolean isVisible){
        tv_finish.setVisibility(isVisible? View.VISIBLE:View.GONE);
    }

    //定制返回后刷新
    @Override
    protected void onRestart() {
        super.onRestart();
        initData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_push_set,menu);
        MenuItem item = menu.findItem(R.id.menu_text);
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(ColorUtils.COLOR_63798A), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CommonUtil.openActicity(CustomProvinceActivity.this,PushSettingActivity.class,null);
        return false;
    }

    private void updatePushCustom(){
        NetHelper.LOTTERY_API.updateCustomPush(CommonUtil.getUserId(),0).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if(args.getFlag()==1) LogUtil.LogE(CustomUploadActivity.class,"updatePushCustom");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }
}
