package com.cp2y.cube.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by js on 2017/1/13.
 */
public class DoubleNumDialog extends ConditionDialog {
    //福彩3D双马
    private AlertDialog dialog = null;
    private Context context;
    private String period = null;
    private EditText history_et_input;
    private SwitchButton my_switch = null;
    private DoubleNumCall call;

    private List<String[]> list = new ArrayList<>();//双码数据

    public List<String[]> getList() {
        list.clear();
        //处理数据 类型转换
        String[] nums=period.replace("，",",").split(",");
        for(int i=0;i<nums.length;i++){
            String[] str_nums=new String[1];
            str_nums[0]=nums[i];
            list.add(str_nums);
        }
        return list;
    }

    public String getPeriod() {
        return period;
    }

    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return !TextUtils.isEmpty(period);
    }

    public void reset() {
        isChecked = true;
        period = null;
    }
    public String getSumStr() {
        return period;
    }
    public DoubleNumDialog(Context context, DoubleNumCall call) {
        this.context = context;
        this.call = call;
        isChecked = true;
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_historyNum = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View doubleNum_view = inflater.inflate(R.layout.alertdialog_filter_doublenum, null);
            //自定义title
            //添加自定义VIew
            builder_historyNum.setView(doubleNum_view);
            builder_historyNum.setCancelable(false);
            Button PositiveBtn= (Button) doubleNum_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) doubleNum_view.findViewById(R.id.NegativeButton);
            history_et_input = (EditText) doubleNum_view.findViewById(R.id.alertdialog_filter_historynum_et);
            TextView tv_title = (TextView) doubleNum_view.findViewById(R.id.custom_filter_history_title);
            TextView tv_switch = (TextView) doubleNum_view.findViewById(R.id.custom_filter_history_tvSwitch);
            RelativeLayout tip_title_layout= (RelativeLayout) doubleNum_view.findViewById(R.id.tip_title_layout);
            RelativeLayout tip_layout = (RelativeLayout) doubleNum_view.findViewById(R.id.alert_dialog_doubleNum_layout_tip);
            my_switch = (SwitchButton) doubleNum_view.findViewById(R.id.custom_filter_history_switch);
            my_switch.setWidth(26);
            my_switch.setHeight(18);
            tv_title.setText("双码");
            tv_switch.setText("包含");
            my_switch.setChecked(true);
            tip_title_layout.setOnClickListener((v1 -> tip_layout.setVisibility(tip_layout.getVisibility()==View.VISIBLE?View.GONE:View.VISIBLE)));
            history_et_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        history_et_input.setBackgroundResource(R.drawable.alertdialog_myfilter_history_select);
                    }
                }
            });
            my_switch.setOnCheckedChangeListener((compoundButton, checked) -> {
                tv_switch.setText(checked?"包含":"排除");
            });
            PositiveBtn.setOnClickListener((v -> {
                String period = history_et_input.getText().toString().trim();
                if (!CommonUtil.is3DValidDoubleNumValue(period)) {//设置了值
                    TipsToast.showTips("请输入00-99的双码组合");
                    return;
                }
                isChecked = my_switch.isChecked();
                this.period = period;
                if (call != null) {
                    call.onSubmit(DoubleNumDialog.this);
                }
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_historyNum.show();
        } else {
            dialog.show();
        }
        my_switch.setChecked(isChecked);
        history_et_input.setText(period);
//        boolean isInput=!(TextUtils.isEmpty(period));
//        tv_openLottery.setTextColor(isInput?ColorUtils.MID_BLUE:ColorUtils.GRAY);
//        tv_date.setTextColor(isInput?ColorUtils.MID_BLUE:ColorUtils.GRAY);
//        history_et_input.setBackgroundResource(isInput?R.drawable.alertdialog_myfilter_history_select:R.drawable.alertdialog_myfilter_history_nomal);
        //光标定位最后
        if(!TextUtils.isEmpty(period)){
            history_et_input.setSelection(period.length());
        }
    }

    public interface DoubleNumCall {
        /**
         * 双码过滤选择回调
         */
        void onSubmit(DoubleNumDialog dialog);
    }

}
