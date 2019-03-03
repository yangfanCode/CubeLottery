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
import com.cp2y.cube.adapter.MyFilterCondition3DJiouAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.MapUtils;
import com.cp2y.cube.widgets.switchbutton.SwitchButton;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2017/1/15.
 */
public class D3OddDialog extends ConditionDialog {
    /*2福彩3D,3排列3,5三星,7重庆时时彩2星*/
    private int flag=0;//默认7
    private Context context;
    private OddCall call;
    private AlertDialog dialog = null;
    private boolean isChecked2 = true;
    private Set<String> sums1 = new LinkedHashSet<>();
    private Set<String> sums2 = new LinkedHashSet<>();
    private SwitchButton my_jiou_switch,my_jiouXingTai_switch;
    private MyFilterCondition3DJiouAdapter conditionJiouAdapter;
    private MyFilterCondition3DJiouAdapter adapter_jiouXingTai_select;
    private TextView tv_jiou_bi, tv_jiou_XingTai;
    private SparseIntArray ballCount=new SparseIntArray(){{put(2,3);put(3,3);put(5,3);put(7,2);}};//球的个数

    public D3OddDialog(Context context, OddCall call) {
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

    public byte[] getPattern(String str) {
        byte[] pattern = new byte[ballCount.get(flag)];
        for(int i=0;i<str.length();i++){
            String val=str.substring(i,i+1);
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
    public String[] getPatterns(){
        String[] pattern=new String[sums2.size()];
        int i = 0;
        for(String val:sums2){
            pattern[i] = val;
            i++;
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

    public Set<String> getSums2() {
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
        sums2.addAll(list);
    }

    public void show() {
        if (dialog == null) {
            AlertDialog.Builder builder_jiou = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View jiou_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_3djiou, null);
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
            conditionJiouAdapter = new MyFilterCondition3DJiouAdapter(context, R.layout.item_filter_condition_3dspan_gv, jiou_view,setDefaultData(1));
            adapter_jiouXingTai_select=new MyFilterCondition3DJiouAdapter(context, R.layout.item_filter_condition_3dbig_gv, jiou_view,setDefaultData(2));
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
                Set<String> sums2 = adapter_jiouXingTai_select.getList();
//                if (sums1.size() == 0 && sums2.size() == 0) {
//                    return;
//                }
                isChecked = my_jiou_switch.isChecked();
                isChecked2 = my_jiouXingTai_switch.isChecked();
                this.sums1.clear();
                this.sums2.clear();
                this.sums1.addAll(sums1);
                this.sums2.addAll(sums2);
                if (call != null) call.onSubmit(D3OddDialog.this);
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

    /**
     * 设置默认数据
     * @param type
     * @return
     */
    private List<String> setDefaultData(int type){
        if(type==1){//奇偶比
            if(flag==2||flag==3||flag==5){//三个号码
                return Arrays.asList(MapUtils.D3_NUM_RATE);
            }else{//两个号码
                return Arrays.asList(MapUtils.CQ2_NUM_RATE);
            }
        }else{//大小形态
            if(flag==2||flag==3||flag==5){//三个号码
                return Arrays.asList(MapUtils.D3_ODD_PATTERN_RATE);
            }else{//两个号码
                return Arrays.asList(MapUtils.CQ2_ODD_PATTERN_RATE);
            }
        }
    }

    public interface OddCall {
        void onSubmit(D3OddDialog dialog);
    }
}
