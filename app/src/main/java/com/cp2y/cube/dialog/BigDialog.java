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
import com.cp2y.cube.adapter.MyFilterConditionDaXiaoBiAdapter;
import com.cp2y.cube.adapter.MyFilterConditionDaXiaoXingTaiAdapter;
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
 * Created by js on 2017/1/13.
 */
public class BigDialog extends ConditionDialog{
    /*0双色球,1大乐透,4排列5,6重庆时时彩5星*/
    private int flag=0;//默认7
    private Context context;
    private BigCall call;
    private AlertDialog dialog = null;
    private boolean isChecked2 = true;
    private SwitchButton my_daxiao_switch = null, my_daxiaoXingTai_switch = null;
    private Set<String> sums1 = new LinkedHashSet<>();
    private Map<Integer, String> sums2 = new LinkedHashMap<>();
    private MyFilterConditionDaXiaoXingTaiAdapter adapter_daxiaoXingTai = null;
    private MyFilterConditionDaXiaoBiAdapter adapter_daxiaoBi = null;
    private TextView tv_daxiao_xingtai,tv_daxiao_bi;
    private SparseIntArray ballCount=new SparseIntArray(){{put(0,7);put(1,7);put(4,5);put(6,5);}};//球的个数

    public BigDialog(Context context, BigCall call) {
        this.context = context;
        this.call = call;
    }

    public void setFlag(int flag){
        this.flag=flag;
    }

    public boolean isChecked2() {
        return isChecked2;
    }

    public Set<String> getSums1() {
        return sums1;
    }

    public Map<Integer, String> getSums2() {
        return sums2;
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
            } else if (val.equals("小")) {
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
            if (val == null || val.equals("大小")) {
            } else {
                sums2.put(i, val);
            }
        }
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_daxiao = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View daxiao_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_daxiao, null);
            builder_daxiao.setView(daxiao_view);
            builder_daxiao.setCancelable(false);
            Button PositiveBtn= (Button) daxiao_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) daxiao_view.findViewById(R.id.NegativeButton);
            TextView tv_daxiao_title = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_title);
            TextView tv_daxiao_switch = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_tvSwitch);
            TextView tv_daxiaoXingTai_switch = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiaoXingTai_tip_tvSwitch);
            tv_daxiao_xingtai = (TextView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tvSingel);
            tv_daxiao_bi = (TextView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tvRange);
            RelativeLayout tip_daxiao_layout = (RelativeLayout) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tip);
            RelativeLayout tip_title_layout= (RelativeLayout) daxiao_view.findViewById(R.id.tip_title_layout);
            my_daxiao_switch = (SwitchButton) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_switch);
            my_daxiaoXingTai_switch = (SwitchButton) daxiao_view.findViewById(R.id.custom_filter_daxiaoXingTai_tip_switch);
            MyGridView daxiao_gv = (MyGridView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_gv);
            MyGridView daxiaoXingTai_gv = (MyGridView) daxiao_view.findViewById(R.id.alert_dialog_daxiaoXingTai_gv);
            adapter_daxiaoBi = new MyFilterConditionDaXiaoBiAdapter(context, R.layout.item_filter_condition_span_gv,daxiao_view,(ballCount.get(flag)+1));
            adapter_daxiaoXingTai = new MyFilterConditionDaXiaoXingTaiAdapter(context, R.layout.item_filter_condition_jiouxingtai_gv, daxiao_view,ballCount.get(flag));
            daxiao_gv.setAdapter(adapter_daxiaoBi);
            daxiaoXingTai_gv.setAdapter(adapter_daxiaoXingTai);
            tv_daxiao_title.setText("大小");
            tv_daxiao_switch.setText("包含");
            tv_daxiaoXingTai_switch.setText("包含");
            my_daxiao_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_daxiao_switch.setText(checked?"包含":"排除"));
            my_daxiaoXingTai_switch.setOnCheckedChangeListener((compoundButton, checked) -> tv_daxiaoXingTai_switch.setText(checked?"包含":"排除"));
            tip_title_layout.setOnClickListener(view -> tip_daxiao_layout.setVisibility(tip_daxiao_layout.getVisibility() == View.VISIBLE ? View.GONE:View.VISIBLE));
            PositiveBtn.setOnClickListener((v -> {
                Set<String> sums1 = adapter_daxiaoBi.getList();
                Map<Integer, String> sums2 = adapter_daxiaoXingTai.getList();
//                if (sums1.size() == 0 && sums2.size() == 0) {//啥都没选
//                    return;
//                }
                isChecked = my_daxiao_switch.isChecked();
                isChecked2 = my_daxiaoXingTai_switch.isChecked();
                this.sums1.clear();
                this.sums2.clear();
                this.sums1.addAll(sums1);
                this.sums2.putAll(sums2);
                if (call != null) call.onSubmit(BigDialog.this);
                dialog.dismiss();
            }));
            NegativeBtn.setOnClickListener((v -> dialog.dismiss()));
            dialog = builder_daxiao.show();
        } else {
            dialog.show();
        }
        my_daxiao_switch.setChecked(isChecked);
        my_daxiaoXingTai_switch.setChecked(isChecked2);
        tv_daxiao_bi.setTextColor(sums1.size() > 0 ? ColorUtils.NORMAL_BLUE:ColorUtils.GRAY);
        tv_daxiao_xingtai.setTextColor(sums2.size() > 0 ? ColorUtils.NORMAL_BLUE:ColorUtils.GRAY);
        adapter_daxiaoBi.reloadData(sums1);
        adapter_daxiaoXingTai.reloadData(sums2);
    }

    public interface BigCall {
        void onSubmit(BigDialog dialog);
    }
}
