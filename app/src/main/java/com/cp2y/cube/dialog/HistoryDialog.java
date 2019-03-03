package com.cp2y.cube.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryOpenHistoryModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by js on 2017/1/13.
 */
public class HistoryDialog extends ConditionDialog {
    private TextView tv_openLottery,tv_date=null;
    private AlertDialog dialog = null;
    private Context context;
    private String period = null;
    private EditText history_et_input;
    private SwitchButton my_switch = null;
    private HistoryCall call;
    private SparseIntArray lotteryId=new SparseIntArray(){{put(0,10002);put(1,10088);put(2,10001);put(3,10003);put(4,10004);put(5,10093);put(6,10089);put(7,10095);}};
    private SparseIntArray ballCount=new SparseIntArray(){{put(0,7);put(1,7);put(2,3);put(3,3);put(4,5);put(5,3);put(6,5);put(7,2);}};
    /**
     * 0双色球,1大乐透,2福彩3D,3排列3,4排列5,5重庆时时彩3星,6重庆5星,7重庆2星
     */
    private int flag=-1;
    private List<byte[]> list = new ArrayList<>();//历史奖期数据

    public List<byte[]> getList() {
        return list;
    }

    public String getPeriod() {
        return period;
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return !TextUtils.isEmpty(period) && list.size() > 0;
    }

    public void reset() {
        isChecked = false;
        period = null;
    }

    public HistoryDialog(Context context, HistoryCall call) {
        this.context = context;
        this.call = call;
        isChecked = false;
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_historyNum = new AlertDialog.Builder(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            View history_view = inflater.inflate(R.layout.alertdialog_filter_historynum, null);
            //自定义title
            View custom_titleView = inflater.inflate(R.layout.custom_filter_history_title, null);
            //添加自定义VIew
            builder_historyNum.setView(history_view);
            builder_historyNum.setCancelable(false);
            Button PositiveBtn= (Button) history_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) history_view.findViewById(R.id.NegativeButton);
            history_et_input = (EditText) history_view.findViewById(R.id.alertdialog_filter_historynum_et);
            TextView tv_title = (TextView) custom_titleView.findViewById(R.id.custom_filter_history_title);
            tv_openLottery = (TextView) history_view.findViewById(R.id.alertdialog_filter_historynum_tv1);
            tv_date = (TextView) history_view.findViewById(R.id.alertdialog_filter_historynum_tv2);
            TextView tv_switch = (TextView) custom_titleView.findViewById(R.id.custom_filter_history_tvSwitch);
            my_switch = (SwitchButton) custom_titleView.findViewById(R.id.custom_filter_history_switch);
            my_switch.setWidth(26);
            my_switch.setHeight(18);
            tv_title.setText("历史号");
            tv_switch.setText("排除");
            builder_historyNum.setCustomTitle(custom_titleView);
            history_et_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        //history_et_input.setHint("");
                        tv_openLottery.setTextColor(ColorUtils.MID_BLUE);
                        tv_date.setTextColor(ColorUtils.MID_BLUE);
                        history_et_input.setBackgroundResource(R.drawable.alertdialog_myfilter_history_select);
                    }
                }
            });
            my_switch.setOnCheckedChangeListener((compoundButton, checked) -> {
                tv_switch.setText(checked?"包含":"排除");
            });
            PositiveBtn.setOnClickListener((v -> {
                String period = history_et_input.getText().toString().trim();
                int p = CommonUtil.parseInt(period);
                if (p == 0 || p > 1000) {//设置了值
                    TipsToast.showTips("请输入1-1000数字");
                    return;
                } else if (!period.equals(this.period)){//下载历史数据
                    NetHelper.LOTTERY_API.lotteryHistoryOpen(p, lotteryId.get(flag)).subscribe(new SafeOnlyNextSubscriber<LotteryOpenHistoryModel>(){
                        @Override
                        public void onNext(LotteryOpenHistoryModel args) {
                            super.onNext(args);
                            //数据转换
                            list.clear();
                            List<String[]> data=args.getDrawNum();
                            for(String[] array:data){
                                byte[] byte_data=new byte[ballCount.get(flag)];
                                for (int i = 0; i < array.length; i++) {
                                    //数据格式不规范
                                    byte_data[i]= (byte) Integer.parseInt(array[i].trim());
                                }
                                list.add(byte_data);
                            }
                        }
                    });
                }
                isChecked = my_switch.isChecked();
                this.period = period;
                if (call != null) {
                    call.onSubmit(HistoryDialog.this);
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

    public interface HistoryCall {
        /**
         * 历史过滤选择回调
         */
        void onSubmit(HistoryDialog dialog);
    }

}
