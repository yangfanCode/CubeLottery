package com.cp2y.cube.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.SingletonNewsGray;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ACacheHelper;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.VersionControlModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.utils.LogUtil;
import com.cp2y.cube.utils.LoginSPUtils;

public class SettingActivity extends BaseActivity {

    private RelativeLayout clear_cache,version,push_layout;
    private TextView setting_cacheSize;
    private AlertDialog dialog = null;
    private ImageView versionControl_icon;
    private String message_text,title_text,url_text;
    private TextView version_name,btn_NoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        setMainTitle("设置");
        initView();
        VersionControl();
        initListener();
        initData();
        updatePushCustom();//更新推送设置
    }
    //检查版本
    public void VersionControl(){
        NetHelper.LOTTERY_API.versionControl(0).subscribe(new SafeOnlyNextSubscriber<VersionControlModel>(){
            @Override
            public void onNext(VersionControlModel args) {
                super.onNext(args);
                //服务器获取版本号大于app版本号
                if(args.getItem().getCurrentVersionid()> CommonUtil.getVersionId(SettingActivity.this)){
                    versionControl_icon.setVisibility(View.VISIBLE);//显示小红点
                    message_text=args.getItem().getDesc();
                    title_text=args.getItem().getCurrentVersiontitle();
                    url_text=args.getItem().getUrl();
                }else{
                    versionControl_icon.setVisibility(View.GONE);//隐藏小红点
                }
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }

    private void initData() {
        setting_cacheSize.setText(FileUtils.getCacheSize());
        version_name.setText(CommonUtil.getVersionName(this));
    }

    private void initListener() {
        clear_cache.setOnClickListener((v -> {
            FileUtils.cleanAllInternalCache();//清除缓存
            FileUtils.cleanAllInternalFiles();//清除文件
            SingletonNewsGray.clearData();//清除新闻置灰
            ACacheHelper.clear();//清除json缓存
            setting_cacheSize.setText(FileUtils.getCacheSize());
            TipsToast.showTips("缓存已清理");
        }));
        version.setOnClickListener(v -> {
            if(versionControl_icon.getVisibility()==View.GONE){
                TipsToast.showTips("已是最新版本");
                return;
            }//小红点显示弹窗
            if(dialog==null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                View view = LayoutInflater.from(SettingActivity.this).inflate(R.layout.app_update_dialog, null);
                builder.setView(view);
                builder.setCancelable(false);
                Button NegativeButton = (Button) view.findViewById(R.id.NegativeButton);
                Button PositiveButton = (Button) view.findViewById(R.id.PositiveButton);
                TextView message = (TextView) view.findViewById(R.id.app_update_message);
                TextView title = (TextView) view.findViewById(R.id.app_update_title);
                message.setText(message_text);
                title.setText(title_text);
                PositiveButton.setOnClickListener((v1 -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_text));
                        startActivity(intent);
                    } catch (Exception e) {
                        TipsToast.showTips( "浏览器打开失败");
                    }
                }));
                NegativeButton.
                        setOnClickListener((v1 -> dialog.dismiss()));
                dialog=builder.show();
            }else{
                dialog.show();
            }
        });
        btn_NoLogin.setOnClickListener((v -> {//退出登录
            GuideFlag.isEdited=true;//走势刷新
            LoginSPUtils.clear(SettingActivity.this);//清除所有登录数据,保留用户openid
            CustomLotteryList.synchronizedData();//同步本地定制数据到内存
            //CommonUtil.jPushAlias(false, CommonUtil.getHxImUserIdByAppUserId(String.valueOf(CommonUtil.getUserId())));//退出登录清空alias
            finish();
        }));
        push_layout.setOnClickListener((v -> CommonUtil.openActicity(SettingActivity.this,PushSettingActivity.class,null)));
    }

    private void initView() {
        setting_cacheSize = (TextView) findViewById(R.id.setting_cacheSize);
        push_layout = (RelativeLayout) findViewById(R.id.setting_layout0);
        clear_cache = (RelativeLayout) findViewById(R.id.setting_layout1);
        version = (RelativeLayout) findViewById(R.id.setting_layout2);
        version_name = (TextView) findViewById(R.id.setting_versionNo);
        versionControl_icon = (ImageView) findViewById(R.id.versionControl_icon);
        btn_NoLogin = (TextView) findViewById(R.id.btn_WeChat_NoLogin);
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        btn_NoLogin.setVisibility(loginState?View.VISIBLE:View.GONE);
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
