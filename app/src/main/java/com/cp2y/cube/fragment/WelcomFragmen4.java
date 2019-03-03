package com.cp2y.cube.fragment;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.adapter.CustomLotteryAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.helper.ProvinceHelper;
import com.cp2y.cube.model.CustomLotteryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.FirstAppSPUtils;
import com.cp2y.cube.utils.PermissionsChecker;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomFragmen4 extends Fragment implements MyInterface.customCount{
    private ImageView btn_skip,btn_add;
    private MyGridView gvAll,gvProvince;
    private TextView tv_provience;
    private boolean shouldRequest = false;//是否有权限
    //定位对象
    public LocationClient locationClient = null;
    private String province;//省份
    private TextView btn_finish;
    private AlertDialog dialog=null;
    private CustomLotteryAdapter adapterLocation,adapterAll;
    private LinearLayout all_layout,location_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.welcome4_fragment_layout, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }

    private void initListener() {
        btn_add.setOnClickListener((v -> {
            FirstAppSPUtils.put(getActivity(), "isFirstAppVer", CommonUtil.getVersionId(getActivity()));
            Intent intent=new Intent(getActivity(), CustomProvinceActivity.class);
            intent.putExtra("isCustom",true);
            startActivity(intent);
            getActivity().finish();
        }));
        btn_finish.setOnClickListener((v -> {
            if(CustomLotteryList.getSize()==0){
                ((BaseActivity)getActivity()).showDialog1();
            }else{
                CustomLotteryList.saveFiles();
                FirstAppSPUtils.put(getActivity(), "isFirstAppVer", CommonUtil.getVersionId(getActivity()));
                Intent intent=new Intent(getActivity(), MainActivity.class);
                intent.putExtra("isCustom",true);
                startActivity(intent);
                getActivity().finish();
            }
        }));
        btn_skip.setOnClickListener((v -> {
            FirstAppSPUtils.put(getActivity(), "isFirstAppVer", CommonUtil.getVersionId(getActivity()));
            Intent intent=new Intent(getActivity(), MainActivity.class);
            intent.putExtra("isCustom",false);
            startActivity(intent);
            getActivity().finish();
        }));
    }

    private void initData() {
        checkPermisson();//判断权限
        LocationGPS();//开启定位
    }

    private void initNet() {
        NetHelper.LOTTERY_API.CustomLottery(shouldRequest?ProvinceHelper.getService(province):"0").subscribe(new SafeOnlyNextSubscriber<CustomLotteryModel>(){
            @Override
            public void onNext(CustomLotteryModel args) {
                super.onNext(args);
                if(args.getWholeLottery()!=null){
                    adapterAll.LoadData(args.getWholeLottery());//全国数据
                    all_layout.setVisibility(View.VISIBLE);
                }
                if(args.getLocationLottery()!=null){
                    adapterLocation.LoadData(args.getLocationLottery());//定位数据
                    location_layout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void initView(View rootView) {
        btn_skip = (ImageView)rootView. findViewById(R.id.btn_skip);
        btn_add = (ImageView)rootView. findViewById(R.id.btn_add);
        gvAll = (MyGridView)rootView. findViewById(R.id.custom_lottery_gvAll);
        gvProvince = (MyGridView)rootView. findViewById(R.id.custom_lottery_gvProvince);
        tv_provience = (TextView)rootView. findViewById(R.id.custom_lottery_provience);
        btn_finish = (TextView)rootView. findViewById(R.id.custom_lottery_finish);
        all_layout = (LinearLayout)rootView. findViewById(R.id.all_layout);
        location_layout = (LinearLayout)rootView. findViewById(R.id.location_layout);
        adapterLocation=new CustomLotteryAdapter(getActivity());
        adapterAll=new CustomLotteryAdapter(getActivity());
        adapterLocation.setCall(this);
        adapterAll.setCall(this);
        gvProvince.setAdapter(adapterLocation);
        gvAll.setAdapter(adapterAll);
    }

    private void LocationGPS(){
        if (!shouldRequest) {
            TipsToast.showTips("请开启定位权限");
            initNet();//未定位只展示全国数据
            return;
        }
        if (locationClient == null) {
            locationClient = new LocationClient(getActivity());
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
                province = bdLocation.getProvince();
                initNet();//定位成功后请求网络数据
                //截取字符串到"省"前一位
                tv_provience.setText("("+province+")");
                ProvinceHelper.setProvince(province);//省份数据处理
            });
            locationClient.start();//开启
        }
        locationClient.requestLocation();//发起请求
    }
    
    //判断定位权限
    public void checkPermisson(){
        PermissionsChecker checker=new PermissionsChecker(getActivity());
        boolean permission = checker.lacksPermissions(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (permission) {//缺少定位权限
            shouldRequest=false;
        }else {//有定位权限
            shouldRequest=true;
        }
    }

    @Override
    public boolean customCounts(int lotteryId) {
        if(CustomLotteryList.getSize()<6){
            return false;//不够6个
        }else{//够了
            if(CustomLotteryList.contains(lotteryId)){
                return false;//包含id为取消操作
            }else{
                //弹窗
                ((BaseActivity)getActivity()).showDialog6();
                return true;
            }
        }
    }
}
