package com.cp2y.cube.activity.selectnums;

import android.content.Context;
import android.content.Intent;
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
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.fragment.doubleball.ConditionFragment;
import com.cp2y.cube.fragment.doubleball.DanTuoFragment;
import com.cp2y.cube.fragment.doubleball.NomalFragment;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.LotteryMissModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectNumFilterActivity extends BaseActivity implements SensorEventListener{
    private TabLayout tablayout;
    private List<Fragment> list =new ArrayList<>();
    private SparseIntArray ignoreReds = new SparseIntArray();
    private SparseIntArray ignoreBlues = new SparseIntArray();
    private SensorManager manager;
    private Sensor sensor;
    private Vibrator vibrator;
    private ConditionFragment conditionFragment = new ConditionFragment();
    private FragmentManager fm = getSupportFragmentManager();
    private Toolbar conditionBar, normalBar;
    private boolean isCondition = false;
    private boolean isIgnore=true;
    private String key = "";

    public String getKey() {
        return key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_num_filter);
        setMainTitle("过滤条件");
        setNavigationIcon(R.mipmap.back_chevron);
        conditionBar = getToolBar();
        normalBar = (Toolbar) findViewById(R.id.toolbar_filter);
        normalBar.setNavigationIcon(R.mipmap.back_chevron);
        tablayout = (TabLayout) findViewById(R.id.selectNum_filter_tablayout);
        tablayout.addTab(tablayout.newTab().setText("普通模式"));
        tablayout.addTab(tablayout.newTab().setText("胆拖模式"));
        NomalFragment nomalFragment = new NomalFragment();
        DanTuoFragment danTuoFragment = new DanTuoFragment();
        list.add(nomalFragment);
        list.add(danTuoFragment);
        //直接开启两个隐藏第二个,接受广播
        fm.beginTransaction().add(R.id.selectNum_filter_container,nomalFragment).add(R.id.selectNum_filter_container,danTuoFragment)
                .add(R.id.selectNum_filter_container, conditionFragment).hide(danTuoFragment).hide(conditionFragment).commit();
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                fm.beginTransaction().show(list.get(position)).hide(list.get(1 - position)).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TextView ignore= (TextView) findViewById(R.id.selectNum_filter_ignore);
        ignore.setOnClickListener(view -> {
            if(isIgnore){
                ignore.setTextColor(ContextCompat.getColor(this,R.color.colorBlueBall));
            }else{
                ignore.setTextColor(ContextCompat.getColor(this,R.color.colorSpanTextSelect));
            }
            isIgnore=!isIgnore;
            NomalFragment fragment0 = (NomalFragment) list.get(0);
            fragment0.switchIgnore();
            DanTuoFragment fragment1 = (DanTuoFragment) list.get(1);
            fragment1.switchIgnore();
        });
        NetHelper.LOTTERY_API.getCurrentLotteryMiss("10002")
                .subscribe(new SafeOnlyNextSubscriber<LotteryMissModel>(){
                    @Override
                    public void onNext(LotteryMissModel args) {
                        super.onNext(args);
                        LotteryMissModel.LotteryMiss data = args.getData();
                        List<List<Integer>> reds = data.getBnc();
                        List<List<Integer>> blues = data.getSnc();
                        for (int i = 0;i<reds.size();i++
                             ) {
                            List<Integer> ignore = reds.get(i);
                            ignoreReds.put(i, ignore.get(2));
                        }
                        for (int i = 0;i<blues.size();i++
                                ) {
                            List<Integer> ignore = blues.get(i);
                            ignoreBlues.put(i, ignore.get(2));
                        }
                    }
                });
        switchToolBar();
        //初始化传感器
        initSensor();
    }

    public ConditionFragment getConditionFragment() {
        return conditionFragment;
    }

    @Override
    public void onBackPressed() {
        if (isCondition) {
            fm.beginTransaction().hide(conditionFragment).show(list.get(tablayout.getSelectedTabPosition())).commit();
            isCondition = false;
            switchToolBar();
            NomalFragment fragment0 = (NomalFragment) list.get(0);
            fragment0.setClearMode();
            DanTuoFragment fragment1 = (DanTuoFragment) list.get(1);
            fragment1.setClearMode();
            key = "";
        } else {
            finish();
        }
    }

    public void setAddMode() {
        onBackPressed();
        NomalFragment fragment0 = (NomalFragment) list.get(0);
        fragment0.setAddMode();
        DanTuoFragment fragment1 = (DanTuoFragment) list.get(1);
        fragment1.setAddMode();
        key = "";
    }

    public void editData(List<String>list, String key, boolean isNormal){
        onBackPressed();
        NomalFragment fragment0 = (NomalFragment) this.list.get(0);
        DanTuoFragment fragment1 = (DanTuoFragment) this.list.get(1);
        fragment0.setEditMode();
        fragment1.setEditMode();
        if (isNormal) {
            tablayout.getTabAt(0).select();
            fragment0.editData(list);
            fragment0.ShowTip();
        } else {
            tablayout.getTabAt(1).select();
            fragment1.editData(list);
            fragment1.ShowTip();
        }
        this.key = key;
    }

    /**
     * 切换ToolBar
     */
    private void switchToolBar() {
        conditionBar.setVisibility(isCondition?View.VISIBLE:View.GONE);
        normalBar.setVisibility(isCondition?View.GONE:View.VISIBLE);
        setSupportActionBar(isCondition?conditionBar:normalBar);
        conditionBar.setNavigationOnClickListener(view1 -> onBackPressed());
        normalBar.setNavigationOnClickListener(view2 -> onBackPressed());
    }

    public SparseIntArray getIgnoreReds() {
        return ignoreReds;
    }

    public SparseIntArray getIgnoreBlues() {
        return ignoreBlues;
    }

    public void gotoConditionPage() {
        fm.beginTransaction().show(conditionFragment).hide(list.get(tablayout.getSelectedTabPosition())).commit();
        isCondition = true;
        conditionFragment.reloadData();
        switchToolBar();
        //过滤条件页面新手引导
        //setMoreGuideDialog(Arrays.asList(new String[]{"filterAddNum","filterDeleteNum","filterEditNum"}),0,new Integer[]{R.mipmap.filter_addnum_guide,R.mipmap.filter_deletenum_guide,R.mipmap.filter_editnum_guide});
        setMoreGuideDialog(Arrays.asList(new String[]{"filterDeleteNum","filterEditNum"}),0,new Integer[]{R.mipmap.filter_deletenum_guide,R.mipmap.filter_editnum_guide});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        NomalFragment fragment0 = (NomalFragment) list.get(0);
        fragment0.onResult(requestCode, resultCode, data);
        DanTuoFragment fragment1 = (DanTuoFragment) list.get(1);
        fragment1.onResult(requestCode, resultCode, data);
        if(data!=null&&resultCode<300){
            tablayout.getTabAt((resultCode / 100 -1)%2).select();
        }
    }

    private void initSensor() {
        manager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        //实例化手机震动的对象
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        sensor = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }
    //销毁清除数据
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁清空数据 初始化
        SingletonMapNomal.getMap().clear();
        SingletonMapFilter.getMap().clear();
        SingletonMapNomal.sign_sort=1;
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
    public void onSensorChanged(SensorEvent event) {
        if(isCondition)return;
        //判断
        //当传感器的值发生变化的时候执行该方法
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        int value = 15;
        //  判断
        if (Math.abs(x) > value || Math.abs(y) > value || Math.abs(z) > 20) {
            if(tablayout.getSelectedTabPosition()==0){
                long patter[] = {0, 100};
                vibrator.vibrate(patter, -1);
                NomalFragment fragment0 = (NomalFragment) list.get(0);
                fragment0.shark();
                return;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
