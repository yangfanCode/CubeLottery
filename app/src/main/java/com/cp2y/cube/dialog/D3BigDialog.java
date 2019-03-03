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
import com.cp2y.cube.adapter.MyFilterCondition3DDaXiaoAdapter;
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
 * Created by js on 2017/1/13.
 */
public class D3BigDialog extends ConditionDialog{
    //福彩3D大小
    /*2福彩3D,3排列3,5三星,7重庆时时彩2星*/
    private int flag=0;//默认7
    private Context context;
    private BigCall call;
    private AlertDialog dialog = null;
    private boolean isChecked2 = true;
    private SwitchButton my_daxiao_switch = null, my_daxiaoXingTai_switch = null;
    private Set<String> sums1 = new LinkedHashSet<>();
    private Set<String> sums2 = new LinkedHashSet<>();
    private MyFilterCondition3DDaXiaoAdapter adapter_daxiaoXingTai = null;
    private MyFilterCondition3DDaXiaoAdapter adapter_daxiaoBi = null;
    private TextView tv_daxiao_xingtai,tv_daxiao_bi;
    private SparseIntArray ballCount=new SparseIntArray(){{put(2,3);put(3,3);put(5,3);put(7,2);}};//球的个数

    public D3BigDialog(Context context, BigCall call) {
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

    public Set<String> getSums2() {
        return sums2;
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
            } else if (val.equals("小")) {
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
            AlertDialog.Builder builder_daxiao = new AlertDialog.Builder(context);
            //获取自定义的VIew
            View daxiao_view = LayoutInflater.from(context).inflate(R.layout.alertdialog_filter_3ddaxiao, null);
            builder_daxiao.setView(daxiao_view);
            builder_daxiao.setCancelable(false);
            Button PositiveBtn= (Button) daxiao_view.findViewById(R.id.PositiveButton);
            Button NegativeBtn= (Button) daxiao_view.findViewById(R.id.NegativeButton);
            TextView tv_daxiao_title = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_title);
            TextView tv_daxiao_switch = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_tvSwitch);
            TextView tv_daxiaoXingTai_switch = (TextView) daxiao_view.findViewById(R.id.custom_filter_daxiaoXingTai_tip_tvSwitch);
            tv_daxiao_xingtai = (TextView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tvSingel);
            tv_daxiao_bi = (TextView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tvRange);
            RelativeLayout tip_title_layout= (RelativeLayout) daxiao_view.findViewById(R.id.tip_title_layout);
            RelativeLayout tip_daxiao_layout = (RelativeLayout) daxiao_view.findViewById(R.id.alert_dialog_daxiao_layout_tip);
            my_daxiao_switch = (SwitchButton) daxiao_view.findViewById(R.id.custom_filter_daxiao_tip_switch);
            my_daxiaoXingTai_switch = (SwitchButton) daxiao_view.findViewById(R.id.custom_filter_daxiaoXingTai_tip_switch);
            MyGridView daxiao_gv = (MyGridView) daxiao_view.findViewById(R.id.alert_dialog_daxiao_gv);
            MyGridView daxiaoXingTai_gv = (MyGridView) daxiao_view.findViewById(R.id.alert_dialog_daxiaoXingTai_gv);
            adapter_daxiaoXingTai = new MyFilterCondition3DDaXiaoAdapter(context, R.layout.item_filter_condition_3dbig_gv, daxiao_view,setDefaultData(2,flag));
            adapter_daxiaoBi = new MyFilterCondition3DDaXiaoAdapter(context, R.layout.item_filter_condition_3dspan_gv,daxiao_view,setDefaultData(1,flag));//1 大小比 2 形态
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
                Set<String> sums2 = adapter_daxiaoXingTai.getList();
//                if (sums1.size() == 0 && sums2.size() == 0) {//啥都没选
//                    return;
//                }
                isChecked = my_daxiao_switch.isChecked();
                isChecked2 = my_daxiaoXingTai_switch.isChecked();
                this.sums1.clear();
                this.sums2.clear();
                this.sums1.addAll(sums1);
                this.sums2.addAll(sums2);
                if (call != null) call.onSubmit(D3BigDialog.this);
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

    /**
     * 设置默认数据
     * @param type
     * @param flag
     * @return
     */
    private List<String> setDefaultData(int type,int flag){
        if(type==1){//大小比
            if(flag==2||flag==3||flag==5){//三个号码
                return Arrays.asList(MapUtils.D3_NUM_RATE);
            }else{//两个号码
                return Arrays.asList(MapUtils.CQ2_NUM_RATE);
            }
        }else{//大小形态
            if(flag==2||flag==3||flag==5){//三个号码
                return Arrays.asList(MapUtils.D3_PATTERN_RATE);
            }else{//两个号码
                return Arrays.asList(MapUtils.CQ2_PATTERN_RATE);
            }
        }
    }

    public interface BigCall {
        void onSubmit(D3BigDialog dialog);
    }
}
