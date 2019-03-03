package com.cp2y.cube.activity.calculate.fragment.doubleball;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.fragment.doubleball.adapter.CalcDanTuoBlueBallAdapter;
import com.cp2y.cube.activity.calculate.fragment.doubleball.adapter.CalcDanTuoRedDanBallAdapter;
import com.cp2y.cube.activity.calculate.fragment.doubleball.adapter.CalcDanTuoRedTuoBallAdapter;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalcDoubleDanTuoFragment extends Fragment implements BallSelectCall,MyInterface.setSelectNumD3PopUps{
    private View rootView;
    private int onClickFlag_dantuo = 1;
    private Set<String> list_select_red1 = new HashSet<>(); //红胆选中数据集合
    private Set<String> list_select_red2 = new HashSet<>(); //红托选中数据集合
    private Set<String> list_select_blue = new HashSet<>(); //蓝球选中数据集合
    private Button btn_clear,btn_submit;
    private LinearLayout tip_ll;
    private static CalcDanTuoBlueBallAdapter adapter_blue;
    private static CalcDanTuoRedDanBallAdapter adapter_red_dan;
    private static CalcDanTuoRedTuoBallAdapter adapter_red_tuo;
    private int selectCount;
    private int editPosition=-1;
    private List<String> list = new ArrayList<>();
    private List<String>list_check=new ArrayList<String>();
    private boolean isEdit=false;//是否编辑形态
    private RelativeLayout selectNum_popUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_dan_tuo, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener(v1 -> clear());
        //btn_filter.setOnClickListener(v ->numSet(1,1));
        btn_submit.setOnClickListener(v -> numSet(1,2));

    }

    public static CalcDoubleDanTuoFragment newInstance() {
        CalcDoubleDanTuoFragment fragment = new CalcDoubleDanTuoFragment();
//        Bundle args = new Bundle();
//        args.putInt("personalUserId", param1);
//        fragment.setArguments(args);
        return fragment;
    }

    private void numSet(int pos1,int pos2) {
        DoubleBallCalcFragment fragment = (DoubleBallCalcFragment) CalcDoubleDanTuoFragment.this.getParentFragment();
        if("".equals(fragment.getKey())){
            submit_filter(pos1);//添加号码
        }else{
            if(CommonUtil.parseInt(CommonUtil.cutKey(fragment.getKey()))>1000){
                submit_filter(pos2);//编辑不同模式删除添加号码 或 编辑号码
            }else{
                submit_filter(3);//切换模式编辑
            }
        }
    }
    public void setEditMode() {
        isEdit=true;
    }

    public void setAddMode() {
        isEdit=false;
        clear();
    }

    public void setClearMode() {
        isEdit=false;
        clear();
    }

    //编辑展示数据
    public void editData(List<String> list) {
        List<String> list_red_dan_edit = new ArrayList<>();
        List<String> list_red_tuo_edit = new ArrayList<>();
        List<String> list_blue_edit = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(list.get(i)) < 50) {
                list_red_dan_edit.add(list.get(i));
            } else if (Integer.parseInt(list.get(i)) < 100 && Integer.parseInt(list.get(i)) > 50) {
                list_red_tuo_edit.add("" + (Integer.parseInt(list.get(i)) - 50));
            } else if (Integer.parseInt(list.get(i)) > 100) {
                list_blue_edit.add("" + (Integer.parseInt(list.get(i)) - 100));
            }

        }
        //清除数据重新刷新
        adapter_red_dan.reloadData(list_red_dan_edit);
        adapter_red_tuo.reloadData(list_red_tuo_edit);
        adapter_blue.reloadData(list_blue_edit);
    }

    //过滤和确定按钮
    public void submit_filter(int type){
        if (list_select_red1.size() == 0 || list_select_red1.size() > 5) {
            TipsToast.showTips("请选择(1-5)个胆码");
            return;
        }
        if (list_select_red2.size() == 0 || (list_select_red1.size() + list_select_red2.size()) < 6 || list_select_blue.size() == 0) {
            TipsToast.showTips("至少选择一注");
            return;
        }
//        if(selectCount>100000){
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        DoubleBallCalcFragment fragment = (DoubleBallCalcFragment) CalcDoubleDanTuoFragment.this.getParentFragment();
        if(SingletonMapNomal.getMapSize()>=10&&isEdit==false){
            TipsToast.showTips("最多添加10注号码");
        }else {
            ArrayList<String> listfilter_dantuo = new ArrayList<>();
            listfilter_dantuo.addAll(list_select_red1);
            //红拖特殊标记 加50
            for (String num : list_select_red2) {
                listfilter_dantuo.add(Integer.parseInt(num) + 50 + "");
            }
            //蓝球特殊标记 加100
            for (String num : list_select_blue) {
                listfilter_dantuo.add(Integer.parseInt(num) + 100 + "");
            }
            CommonUtil.SortCollection(listfilter_dantuo);
            if(type==2){//编辑模式
                SingletonMapNomal.editDelete(fragment.getKey(),listfilter_dantuo);
            }else if(type==1){//添加号码
                SingletonMapNomal.registerService(SingletonMapNomal.sign_sort+"_"+(onClickFlag_dantuo + 1000) , listfilter_dantuo);
                onClickFlag_dantuo++;
                SingletonMapNomal.sign_sort++;
            }else if(type==3){//改变模式编辑
                SingletonMapNomal.removeMap(fragment.getKey());//删除原数据
                SingletonMapNomal.registerService(CommonUtil.cutFirstKey(fragment.getKey())+"_"+onClickFlag_dantuo + 1000 , listfilter_dantuo);
                onClickFlag_dantuo++;
            }
            fragment.gotoConditionPage();
        }
    }
    public void clear() {
        adapter_red_dan.reset();
        adapter_red_tuo.reset();
        adapter_blue.reset();
        tip_ll.setVisibility(View.GONE);
    }
    private void initView() {
        MyGridView gv_red_dan = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_redDan_gv);
        MyGridView gv_red_tuo = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_RedTuo_gv);
        MyGridView gv_blue = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_blue_gv);
        gv_red_dan.setSelector(android.R.color.transparent);
        gv_red_tuo.setSelector(android.R.color.transparent);
        gv_blue.setSelector(android.R.color.transparent);
        btn_clear = (Button) rootView.findViewById(R.id.selectNum_filterDanTuo_btn_clear);
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterDantuo_btn_submit);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_filterDanTuo_ll);
        selectNum_popUp = (RelativeLayout)rootView. findViewById(R.id.selectNum_3d_layout);
        List<String> list_red = new ArrayList<>(), list_blue = new ArrayList<>();
        for (int i = 1; i < 34; i++) {
            list_red.add(String.valueOf(i));
        }
        for (int i = 1; i < 17; i++) {
            list_blue.add(i + "");
        }
        adapter_red_dan = new CalcDanTuoRedDanBallAdapter(getContext(), R.layout.item_selectfilter_nomalred,false, list_select_red1);
        adapter_red_dan.initData(list_red);
        gv_red_dan.setAdapter(adapter_red_dan);
        adapter_red_tuo = new CalcDanTuoRedTuoBallAdapter(getContext(), R.layout.item_selectfilter_nomalred,false, list_select_red2);
        adapter_red_tuo.initData(list_red);
        adapter_red_tuo.setDanList(list_select_red1);
        gv_red_tuo.setAdapter(adapter_red_tuo);
        adapter_blue = new CalcDanTuoBlueBallAdapter(getContext(), R.layout.item_selectfilter_nomalblue, false, list_select_blue);
        adapter_blue.initData(list_blue);
        gv_blue.setAdapter(adapter_blue);
        adapter_red_dan.setCall(this);
        adapter_red_tuo.setCall(this);
        adapter_blue.setCall(this);
        adapter_red_dan.setPopUps(this);
        adapter_red_tuo.setPopUps(this);
        adapter_blue.setPopUps(this);
    }

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter, String val, boolean isLongClick) {
        if (adapter == adapter_red_dan && list_select_red1.size() > 4 && !list_select_red1.contains(val)) {
            TipsToast.showTips("胆码选择不能超过5个");
            return false;
        } else if (adapter == adapter_red_tuo && list_select_red1.contains(val)) {
            return false;
        }
        return true;
    }


    @Override
    public void onBallSelected(BaseAdapter adapter, String val, boolean selected) {
        if (adapter == adapter_red_dan) {
            if (selected) list_select_red2.remove(val);
            adapter_red_tuo.notifyDataSetChanged();
        }
        ShowTip();
    }
    public void ShowTip(){
        boolean showTips = list_select_blue.size()>0 && (list_select_red1.size() + list_select_red2.size()) > 5&&list_select_red1.size()>0;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (showTips) {
            int redCount1 = list_select_red1.size();
            selectCount = CombineAlgorithm.combination(list_select_red2.size(), 6 - redCount1) * list_select_blue.size();
            TextView tv_count= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_count);
            tv_count.setText(String.valueOf(selectCount));
            TextView tv_red = (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_RedNum);
            tv_red.setText(String.valueOf(list_select_red1.size()));
            TextView tv_red_tuo= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_RedtuoNum);
            tv_red_tuo.setText(String.valueOf(list_select_red2.size()));
            TextView tv_blue= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_BlueNum);
            tv_blue.setText(String.valueOf(list_select_blue.size()));
        } else {
            selectCount = 0;
        }
    }
    @Override
    public boolean onLottoBeforeSelected(BaseAdapter adapter, String val) {
        return false;
    }

    @Override
    public void setSelectNumD3PopUp(int x, int y, int postion, boolean isShow, boolean isRed) {
//        if(isShow){
//            if(selectNum_popUp.getChildCount()==0){
//                View popUp=null;
//                if(isRed){
//                    popUp=LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_redlayout,selectNum_popUp,false);
//                }else{
//                    popUp=LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_bluelayout,selectNum_popUp,false);
//                }
//                TextView num_ball= (TextView) popUp.findViewById(R.id.selectNum_pop_ball);
//                num_ball.setText(CommonUtil.preZeroForBall(String.valueOf(postion)));
//                Toolbar toolbar=ContextHelper.getActivity(SelectNumFilterActivity.class).getToolBar();
//                toolbar.measure(0,0);
//                int toolBarHeight=toolbar.getMeasuredHeight();//toolbar高度
//                int systemBarHeight=CommonUtil.getStatusBarHeight();//系统状态栏高度
//                selectNum_popUp.addView(popUp);
//                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) popUp.getLayoutParams();
//                params.setMargins((x- DisplayUtil.dip2px(13.5f)),(y-toolBarHeight-systemBarHeight- DisplayUtil.dip2px(54.5f)),0,0);
//                popUp.setLayoutParams(params);
//            }
//        }else{
//            if(selectNum_popUp.getChildCount()>0){
//                selectNum_popUp.removeAllViews();
//            }
//        }
    }
}
