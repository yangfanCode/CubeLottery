package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.NoDoubleClickListener;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.LoginModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class WeChatLoginActivity extends BaseActivity {
    private CheckBox check_agree;
    private Button btn_login;
    private UMShareAPI mShareAPI;
    private SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
    private UMAuthListener umAuthListener;//微信登陆回调
    private String customLottery = "10002,10088,10001,10003";//默认
    private TextView tv_term;
    private boolean click=true;//是否可点击

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_login);
        setMainTitle("登录");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();
        initListener();
    }

    private void initListener() {
        check_agree.setOnCheckedChangeListener(((buttonView, isChecked) -> {
        if (isChecked) {
                btn_login.setBackgroundResource(R.mipmap.btn_login_n);
                btn_login.setEnabled(true);
            } else {
                btn_login.setBackgroundResource(R.mipmap.btn_login_h);
                btn_login.setEnabled(false);
            }
        }));
        btn_login.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!CommonUtil.isNetworkConnected(WeChatLoginActivity.this)) {//断网机制
                    TipsToast.showTips("请检查网络设置");
                    return;
                }
                /**
                 * 如果没有安装微信,友盟
                 */
                if (!mShareAPI.isInstall(WeChatLoginActivity.this, SHARE_MEDIA.WEIXIN)) {
                    TipsToast.showTips("请安装微信");
                    return;
                }
                /**每次都调授权页面**/
                UMShareConfig config = new UMShareConfig();
                config.isNeedAuthOnGetUserInfo(true);
                UMShareAPI.get(WeChatLoginActivity.this).setShareConfig(config);
                mShareAPI.getPlatformInfo(WeChatLoginActivity.this, platform, umAuthListener);
            }
        });
        umAuthListener = new UMAuthListener() {//友盟登陆回调
            @Override
            public void onStart(SHARE_MEDIA platform) {
                //授权开始的回调

            }

            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                //成功登陆
                String openid = data.get("openid");
                String unionid = data.get("unionid");
                String name = data.get("name");
                String iconurl = data.get("iconurl");
                login(openid, unionid, name, iconurl);
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "登录失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "取消登录", Toast.LENGTH_SHORT).show();
            }
        };
        tv_term.setOnClickListener((v -> startActivity(new Intent(WeChatLoginActivity.this, Termctivity.class))));
    }

    private void initView() {
        mShareAPI = UMShareAPI.get(WeChatLoginActivity.this);
        check_agree = (CheckBox) findViewById(R.id.weChart_checkBox);
        btn_login = (Button) findViewById(R.id.weChart_icon_btnLogin);
        tv_term = (TextView) findViewById(R.id.tv_term);
        customLottery=CommonUtil.getNoLoginLotteryId();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
    //登录
    private void login(String openid, String unionid, String name, String iconurl) {
        NetHelper.USER_API.WeChartLogin(openid, unionid, CommonUtil.getIMEI(),iconurl,name,0).subscribe(new SafeOnlyNextSubscriber<LoginModel>() {
            @Override
            public void onNext(LoginModel args) {
                super.onNext(args);
                int id = args.getData().getId();//唯一标识
                String nikeName = args.getData().getNikeName();//昵称
                boolean isCustom=args.isCustom();
                LoginSPUtils.put("id", id);//昵称
                LoginSPUtils.put("iconurl", iconurl);//头像
                LoginSPUtils.put("isLogin", true);//登录状态
                if (TextUtils.isEmpty(name)) {//微信昵称空 用默认
                    LoginSPUtils.put("name", nikeName);//昵称
                } else {
                    LoginSPUtils.put("name", name);//昵称
                }
                if(isCustom){//帐号已定制
                    finish();
                }else{//帐号未定制
                    upload(id);//上传定制结果
                }
                GuideFlag.isEdited=true;//切换登录状态后走势刷新
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
            }
        });
    }
    //同步本地数据
    public void upload(int id){
        NetHelper.USER_API.setLoginCustom(id, customLottery, NetConst.VERSION_CONTROL_ID).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
            @Override
            public void onNext(FlagModel args) {
                super.onNext(args);
                if (args.getFlag() == 0) {
                    TipsToast.showTips("上传定制结果失败");
                }
                finish();
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                TipsToast.showTips("请检查网络设置");
                finish();
            }
        });
    }
}
