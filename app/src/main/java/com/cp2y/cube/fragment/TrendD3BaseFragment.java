package com.cp2y.cube.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.trends.D3TrendPicActivity;
import com.cp2y.cube.activity.trends.P3TrendPicActivity;
import com.cp2y.cube.adapter.MyTrendDialogSupportAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.model.ConditionModel;
import com.cp2y.cube.utils.MapUtils;

import java.util.Arrays;

/**
 * Created by js on 2017/1/5.
 */
public abstract class TrendD3BaseFragment extends BaseFragment {

    protected ConditionModel condition = new ConditionModel();
    private AlertDialog settingDialog = null;
    private String tips = ContextHelper.getApplication().getString(R.string.num_tips);//默认基本走势
    private String[] support = MapUtils.NUM_SUPPORT;
    private int[] select = {0};//默认选中值
    protected CheckBox mReverse;
    public boolean isReverse() {
        return !mReverse.isChecked();
    }

    public void initSettingDialog(String tips, String[] support, int[] select) {
        this.tips = tips;
        this.support = support;
        this.select = select;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mReverse = (CheckBox) getView().findViewById(R.id.trend_reverse);
        mReverse.setOnCheckedChangeListener(((compoundButton, b) -> reverseData()));
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadData();
    }
    public void closePop(){
        Activity activity=getActivity();
        if(activity instanceof D3TrendPicActivity){
            D3TrendPicActivity activityD3= (D3TrendPicActivity) getActivity();
            activityD3.ClosePop();
        }else if(activity instanceof P3TrendPicActivity){
            P3TrendPicActivity activityP3= (P3TrendPicActivity) getActivity();
            activityP3.ClosePop();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (settingDialog == null) {//默认值
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            //获取自定义的VIew
            View trendNumView=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_3d_numtrend, null);
            //添加自定义VIew
            builder.setView(trendNumView);
            settingDialog = builder.create();
            ImageView iv_support= (ImageView) trendNumView.findViewById(R.id.dialog_numTrend_ivSupport);
            TextView tv_tip= (TextView) trendNumView.findViewById(R.id.dialog_numTrend_shuoming);
            iv_support.setVisibility(View.GONE);
            tv_tip.setText(tips);
            int[] pos = new int[]{2};
            RadioGroup radioGroup = (RadioGroup) trendNumView.findViewById(R.id.dialog_numTrend_rg);
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId){
                        case R.id.trend_dialog_rg1:
                            pos[0] =0;
                            break;
                        case R.id.trend_dialog_rg2:
                            pos[0] =1;
                            break;
                        case R.id.trend_dialog_rg3:
                            pos[0] =2;
                            break;
                        case R.id.trend_dialog_rg4:
                            pos[0] =3;
                            break;
                        case R.id.trend_dialog_rg5:
                            pos[0] =4;
                            break;
                    }
                }
            });
            radioGroup.check(R.id.trend_dialog_rg3);
            RadioButton[] buttons = new RadioButton[] {
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_dateAll),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date1),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date2),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date3),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date4),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date5),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date6),
                    (RadioButton) trendNumView.findViewById(R.id.trend_dialog_date7),
            };
            //初始值100
            final int[] pos2 = new int[]{0};
            buttons[0].setChecked(true);
            CompoundButton.OnCheckedChangeListener listener = (compoundButton , checked) -> {
                if (checked) {
                    for (int i = 0; i < buttons.length; i++) {
                        RadioButton button = buttons[i];
                        if (compoundButton == button) {
                            pos2[0] =i;
                            continue;
                        }
                        button.setChecked(false);
                    }
                }
            };
            for (RadioButton button: buttons) {
                button.setOnCheckedChangeListener(listener);
            }
//            RadioGroup weeks = (RadioGroup) trendNumView.findViewById(R.id.dialog_numTrend_WeekCount_rg);
//            for (int i = 0; i < weeks.getChildCount(); i++) {
//                weeks.getChildAt(i).setId(R.id.startId + i);
//            }
//            weeks.check(R.id.startId);
            //点击弹出说明
            iv_support.setOnClickListener(v ->{
                Object tag = v.getTag();
                tv_tip.setVisibility(tag == null ? View.VISIBLE : View.GONE);
                iv_support.setBackgroundResource(tag == null ? R.mipmap.icon_shuoming_pre : R.mipmap.icon_shuoming);
                tag = tag == null ? "": null;
                v.setTag(tag);
            });
            MyGridView gv_support= (MyGridView) trendNumView.findViewById(R.id.dialog_numTrend_suopprt_gv);
            MyTrendDialogSupportAdapter adapter = new MyTrendDialogSupportAdapter(Arrays.asList(support),getActivity(),R.layout.item_trendsupport);
            for (int s:select) {
                adapter.addSelect(s);
            }
            gv_support.setAdapter(adapter);
            //辅助线点击事件
            gv_support.setOnItemClickListener((parent, view, position, id) ->adapter.replaceSelect(position));
            //点击说明隐藏
            tv_tip.setOnClickListener(v -> iv_support.performClick());
            trendNumView.findViewById(R.id.NegativeButton).setOnClickListener(v -> settingDialog.dismiss());//取消
            trendNumView.findViewById(R.id.PositiveButton).setOnClickListener(v -> {//点击确定的时候改变筛选条件
                condition.setPeriod(MapUtils.TREND_PERIOD[pos[0]]);
                condition.setWeekDay(MapUtils.D3_TREND_WEEK[pos2[0]]);
                onConditionChanged(adapter);
                reloadData();
                settingDialog.dismiss();
            });//确定
        } else {//非默认,根据当前条件加载
            RadioGroup radioGroup = (RadioGroup) settingDialog.findViewById(R.id.dialog_numTrend_rg);
            ((RadioButton)radioGroup.getChildAt(MapUtils.TREND_PERIOD_MAP.get(condition.getCurrentPeriod()))).setChecked(true);
            RadioButton[] buttons = new RadioButton[] {
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_dateAll),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date1),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date2),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date3),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date4),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date5),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date6),
                    (RadioButton) settingDialog.findViewById(R.id.trend_dialog_date7),
            };
            buttons[MapUtils.TREND_D3_WEEK_MAP.get(condition.getWeekDay())].setChecked(true);
            MyGridView gv_support = (MyGridView) settingDialog.findViewById(R.id.dialog_numTrend_suopprt_gv);
            MyTrendDialogSupportAdapter adapter = (MyTrendDialogSupportAdapter) gv_support.getAdapter();
            onBeforeSettingShow(adapter);
            adapter.notifyDataSetChanged();
        }
        settingDialog.show();
        return true;
    }
    /**显示设置之前的操作**/
    protected abstract void onBeforeSettingShow(MyTrendDialogSupportAdapter adapter);
    /**点击设置确定按钮**/
    protected abstract void onConditionChanged(MyTrendDialogSupportAdapter adapter);
    /**重新加载数据**/
    protected abstract void reloadData();
    /**反转数组**/
    protected abstract void reverseData();
    /**双击后**/
    public abstract void onDoubleTaped();
//    /**断网机制**/
//    public abstract void netOff();

}
