package com.cp2y.cube.activity.selectnums;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.custom.SingletonMap3D;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.fragment.chongqing3.CQ3ConditionFragment;
import com.cp2y.cube.fragment.chongqing3.CQ3ZhiXuanFragment;
import com.cp2y.cube.fragment.chongqing3.CQ3Zu3Fragment;
import com.cp2y.cube.fragment.chongqing3.CQ3Zu6Fragment;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.D3LotteryMissModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectCQ3XingNumActivity extends BaseActivity implements SensorEventListener {
    private int tabSelectPos = 0;
    private List<Fragment> list = new ArrayList<>();
    private Toolbar conditionBar, normalBar;
    private TabLayout tablayout;
    private FragmentManager fm = getSupportFragmentManager();
    private CQ3ConditionFragment conditionFragment = new CQ3ConditionFragment();
    private boolean isIgnore = true;
    private boolean isCondition = false;
    private SensorManager manager;
    private Sensor sensor;
    private Vibrator vibrator;
    private String key = "";
    private SparseIntArray ZuXuanMiss=new SparseIntArray();
    private SparseIntArray BaiMiss=new SparseIntArray();
    private SparseIntArray ShiMiss=new SparseIntArray();
    private SparseIntArray GeMiss=new SparseIntArray();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select3_dnum);
        setMainTitle("过滤条件");
        setNavigationIcon(R.mipmap.back_chevron);
        initView();//初始化
        initData();
        initListener();
        switchToolBar();
        initSensor();//初始化传感器
    }

    public String getKey() {
        return key;
    }

    private void initListener() {
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if(SingletonMap3D.getMap().size()>0){//有号码数据
                    //处在添加编辑模式
                    if (tabSelectPos == 0 && position != 0) {
                        //直选选号
                        switchDialog(position);
                    } else if (tabSelectPos == 1 && position == 0) {
                        switchDialog(position);//组3切换
                    } else if (tabSelectPos == 2 && position == 0) {
                        switchDialog(position);//组6切换
                    } else {
                        switchFragment(position);//组选之间切换
                    }
                }else{
                    switchFragment(position);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TextView ignore = (TextView) findViewById(R.id.selectNum_filter_ignore);
        ignore.setOnClickListener(view -> {
            if (isIgnore) {
                ignore.setTextColor(ContextCompat.getColor(this, R.color.colorBlueBall));
            } else {
                ignore.setTextColor(ContextCompat.getColor(this, R.color.colorSpanTextSelect));
            }
            isIgnore = !isIgnore;
            CQ3ZhiXuanFragment fragment0 = (CQ3ZhiXuanFragment) list.get(0);
            fragment0.switchIgnore();
            CQ3Zu3Fragment fragment1 = (CQ3Zu3Fragment) list.get(1);
            fragment1.switchIgnore();
            CQ3Zu6Fragment fragment2 = (CQ3Zu6Fragment) list.get(2);
            fragment2.switchIgnore();
        });
    }

    //切换弹窗
    private void switchDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCQ3XingNumActivity.this);
        View view = LayoutInflater.from(SelectCQ3XingNumActivity.this).inflate(R.layout.selectnum_3d_tabswitch, null);
        builder.setView(view);
        TextView message= (TextView) view.findViewById(R.id.selectNum_3d_switchTab_message);
        Button btn_submit = (Button) view.findViewById(R.id.selectNum_3d_switchTab_btnSubmit);
        Button btn_cancle = (Button) view.findViewById(R.id.selectNum_3d_switchTab_btnCancle);
        message.setText(position==0?R.string.select3dnum_switch_tab2:R.string.select3dnum_switch_tab1);
        AlertDialog dialog = builder.show();
        dialog.setCancelable(false);
        btn_submit.setOnClickListener((v -> {
            dialog.dismiss();
            SingletonMap3D.getMap().clear();
            key="";//双清
            switchFragment(position);
            tabSelectPos = position;
        }));
        btn_cancle.setOnClickListener((v -> {
            dialog.dismiss();//弹窗消失切换到直选
             tablayout.getTabAt(tabSelectPos).select();//切换tab
        }));
    }

    //切换显示fragment
    private void switchFragment(int position) {
        if (position == 0) {
            selectPos(0, 1, 2);
        } else if (position == 1) {
            selectPos(1, 0, 2);
        } else {
            selectPos(2, 0, 1);
        }
    }

    /**
     * 切换ToolBar
     */
    private void switchToolBar() {
        conditionBar.setVisibility(isCondition ? View.VISIBLE : View.GONE);
        normalBar.setVisibility(isCondition ? View.GONE : View.VISIBLE);
        setSupportActionBar(isCondition ? conditionBar : normalBar);
        conditionBar.setNavigationOnClickListener(view1 -> onBackPressed());
        normalBar.setNavigationOnClickListener(view2 -> onBackPressed());
    }

    private void initSensor() {
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //实例化手机震动的对象
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    private void initData() {
        //遗漏接口数据
        NetHelper.LOTTERY_API.getD3CurrentLotteryMiss("10093")
                .subscribe(new SafeOnlyNextSubscriber<D3LotteryMissModel>(){
                    @Override
                    public void onNext(D3LotteryMissModel args) {
                        super.onNext(args);
                        D3LotteryMissModel.D3LotteryMiss data = args.getData();
                        List<List<List<Object>>> zhixuan= data.getDecide();//定位数据
                        List<List<Object>> zuxuan= data.getMiss().get(0);//组选就一种情况,直接传数据
                        for(int i=0;i<zhixuan.get(0).size();i++){
                            List<Object> ignore = zhixuan.get(0).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            BaiMiss.put(i, Integer.valueOf(miss));
                        }
                        for(int i=0;i<zhixuan.get(1).size();i++){
                            List<Object> ignore = zhixuan.get(1).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            ShiMiss.put(i,Integer.valueOf(miss));
                        }
                        for(int i=0;i<zhixuan.get(2).size();i++){
                            List<Object> ignore = zhixuan.get(2).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            GeMiss.put(i,Integer.valueOf(miss));
                        }
                        for(int i=0;i<zuxuan.size();i++){
                            String missD=String.valueOf(zuxuan.get(i).get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            ZuXuanMiss.put(i,Integer.valueOf(miss));
                        }
                    }
                });
        CQ3ZhiXuanFragment zhixuanFragment = new CQ3ZhiXuanFragment();
        CQ3Zu3Fragment zu3Fragment = new CQ3Zu3Fragment();
        CQ3Zu6Fragment zu6fragment = new CQ3Zu6Fragment();
        list.add(zhixuanFragment);
        list.add(zu3Fragment);
        list.add(zu6fragment);
        fm.beginTransaction().add(R.id.selectNum_filter_container, zhixuanFragment).add(R.id.selectNum_filter_container, zu3Fragment).add(R.id.selectNum_filter_container, zu6fragment)
                .add(R.id.selectNum_filter_container, conditionFragment).hide(zu3Fragment).hide(zu6fragment).hide(conditionFragment).commit();
    }

    private void initView() {
        conditionBar = getToolBar();//过滤条件toolbar
        normalBar = (Toolbar) findViewById(R.id.toolbar_filter);//选号toolbar
        normalBar.setNavigationIcon(R.mipmap.back_chevron);
        tablayout = (TabLayout) findViewById(R.id.selectNum_filter_tablayout);
        tablayout.addTab(tablayout.newTab().setText("直选"));
        tablayout.addTab(tablayout.newTab().setText("组三"));
        tablayout.addTab(tablayout.newTab().setText("组六"));
    }

    //控制fragment
    public void selectPos(int pos1, int pos2, int pos3) {
        fm.beginTransaction().show(list.get(pos1)).hide(list.get(pos2)).hide(list.get(pos3)).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        if (isCondition) {
            tabSelectPos = tablayout.getSelectedTabPosition();
            fm.beginTransaction().hide(conditionFragment).show(list.get(tablayout.getSelectedTabPosition())).commit();
            isCondition = false;
            switchToolBar();
            CQ3ZhiXuanFragment fragment0 = (CQ3ZhiXuanFragment) list.get(0);
            fragment0.setClearMode();
            CQ3Zu3Fragment fragment1 = (CQ3Zu3Fragment) list.get(1);
            fragment1.setClearMode();
            CQ3Zu6Fragment fragment2 = (CQ3Zu6Fragment) list.get(2);
            fragment2.setClearMode();
            key = "";
        } else {
            finish();
        }
    }
    //直选百位遗漏
    public SparseIntArray getIgnoreBai() {
        return BaiMiss;
    }
    //直选十位遗漏
    public SparseIntArray getIgnoreShi() {
        return ShiMiss;
    }
    //直选个位遗漏
    public SparseIntArray getIgnoreGe() {
        return GeMiss;
    }
    //组选遗漏
    public SparseIntArray getIgnoreZuXuan() {
        return ZuXuanMiss;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isCondition) return;
        //判断
        //当传感器的值发生变化的时候执行该方法
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        int value = 12;
        //  判断
        if (Math.abs(x) > value || Math.abs(y) > value || Math.abs(z) > 18) {
            long patter[] = {0, 100};
            vibrator.vibrate(patter, -1);
            if (tablayout.getSelectedTabPosition() == 0) {//直选摇一摇
                CQ3ZhiXuanFragment fragment0 = (CQ3ZhiXuanFragment) list.get(0);
                fragment0.shark();
            } else if (tablayout.getSelectedTabPosition() == 1) {//组三摇一摇
                CQ3Zu3Fragment fragment1 = (CQ3Zu3Fragment) list.get(1);
                fragment1.shark();
            } else {//组六摇一摇
                CQ3Zu6Fragment fragment2 = (CQ3Zu6Fragment) list.get(2);
                fragment2.shark();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //切换到过滤条件页面
    public void gotoConditionPage() {
        fm.beginTransaction().show(conditionFragment).hide(list.get(tablayout.getSelectedTabPosition())).commit();
        isCondition = true;
        conditionFragment.reloadData();
        switchToolBar();
        //过滤条件页面新手引导
        //setMoreGuideDialog(Arrays.asList(new String[]{"filterAddNum","filterDeleteNum","filterEditNum"}),0,new Integer[]{R.mipmap.filter_addnum_guide,R.mipmap.filter_deletenum_guide,R.mipmap.filter_editnum_guide});
        setMoreGuideDialog(Arrays.asList(new String[]{"filterDeleteNum","filterEditNum"}),0,new Integer[]{R.mipmap.filter_deletenum_guide,R.mipmap.filter_editnum_guide});
    }

    //加号按钮添加
    public void setAddMode() {
        onBackPressed();
        CQ3ZhiXuanFragment fragment0 = (CQ3ZhiXuanFragment) list.get(0);
        fragment0.setAddMode();
        CQ3Zu3Fragment fragment1 = (CQ3Zu3Fragment) list.get(1);
        fragment1.setAddMode();
        CQ3Zu6Fragment fragment2 = (CQ3Zu6Fragment) list.get(2);
        fragment2.setAddMode();
        key = "";
    }


    public void editData(List<String>list, String key, int keyNum){
        onBackPressed();
        CQ3ZhiXuanFragment fragment0 = (CQ3ZhiXuanFragment) this.list.get(0);
        CQ3Zu3Fragment fragment1 = (CQ3Zu3Fragment) this.list.get(1);
        CQ3Zu6Fragment fragment2 = (CQ3Zu6Fragment) this.list.get(2);
        fragment0.setEditMode();//按钮变化
        fragment1.setEditMode();//按钮变化
        fragment2.setEditMode();//按钮变化
        if (keyNum < 1000) {//普通模式
            tablayout.getTabAt(0).select();
            fragment0.editData(list);
            fragment0.ShowTip();
        } else if(keyNum>3000&&keyNum<6000){//组3
            tablayout.getTabAt(1).select();
            fragment1.editData(list,keyNum);
            fragment1.ShowTip();
        }else {//组6
            tablayout.getTabAt(2).select();
            fragment2.editData(list,keyNum);
            fragment2.ShowTip();
        }
        this.key = key;//key赋值
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁清空数据 初始化
        SingletonMap3D.getMap().clear();
        SingletonMapFilter.getCQ3Map().clear();
        SingletonMap3D.sign_sort=1;
    }

    /**福彩3D过滤号码规则
 * 直选 单式3个号码
 *      定位 百位 十位+50 个位+100
 * 组3 key3000起
 *       4000胆 拖+50
 *
 * 组6 key6000起 组六单式3个号码
 *     组6复式大于3个号码
 *     7000组6胆拖胆 拖+50
 */
}
