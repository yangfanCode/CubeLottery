package com.cp2y.cube.fragment.lotto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.adapter.MyFilterLottoDanTuoBlueBallAdapter;
import com.cp2y.cube.adapter.MyFilterLottoDanTuoBlueHouTuoBallAdapter;
import com.cp2y.cube.adapter.MyFilterLottoDanTuoRedDanBallAdapter;
import com.cp2y.cube.adapter.MyFilterLottoDanTuoRedTuoBallAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.FilterBaseFragment;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.services.LotteryService;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class LottoDanTuoFragment extends FilterBaseFragment implements BallSelectCall,MyInterface.setSelectNumD3PopUps,MyInterface.isSavaNumSuccess{
    private static final int REQUEST_CODE=200;
    private View rootView;
    private boolean ignoreFlag = true;
    private int onClickFlag_dantuo = 1;
    private Set<String> list_select_red1 = new HashSet<>(); //红胆选中数据集合
    private Set<String> list_select_red2 = new HashSet<>(); //红托选中数据集合
    private Set<String> list_select_blue = new HashSet<>(); //后胆选中数据集合
    private Set<String> list_select_blue_houTuo = new HashSet<>(); //后托选中数据集合
    private Button btn_clear, btn_save, btn_filter,btn_cancel,btn_submit,btn_back;
    private LinearLayout tip_ll;
    private static MyFilterLottoDanTuoBlueBallAdapter adapter_blue;
    private static MyFilterLottoDanTuoBlueHouTuoBallAdapter adapter_blue_houTuo;
    private static MyFilterLottoDanTuoRedDanBallAdapter adapter_red_dan;
    private static MyFilterLottoDanTuoRedTuoBallAdapter adapter_red_tuo;
    private int selectCount;
    private int editPosition=-1;
    private List<String> list = new ArrayList<>();
    private  List<String>list_check=new ArrayList<String>();
    private boolean isEdit=false;
    private RelativeLayout selectNum_popUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_lotto_dantuo, container, false);
        MyGridView gv_red_dan = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_redDan_gv);
        MyGridView gv_red_tuo = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_RedTuo_gv);
        MyGridView gv_blue = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_blue_gv);
        MyGridView gv_blue_houTuo = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_DanTuo_blue_gv_houTuo);
        gv_red_dan.setSelector(android.R.color.transparent);
        gv_red_tuo.setSelector(android.R.color.transparent);
        gv_blue.setSelector(android.R.color.transparent);
        gv_blue_houTuo.setSelector(android.R.color.transparent);//无点击效果
        selectNum_popUp = (RelativeLayout)rootView. findViewById(R.id.selectNum_3d_layout);
        btn_clear = (Button) rootView.findViewById(R.id.selectNum_filterDanTuo_btn_clear);
        btn_save = (Button) rootView.findViewById(R.id.selectNum_filterfilterDanTuo_btn_save);
        btn_filter = (Button) rootView.findViewById(R.id.selectNum_filter_btnfilterDanTuo_btn_filter);
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterDantuo_btn_submit);
        btn_cancel = (Button) rootView.findViewById(R.id.selectNum_filterDantuo_btn_cancel);
        btn_back = (Button) rootView.findViewById(R.id.selectNum_filterDantuo_btn_back);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_filterDanTuo_ll);
        getService(LotteryService.class).isSaveNumSuccess(this);//初始化保存接口
        List<String> list_red = new ArrayList<>(), list_blue = new ArrayList<>();
        for (int i = 1; i < 36; i++) {
            list_red.add(String.valueOf(i));
        }
        for (int i = 1; i < 13; i++) {
            list_blue.add(i + "");
        }
        adapter_red_dan = new MyFilterLottoDanTuoRedDanBallAdapter(getContext(), R.layout.item_selectfilter_nomalred,false, list_select_red1);
        adapter_red_dan.initData(list_red);
        gv_red_dan.setAdapter(adapter_red_dan);
        adapter_red_tuo = new MyFilterLottoDanTuoRedTuoBallAdapter(getContext(), R.layout.item_selectfilter_nomalred,false, list_select_red2);
        adapter_red_tuo.initData(list_red);
        adapter_red_tuo.setDanList(list_select_red1);
        gv_red_tuo.setAdapter(adapter_red_tuo);
        adapter_blue = new MyFilterLottoDanTuoBlueBallAdapter(getContext(), R.layout.item_selectfilter_nomalblue, false, list_select_blue);
        adapter_blue.initData(list_blue);
        gv_blue.setAdapter(adapter_blue);
        adapter_blue_houTuo = new MyFilterLottoDanTuoBlueHouTuoBallAdapter(getContext(), R.layout.item_selectfilter_nomalblue, false, list_select_blue_houTuo);
        adapter_blue_houTuo.initData(list_blue);
        adapter_blue_houTuo.setDanList(list_select_blue);
        gv_blue_houTuo.setAdapter(adapter_blue_houTuo);
        //初始化接口
        adapter_red_dan.setCall(this);
        adapter_red_tuo.setCall(this);
        adapter_blue.setCall(this);
        adapter_blue_houTuo.setCall(this);
        adapter_red_dan.setPopUps(this);
        adapter_red_tuo.setPopUps(this);
        adapter_blue.setPopUps(this);
        adapter_blue_houTuo.setPopUps(this);
        btn_clear.setOnClickListener(v1 -> clear());
        btn_filter.setOnClickListener(v -> numSet(1,1));
        btn_submit.setOnClickListener(v -> numSet(1,2));
        btn_cancel.setOnClickListener(view -> ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage());
        btn_back.setOnClickListener(view -> ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage());
        btn_save.setOnClickListener(view -> {
            boolean loginState= LoginSPUtils.isLogin();
            if(loginState) {
                //判断相同号码
                list_check.clear();
                list_check.addAll(list_select_red1);
                for (String num : list_select_red2) {
                    list_check.add((Integer.parseInt(num) + 50) + "");
                }
                for (String num : list_select_blue) {
                    list_check.add((Integer.parseInt(num) + 100) + "");
                }
                for (String num : list_select_blue_houTuo) {
                    list_check.add((Integer.parseInt(num) + 150) + "");
                }
                if (CommonUtil.ListCheck(list_check, list)) {
                    TipsToast.showTips("已存在号码库");
                    return;
                }
                if (list_select_red1.size() > 0 && list_select_red2.size() > 0 && (list_select_red1.size() + list_select_red2.size()) > 4 && (list_select_blue.size() + list_select_blue_houTuo.size()) > 1) {
                    list.clear();
                    list.addAll(list_select_red1);
                    for (String num : list_select_red2) {
                        list.add((Integer.parseInt(num) + 50) + "");
                    }
                    for (String num : list_select_blue) {
                        list.add((Integer.parseInt(num) + 100) + "");
                    }
                    for (String num : list_select_blue_houTuo) {
                        list.add((Integer.parseInt(num) + 150) + "");
                    }
                    CommonUtil.SortCollection(list);
                    getService(LotteryService.class).saveLotteryNumber(list, 2, 1,selectCount);
                } else if (list_select_red1.size() == 0 && list_select_red2.size() == 0 && list_select_blue.size() == 0 && list_select_blue_houTuo.size() == 0) {
                    TipsToast.showTips("请至少选择5个前区+2个后区");
                } else if (list_select_red1.size() == 0) {
                    TipsToast.showTips("前胆至少选择1个");
                } else if (list_select_red1.size() + list_select_red2.size() < 5) {
                    TipsToast.showTips("前胆+前拖至少选择5个");
                } else if ((list_select_blue.size() + list_select_blue_houTuo.size()) < 2) {
                    TipsToast.showTips("后胆+后拖至少选择2个");
                }
            }else{
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        });

        return rootView;
    }

    private void numSet(int pos1,int pos2) {
        LottoSelectNumActivity act =  ContextHelper.getActivity(LottoSelectNumActivity.class);
        if("".equals(act.getKey())){
            submit_filter(pos1);//添加号码
        }else{
            if(CommonUtil.parseInt(CommonUtil.cutKey(act.getKey()))>1000){
                submit_filter(pos2);//正常编辑
            }else{
                submit_filter(3);//切换模式
            }
            //编辑不同模式删除添加号码 或 编辑号码
        }
    }

    //编辑展示数据
    public void editData(List<String> list) {
        List<String> list_red_dan_edit = new ArrayList<>();
        List<String> list_red_tuo_edit = new ArrayList<>();
        List<String> list_blue_edit = new ArrayList<>();
        List<String> list_blue_houtuo_edit = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(list.get(i)) < 50) {
                list_red_dan_edit.add(list.get(i));
            } else if (Integer.parseInt(list.get(i)) < 100 && Integer.parseInt(list.get(i)) > 50) {
                list_red_tuo_edit.add("" + (Integer.parseInt(list.get(i)) - 50));
            } else if (Integer.parseInt(list.get(i)) > 100&&Integer.parseInt(list.get(i))<150) {
                list_blue_edit.add("" + (Integer.parseInt(list.get(i)) - 100));
            }else if(Integer.parseInt(list.get(i)) > 150){
                list_blue_houtuo_edit.add("" + (Integer.parseInt(list.get(i)) - 150));
            }
        }
        //清除数据重新刷新
        adapter_red_dan.reloadData(list_red_dan_edit);
        adapter_red_tuo.reloadData(list_red_tuo_edit);
        adapter_blue.reloadData(list_blue_edit);
        adapter_blue_houTuo.reloadData(list_blue_houtuo_edit);
    }

    //确定按钮
    public void submit_filter(int type){
        if(list_select_red1.size() == 0&&list_select_red2.size()==0&&list_select_blue.size()==0&&list_select_blue_houTuo.size()==0){
            TipsToast.showTips("请至少选择5个前区+2个后区");
            return;
        }
        if (list_select_red1.size() == 0 || list_select_red1.size() > 4) {
            TipsToast.showTips("前胆至少选择1个");
            return;
        }
        if((list_select_red1.size() + list_select_red2.size()) < 5){
            TipsToast.showTips("前胆+前拖至少选择5个");
            return;
        }
        if (list_select_blue.size()+list_select_blue_houTuo.size()<2 && (list_select_red1.size() + list_select_red2.size()) > 4 ) {
            TipsToast.showTips("后胆+后拖至少选择2个");
            return;
        }
        if(selectCount>100000){
            TipsToast.showTips("选号不可超过10万注");
            return;
        }
        if(SingletonMapNomal.getMapSize()>=10&&isEdit==false){
            TipsToast.showTips("“我的选号”已满，最多10组号码");
            ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage();
        }else {
            ArrayList<String> listfilter_dantuo = new ArrayList<>();
            listfilter_dantuo.addAll(list_select_red1);
            //红拖特殊标记 加50
            for (String num : list_select_red2) {
                listfilter_dantuo.add(Integer.parseInt(num) + 50 + "");
            }
            //后胆特殊标记 加100
            if(list_select_blue.size()>0){
                for (String num : list_select_blue) {
                    listfilter_dantuo.add(Integer.parseInt(num) + 100 + "");
                }
            }else{//后胆不选传100
                listfilter_dantuo.add("100");
            }

            //后拖特殊标记 加150
            for (String num : list_select_blue_houTuo) {
                listfilter_dantuo.add(Integer.parseInt(num) + 150 + "");
            }
            CommonUtil.SortCollection(listfilter_dantuo);
            LottoSelectNumActivity act = ContextHelper.getActivity(LottoSelectNumActivity.class);
            if(type==2){//编辑号码
                SingletonMapNomal.editDelete(act.getKey(),listfilter_dantuo);
            }else if(type==1){//添加号码
                SingletonMapNomal.registerService(SingletonMapNomal.sign_sort+"_"+(onClickFlag_dantuo + 1000), listfilter_dantuo);
                onClickFlag_dantuo++;
                SingletonMapNomal.sign_sort++;
            }else if(type==3){//改变模式编辑
                SingletonMapNomal.removeMap(act.getKey());//删除原数据
                SingletonMapNomal.registerService(CommonUtil.cutFirstKey(act.getKey())+"_"+onClickFlag_dantuo + 1000 , listfilter_dantuo);
                onClickFlag_dantuo++;
            }
            act.gotoConditionPage();
        }
    }
    public void clear() {
        adapter_red_dan.reset();
        adapter_red_tuo.reset();
        adapter_blue.reset();
        adapter_blue_houTuo.reset();
        tip_ll.setVisibility(View.GONE);
    }

    public void setEditMode() {
        isEdit=true;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
    }

    public void setAddMode() {
        isEdit=false;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
        clear();
    }

    public void setClearMode() {
        isEdit=false;
        btn_clear.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
        btn_filter.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
        btn_submit.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        clear();
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Bundle bundle = data.getBundleExtra("bundle");
            if (resultCode == 200 && bundle != null) {
                setEditMode();
                List<String> list = (List<String>) bundle.getSerializable("edit_list");
                editPosition=bundle.getInt("pos");
                editData(list);
            }else if(resultCode==300){
                //除编辑形态清空数据
                setAddMode();
            }else if(resultCode==400){
                //除编辑形态清空数据
                setClearMode();
            }
        }
    }

    public void switchIgnore() {
        adapter_red_dan.setIgnore(ignoreFlag);
        adapter_red_tuo.setIgnore(ignoreFlag);
        adapter_blue.setIgnore(ignoreFlag);
        adapter_blue_houTuo.setIgnore(ignoreFlag);
        ignoreFlag = !ignoreFlag;
    }

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter, String val,boolean isLongClick) {
        if (adapter == adapter_red_dan && list_select_red1.size() > 3 && !list_select_red1.contains(val)) {
            TipsToast.showTips("前胆选择不能超过4个");
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
        //后托刷新
        if(adapter == adapter_blue){
            if (selected) list_select_blue_houTuo.remove(val);
            adapter_blue_houTuo.notifyDataSetChanged();
        }
        ShowTip();
    }
    public void ShowTip(){
        boolean showTips = (list_select_blue.size()+list_select_blue_houTuo.size()>1)&&(list_select_red1.size() + list_select_red2.size()) > 4&&list_select_red1.size()>0;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (showTips) {
            int redCount1 = list_select_red1.size();
            selectCount = CombineAlgorithm.combination(list_select_red2.size(), 5 - redCount1) * CombineAlgorithm.combination(list_select_blue_houTuo.size(), 2 - list_select_blue.size());
            TextView tv_count= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_count);
            tv_count.setText(String.valueOf(selectCount));
            TextView tv_red = (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_RedNum);
            tv_red.setText(String.valueOf(list_select_red1.size()));
            TextView tv_red_tuo= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_RedtuoNum);
            tv_red_tuo.setText(String.valueOf(list_select_red2.size()));
            TextView tv_blue= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_BlueNum);
            tv_blue.setText(String.valueOf(list_select_blue.size()));
            TextView tv_blue_houTuo= (TextView) rootView.findViewById(R.id.selectNum_filterDanTuo_BlueNum_houTuo);
            tv_blue_houTuo.setText(String.valueOf(list_select_blue_houTuo.size()));
        } else {
            selectCount = 0;
        }
    }
    //大乐透蓝球胆拖控制
    @Override
    public boolean onLottoBeforeSelected(BaseAdapter adapter, String val) {
        if (adapter == adapter_blue && list_select_blue.size() > 0 && !list_select_blue.contains(val)) {
            TipsToast.showTips("后胆选择不能超过1个");
            return false;
        } else if (adapter == adapter_blue_houTuo && list_select_blue.contains(val)) {
            return false;
        }
        return true;
    }

    @Override
    public void Success(boolean success) {
//        if(success){
//
//        }
    }

    @Override
    public void setSelectNumD3PopUp(int x, int y, int postion, boolean isShow, boolean isRed) {
        if(isShow){
            if(selectNum_popUp.getChildCount()==0){
                View popUp=null;
                if(isRed){
                    popUp=LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_redlayout,selectNum_popUp,false);
                }else{
                    popUp=LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_bluelayout,selectNum_popUp,false);
                }
                TextView num_ball= (TextView) popUp.findViewById(R.id.selectNum_pop_ball);
                num_ball.setText(CommonUtil.preZeroForBall(String.valueOf(postion)));
                Toolbar toolbar=ContextHelper.getActivity(LottoSelectNumActivity.class).getToolBar();
                toolbar.measure(0,0);
                int toolBarHeight=toolbar.getMeasuredHeight();//toolbar高度
                int systemBarHeight=CommonUtil.getStatusBarHeight();//系统状态栏高度
                selectNum_popUp.addView(popUp);
                RelativeLayout.LayoutParams params= (RelativeLayout.LayoutParams) popUp.getLayoutParams();
                params.setMargins((x- DisplayUtil.dip2px(13.5f)),(y-toolBarHeight-systemBarHeight- DisplayUtil.dip2px(54.5f)),0,0);
                popUp.setLayoutParams(params);
            }
        }else{
            if(selectNum_popUp.getChildCount()>0){
                selectNum_popUp.removeAllViews();
            }
        }
    }
}
