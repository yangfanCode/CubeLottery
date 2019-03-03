package com.cp2y.cube.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.helper.ProvinceHelper;
import com.cp2y.cube.model.BaiduMapPoiModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.PoiOverlay;
import com.cp2y.cube.utils.SnackBarUtils;

import java.util.List;

public class MapActivity extends BaseActivity {
    private MapView mapView = null;
    private PoiSearch mPoiSearch;
    private BaiduMap baiduMap;
    private PoiInfo poiInfo;
    private String address = "";
    //定位对象
    private LocationClient locationClient;
    private LatLng latLng;
    private String PoiName="";
    private String endAddress="";
    private String gpsAddress ="";
    private String province="";
    private String phone="";
    private TextView gps_city;
    private double center_latitude;
    private double center_longitude;
    private double end_latitude;
    private double end_longitude;
    private int distance=0;
    private boolean shouldRequest = false;//是否有权限
    private Overlay locateOverlay = null;
    private MyPoiOverlay myPoiOverlay = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_map);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        mapView = (MapView) findViewById(R.id.bmapView);
        //初始化
        baiduMap = mapView.getMap();
        gps_city = (TextView) findViewById(R.id.app_main_gpsCity);
        //开启定位
        LocationGPS();
        baiduMap.setOnMapLoadedCallback(() -> {//添加放大缩小控件
            int positionX = getResources().getDisplayMetrics().widthPixels;
            mapView.setZoomControlsPosition(new Point(positionX - DisplayUtil.dip2px(50f), 0));
        });
        MovePoiSearch();
    }

    private void LocationGPS() {
        if (shouldRequest) return;
        if (locationClient == null) {
            baiduMap.setMyLocationEnabled(true);
            locationClient = new LocationClient(getApplicationContext());
            //对定位的设置
            LocationClientOption option = new LocationClientOption();
            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setOpenGps(true);
            //设置定位间隔时间
//            option.setScanSpan(1000 * 2);
            //设置定位超出时间
            option.setTimeOut(1000 * 10);
            //是否需要得到地址
            option.setIsNeedAddress(true);
            //是否设置手机 机头方向
            option.setNeedDeviceDirect(true);
            //2给定位对象进行设置
            locationClient.setLocOption(option);
            //定义地图状态  判断是否第一次进入,指定位一次
            //3监听
            locationClient.registerLocationListener(bdLocation -> {
                //获取坐标
                if (locateOverlay != null) locateOverlay.remove();
                latLng = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.sign_icon_location);
                OverlayOptions options = new MarkerOptions().position(latLng).icon(descriptor).title("当前位置");
                locateOverlay = baiduMap.addOverlay(options);
                /**
                 * 设置中心点
                 */
                center_latitude=latLng.latitude;//中心点坐标
                center_longitude=latLng.longitude;
                address = bdLocation.getCity();
                //截取字符串到"市"前一位
//                if(address!=null&&address.contains("市")){
//                    gpsAddress = address.substring(0, address.lastIndexOf("市"));
//                }
                //定位城市
                gps_city.setText(address);
                ProvinceHelper.setProvince(bdLocation.getProvince());//省份数据处理
                MapStatus mMapStatus = new MapStatus.Builder().target(latLng).zoom(18).build();
                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
                //改变地图状态
                baiduMap.setMapStatus(mMapStatusUpdate);
                //在地图定位完成后开启poi检索
                PoiSearch(latLng);
                //设置地图缩放级别
                baiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(17).build()));
            });
            locationClient.start();//开启
        }
        locationClient.requestLocation();//发起请求
    }

    //POI检索方法
    public void PoiSearch(LatLng latLng) {
        if (mPoiSearch == null) {
            mPoiSearch = PoiSearch.newInstance();
            //Poi搜索的监听
            mPoiSearch.setOnGetPoiSearchResultListener(new OnGetPoiSearchResultListener() {
                @Override
                public void onGetPoiResult(PoiResult poiResult) {
                    //Poi搜索的结果
                    if(poiResult.getAllPoi()!=null&&poiResult.getAllPoi().size()>0){
                        //如果结果大于0
                        if (myPoiOverlay != null) myPoiOverlay.removeFromMap();
                        myPoiOverlay = new MyPoiOverlay(baiduMap);
                        myPoiOverlay.setData(poiResult);
                        baiduMap.setOnMarkerClickListener(myPoiOverlay);
                        myPoiOverlay.addToMap();
                    }else{
                        TipsToast.showTips("您附近5公里范围内，暂无彩票站点");
                    }

                }

                @Override
                public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
                    //得到结果的详情
                    Intent intent = new Intent(MapActivity.this, PoiDetailActivity.class);
                    intent.putExtra("end",endAddress);
                    intent.putExtra("province",province);
                    intent.putExtra("center_latitude",center_latitude);
                    intent.putExtra("center_longitude",center_longitude);
                    intent.putExtra("end_latitude",end_latitude);
                    intent.putExtra("end_longitude",end_longitude);
                    intent.putExtra("PoiName",PoiName);
                    intent.putExtra("phone",phone);
                    intent.putExtra("distance",distance);
                    startActivity(intent);
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
            //citySearchOption.pageCapacity(20);// 默认每页10条
            nearbySearchOption.location(latLng);//设置中心点
            nearbySearchOption.radius(5000);
            // 发起检索请求
            mPoiSearch.searchNearby(nearbySearchOption);
        }
    }


    /**
     * 移动屏幕重新POI
     */
    public void MovePoiSearch(){
        baiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                LatLng latLng=baiduMap.getMapStatus().target;
                PoiSearch(latLng);
            }
        });
    }

    public void getPoiProvince(){
        NetHelper.LOTTERY_API.getBaiduMapPoi(end_latitude,end_longitude).subscribe(new SafeOnlyNextSubscriber<BaiduMapPoiModel>(){
            @Override
            public void onNext(BaiduMapPoiModel args) {
                super.onNext(args);
                if(args.getResult().getAddressComponent().getProvince()!=null){
                    province=args.getResult().getAddressComponent().getProvince();
                }
            }
        });
    }

    public void myClick(View view) {
        switch (view.getId()) {
            case R.id.app_btn_location:
                requestLocClick();
                SnackBarUtils.ShortSnackbar(view, "正在定位中..", Color.BLACK, Color.WHITE).show();
                break;
        }

    }

    /**
     * 手动触发一次定位请求
     */
    public void requestLocClick() {
        LocationGPS();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
        try {
            LocationGPS();
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("请开启权限");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        try {
            locationClient.start();
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("请开启权限");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
        locationClient.stop();//后台停止定位
        //设置无动画
        overridePendingTransition(0,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        if (mPoiSearch != null) mPoiSearch.destroy();
        if (locationClient != null) locationClient.stop();
        mapView.onDestroy();
    }

    //Poi搜索的覆盖物
    class MyPoiOverlay extends PoiOverlay {
        /**
         * 构造函数
         *
         * @param baiduMap 该 PoiOverlay 引用的 BaiduMap 对象
         */
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        //点击覆盖物的监听
        @Override
        public boolean onPoiClick(int i) {
            //获取Poi所有节点
            List<PoiInfo> list = getPoiResult().getAllPoi();
            //获取点击的节点
            poiInfo = list.get(i);

            Button button = new Button(getApplicationContext());
            button.setBackgroundResource(R.drawable.app_baidumap_poiresult);
            distance= (int) DistanceUtil.getDistance(latLng, poiInfo.location);
            button.setText(distance>1000?poiInfo.name+"\n\t"+ CommonUtil.changeDouble((double)distance/1000)+"公里":poiInfo.name+"\n\t"+distance+"米");
            button.setPadding(20,0,20,0);
            button.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimension(R.dimen.app_tvNomal_size));
            end_latitude=poiInfo.location.latitude;
            end_longitude=poiInfo.location.longitude;
            endAddress=poiInfo.address;
            PoiName=poiInfo.name;
            phone=poiInfo.phoneNum;
            getPoiProvince();
            button.setTextColor(getResources().getColor(R.color.colorPoiSearchText));
            BitmapDescriptor descriptor = BitmapDescriptorFactory.fromView(button);

            InfoWindow infoWindow = new InfoWindow(descriptor, poiInfo.location, -100, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {
                    //根据Uid搜索详情
                    mPoiSearch.searchPoiDetail(new PoiDetailSearchOption().poiUid(poiInfo.uid));
                    baiduMap.hideInfoWindow();
                }
            });
            baiduMap.showInfoWindow(infoWindow);

            return super.onPoiClick(i);
        }
    }
}
