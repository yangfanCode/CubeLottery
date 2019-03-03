package com.cp2y.cube.activity.calculate.fragment.p5;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.CalculateActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class P5CalcFragment extends Fragment {
    private boolean isCondition = false;
    private FragmentManager fm ;
    private CalcP5NumFragment calcP5NumFragment=new CalcP5NumFragment();
    private List<Fragment> list ;
    private String key = "";
    public String getKey() {
        return key;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_p5_calc, container, false);
        initView();//初始化
        return rootView;
    }
    public static P5CalcFragment newInstance() {
        P5CalcFragment fragment = new P5CalcFragment();
//        Bundle args = new Bundle();
//        args.putInt("personalUserId", param1);
//        fragment.setArguments(args);
        return fragment;
    }

    private void initView() {
        list=new ArrayList<>();
        fm=getChildFragmentManager();
        CalcP5Fragment p5Fragment=new CalcP5Fragment();
        list.add(p5Fragment);
        fm.beginTransaction().add(R.id.calculate_container,p5Fragment).add(R.id.calculate_container, calcP5NumFragment).hide(calcP5NumFragment).commit();
    }

    //切换到过滤条件页面
    public void gotoConditionPage() {
        fm.beginTransaction().show(calcP5NumFragment).hide(list.get(0)).commit();
        isCondition = true;
        calcP5NumFragment.reloadData();
        CalculateActivity act= (CalculateActivity) getActivity();
        act.switchToolBar(isCondition);
    }

    //加号按钮添加
    public void setAddMode() {
        onBackPressed();
        CalcP5Fragment fragment0 = (CalcP5Fragment) list.get(0);
        fragment0.setAddMode();
        key = "";
    }


    public void editData(List<String>list, String key){
        onBackPressed();
        CalcP5Fragment fragment0 = (CalcP5Fragment) this.list.get(0);
        fragment0.setEditMode();//按钮变化
        fragment0.editData(list);
        fragment0.ShowTip();
        this.key = key;//key赋值
    }

    public void onBackPressed() {
        CalculateActivity act= (CalculateActivity) getActivity();
        if (isCondition) {
            isCondition = false;
            act.switchToolBar(isCondition);
            CalcP5Fragment fragment0 = (CalcP5Fragment) list.get(0);
            fragment0.setClearMode();
            key = "";
            fm.beginTransaction().hide(calcP5NumFragment).show(list.get(0)).commit();
        } else {
            act.finishAct();
        }
    }
}
