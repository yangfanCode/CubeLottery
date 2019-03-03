package com.cp2y.cube.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;
import com.cp2y.cube.widgets.wheel.WheelFactory;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;

import java.util.List;

import cn.qqtheme.framework.picker.LinkagePicker;

/**
 * Created by js on 2017/1/13.
 */
public class SumDialog extends ConditionDialog {
    /*0双色球,1大乐透,2福彩3D,3排列3,4排列5*/
    private int flag=-1;
    private AlertDialog dialog = null;
    private TextView tv_singel = null;
    private EditText et_sumnum = null;
    private TextView tv_wheel = null, tv_sumnum_range = null;
    private SwitchButton my_sunnum_switch = null;
    private Context context;
    private SumCall call;
    private int start = -1, end = -1;//福彩3D范围包括0 遂取值-1
    private String sums = "";
    private SparseIntArray sumStart=new SparseIntArray(){{put(0,22);put(1,18);put(2,0);put(3,0);put(4,0);put(5,0);put(6,0);put(7,0);}};//和值范围开始
    private SparseIntArray sumEnd=new SparseIntArray(){{put(0,199);put(1,188);put(2,27);put(3,27);put(4,45);put(5,27);put(6,45);put(7,18);}};//和值范围结束
    private WheelConfig[] wheelConfigs={WheelConfig.SUM_WHEEL_CONFIG,WheelConfig.LOTTO_SUM_WHEEL_CONFIG,WheelConfig.D3_SUM_WHEEL_CONFIG,WheelConfig.D3_SUM_WHEEL_CONFIG,
            WheelConfig.P5_SUM_WHEEL_CONFIG,WheelConfig.D3_SUM_WHEEL_CONFIG,WheelConfig.P5_SUM_WHEEL_CONFIG,WheelConfig.CQ2_SUM_WHEEL_CONFIG};

    public SumDialog(Context context, SumCall call) {
        this.context = context;
        this.call = call;
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return start != -1 || !TextUtils.isEmpty(sums);
    }

    public Range getRange() {
        return new Range(start, end);
    }

    public Integer[] getSums() {
        if (TextUtils.isEmpty(sums)) return new Integer[0];
        String[] sumArr = sums.replace("，",",").split(",");
        Integer[] arr = new Integer[sumArr.length];
        for (int i = 0; i < sumArr.length; i++) {
            arr[i] = CommonUtil.parseInt(sumArr[i]);
        }
        return arr;
    }

    public String getSumStr() {
        return sums;
    }

    public void reset() {
        isChecked = true;
        sums = "";
        start = end = -1;
    }

