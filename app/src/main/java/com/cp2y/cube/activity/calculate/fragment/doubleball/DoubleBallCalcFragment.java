package com.cp2y.cube.activity.calculate.fragment.doubleball;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.CalculateActivity;
import com.cp2y.cube.utils.DisplayUtil;
import com.yangfan.widget.CustomTabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DoubleBallCalcFragment extends Fragment {
    private boolean isCondition = false;
    private FragmentManager fm ;
    private CalcDoubleNumFragment calcDoubleNumFragment=new CalcDoubleNumFragment();
    private CustomTabLayout tab;
    private List<Fragment> list ;
    private String key = "";
    private RelativeLayout calculate_layout;


    public String getKey() {
        return key;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_double_ball_calc, container, false);
        initView(rootView);
        initData();
        initstener();
        return rootView;
    }
    public static DoubleBallCalcFragment newInstance() {
        DoubleBallCalcFragment fragment = new DoubleBallCalcFragment();
//        Bundle args = new Bundle();
//        args.putInt("personalUserId", param1);
//        fragment.setArguments(args);
        return fragment;
    }
    private void initData() {
    }

    private void initstener() {
        tab.setOnTabLayoutItemSelectListener(new CustomTabLayout.OnTabLayoutItemSelectListener() {
            @Override
            public void onTabLayoutItemSelect(int i) {
                fm.beginTransaction().show(list.get(i)).hide(list.get(1 - i)).commit();
            }
        });
    }

    private void initView(View rootView) {
        fm = getChildFragmentManager();
        list=new ArrayList<>();
        calculate_layout = (RelativeLayout) rootView.findViewById(R.id.calculate_layout);
        tab = (CustomTabLayout) rootView.findViewById(R.id.calculate_tab);
        tab.setViewHeight(DisplayUtil.dip2px(40));
        tab.setBottomLineWidth(DisplayUtil.dip2px(61));
        tab.setBottomLineHeight(DisplayUtil.dip2px(4));
        tab.setBottomLineHeightBgResId(R.color.colorBlueBall);
        tab.setmTextColorSelect(R.color.color555555);
        tab.setmTextColorUnSelect(R.color.color555555);
        tab.setTextSize(14);
        tab.setItemInnerPaddingLeft(DisplayUtil.dip2px(60));
        tab.setItemInnerPaddingRight(DisplayUtil.dip2px(60));
        int width=getResources().getDisplayMetrics().widthPixels;
        tab.setNeedEqual(true,width);
        tab.setTabData(new ArrayList<CharSequence>(){{add("普通");add("胆拖");}},0);
        CalcDoubleNomalFragment nomalFragment=new CalcDoubleNomalFragment();
        CalcDoubleDanTuoFragment danTuoFragment=new CalcDoubleDanTuoFragment();
        list.add(nomalFragment);
        list.add(danTuoFragment);
        //直接开启两个
        fm.beginTransaction().add(R.id.calculate_container,nomalFragment).add(R.id.calculate_container,danTuoFragment)
                .add(R.id.calculate_container, calcDoubleNumFragment).hide(danTuoFragment).hide(calcDoubleNumFragment).commit();
    }
    public void gotoConditionPage() {
        fm.beginTransaction().show(calcDoubleNumFragment).hide(list.get(tab.getSelectedTabPosition())).commit();
        isCondition = true;
        calcDoubleNumFragment.reloadData();
        CalculateActivity act= (CalculateActivity) getActivity();
        act.switchToolBar(isCondition);
        calculate_layout.setVisibility(View.GONE);
    }
    public void setAddMode() {
        onBackPressed();
        CalcDoubleNomalFragment fragment0 = (CalcDoubleNomalFragment) list.get(0);
        fragment0.setAddMode();
        CalcDoubleDanTuoFragment fragment1 = (CalcDoubleDanTuoFragment) list.get(1);
        fragment1.setAddMode();
        key = "";
    }

    public void editData(List<String>list, String key, boolean isNormal){
        onBackPressed();
        CalcDoubleNomalFragment fragment0 = (CalcDoubleNomalFragment) this.list.get(0);
        CalcDoubleDanTuoFragment fragment1 = (CalcDoubleDanTuoFragment) this.list.get(1);
        fragment0.setEditMode();
        fragment1.setEditMode();
        if (isNormal) {
            tab.setCurrentItem(0);
            fragment0.editData(list);
            fragment0.ShowTip();
        } else {
            tab.setCurrentItem(1);
            fragment1.editData(list);
            fragment1.ShowTip();
        }
        this.key = key;
    }

    public void onBackPressed() {
        CalculateActivity act= (CalculateActivity) getActivity();
        if (isCondition) {
            calculate_layout.setVisibility(View.VISIBLE);
            isCondition = false;
            act.switchToolBar(isCondition);
            CalcDoubleNomalFragment fragment0 = (CalcDoubleNomalFragment) list.get(0);
            fragment0.setClearMode();
            CalcDoubleDanTuoFragment fragment1 = (CalcDoubleDanTuoFragment) list.get(1);
            fragment1.setClearMode();
            key = "";
            fm.beginTransaction().hide(calcDoubleNumFragment).show(list.get(tab.getSelectedTabPosition())).commit();
        } else {
            act.finishAct();
        }
    }
}
