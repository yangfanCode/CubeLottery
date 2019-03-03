package com.cp2y.cube.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyPoiDetailAdapter;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.helper.ProvinceHelper;
import com.cp2y.cube.model.BetModel;
import com.cp2y.cube.model.IssueModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.NavigationUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

public class PoiDetailActivity extends BaseActivity {
    private List<BetModel.delail>list=new ArrayList<>();
    private int distance=0;
    private String endAddress="";
    private String phone="";
    private String province;
    private String poiName;
    private ListView lv;
    private TextView tv_name;
    private TextView tv_add;
    private LinearLayout ll;
    private TextView tv_phone;
    private double center_latitude;
    private double center_longitude;
    private double end_latitude;
    private double end_longitude;
    private SimpleDraweeView detailView;
    private Button btn_goNavigation;
    private TextView walk_tv,car_tv,bus_tv;
    private MyPoiDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化
        setContentView(R.layout.activity_poi_detail);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v)->finish());
        setMainTitle("投注站");
        endAddress=getIntent().getStringExtra("end");
        province=getIntent().getStringExtra("province");
        poiName=getIntent().getStringExtra("PoiName");
        phone=getIntent().getStringExtra("phone");
        distance=getIntent().getIntExtra("distance",0);
        center_latitude=getIntent().getDoubleExtra("center_latitude",0.0);
        center_longitude=getIntent().getDoubleExtra("center_longitude",0.0);
        end_latitude=getIntent().getDoubleExtra("end_latitude",0.0);
        end_longitude=getIntent().getDoubleExtra("end_longitude",0.0);
        list=new ArrayList<>();
        View view= LayoutInflater.from(PoiDetailActivity.this).inflate(R.layout.poi_summary_head,null);
        lv = (ListView) findViewById(R.id.poi_detail_lv);
        tv_name = (TextView) view.findViewById(R.id.poi_detail_poiName);
        tv_add = (TextView) view.findViewById(R.id.poi_detail_poiAdd);
        ll = (LinearLayout) view.findViewById(R.id.poi_detail_phone_ll);
        tv_phone = (TextView)view. findViewById(R.id.poi_detail_poiPhone);
        detailView = (SimpleDraweeView) view.findViewById(R.id.poi_detailview);
        car_tv = (TextView) view.findViewById(R.id.poi_detail_qiche_tv);
        bus_tv = (TextView) view.findViewById(R.id.poi_detail_bus_tv);
        walk_tv = (TextView) view.findViewById(R.id.poi_detail_walk_tv);
        btn_goNavigation = (Button) view.findViewById(R.id.poi_detail_goNavagation_btn);
        adapter=new MyPoiDetailAdapter(this,R.layout.item_poidetail);
        lv.addHeaderView(view);
        lv.setAdapter(adapter);
        tv_name.setText(poiName);
        tv_add.setText(endAddress);
        if(!"".equals(phone)){
            ll.setVisibility(View.VISIBLE);
            tv_phone.setText(phone);
        }else{
            ll.setVisibility(View.INVISIBLE);
        }
        car_tv.setText(((distance/500)+1)>60? CommonUtil.changeDouble((double)((distance/500)+1)/60)+"小时":((distance/500)+1)+"分钟");
        bus_tv.setText(((distance/200)+5)>60? CommonUtil.changeDouble((double)((distance/200)+1)/60)+"小时":((distance/200)+1)+"分钟");
        walk_tv.setText(((distance/80)+1)>60? CommonUtil.changeDouble((double)((distance/80)+1)/60)+"小时":((distance/80)+1)+"分钟");
        initListView();
        //加载图片
        detailView.setImageURI("http://api.map.baidu.com/staticimage?center="+
                center_longitude+","+ center_latitude+"&width=1024&height=300&zoom=14&markers="+
                center_longitude+","+ center_latitude+"|"+end_longitude+","+end_latitude+"&markerStyles=l,A|l,B");
    }

    private void initListView() {
        int wOrs=poiName.contains("体育")?0:1;
        NetHelper.LOTTERY_API.getByBetting(ProvinceHelper.getService(province),wOrs).subscribe(new SafeOnlyNextSubscriber<BetModel>(){
            @Override
            public void onNext(BetModel args) {
                super.onNext(args);
                if(args.getBettingList()!=null&&args.getBettingList().size()>0) {
                    list.clear();
                    list.addAll(args.getBettingList());
                    adapter.loadData(list);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        lv.setOnItemClickListener(((parent, view, position, id) -> {
            if(position==0)return;
            //获取奖期
            getLotterySummary(list.get(position-1).getLotteryID(),list.get(position-1).getLotteryName());
        }));
        btn_goNavigation.setOnClickListener((v -> {
            //如果已安装,
            if(NavigationUtil.isAvilible(PoiDetailActivity.this,"com.baidu.BaiduMap")) {//传入指定应用包名
                TipsToast.showTips("即将用百度地图打开导航");
                Uri mUri = Uri.parse("geo:"+end_longitude+","+end_latitude+"?q="+poiName);
                Intent mIntent = new Intent(Intent.ACTION_VIEW,mUri);
                startActivity(mIntent);
            }else if(NavigationUtil.isAvilible(PoiDetailActivity.this,"com.autonavi.minimap")){
                TipsToast.showTips("即将用高德地图打开导航");
                Uri mUri = Uri.parse("geo:"+end_longitude+","+end_latitude+"?q="+poiName);
                Intent intent = new Intent("android.intent.action.VIEW",mUri);
                startActivity(intent);
            }else {
                TipsToast.showTips("请安装第三方地图方可导航");
                return;
            }
        }));
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
    public void getLotterySummary(int lotteryId,String lotteryName){
        if(!CommonUtil.isNetworkConnected(this)){
            TipsToast.showTips("请检查网络设置");
            return;
        }
        NetHelper.LOTTERY_API.getNewDrawIssue(String.valueOf(lotteryId)).subscribe(new SafeOnlyNextSubscriber<IssueModel>(){
            @Override
            public void onNext(IssueModel args) {
                super.onNext(args);
                String Issu=args.getIssue();
                if(!TextUtils.isEmpty(Issu)){
                    Intent intent=new Intent();
                    boolean isDetail=args.isDetail();
                    boolean isQuick=args.isQuick();
                    if(isDetail){
                        intent.setClass(PoiDetailActivity.this, OpenLotterySummaryActivity.class);
                        intent.putExtra("issueOrder",Issu);
                    }else{
                        intent.setClass(PoiDetailActivity.this,FastLotteryHistoryActivity.class);
                        intent.putExtra("isQuick",isQuick);
                    }
                    intent.putExtra("lottery_id",lotteryId);
                    intent.putExtra("lottery_Name",lotteryName);
                    startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }
}
