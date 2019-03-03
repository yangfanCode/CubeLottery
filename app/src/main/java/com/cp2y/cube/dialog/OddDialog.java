package com.cp2y.cube.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyFilterConditionJiouAdapter;
import com.cp2y.cube.adapter.MyFilterConditionJiouXingTaiAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by js on 2017/1/15.
 */
public class OddDialog extends ConditionDialog {
    /*0双色球,1大乐透,4排列5,重庆时时彩5新星*/
    private int flag=0;//默认
    private Context context;
    private OddCall call;
    private AlertDialog dialog = null;
    private boolean isChecked2 = true;
    private Set<String> sums1 = new LinkedHashSet<>();
    private Map<Integer, String> sums2 = new LinkedHashMap<>();
    private SwitchButton my_jiou_switch,my_jiouXingTai_switch;
    private MyFilterConditionJiouAdapter conditionJiouAdapter;
    private MyFilterConditionJiouXingTaiAdapter adapter_jiouXingTai_select;
    private TextView tv_jiou_bi, tv_jiou_XingTai;
    private SparseIntArray ballCount=new SparseIntArray(){{put(0,7);put(1,7);put(4,5);put(6,5);}};//球的个数

    public OddDialog(Context context, OddCall call) {
        this.context = context;
        this.call = call;
    }

    public void setFlag(int flag){
        this.flag=flag;
    }

    public boolean isChecked2() {
        return isChecked2;
    }

    /**
     * 是否启用过滤条件
     * @return
     */
    public boolean isConditionEnable() {
        return sums1.size() > 0 || sums2.size() > 0;
    }

    public byte[] getPattern() {
        byte[] pattern = new byte[ballCount.get(flag)];
        for (int i = 0; i < pattern.length; i++) {
            String val = sums2.get(i);
            if (val == null) {
                pattern[i] = 0;
            } else if (val.equals("奇")) {
                pattern[i] = -1;
            } else {
                pattern[i] = 1;
            }
        }
        return pattern;
    }

    public Integer[] getNums() {
        Integer[] nums = new Integer[sums1.size()];
        int i = 0;
        for (String num:sums1) {
            nums[i] = CommonUtil.parseInt(num.substring(0,1));
            i++;
        }
        return nums;
    }

    public Set<String> getSums1() {
        return sums1;
    }

    public Map<Integer, String> getSums2() {
        return sums2;
    }

    public void reset() {
        isChecked = isChecked2 = true;
        sums1.clear();
        sums2.clear();
    }

    public void initNums(List<String> list) {
        sums1.addAll(list);
    }

    public void initPattern(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String val = list.get(i);
            if (val == null || val.equals("奇偶")) {
            } else {
                sums2.put(i, val);
            }
        }
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_jiou = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View jiou_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_jiou, null);
            builder_jiou.setView(jiou_view);
            builder_jiou.setCancelable(false);
            Button PositiveBtn= (Button) jiou_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) jiou_view.findViewById(R.id.NegativeButton);
            TextView tv_jiou_title = (TextView) jiou_view.findViewById(R.id.custom_filter_jiou_tip_title);
            TextView tv_jiou_switch = (TextView) jiou_view.findViewById(R.id.custom_filter_jiou_tip_tvSwitch);
            TextView tv_jiouXingTai_switch = (TextView) jiou_view.findViewById(R.id.custom_filter_jiouXingTai_tip_tvSwitch);
            tv_jiou_bi = (TextView) jiou_view.findViewById(R.id.alert_dialog_jiou_layout_tvRange);
            tv_jiou_XingTai = (TextView) jiou_view.findViewById(R.id.alert_dialog_jiou_layout_tvSingel);
            RelativeLayout tip_title_layout= (RelativeLayout) jiou_view.findViewById(R.id.tip_title_layout);
            RelativeLayout tip_jiou_layout = (RelativeLayout) jiou_view.findViewById(R.id.alert_dialog_jiou_layout_tip);
            my_jiou_switch = (SwitchButton) jiou_view.findViewById(R.id.custom_filter_jiou_tip_switch);
            my_jiouXingTai_switch = (SwitchButton) jiou_view.findViewById(R.id.custom_filter_jiouXingTai_tip_switch);
            MyGridView jiou_gv = (MyGridView) jiou_view.findViewById(R.id.alert_dialog_jiou_gv);
            MyGridView jiouXingTai_gv = (MyGridView) jiou_view.findViewById(R.id.alert_dialog_jiouXingTai_gv);
            conditionJiouAdapter = new MyFilterConditionJiouAdapter(context, R.layout.item_filter_condition_span_gv, jiou_view,ballCount.get(flag)+1);
            adapter_jiouXingTai_select=new MyFilterConditionJiouXingTaiAdapter(context, R.layout.item_filter_condition_jiouxingtai_gv, jiou_view,ballCount.get(flag));
            jiouXingTai_gv.setAdapter(adapter_jiouXingTai_select);
            jiou_gv.setAdapter(conditionJiouAdapter);
            tv_jiou_title.setText("奇偶");
            tv_jiou_switch.setText("包含");
            tv_jiouXingTai_switch.setText("包含");
            my_jiou_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_jiou_switch.setText(checked?"包含":"排除"));
            my_jiouXingTai_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_jiouXingTai_switch.setText(checked?"包含":"排除"));
            tip_title_layout.setOnClickListener(view -> tip_jiou_layout.setVisibility(tip_jiou_layout.getVisibility() == View.VISIBLE?View.GONE:View.VISIBLE));
            PositiveBtn.setOnClickListener((v -> {
                Set<String> sums1 = conditionJiouAdapter.getList();
                Map<Integer, String> sums2 = adapter_jiouXingTai_select.getList();
//                if (sums1.size() == 0 && sums2.size() == 0) {
//                    return;
//                }
                isChecked = my_jiou_switch.isChecked();
                isChecked2 = my_jiouXingTai_switch.isChecked();
                this.sums1.clear();
                this.sums2.clear();
                this.sums1.addAll(sums1);
                this.sums2.putAll(sums2);
                if (call != null) call.onSubmit(OddDialog.this);
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_jiou.show();
        } else {
            dialog.show();
        }
        my_jiou_switch.setChecked(isChecked);
        my_jiouXingTai_switch.setChecked(isChecked2);
        tv_jiou_bi.setTextColor(sums1.size() > 0 ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        tv_jiou_XingTai.setTextColor(sums2.size() > 0 ? ColorUtils.MID_BLUE:ColorUtils.GRAY);
        conditionJiouAdapter.reloadData(sums1);
        adapter_jiouXingTai_select.reloadData(sums2);
    }

    public interface OddCall {
        void onSubmit(OddDialog dialog);
    }
}
