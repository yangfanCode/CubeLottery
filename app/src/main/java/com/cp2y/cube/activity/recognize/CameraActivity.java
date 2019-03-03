package com.cp2y.cube.activity.recognize;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.utils.BitmapUtil;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MediaUtil;
import com.cp2y.cube.utils.PermissionsChecker;
import com.cp2y.cube.utils.ReadBase64;
import com.google.android.cameraview.CameraView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 彩票扫描页
 */
public class CameraActivity extends BaseActivity implements SensorEventListener {
    private boolean shouldRequest = false;//是否有权限
    private static final int RESULT_LOAD_IMAGE = 100;//系统相册
    private AlertDialog dialog = null;
    private ImageView mCameraScan;
    private Animation mAnimation;
    private CameraView cameraView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float[] mLastEvent = new float[3];
    private static final double ACC_THRESHOLD = 0.025d;//加速度阈值
    private AtomicInteger mSensorCount = new AtomicInteger(5);//计数器
    private CountDownTimer timer;
    private AtomicBoolean mAutoTake = new AtomicBoolean();
    private AtomicBoolean mAlreadyTake = new AtomicBoolean();
    static final int UPDATE_INTERVAL = 20;
    long mLastUpdateTime;
    private ImageView back;
    private String brand = android.os.Build.BRAND;
    private ImageView tip, pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        isShowSystemStatusBar(false);//隐藏系统状态栏
        setContentView(R.layout.camera_activity);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        mCameraScan = (ImageView) findViewById(R.id.camera_scan);
        pic = (ImageView) findViewById(R.id.camera_picture);
        tip = (ImageView) findViewById(R.id.camera_tip);
        back = (ImageView) findViewById(R.id.camera_picture_back);
        mAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0.92f);
        mAnimation.setDuration(3500);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mCameraScan.startAnimation(mAnimation);
        checkPermisson();//检查权限
        initTimer();
        initListener();
        if(!shouldRequest){
            finishCamera();
        }else{
            try {
                if (cameraView != null) {
                    cameraView.addCallback(callback);
                }
                cameraView.setAutoFocus(true);
                cameraView.setAdjustViewBounds(true);//调整范围
                cameraView.start();
            } catch (Exception e) {
                e.printStackTrace();
                finishCamera();
            }
        }
        if (!CommonUtil.isNetworkConnected(CameraActivity.this)) {
            TipsToast.showTips("请检查网络设置");
        }
    }
    private void finishCamera() {
        TipsToast.showTips("请开启相机权限");
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    //判断相机权限
    public void checkPermisson(){
        PermissionsChecker checker=new PermissionsChecker(CameraActivity.this);
        boolean permission = checker.lacksPermissions(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO);
        if (permission) {//缺少相机权限
            shouldRequest=false;
        }else {//有相机权限
            shouldRequest=true;
        }
    }

    private void initListener() {
        pic.setOnClickListener((v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }));
        back.setOnClickListener((v -> finish()));
        tip.setOnClickListener((v -> {
            mAlreadyTake.set(true);//弹窗开启禁止拍照
            if (dialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
                View view = LayoutInflater.from(CameraActivity.this).inflate(R.layout.scan_tip, null);
                builder.setView(view);
                Button IKnow = (Button) view.findViewById(R.id.scan_show_btn);
                IKnow.setOnClickListener((v1 -> {
                    mAlreadyTake.set(false);//关闭弹窗可以拍照
                    mSensorCount.set(5);
                    dialog.dismiss();
                }));
                dialog = builder.show();
            } else {
                dialog.show();
            }
        }));
    }

    private void initTimer() {
        timer = new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // TODO Auto-generated method stub
                if (mSensorCount.getAndDecrement() == 0) {//一个计时周期
                    mSensorCount.set(5);
                }
            }

            @Override
            public void onFinish() {
                // TODO Auto-generated method stub

            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {

                String picPath = MediaUtil.getPathFromUri(this, data.getData());
                mAlreadyTake.set(true);
                Bitmap bmp = BitmapFactory.decodeFile(picPath); //使用bmp 进行识别
                BitmapCompress(bmp);//压缩处理bitmap
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorCount.set(5);
        if (timer != null) {
            timer.start();
        } else {
            initTimer();
            timer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        cameraView.stop();
        if (timer != null) {//防止new多个timer计时不准
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        long currentTime = System.currentTimeMillis();
        long diffTime = currentTime - mLastUpdateTime;
        if (diffTime < UPDATE_INTERVAL)
            return;
        mLastUpdateTime = currentTime;
        float xValue = event.values[0];// Acceleration minus GX on the x-axis
        float yValue = event.values[1];//Acceleration minus GY on the y-axis
        float zValue = event.values[2];//Acceleration minus GZ on the z-axis
        float dx = mLastEvent[0] - xValue;
        float dy = mLastEvent[1] - yValue;
        float dz = mLastEvent[2] - zValue;
        double dt = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2) + Math.pow(dz, 2)) / diffTime;
        if (dt > ACC_THRESHOLD) {//晃动太大
            mAutoTake.set(false);
            mSensorCount.set(5);
        } else if (mSensorCount.get() <= 1) {//5秒中之内没啥晃动的话认为可以拍照了
            mAutoTake.set(true);
        }
        mLastEvent[0] = xValue;
        mLastEvent[1] = yValue;
        mLastEvent[2] = zValue;
        if (mAutoTake.get() && !mAlreadyTake.get()) {//对焦结束并且没有晃动的话就自动拍照
            mAutoTake.set(false);
            if (mSensorCount.get() <= 1) {
                mSensorCount.set(5);
            }
            mAlreadyTake.set(true);
            takePhoto();
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        restartPreview();
    }

    private void restartPreview() {
        try {
            mAlreadyTake.set(false);
            cameraView.setAutoFocus(true);
            cameraView.setAdjustViewBounds(true);//调整范围
            cameraView.start();
            mSensorCount.set(5);
            initTimer();
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("请开启相机权限");
        }
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            cameraView.takePicture();
            TipsToast.showTips("正在处理中...");
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("相机开启失败,请确认权限后重试");
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private CameraView.Callback callback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, byte[] data) {
            super.onPictureTaken(cameraView, data);
            try {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                BitmapCompress(bitmap);//压缩处理bitmap
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //压缩处理bitmap
    private void BitmapCompress(Bitmap bitmap) {
        Bitmap newBit = null;//缩放c
        try {
            newBit = BitmapUtil.getScaleBitmap(bitmap, 650, 730);//压缩分辨率 240K左右
            newBit = BitmapUtil.compressImage(newBit);//压缩质量
            //save(newBit);
            if (brand.equals("samsung")) {//三星旋转
                newBit = BitmapUtil.rotaingImageView(90, newBit);
            }
            Intent intent = new Intent(CameraActivity.this, CheckLotteryActivity.class);
            String image = ReadBase64.imgToBase64(newBit);
            bitmap.recycle();//释放原数据
            newBit.recycle();
            intent.putExtra("image", image);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showTips("读取失败,请重试");
            mAlreadyTake.set(false);
        }
    }

    public void save(Bitmap bitmap) {
        try {
            boolean success = BitmapUtil.saveMyBitmap(bitmap, "lottery2");
            if (success) {
                TipsToast.showTips("保存成功!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //显示与隐藏系统状态栏
    public void isShowSystemStatusBar(boolean isShow) {
        if (!isShow) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(lp);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
}
