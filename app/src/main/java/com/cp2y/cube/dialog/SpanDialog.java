package com.cp2y.cube.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyFilterConditionSpanAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;
import com.cp2y.cube.widgets.wheel.WheelFactory;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import cn.qqtheme.framework.picker.LinkagePicker;

/**
 * Created by js on 2017/1/13.
 */
public class SpanDialog extends ConditionDialog {
    /*0双色球,1大乐透,2福彩3D,3排列3,4排列5*/
    private int flag=-1;
    private Context context;
    private SpanCall call;
    private AlertDialog dialog;
    private TextView tv_span_wheel = null, tv_span_range = null;
    private TextView tv_span_tvSingle = null;
    private int start = -1, end = -1;
    private SwitchButton my_span_switch = null;
    private Set<Integer> sums = new LinkedHashSet<>();
    private MyFilterConditionSpanAdapter span_alert_adapter = null;
    private SparseIntArray rangeStart=new SparseIntArray(){{put(0,5);put(1,4);put(2,0);put(3,0);put(4,0);put(5,0);put(6,0);put(7,0);}};//跨度范围开始
    private SparseIntArray rangeEnd=new SparseIntArray(){{put(0,32);put(1,34);put(2,9);put(3,9);put(4,9);put(5,9);put(6,9);put(7,9);}};//跨度范围结束
    private WheelConfig[] wheelConfigs={WheelConfig.RANGE_WHEEL_CONFIG,WheelConfig.LOTTO_RANGE_WHEEL_CONFIG,WheelConfig.D3_RANGE_WHEEL_CONFIG,WheelConfig.D3_RANGE_WHEEL_CONFIG,
            WheelConfig.P5_RANGE_WHEEL_CONFIG,WheelConfig.D3_RANGE_WHEEL_CONFIG,WheelConfig.P5_RANGE_WHEEL_CONFIG,WheelConfig.P5_RANGE_WHEEL_CONFIG};

    public SpanDialog(Context context, SpanCall call) {
        this.context = context;
        this.call = call;
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    public void initSums(List<String> list) {
        for (String sum:list) {
            sums.add(CommonUtil.parseInt(sum));
        }
    }

    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return start != -1 || sums.size() > 0;
    }

    public Range getRange() {
        return new Range(start, end);
    }

    public Integer[] getSums() {
        if (sums.size() == 0) return new Integer[0];
        Integer[] arr = new Integer[sums.size()];
        int i = 0;
        for (Integer sum:sums) {
            arr[i] = sum;
            i++;
        }
        return arr;
    }

    public void reset() {
        isChecked = true;
        sums.clear();
        start = end = -1;
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_span = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View span_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_span, null);
            builder_span.setView(span_view);
            builder_span.setCancelable(false);
            Button PositiveBtn= (Button) span_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) span_view.findViewById(R.id.NegativeButton);
            TextView tv_span_title = (TextView) span_view.findViewById(R.id.custom_filter_span_tip_title);
            tv_span_tvSingle = (TextView) span_view.findViewById(R.id.alert_dialog_span_layout_tvSingel);
            TextView tv_span_switch = (TextView) span_view.findViewById(R.id.custom_filter_span_tip_tvSwitch);
            tv_span_range = (TextView) span_view.findViewById(R.id.alert_dialog_span_layout_tvRange);
            tv_span_wheel = (TextView) span_view.findViewById(R.id.alert_dialog_span_layout_tvAlertWheel);
            RelativeLayout tip_title_layout= (RelativeLayout) span_view.findViewById(R.id.tip_title_layout);
            RelativeLayout tip_span_layout = (RelativeLayout) span_view.findViewById(R.id.alert_dialog_span_layout_tip);
            my_span_switch = (SwitchButton) span_view.findViewById(R.id.custom_filter_span_tip_switch);
            MyGridView span_gv_dialog = (MyGridView) span_view.findViewById(R.id.alert_dialog_span_gv);
            //弹窗里的gridview
            span_alert_adapter = new MyFilterConditionSpanAdapter(context, R.layout.item_filter_condition_span_gv, span_view,rangeStart.get(flag),(rangeEnd.get(flag)+1-rangeStart.get(flag)));
            span_gv_dialog.setAdapter(span_alert_adapter);
            tv_span_title.setText("跨度");
            tv_span_wheel.setHint(rangeStart.get(flag)+"-"+rangeEnd.get(flag));//跨度范围
            tip_title_layout.setOnClickListener(view -> tip_span_layout.setVisibility(tip_span_layout.getVisibility() == View.VISIBLE? View.GONE:View.VISIBLE));
            tv_span_wheel.setOnClickListener(view -> {
                tv_span_wheel.setBackgroundResource(R.mipmap.gltj_btn_xiala_150_pre);
                tv_span_range.setTextColor(ColorUtils.MID_BLUE);
                tv_span_wheel.setTextColor(ColorUtils.MID_BLUE);
                LinkagePicker picker=null;
                picker= WheelFactory.generateWheelWithConfig((Activity) context, wheelConfigs[flag]);
                if (start == -1) {
                    picker.setSelectedIndex(0, (rangeEnd.get(flag)-rangeStart.get(flag)));
                } else {
                    picker.setSelectedIndex(start-rangeStart.get(flag), end-rangeStart.get(flag));
                }
                picker.setOnLinkageListener((first, second, third) -> {
                    int start = Integer.valueOf(first);
                    int end = Integer.valueOf(second);
                    tv_span_wheel.setText(String.valueOf(Math.min(start,end)).concat("-").concat(String.valueOf(Math.max(start,end))));
                    tv_span_wheel.setTextColor(ColorUtils.MID_BLUE);
                });
                picker.show();
            });
            tv_span_switch.setText("包含");
            my_span_switch.setOnCheckedChangeListener((compoundButton, isChecked) -> tv_span_switch.setText(isChecked?"包含":"排除"));
            PositiveBtn.setOnClickListener((v -> {
                String range = tv_span_wheel.getText().toString().trim();
                Set<Integer> sums = span_alert_adapter.getList();
//                if (TextUtils.isEmpty(range) && sums.size() == 0) {//啥都没选
//                    return;
//                }
                if (!TextUtils.isEmpty(range)) {
                    String[] arr = range.split("-");
                    start = Integer.valueOf(arr[0]);
                    end = Integer.valueOf(arr[1]);
                }
                isChecked = my_span_switch.isChecked();
                this.sums.clear();
                this.sums.addAll(sums);
                if (call != null) call.onSubmit(SpanDialog.this, new Range(start, end), this.sums, isChecked);
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_span.show();
        } else {
            dialog.show();
        }
        boolean isWheel = !(start==-1&&end==-1);
        my_span_switch.setChecked(isChecked);
        tv_span_wheel.setText(isWheel?String.valueOf(start).concat("-".concat(String.valueOf(end))):null);
        tv_span_wheel.setTextColor(isWheel?ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_span_range.setTextColor(isWheel?ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_span_tvSingle.setTextColor(sums.size()>0?ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_span_wheel.setBackgroundResource(isWheel?R.mipmap.gltj_btn_xiala_150_pre: R.mipmap.gltj_btn_xiala_150);
        span_alert_adapter.reloadData(sums);

    }

    public interface SpanCall {//跨度回调
        void onSubmit(SpanDialog dialog, Range range, Set<Integer> sums, boolean include);
    }
}
