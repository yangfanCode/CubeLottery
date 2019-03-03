package com.cp2y.cube.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyFilterConditionChu3XingTaiAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;
import com.cp2y.cube.widgets.wheel.WheelFactory;
import com.cp2y.cube.widgets.wheelconfig.WheelConfig;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.qqtheme.framework.picker.LinkagePicker;

/**
 * Created by js on 2017/1/15.
 */
public class DivideDialog extends ConditionDialog{
    /*0双色球,1大乐透,2福彩3D,3排列3,4排列5,5,重庆3星,6重庆时时彩5星*/
    private int flag=-1;
    private Context context;
    private DivideCall call;
    private AlertDialog dialog = null;
    private Range range0 = null, range1 = null, rangeTmp0 = null, rangeTmp1 = null;
    private Map<Integer, String> sums = new LinkedHashMap<>();
    private boolean isChecked2 = true;
    private SwitchButton my_chu3_switch, my_chu3XingTai_switch;
    private MyFilterConditionChu3XingTaiAdapter adapter_chu3XingTai;
    private TextView tv_chu3XingTaiTitle, tv_chu3yu0, tv_chu3yu1,tv_chu3yu2, tv_yu0, tv_yu1, tv_yu2, tv_chu3yu0ge, tv_chu3yu1ge;
    private SparseIntArray ballCount=new SparseIntArray(){{put(0,7);put(1,7);put(2,3);put(3,3);put(4,5);put(5,3);put(6,5);put(7,2);}};//球的个数
    private WheelConfig[] wheelConfigs={WheelConfig.DIVIDE_WHEEL_CONFIG,WheelConfig.DIVIDE_WHEEL_CONFIG,WheelConfig.D3_DIVIDE_WHEEL_CONFIG,WheelConfig.D3_DIVIDE_WHEEL_CONFIG,
            WheelConfig.P5_DIVIDE_WHEEL_CONFIG,WheelConfig.D3_DIVIDE_WHEEL_CONFIG,WheelConfig.P5_DIVIDE_WHEEL_CONFIG,WheelConfig.CQ2_DIVIDE_WHEEL_CONFIG};


