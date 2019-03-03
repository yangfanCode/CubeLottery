package com.cp2y.cube.activity.selectnums;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
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
import com.cp2y.cube.custom.SingletonMapP5;
import com.cp2y.cube.fragment.chongqing5.CQ5ConditionFragment;
import com.cp2y.cube.fragment.chongqing5.CQ5ZhiXuanFragment;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.D3LotteryMissModel;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectCQ5NumActivity extends BaseActivity implements SensorEventListener {
    private int tabSelectPos = 0;
    private List<Fragment> list = new ArrayList<>();
    private Toolbar conditionBar, normalBar;
    private FragmentManager fm = getSupportFragmentManager();
    private CQ5ConditionFragment conditionFragment = new CQ5ConditionFragment();
    private boolean isIgnore = true;
    private boolean isCondition = false;
    private SensorManager manager;
    private Sensor sensor;
    private Vibrator vibrator;
    private String key = "";
    private SparseIntArray WanMiss=new SparseIntArray();
    private SparseIntArray QianMiss=new SparseIntArray();
    private SparseIntArray BaiMiss=new SparseIntArray();
    private SparseIntArray ShiMiss=new SparseIntArray();
    private SparseIntArray GeMiss=new SparseIntArray();
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_p5_num);
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
        TextView ignore = (TextView) findViewById(R.id.selectNum_filter_ignore);
        ignore.setOnClickListener(view -> {
            if (isIgnore) {
                ignore.setTextColor(ContextCompat.getColor(this, R.color.colorBlueBall));
            } else {
                ignore.setTextColor(ContextCompat.getColor(this, R.color.colorSpanTextSelect));
            }
            isIgnore = !isIgnore;
            CQ5ZhiXuanFragment fragment0 = (CQ5ZhiXuanFragment) list.get(0);
            fragment0.switchIgnore();
        });
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
        NetHelper.LOTTERY_API.getD3CurrentLotteryMiss("10089")
                .subscribe(new SafeOnlyNextSubscriber<D3LotteryMissModel>(){
                    @Override
                    public void onNext(D3LotteryMissModel args) {
                        super.onNext(args);
                        D3LotteryMissModel.D3LotteryMiss data = args.getData();
                        List<List<List<Object>>> zhixuan= data.getDecide();//定位数据
                        for(int i=0,size=zhixuan.get(0).size();i<size;i++){
                            List<Object> ignore = zhixuan.get(0).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            WanMiss.put(i, Integer.valueOf(miss));
                        }
                        for(int i=0,size=zhixuan.get(1).size();i<size;i++){
                            List<Object> ignore = zhixuan.get(1).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            QianMiss.put(i, Integer.valueOf(miss));
                        }
                        for(int i=0,size=zhixuan.get(2).size();i<size;i++){
                            List<Object> ignore = zhixuan.get(2).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            BaiMiss.put(i, Integer.valueOf(miss));
                        }
                        for(int i=0,size=zhixuan.get(3).size();i<size;i++){
                            List<Object> ignore = zhixuan.get(3).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            ShiMiss.put(i,Integer.valueOf(miss));
                        }
                        for(int i=0,size=zhixuan.get(4).size();i<size;i++){
                            List<Object> ignore = zhixuan.get(4).get(i);
                            String missD=String.valueOf(ignore.get(2));
                            String miss=missD.substring(0,missD.indexOf("."));
                            GeMiss.put(i,Integer.valueOf(miss));
                        }
                    }
                });
        CQ5ZhiXuanFragment zhixuanFragment = new CQ5ZhiXuanFragment();
        list.add(zhixuanFragment);
        fm.beginTransaction().add(R.id.selectNum_filter_container, zhixuanFragment).add(R.id.selectNum_filter_container, conditionFragment).hide(conditionFragment).commit();
    }

    private void initView() {
        conditionBar = getToolBar();//过滤条件toolbar
        normalBar = (Toolbar) findViewById(R.id.toolbar_filter);//选号toolbar
        normalBar.setNavigationIcon(R.mipmap.back_chevron);
        title = (TextView) findViewById(R.id.selectNum_filter_title);
        title.setText("重庆时时彩");
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
            fm.beginTransaction().hide(conditionFragment).show(list.get(0)).commit();
            isCondition = false;
            switchToolBar();
            CQ5ZhiXuanFragment fragment0 = (CQ5ZhiXuanFragment) list.get(0);
            fragment0.setClearMode();
            key = "";
        } else {
            finish();
        }
    }
    //直选万位遗漏
    public SparseIntArray getIgnoreWan() {
        return WanMiss;
    }
    //直选千位遗漏
    public SparseIntArray getIgnoreQian() {
        return QianMiss;
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
            CQ5ZhiXuanFragment fragment0 = (CQ5ZhiXuanFragment) list.get(0);
            fragment0.shark();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    //切换到过滤条件页面
    public void gotoConditionPage() {
        fm.beginTransaction().show(conditionFragment).hide(list.get(0)).commit();
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
        CQ5ZhiXuanFragment fragment0 = (CQ5ZhiXuanFragment) list.get(0);
        fragment0.setAddMode();
        key = "";
    }


    public void editData(List<String>list, String key){
        onBackPressed();
        CQ5ZhiXuanFragment fragment0 = (CQ5ZhiXuanFragment) this.list.get(0);
        fragment0.setEditMode();//按钮变化
        fragment0.editData(list);
        fragment0.ShowTip();
        this.key = key;//key赋值
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁清空数据 初始化
        SingletonMapP5.getMap().clear();
        SingletonMapFilter.getCQ5Map().clear();
        SingletonMapP5.sign_sort=1;
    }

    /**排列5过滤号码规则
 * 单式3个号码
 * 复式 万位 千位+50 百位+100 十位+150 个位+200

 */
}
