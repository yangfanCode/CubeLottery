package com.cp2y.cube.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryUploadModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;

public class FeedbackActivity extends BaseActivity {
    private static final int RESULT_LOAD_IMAGE = 100;
    private TextView tv_submit;
    private ImageView iv_Pic, iv_Del;
    private String file_img = null;
    private EditText et_content;
    private EditText et_phone;
    private String content, phone;
    private File img = null;
    private RelativeLayout feedback_phoneLayout;
    private AVLoadingIndicatorView AVLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        setMainTitle("意见反馈");
        initView();
        initListener();
    }

    private void initData() {
        content = et_content.getText().toString();
        phone = et_phone.getText().toString();
    }

    private void initListener() {
        tv_submit.setOnClickListener((v -> {
            initData();
            if (!CommonUtil.isMobileNO(phone)) {
                TipsToast.showTips("请输入11位手机号码");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                TipsToast.showTips("请输入手机号码");
                return;
            }
            if (TextUtils.isEmpty(content)) {
                TipsToast.showTips("请输入您的意见反馈");
                return;
            }
            if (file_img == null) {
                img = null;
            } else {
                img = new File(file_img);
            }
            AVLoading.setVisibility(View.VISIBLE);
            NetHelper.LOTTERY_API.getFeedBack(content, phone, "Android", 0, img).subscribe(new SafeOnlyNextSubscriber<LotteryUploadModel>() {
                @Override
                public void onNext(LotteryUploadModel args) {
                    super.onNext(args);
                    AVLoading.setVisibility(View.GONE);
                    if (args.getFlag() == 1) {
                        TipsToast.showTips("您的意见反馈已提交，有疑问客服MM会及时与您取得联系");
                        startActivity(new Intent(FeedbackActivity.this, MainActivity.class));
                    } else {
                        TipsToast.showTips("提交失败,请检查网络设置");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    try {
                        AVLoading.setVisibility(View.GONE);
                        if (ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            TipsToast.showTips("请开启权限");
                        } else {
                            TipsToast.showTips("提交失败,请检查网络设置");
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }));
        iv_Pic.setOnClickListener((v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }));
        iv_Del.setOnClickListener((v -> {
            //删除之后隐藏删除,还原图片
            iv_Del.setVisibility(View.GONE);
            iv_Pic.setImageResource(R.mipmap.feedback_pic);
            iv_Pic.setEnabled(true);
        }));
        feedback_phoneLayout.setOnClickListener((v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "400-666-7575"));//跳转到拨号界面，同时传递电话号码
            startActivity(dialIntent);
        }));
    }

    private void initView() {
        AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        tv_submit = (TextView) findViewById(R.id.toolbar_submit);
        iv_Pic = (ImageView) findViewById(R.id.feedback_ivPic);
        iv_Del = (ImageView) findViewById(R.id.feedback_ivPicDel);
        et_content = (EditText) findViewById(R.id.feedback_etContent);
        et_phone = (EditText) findViewById(R.id.feedback_etPhone);
        feedback_phoneLayout = (RelativeLayout) findViewById(R.id.feedback_phoneLayout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            //点击之后不可点击 显示删除 展示图片
            iv_Pic.setEnabled(false);
            iv_Del.setVisibility(View.VISIBLE);
            iv_Pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            file_img = picturePath;
        }
    }
}