    public void setRange(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public void initSums(List<String> list) {
        StringBuilder stringBuilder=new StringBuilder();
        for (String sum:list) {
            stringBuilder.append(sum).append(",");
        }
        sums=stringBuilder.toString();
    }
    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_sum = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View sunnum_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_sumnum, null);
            builder_sum.setView(sunnum_view);
            builder_sum.setCancelable(false);
            Button PositiveBtn= (Button) sunnum_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) sunnum_view.findViewById(R.id.NegativeButton);
            et_sumnum = (EditText) sunnum_view.findViewById(R.id.alert_dialog_sumnum_et);
            TextView tv_sunnum_title = (TextView) sunnum_view.findViewById(R.id.custom_filter_tip_title);
            TextView tv_sumnum_switch = (TextView) sunnum_view.findViewById(R.id.custom_filter_tip_tvSwitch);
            RelativeLayout tip_title_layout= (RelativeLayout) sunnum_view.findViewById(R.id.tip_title_layout);
            tv_sumnum_range = (TextView) sunnum_view.findViewById(R.id.alert_dialog_sumnum_layout_tvRange);
            tv_singel = (TextView) sunnum_view.findViewById(R.id.alert_dialog_sumnum_layout_tvSingel);
            tv_wheel = (TextView) sunnum_view.findViewById(R.id.alert_dialog_sumnum_layout_tvAlertWheel);
            RelativeLayout tip_layout = (RelativeLayout) sunnum_view.findViewById(R.id.alert_dialog_sumnum_layout_tip);
            my_sunnum_switch = (SwitchButton) sunnum_view.findViewById(R.id.custom_filter_tip_switch);
            tv_sunnum_title.setText("和值");
            tip_title_layout.setOnClickListener(view -> tip_layout.setVisibility(tip_layout.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE));
            et_sumnum.setOnFocusChangeListener((view1, hasFocus) -> {
                if (hasFocus) {
                    tv_singel.setTextColor(ColorUtils.MID_BLUE);
                    et_sumnum.setBackgroundResource(R.drawable.alertdialog_myfilter_history_select);
                }
            });
            tv_sumnum_switch.setText("包含");
            tv_wheel.setHint(sumStart.get(flag)+"-"+sumEnd.get(flag));
            my_sunnum_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> tv_sumnum_switch.setText(isChecked?"包含":"排除"));
            tv_wheel.setOnClickListener(view -> {
                LinkagePicker picker=null;
                    picker = WheelFactory.generateWheelWithConfig((Activity) context, wheelConfigs[flag]);
                    if (start == -1) {
                        picker.setSelectedIndex(0, (sumEnd.get(flag)-sumStart.get(flag)));//滚轮默认坐标位置
                    } else {
                        picker.setSelectedIndex(start - sumStart.get(flag), end - sumStart.get(flag));//选择后的滚轮位置
                    }
                picker.setOnLinkageListener((first, second, third) -> {
                    int start = Integer.valueOf(first);
                    int end = Integer.valueOf(second);
                    tv_wheel.setText(String.valueOf(Math.min(start,end)).concat("-").concat(String.valueOf(Math.max(start,end))));
                    tv_wheel.setTextColor(ColorUtils.MID_BLUE);
                });

                picker.show();
                tv_sumnum_range.setTextColor(ColorUtils.MID_BLUE);
                tv_wheel.setTextColor(ColorUtils.MID_BLUE);
                tv_wheel.setBackgroundResource(R.mipmap.gltj_btn_xiala_150_pre);
            });
            PositiveBtn.setOnClickListener((v -> {
                String range = tv_wheel.getText().toString().trim();
                String sums = et_sumnum.getText().toString().trim();
                if (!checkSumRegex(sums)) {
                    TipsToast.showTips("请输入"+sumStart.get(flag)+"-"+sumEnd.get(flag)+"和值，多个和值“,”逗号隔开");
                    return;
                }
                if (!TextUtils.isEmpty(range)) {
                    String[] arr = range.split("-");
                    start = Integer.valueOf(arr[0]);
                    end = Integer.valueOf(arr[1]);
                }
                isChecked = my_sunnum_switch.isChecked();
                this.sums = sums;
                if (call != null) call.onSubmit(SumDialog.this);
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_sum.show();
        } else {
            dialog.show();
        }
        boolean isWheel = !(start==-1&&end==-1);
        my_sunnum_switch.setChecked(isChecked);
        tv_wheel.setText(isWheel?String.valueOf(start).concat("-".concat(String.valueOf(end))):null);
        tv_wheel.setTextColor(isWheel?ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_wheel.setBackgroundResource(isWheel?R.mipmap.gltj_btn_xiala_150_pre:R.mipmap.gltj_btn_xiala_150);
        tv_sumnum_range.setTextColor(isWheel?ColorUtils.MID_BLUE:ColorUtils.GRAY);
        boolean hasInput = sums.trim().length() > 0;
        et_sumnum.setText(sums);
        //光标定位最后
        if(!TextUtils.isEmpty(sums)){
            et_sumnum.setSelection(sums.length());
        }
//        tv_singel.setTextColor(hasInput?ColorUtils.MID_BLUE:ColorUtils.GRAY);
//        et_sumnum.setBackgroundResource(hasInput?R.drawable.alertdialog_myfilter_history_select:R.drawable.alertdialog_myfilter_history_nomal);
    }

    public interface SumCall {
        void onSubmit(SumDialog dialog);
    }
    //检查和值格式
    public boolean checkSumRegex(String sums){
        boolean isRegex=false;
        if(flag==0){
            isRegex=CommonUtil.isValidSumValue(sums);
        }else if(flag==1){
            isRegex=CommonUtil.isLottoValidSumValue(sums);
        }else if(flag==2||flag==3||flag==5){
            isRegex=CommonUtil.is3DValidSumValue(sums);
        }else if(flag==4||flag==6){
            isRegex=CommonUtil.isP5ValidSumValue(sums);
        }else if(flag==7){
            isRegex=CommonUtil.isCQ2ValidSumValue(sums);
        }
        return isRegex;
    }
}
