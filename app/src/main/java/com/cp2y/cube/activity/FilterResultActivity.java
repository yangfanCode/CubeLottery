package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.activity.numlibrary.NumLibraryActivity;
import com.cp2y.cube.adapter.MyFilterResultAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.SingletonMapFilterResult;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.services.CQ2LotteryService;
import com.cp2y.cube.services.D3LotteryService;
import com.cp2y.cube.services.LotteryService;
import com.cp2y.cube.services.P5LotteryService;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorInflater;

import java.util.List;

public class FilterResultActivity extends BaseActivity implements MyInterface.closeAVLoading {
    private ListView lv;
    private List<byte[]> list;
    private MyFilterResultAdapter adapter;
    private int totalCount = 0;
    private int afterCount = 0;
    private TextView tv_result;
    private TextView tv_num_libaray;
    private Button btn_save;
    private int btnFlag = 0;
    private ImageView back;
    private ImageView close;
    private boolean isZhixuan = false;
    /*0双色球,1大乐透,2福彩3D,3排列3,4排列5,5重庆时时彩3星,6重庆时时彩5星,7重庆时时彩2星*/
    private int flag = -1;
    //private AVLoadingIndicatorView AVLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_result);
        totalCount = getIntent().getIntExtra("totalCount", 0);
        afterCount = getIntent().getIntExtra("afterCount", 0);
        flag = getIntent().getIntExtra("flag", -1);
        isZhixuan = getIntent().getBooleanExtra("isZhixuan", false);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        //号码库
        tv_num_libaray.setOnClickListener((v1 -> {
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                Intent intent = new Intent();
                intent.setClass(FilterResultActivity.this, NumLibraryActivity.class);
                int tag=(flag==6)||(flag==7)?5:flag;//重庆时时彩号码库合并
                intent.putExtra("flag", tag);
                startActivity(intent);
            } else {
                intentLogin();//登录页面
            }
        }));
        //保存
        btn_save.setOnClickListener((v -> {
            // AVLoading.setVisibility(View.VISIBLE);
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                if (CommonUtil.isNetworkConnected(FilterResultActivity.this)) {
                    if (afterCount == 0) {
                        TipsToast.showTips("过滤结果为空");
                        return;
                    }
                    if (btnFlag > 0) {
                        TipsToast.showTips("已存在号码库");
                        return;
                    }
                    if (flag == 0 || flag == 1) {
                        getService(LotteryService.class).saveLotteryNumber(list, 3, flag,afterCount);
                        //点击一次之后不可点击
                    } else if (flag == 2 || flag == 3 || flag == 5) {
                        getService(D3LotteryService.class).setIsZhiXuan(isZhixuan);//区分过滤直选组选
                        getService(D3LotteryService.class).saveLotteryNumber(list, 7, flag,afterCount);
                    } else if (flag == 7){//二星
                        getService(CQ2LotteryService.class).setIsZhiXuan(isZhixuan);//区分过滤直选组选
                        getService(CQ2LotteryService.class).saveLotteryNumber(list, 5, flag,afterCount);
                    } else {
                        getService(P5LotteryService.class).saveLotteryNumber(list, 2,flag,afterCount);
                    }
                    setSaveAnimaltion();//保存动画
                    btnFlag++;
                } else {
                    //AVLoading.setVisibility(View.GONE);
                    TipsToast.showTips("保存失败,请检查网络设置");
                }
            } else {
                intentLogin();//登录页面
            }
        }));
    }

    private void initData() {
        list = (List<byte[]>) SingletonMapFilterResult.getService("filterResult");
        adapter = new MyFilterResultAdapter(list, this, flag);
        lv.setAdapter(adapter);
        tv_result.setText("过滤后共" + afterCount + "注,已过滤掉" + (totalCount - afterCount) + "注");
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.filter_result_lv);
        tv_result = (TextView) findViewById(R.id.filter_result_tv_result);
        tv_num_libaray = (TextView) findViewById(R.id.toolbar_title_back_numLibrary);
        btn_save = (Button) findViewById(R.id.filter_result_saveBtn);
        back = (ImageView) findViewById(R.id.filter_result_back);
        close = (ImageView) findViewById(R.id.filter_result_close);
        // AVLoading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        back.setOnClickListener((v -> finish()));
        close.setOnClickListener((v -> startActivity(new Intent(this, MainActivity.class))));
    }

    @Override
    public void closeAVLOAding() {
        //AVLoading.setVisibility(View.GONE);
    }

    //保存动画
    public void setSaveAnimaltion() {
        //属性动画 动画部分在xml中 引入动画
        Animator animator = AnimatorInflater.loadAnimator(FilterResultActivity.this, R.animator.save_num);
        animator.setTarget(tv_num_libaray);//设置操作对象
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {//开始动画
                tv_num_libaray.setTextColor(ColorUtils.MID_BLUE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {//结束动画
                tv_num_libaray.setTextColor(ColorUtils.NORMAL_GRAY);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animator.start();//开启
    }
}
