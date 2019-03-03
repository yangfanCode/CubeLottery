package com.cp2y.cube.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.DisplayUtil;

/**
 * create by yangfan 2016/11/25
 */

public class NotLoginForOptional extends LinearLayout {
    private TextView tvStatus, tvLogin;
    private ImageView imvStatus;

    private TypeEnum typeEnum = TypeEnum.NOT_LOGIN;

    public NotLoginForOptional(Context context) {
        this(context, null);
    }

    public NotLoginForOptional(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        imvStatus = new ImageView(context);
        imvStatus.setImageResource(R.mipmap.gz_rocket);
        LinearLayout.LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = DisplayUtil.dip2px(89f);
        addView(imvStatus, layoutParams);
        tvStatus = new TextView(context);
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int pad15 = DisplayUtil.dip2px(15f);
        layoutParams.leftMargin = pad15;
        layoutParams.rightMargin = pad15;
        layoutParams.topMargin = DisplayUtil.dip2px(37f);
        layoutParams.bottomMargin = DisplayUtil.dip2px(150f);
        tvStatus.setGravity(Gravity.CENTER);
        tvStatus.setTextColor(ColorUtils.COLOR_C3C2C2);
        tvStatus.setText(R.string.no_push_single);
        addView(tvStatus, layoutParams);
        tvLogin = new TextView(context);
        tvLogin.setText(R.string.no_login);
        tvLogin.setGravity(Gravity.CENTER);
        tvLogin.setTextColor(ColorUtils.MID_BLUE);
        tvLogin.setBackgroundResource(R.drawable.nologinfor_optional_bg);
        int pad4 = DisplayUtil.dip2px(4f);
        int pad45 = DisplayUtil.dip2px(45f);
        tvLogin.setPadding(pad45, pad4, pad45, pad4);

        addView(tvLogin, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setTypeEnum(typeEnum);
    }

    public void setTextToTvStatus(int textId) {
        tvStatus.setText(textId);
    }

    public void setTextToTvLogin(int textId) {
        tvLogin.setText(textId);
    }

    public void setTextNoBackground() {
        tvLogin.setBackground(null);
    }

    public TextView getTvStatus() {
        return tvStatus;
    }

    public TextView getTvLogin() {
        return tvLogin;
    }

    public ImageView getImvStatus() {
        return imvStatus;
    }

    public void setTextTvLoginVisibility(int visibility){
        tvLogin.setVisibility(visibility);
    }

    public void setBtnOnClickListener(View.OnClickListener l) {
        tvLogin.setOnClickListener(l);
    }

    public void setTypeEnum(TypeEnum typeEnum) {
        setContent(typeEnum);
    }

    private void setContent(TypeEnum typeEnum) {
        if (typeEnum == null) return;
        this.typeEnum = typeEnum;

        switch (typeEnum) {
            case NOT_LOGIN:
                getImvStatus().setImageResource(R.mipmap.gz_rocket);
                setTextToTvStatus(R.string.no_subscribe_login);
                setTextToTvLogin(R.string.btn_go_login);
                break;
            case NO_PUSH:
                getImvStatus().setImageResource(R.mipmap.td_none);
                setTextToTvStatus(R.string.no_push_single);
                setTextToTvLogin(R.string.btn_go_push);
                break;
            case NO_GUANZHU:
                getImvStatus().setImageResource(R.mipmap.gz_gz);
                setTextToTvStatus(R.string.no_subscribe);
                setTextToTvLogin(R.string.btn_go_rank_look);
                setTextNoBackground();
                break;
            case NO_MY_GUANZHU:
                getImvStatus().setImageResource(R.mipmap.gz_gz);
                setTextToTvStatus(R.string.no_my_subscribe);
                setTextToTvLogin(R.string.btn_go_rank);
                break;
            case NO_FANS:
                getImvStatus().setImageResource(R.mipmap.fs_fans);
                setTextToTvStatus(R.string.no_fans);
                setTextToTvLogin(R.string.btn_go_push);
                break;
            case NO_SELECTPUSH:
                getImvStatus().setImageResource(R.mipmap.td_empty);
                setTextToTvStatus(R.string.no_numLibrary_push);
                setTextTvLoginVisibility(GONE);
                break;
        }
    }

    public enum TypeEnum {
        NOT_LOGIN("未登录"),
        NO_PUSH("无推单"),
        NO_GUANZHU("无关注"),
        NO_MY_GUANZHU("无我的关注"),
        NO_FANS("无我的粉丝"),
        NO_SELECTPUSH("无选单");

        private String typeDesc;


        TypeEnum(String typeDesc) {
            this.typeDesc = typeDesc;
        }
    }
}