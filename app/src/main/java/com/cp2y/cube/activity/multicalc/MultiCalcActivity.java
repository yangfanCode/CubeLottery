package com.cp2y.cube.activity.multicalc;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.multicalc.fragment.MultiCalcFragment;
import com.cp2y.cube.activity.multicalc.fragment.MultiResultFragment;
import com.cp2y.cube.model.MultiCalcResultModel;

import java.util.List;

public class MultiCalcActivity extends BaseActivity {
   private FragmentManager manager=getSupportFragmentManager();
   private MultiCalcFragment multiCalcFragment=new MultiCalcFragment();
   private MultiResultFragment multiResultFragment=new MultiResultFragment();
   private boolean isResult=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_calc);
        setMainTitle("倍投计算器");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener(v -> onBackPressed());
        manager.beginTransaction().add(R.id.container,multiCalcFragment).add(R.id.container,multiResultFragment)
                .show(multiCalcFragment).hide(multiResultFragment).commit();
    }

    public void gotoResultPage(List<MultiCalcResultModel> list) {
        isResult=true;
        manager.beginTransaction().show(multiResultFragment).hide(multiCalcFragment).commit();
        multiResultFragment.initData(list);
    }

    @Override
    public void onBackPressed() {
       if(isResult){//在结果页返回
           isResult=false;
           manager.beginTransaction().show(multiCalcFragment).hide(multiResultFragment).commit();
       }else{
           finish();
       }
    }
}
