package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.FlagModel;
import com.cp2y.cube.model.LoginModel;
import com.cp2y.cube.network.NetConst;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;

public class PhoneLoginActivity extends BaseActivity {

    private EditText et_phone,et_code;
    private TextView tv_getCode,tv_term;
    private Button btn_login;
    private CheckBox cb;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        setMainTitle("手机号登录");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        initView();//初始化
        initListener();
    }

    private void initListener() {
        et_phone.addTextChangedListener(new TextWatcher() {//手机号验证
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(checkPhoneNum()){//合格电话
                    tv_getCode.setEnabled(true);
                    tv_getCode.setTextColor(ColorUtils.MID_BLUE);
                }else{
                    tv_getCode.setEnabled(false);
                    tv_getCode.setTextColor(ColorUtils.GRAY_WEIGHT);
                }
                btn_login.setEnabled(checkPhoneNum()&&et_code.getText().toString().length()==4);//正确手机号和4位验证码
            }
        });
        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                btn_login.setEnabled(checkPhoneNum()&&et_code.getText().toString().length()==4);//正确手机号和4位验证码
            }
        });
        tv_getCode.setOnClickListener((v -> {
            if(!CommonUtil.isNetworkConnected(PhoneLoginActivity.this)){//断网机制
                TipsToast.showTips("请检查网络设置");
                return;
            }
            if(!checkPhoneNum())return;
            NetHelper.USER_API.getLoginCode(et_phone.getText().toString(),1).subscribe(new SafeOnlyNextSubscriber<FlagModel>(){
                @Override
                public void onNext(FlagModel args) {
                    super.onNext(args);
                    if(args.getFlag()==1){
                        initTimer();//验证码发送成功开始倒计时
                        timer.start();
                    }else{
                        TipsToast.showTips("验证码不存在");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    TipsToast.showNewTips("系统异常，请反馈400-666-7575及时处理");
                }
            });
        }));
        cb.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if(isChecked){
                btn_login.setEnabled(checkPhoneNum()&&et_code.getText().toString().length()==4);//正确手机号和4位验证码
            }else{
                btn_login.setEnabled(false);
            }
        }));//用户协议勾选
        tv_term.setOnClickListener((v -> startActivity(new Intent(PhoneLoginActivity.this, Termctivity.class))));
        btn_login.setOnClickListener((v -> {
            if(!CommonUtil.isNetworkConnected(PhoneLoginActivity.this)){//断网机制
                TipsToast.showTips("请检查网络设置");
                return;
            }
            String phone=et_phone.getText().toString();
            String code=et_code.getText().toString();
            NetHelper.USER_API.PhoneLogin(phone,code,CommonUtil.getIMEI(),0).subscribe(new SafeOnlyNextSubscriber<LoginModel>(){
                @Override
                public void onNext(LoginModel args) {
                    super.onNext(args);
                    if(args.getFlag()==1){
                        int id = args.getData().getId();//唯一标识
                        String mobile=args.getData().getMobile();
                        boolean isCustom=args.isCustom();
                        LoginSPUtils.put("id", id);//昵称
                        LoginSPUtils.put("iconurl", "");//头像
                        LoginSPUtils.put("isLogin", true);//登录状态
                        if (!TextUtils.isEmpty(mobile)) {//微信昵称空 用默认
                            LoginSPUtils.put("name", mobile);//昵称
                        }
                        if(isCustom){//帐号已定制
                            finish();
                        }else{//帐号未定制
                            upload(id);//上传定制结果
                        }
                        GuideFlag.isEdited=true;//切换登录状态后走势刷新
                    }else{
                        TipsToast.showTips("验证码不存在");
                    }
                }

                @Override
                public void onError(Throwable e) {
                    super.onError(e);
                    TipsToast.showNewTips("系统异常，请反馈400-666-7575及时处理");
                }
            });
        }));
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.phone_login_num);
        et_code = (EditText) findViewById(R.id.phone_login_code);
        tv_getCode = (TextView) findViewById(R.id.phone_login_getCode);
        tv_term = (TextView) findViewById(R.id.tv_term);
        btn_login = (Button) findViewById(R.id.phone_icon_btnLogin);
        cb = (CheckBox) findViewById(R.id.phoneLogin_checkBox);
        customLottery=CommonUtil.getNoLoginLotteryId();
    }

    private void initTimer(){
        tv_getCode.setEnabled(false);
        timer=new CountDownTimer(61000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_getCode.setText(millisUntilFinished/1000+"s");
                tv_getCode.setTextColor(ColorUtils.MID_BLUE);
            }

            @Override
            public void onFinish() {
                tv_getCode.setEnabled(true);
                tv_getCode.setText("获取验证码");
                if(timer!=null){
                    timer.cancel();//置空
                    timer=null;//计时结束释放资源,防止对个对象计时不准
                }
            }
        };
    }
    //是否为正确手机号
    public boolean checkPhoneNum(){
        String phone=et_phone.getText().toString();
        return CommonUtil.isMobileNO(phone);
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
