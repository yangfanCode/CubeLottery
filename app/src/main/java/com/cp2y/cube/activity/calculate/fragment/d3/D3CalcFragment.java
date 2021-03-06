package com.cp2y.cube.activity.calculate.fragment.d3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
public class D3CalcFragment extends Fragment {
    private boolean isCondition = false;
    private FragmentManager fm ;
    private CalcD3NumFragment calcD3NumFragment=new CalcD3NumFragment();
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
        View rootView=inflater.inflate(R.layout.fragment_d3_calc, container, false);
        initView(rootView);
        initData();
        initstener();
        return rootView;
    }
    public static D3CalcFragment newInstance() {
        D3CalcFragment fragment = new D3CalcFragment();
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
                FragmentTransaction transaction=fm.beginTransaction();
                for(int j=0;j<list.size();j++){
                    if(j==i){
                        transaction.show(list.get(j));
                    }else{
                        transaction.hide(list.get(j));
                    }
                }
                transaction.commit();
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
        tab.setItemInnerPaddingLeft(DisplayUtil.dip2px(20));
        tab.setItemInnerPaddingRight(DisplayUtil.dip2px(20));
        int width=getResources().getDisplayMetrics().widthPixels;
        tab.setNeedEqual(true,width);
        tab.setTabData(new ArrayList<CharSequence>(){{add("直选");add("组3");add("组6");add("组选");}},0);
        CalcD3ZhixuanFragment zhixuanFragment=new CalcD3ZhixuanFragment();
        CalcD3Zu3Fragment zu3Fragment=new CalcD3Zu3Fragment();
        CalcD3Zu6Fragment zu6Fragment=new CalcD3Zu6Fragment();
        CalcD3ZuXuanFragment zuxuanFragment=new CalcD3ZuXuanFragment();
        list.add(zhixuanFragment);
        list.add(zu3Fragment);
        list.add(zu6Fragment);
        list.add(zuxuanFragment);
        //初始化
        FragmentTransaction transaction=fm.beginTransaction();
        for(int i=0;i<list.size();i++){
            transaction.add(R.id.calculate_container,list.get(i));
            if(i!=0){
                transaction.hide(list.get(i));
            }
        }
        transaction.add(R.id.calculate_container, calcD3NumFragment).hide(calcD3NumFragment).commit();
    }
    public void gotoConditionPage() {
        fm.beginTransaction().show(calcD3NumFragment).hide(list.get(tab.getSelectedTabPosition())).commit();
        isCondition = true;
        calcD3NumFragment.reloadData();
        CalculateActivity act= (CalculateActivity) getActivity();
        act.switchToolBar(isCondition);
        calculate_layout.setVisibility(View.GONE);
    }
    public void setAddMode() {
        onBackPressed();
        CalcD3ZhixuanFragment fragment0 = (CalcD3ZhixuanFragment) list.get(0);
        fragment0.setAddMode();
        CalcD3Zu3Fragment fragment1 = (CalcD3Zu3Fragment) list.get(1);
        fragment1.setAddMode();
        CalcD3Zu6Fragment fragment2 = (CalcD3Zu6Fragment) list.get(2);
        fragment2.setAddMode();
        CalcD3ZuXuanFragment fragment3 = (CalcD3ZuXuanFragment) list.get(3);
        fragment3.setAddMode();
        key = "";
    }

    public void editData(List<String>list, String key,int keyNum){
        onBackPressed();
        CalcD3ZhixuanFragment fragment0 = (CalcD3ZhixuanFragment) this.list.get(0);
        CalcD3Zu3Fragment fragment1 = (CalcD3Zu3Fragment) this.list.get(1);
        CalcD3Zu6Fragment fragment2 = (CalcD3Zu6Fragment) this.list.get(2);
        CalcD3ZuXuanFragment fragment3 = (CalcD3ZuXuanFragment) this.list.get(3);
        fragment0.setEditMode();
        fragment1.setEditMode();
        fragment2.setEditMode();
        fragment3.setEditMode();
        if (keyNum < 1000) {//普通模式
           tab.setCurrentItem(0);
            fragment0.editData(list);
            fragment0.ShowTip();
        } else if(keyNum>3000&&keyNum<6000){//组3
            tab.setCurrentItem(1);
            fragment1.editData(list,keyNum);
            fragment1.ShowTip();
        }else if(keyNum>6000&&keyNum<9000){//组6
            tab.setCurrentItem(2);
            fragment2.editData(list,keyNum);
            fragment2.ShowTip();
        }else{
            tab.setCurrentItem(3);
            fragment3.editData(list,keyNum);
            fragment3.ShowTip();
        }
        this.key = key;//key赋值
    }

    public void onBackPressed() {
        CalculateActivity act= (CalculateActivity) getActivity();
        if (isCondition) {
            calculate_layout.setVisibility(View.VISIBLE);
            isCondition = false;
            act.switchToolBar(isCondition);
            CalcD3ZhixuanFragment fragment0 = (CalcD3ZhixuanFragment) list.get(0);
            fragment0.setClearMode();
            CalcD3Zu3Fragment fragment1 = (CalcD3Zu3Fragment) list.get(1);
            fragment1.setClearMode();
            CalcD3Zu6Fragment fragment2 = (CalcD3Zu6Fragment) list.get(2);
            fragment2.setClearMode();
            CalcD3ZuXuanFragment fragment3 = (CalcD3ZuXuanFragment) list.get(3);
            fragment3.setClearMode();
            key = "";
            fm.beginTransaction().hide(calcD3NumFragment).show(list.get(tab.getSelectedTabPosition())).commit();
        } else {
            act.finishAct();
        }
    }
}