    public DivideDialog(Context context, DivideCall call) {
        this.context = context;
        this.call = call;
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    public boolean isChecked2() {
        return isChecked2;
    }

    public int[] getNums1() {
        if (range0 == null) return new int[0];
        int[] arr = new int[range0.end - range0.start+1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = range0.start+i;
        }
        return arr;
    }

    public int[] getNums2() {
        if (range1 == null) return new int[0];
        int[] arr = new int[range1.end - range1.start+1];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = range1.start+i;
        }
        return arr;
    }

    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return range0!=null||range1!=null||sums.size()>0;
    }

    public byte[] getPattern() {
        byte[] pattern = new byte[ballCount.get(flag)];
        for (int i = 0; i < pattern.length; i++) {
            String val = sums.get(i);
            if (val == null) {
                pattern[i] = -1;
            } else {
                pattern[i] = (byte) CommonUtil.parseInt(val);
            }
        }
        return pattern;
    }

    public Map<Integer, String> getSums() {
        return sums;
    }

    public void reset() {
        isChecked = isChecked2 = true;
        range0 = range1 = null;
        sums.clear();
    }

    public Range getRange0() {
        return range0;
    }

    public Range getRange1() {
        return range1;
    }

    public void setRange0(int start, int end) {
        range0 = new Range(start, end);
    }

    public void setRange1(int start, int end) {
        range1 = new Range(start, end);
    }

    public void initPattern(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String val = list.get(i);
            if (val == null || val.equals("012")) {
            } else {
                sums.put(i, val);
            }
        }
    }
    public void initNum(List<String> list){
        if(list.size()==2){
            setRange0(Integer.parseInt(list.get(0)),Integer.parseInt(list.get(1)));
        }else if(list.size()==4){
            setRange0(Integer.parseInt(list.get(0)),Integer.parseInt(list.get(1)));
            setRange1(Integer.parseInt(list.get(2)),Integer.parseInt(list.get(3)));
        }
    }
    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_chu3 = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View chu3_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_chu3yu, null);
            builder_chu3.setView(chu3_view);
            builder_chu3.setCancelable(false);
            Button PositiveBtn= (Button) chu3_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) chu3_view.findViewById(R.id.NegativeButton);
            TextView tv_chu3_title = (TextView) chu3_view.findViewById(R.id.custom_filter_chu3_tip_title);
            TextView tv_chu3_switch = (TextView) chu3_view.findViewById(R.id.custom_filter_chu3_tip_tvSwitch);
            TextView tv_chu3XingTai_switch = (TextView) chu3_view.findViewById(R.id.custom_filter_chu3XingTai_tip_tvSwitch);
            tv_chu3yu0ge = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_yu0_tvge);
            tv_chu3yu1ge = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_yu1_tvge);
            tv_chu3yu0 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvYu0);
            tv_chu3yu1 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvYul);
            tv_chu3yu2 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvYu2);
            tv_yu0 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvAlertWheel);
            tv_yu1 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvAlertWhee2);
            tv_chu3XingTaiTitle = (TextView) chu3_view.findViewById(R.id.custom_filter_chu3XingTai_tip_title);
            tv_yu2 = (TextView) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tvYu2_text);
            RelativeLayout tip_title_layout= (RelativeLayout) chu3_view.findViewById(R.id.tip_title_layout);
            RelativeLayout tip_chu3_layout = (RelativeLayout) chu3_view.findViewById(R.id.alert_dialog_chu3_layout_tip);
            my_chu3_switch = (SwitchButton) chu3_view.findViewById(R.id.custom_filter_chu3_tip_switch);
            my_chu3XingTai_switch = (SwitchButton) chu3_view.findViewById(R.id.custom_filter_chu3XingTai_tip_switch);
            MyGridView chu3XingTai_gv = (MyGridView) chu3_view.findViewById(R.id.custom_filter_chu3XingTai_gv);
            adapter_chu3XingTai=new MyFilterConditionChu3XingTaiAdapter(context, R.layout.item_filter_condition_jiouxingtai_gv, chu3_view,ballCount.get(flag));
            chu3XingTai_gv.setAdapter(adapter_chu3XingTai);
            int ballCounts=ballCount.get(flag);//球的个数
            tv_yu0.setHint("0-"+ballCounts);
            tv_yu1.setHint("0-"+ballCounts);
            tv_yu2.setText("0-"+ballCounts);
            tv_chu3_title.setText("除3余");
            tv_chu3_switch.setText("包含");
            tv_chu3XingTai_switch.setText("包含");
            tip_title_layout.setOnClickListener(view -> tip_chu3_layout.setVisibility(tip_chu3_layout.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE));
            tv_yu0.setOnClickListener(view -> {
                LinkagePicker picker = WheelFactory.generateWheelWithConfig((Activity) context, wheelConfigs[flag]);
                picker.setOnLinkageListener((first, second, third) -> {
                    int chu3yu0_start = Integer.valueOf(first);
                    int chu3yu0_end = Integer.valueOf(second);
                    int start = Math.min(chu3yu0_start, chu3yu0_end);
                    int end = Math.max(chu3yu0_start, chu3yu0_end);
                    //此处判断修改成0-7
                    //if (start != 0 || end != (flag==2?3:7)) {
                        rangeTmp0 = new Range(start, end);
                    //}
                    tv_yu0.setText(start + "-" + end);
                    calcRemainTwo();
                });
                if (rangeTmp0 == null) {
                    picker.setSelectedIndex(0, ballCount.get(flag));
                } else {
                    picker.setSelectedIndex(rangeTmp0.start, rangeTmp0.end);
                }
                picker.show();
                tv_chu3yu0.setTextColor(ColorUtils.MID_BLUE);
                tv_chu3yu0ge.setTextColor(ColorUtils.MID_BLUE);
                tv_yu0.setBackgroundResource(R.mipmap.gltj_btn_xiala_150_pre);
                tv_yu0.setTextColor(ColorUtils.MID_BLUE);
            });
            tv_yu1.setOnClickListener(view -> {
                LinkagePicker picker = WheelFactory.generateWheelWithConfig((Activity) context, wheelConfigs[flag]);
                picker.setOnLinkageListener((first, second, third) -> {
                    int chu3yu1_start = Integer.valueOf(first);
                    int chu3yu1_end = Integer.valueOf(second);
                    int start = Math.min(chu3yu1_start, chu3yu1_end);
                    int end = Math.max(chu3yu1_start, chu3yu1_end);
                    //if (start != 0 || end != (flag==2?3:7)) {
                        rangeTmp1 = new Range(start, end);
                    //}
                    tv_yu1.setText(start + "-" + end);
                    calcRemainTwo();
                });
                if (rangeTmp1 == null) {
                    picker.setSelectedIndex(0,ballCount.get(flag));
                } else {
                    picker.setSelectedIndex(rangeTmp1.start, rangeTmp1.end);
                }
                picker.show();
                tv_chu3yu1.setTextColor(ColorUtils.MID_BLUE);
                tv_chu3yu1ge.setTextColor(ColorUtils.MID_BLUE);
                tv_yu1.setTextColor(ColorUtils.MID_BLUE);
                tv_yu1.setBackgroundResource(R.mipmap.gltj_btn_xiala_150_pre);
            });
            my_chu3_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_chu3_switch.setText(checked?"包含":"排除"));
            my_chu3XingTai_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_chu3XingTai_switch.setText(checked?"包含":"排除"));
            PositiveBtn.setOnClickListener((v -> {
                Map<Integer, String> sums = adapter_chu3XingTai.getList();
                isChecked = my_chu3_switch.isChecked();
                isChecked2 = my_chu3XingTai_switch.isChecked();
                range0 = rangeTmp0;
                range1 = rangeTmp1;
                this.sums.clear();
                this.sums.putAll(sums);
                if (call != null) call.onSubmit(DivideDialog.this);
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_chu3.show();
        } else {
            dialog.show();
        }
        rangeTmp0 = range0;
        rangeTmp1 = range1;
        tv_chu3yu0.setTextColor(range0 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_chu3yu0ge.setTextColor(range0 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_yu0.setBackgroundResource(range0 != null?R.mipmap.gltj_btn_xiala_150_pre: R.mipmap.gltj_btn_xiala_150);
        tv_yu0.setTextColor(range0 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_yu0.setText(range0 != null?range0.start+"-"+range0.end:null);

        tv_chu3yu1.setTextColor(range1 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_chu3yu1ge.setTextColor(range1 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_yu1.setBackgroundResource(range1 != null?R.mipmap.gltj_btn_xiala_150_pre: R.mipmap.gltj_btn_xiala_150);
        tv_yu1.setTextColor(range1 != null ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_yu1.setText(range1 != null?range1.start+"-"+range1.end:null);

        calcRemainTwo();
        my_chu3_switch.setChecked(isChecked);
        my_chu3XingTai_switch.setChecked(isChecked2);
        adapter_chu3XingTai.reloadData(sums);
        tv_chu3XingTaiTitle.setTextColor(sums.size()>0?ColorUtils.MID_BLUE:ColorUtils.GRAY);
    }

    private void calcRemainTwo() {
        int calc=ballCount.get(flag);
        int start = 0, end = 0;
        if (rangeTmp0 != null) {
            start += rangeTmp0.start;
            end += rangeTmp0.end;
        }else{
            start += 0;
            end += calc;
        }
        if (rangeTmp1 != null) {
            start += rangeTmp1.start;//2
            end += rangeTmp1.end;
        }else{
            start +=0;
            end +=calc;
        }
        if (start > calc) start = calc;
        if (end > calc) end = calc;
        tv_yu2.setText((calc-Math.max(start,end))+"-"+(calc-Math.min(start,end)));


    }

    public interface DivideCall {
        void onSubmit(DivideDialog dialog);
    }
}
