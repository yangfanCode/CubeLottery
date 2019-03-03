package com.cp2y.cube.activity;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.CustomProvinceDetailAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryProvinceModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.LoginSPUtils;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.List;

public class CustomProvinceDetailActivity extends CustomUploadActivity implements MyInterface.customCount{
    private AlertDialog dialog=null;
    private MyGridView gv;
    private CustomProvinceDetailAdapter adapter;
    private int provinceId;
    private int lotteryType;
    private TextView tv_finish;
    private AVLoadingIndicatorView AVLoading;
    private String provinceName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_province_detail);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        provinceId=getIntent().getIntExtra("provinceId",0);
        lotteryType=getIntent().getIntExtra("lotteryType",0);
        provinceName=getIntent().getStringExtra("provinceName");
        boolean lotteryDetail=getIntent().getBooleanExtra("lotteryDetail",false);
        setMainTitle(lotteryDetail?provinceName:provinceName+"彩种");
        initView();
        initNet();
        initListener();
    }

    private void initListener() {
        tv_finish.setOnClickListener((v -> {//定制完成 保存本地
            AVLoading.setVisibility(View.VISIBLE);
            if(CustomLotteryList.getSize()==0){
                showDialog1();
                AVLoading.setVisibility(View.GONE);
            }else {
                boolean loginState= LoginSPUtils.isLogin();//获取登录状态
                if(!loginState){
                    CustomLotteryList.saveFiles();
                    AVLoading.setVisibility(View.GONE);
                    finish();
                }else{
                    upLoadCustom();
                }
            }
        }));
    }
    //登陆时点击完成 上传成功
    public void setLoginUpLoadEnd(){
        AVLoading.setVisibility(View.GONE);
        finish();//返回
    }
    private void initNet() {
        NetHelper.LOTTERY_API.getLotteryProvince(provinceId,lotteryType).subscribe(new SafeOnlyNextSubscriber<LotteryProvinceModel>(){
            @Override
            public void onNext(LotteryProvinceModel args) {
                super.onNext(args);
                List<LotteryProvinceModel.LotteryDetail> list=args.getAreaLotteryList();
                adapter.LoadData(list);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

    private void initView() {
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        gv = (MyGridView) findViewById(R.id.custom_detail_gv);
        tv_finish=(TextView)findViewById(R.id.toolbar_text);
        adapter=new CustomProvinceDetailAdapter(CustomProvinceDetailActivity.this);
        gv.setAdapter(adapter);
        tv_finish.setText("完成");
        adapter.setCall(this);
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
                showDialog();
                return true;
            }
        }
    }
    //展示选6弹窗
    public void showDialog(){
        if(dialog==null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomProvinceDetailActivity.this);
            View view = LayoutInflater.from(CustomProvinceDetailActivity.this).inflate(R.layout.custom_lottery_full, null);
            builder.setView(view);
            builder.setCancelable(false);
            view.findViewById(R.id.custom_dialog_btn).setOnClickListener((v -> {dialog.dismiss();}));
            dialog=builder.show();
        }else{
            dialog.show();
        }
    }
}
