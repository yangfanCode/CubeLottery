package com.cp2y.cube.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.FirstAppSPUtils;
import com.cp2y.cube.utils.LogUtil;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatConfig;
import com.tencent.stat.StatService;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends BaseActivity {
    private boolean shouldRequest = false;//是否有权限
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StatConfig.init(this);
        //StatConfig.setDebugEnable(true);
        //StatService.trackCustomEvent(this, "onCreate", "");
        String appkey = "Aqc1105994284";
        CommonUtil.jPushAlias(true, CommonUtil.getHxImUserIdByAppUserId(String.valueOf(CommonUtil.getUserId())));
        updatePushCustom();//同步推送数据
        try {
            // 第三个参数必须为：com.tencent.stat.common.StatConstants.VERSION
            StatService.startStatService(this, appkey,
                    com.tencent.stat.common.StatConstants.VERSION);
        } catch (MtaSDkException e) {
            // MTA初始化失败
            Log.e("MTA start failed.", "onCreate: "+e);
        }
        //检测是否添加权限   PERMISSION_GRANTED  表示已经授权并可以使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,Manifest.permission.READ_PHONE_STATE};
            for (String permission:permissions){
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        shouldRequest = true;
                }
            }
            if (shouldRequest) {
                ActivityCompat.requestPermissions(this,  permissions, 100);
            }else{
                //请求过权限之后执行timer
                initTimer();
            }
        }else{//6.0以前执行timer
            initTimer();
        }
    }
    //原版 跳转
    private void initTimer() {
        Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                //判断是否进入过程序
                int verId = (int) FirstAppSPUtils.get(getApplicationContext(), "isFirstAppVer", 0);
                if(verId!= CommonUtil.getVersionId(SplashActivity.this)){
                    Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

//    //临时改版 跳转
//    private void initTimer() {
//        Timer timer=new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                //判断是否进入过程序
//                int verId = (int) FirstAppSPUtils.get(getApplicationContext(), "isFirstAppVer", 0);
//                if(verId!= CommonUtil.getVersionId(SplashActivity.this)){
//                    if(!CommonUtil.isNetworkConnected(SplashActivity.this)) {//断网机制
//                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }else{
//                        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }else{
//                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        }, 2000);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int res: grantResults){
                if (res == PackageManager.PERMISSION_GRANTED) {
                    shouldRequest = false;
                }else{
                    TipsToast.showTips("请开启权限");
                }
            }
            //请求权限之后执行timer
            initTimer();
        }
    }
    private void updatePushCustom(){
        NetHelper.LOTTERY_API.updateCustomPush(CommonUtil.getUserId(),0).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if(args.getFlag()==1) LogUtil.LogE(SplashActivity.class,"updatePushCustom");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });
    }

}
