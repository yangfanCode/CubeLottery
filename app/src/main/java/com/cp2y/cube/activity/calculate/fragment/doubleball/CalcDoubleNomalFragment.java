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
import com.cp2y.cube.activity.calculate.fragment.doubleball.adapter.CalcNomalBlueBallAdapter;
import com.cp2y.cube.activity.calculate.fragment.doubleball.adapter.CalcNomalRedBallAdapter;
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
public class CalcDoubleNomalFragment extends Fragment implements BallSelectCall,MyInterface.setSelectNumD3PopUps{
    private View rootView;
    private int onClickFlag=1;
    private int editPosition=-1;
    private boolean ignoreFlag=true;
    private MyGridView gv_red,gv_blue;
    private static Set<String> list_red_select=new HashSet<>();
    private static Set<String> list_blue_select=new HashSet<>();
    private Button btn_clear,btn_submit;
    private TextView tv_redNum,tv_BlueNum;
    private LinearLayout tip_ll;
    private CalcNomalRedBallAdapter adapter_red;
    private CalcNomalBlueBallAdapter adapter_blue;
    private boolean ignore=false;
    private int selectCount;
    private List<String> list=new ArrayList<>();
    private List<String>list_check1=new ArrayList<String>();//检测重复保存
    private List<String>list_check2=new ArrayList<String>();//检测重复保存
    private boolean isEdit=false;//是否编辑形态
    private RelativeLayout selectNum_popUp;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.fragment_nomal2, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener((v1 -> clear()));
        //过滤
        //btn_filter.setOnClickListener(v->numSet(1,1));
        //编辑提交
        btn_submit.setOnClickListener(v ->numSet(1,2));

    }

    public static CalcDoubleNomalFragment newInstance() {
        CalcDoubleNomalFragment fragment = new CalcDoubleNomalFragment();
//        Bundle args = new Bundle();
//        args.putInt("personalUserId", param1);
//        fragment.setArguments(args);
        return fragment;
    }

    //号码设置
    private void numSet(int pos1,int pos2) {
        DoubleBallCalcFragment fragment = (DoubleBallCalcFragment) CalcDoubleNomalFragment.this.getParentFragment();
        if("".equals(fragment.getKey())){
            submit_filter(pos1);//添加号码
        }else{
            if(CommonUtil.parseInt(CommonUtil.cutKey(fragment.getKey()))>1000){
                submit_filter(3);
            }else{
                submit_filter(pos2);//改变模式编辑号码 或者编辑号码
            }
        }
    }
    private void initView() {
        gv_red = (MyGridView)rootView. findViewById(R.id.selectNum_filter_layout_Nomalred_gv);
        gv_blue = (MyGridView)rootView. findViewById(R.id.selectNum_filter_layout_Nomalblue_gv);
        gv_red.setSelector(android.R.color.transparent);
        gv_blue.setSelector(android.R.color.transparent);
        btn_clear = (Button)rootView. findViewById(R.id.selectNum_filterNomal_btn_clear);
        btn_submit = (Button)rootView. findViewById(R.id.selectNum_filterfilterNomal_btn_submit);
        selectNum_popUp = (RelativeLayout)rootView. findViewById(R.id.selectNum_3d_layout);
        tv_redNum = (TextView)rootView. findViewById(R.id.selectNum_filterNomal_RedNum);
        tv_BlueNum = (TextView)rootView. findViewById(R.id.selectNum_filterNomal_BlueNum);
        tip_ll = (LinearLayout)rootView. findViewById(R.id.selectNum_filterNomal_ll);
        List<String> list_red = new ArrayList<>(),list_blue = new ArrayList<>();
        for (int i = 1; i < 34; i++) {
            list_red.add(i+"");
        }
        adapter_red=new CalcNomalRedBallAdapter(getActivity(),R.layout.item_selectfilter_nomalred,ignore,list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_red.initData(list_red);
        for (int i = 1; i < 17; i++) {
            list_blue.add(i+"");
        }
        adapter_blue=new CalcNomalBlueBallAdapter(getActivity(),R.layout.item_selectfilter_nomalblue,ignore,list_blue_select);
        gv_blue.setAdapter(adapter_blue);
        adapter_blue.initData(list_blue);
        adapter_red.setCall(this);
        adapter_blue.setCall(this);
        adapter_red.setPopUps(this);
        adapter_blue.setPopUps(this);
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
    public void clear(){
        list_red_select.clear();
        list_blue_select.clear();
        adapter_red.notifyDataSetChanged();
        adapter_blue.notifyDataSetChanged();
        tip_ll.setVisibility(View.GONE);
        tv_BlueNum.setText("");
        tv_redNum.setText("");
    }

    //编辑展示数据
    public  void editData(List<String>list){
        List<String>list_red_edit=new ArrayList<>();
        List<String>list_blue_edit=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(Integer.parseInt(list.get(i))<50){
                list_red_edit.add(list.get(i));
            }else{
                list_blue_edit.add(""+(Integer.parseInt(list.get(i))-50));
            }

        }
        //清除数据重新刷新
        list_red_select.clear();
        list_red_select.addAll(list_red_edit);
        adapter_red.notifyDataSetChanged();
        list_blue_select.clear();
        list_blue_select.addAll(list_blue_edit);
        adapter_blue.notifyDataSetChanged();
    }

    //区分红篮球
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
//                Toolbar toolbar= ContextHelper.getActivity(CalculateActivity.class).getToolBar();
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

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter, String val, boolean isLongClick) {
        return true;
    }

    @Override
    public void onBallSelected(BaseAdapter adapter, String val, boolean selected) {
        ShowTip();
    }
    public void ShowTip(){
        boolean showTips = list_blue_select.size()>0 && list_red_select.size() > 5;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (!showTips) {
            selectCount = 0;
        }else {
            selectCount = CombineAlgorithm.combination(list_red_select.size(), 6) * list_blue_select.size();
            TextView tv_red = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_RedNum);
            TextView tv_blue = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_BlueNum);
            TextView tv_count = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_count);
            tv_count.setText(String.valueOf(selectCount));
            tv_red.setText(String.valueOf(list_red_select.size()));
            tv_blue.setText(String.valueOf(list_blue_select.size()));
        }
    }
    @Override
    public boolean onLottoBeforeSelected(BaseAdapter adapter, String val) {
        return false;
    }

    //过滤和确定按钮
    public void submit_filter(int type){
        if(list_red_select.size()<6||list_blue_select.size()==0){
            TipsToast.showTips("请至少选择一注");
            return;
        }
//        if(selectCount>100000){
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        DoubleBallCalcFragment fragment = (DoubleBallCalcFragment) CalcDoubleNomalFragment.this.getParentFragment();
        //号码选择10条不保存
        if(SingletonMapNomal.getMapSize()>=10&&isEdit==false){
            TipsToast.showTips("最多添加10注号码");
        }else{
            ArrayList<String> listfilter_nomal=new ArrayList<>();
            listfilter_nomal.addAll(list_red_select);
            //蓝球特殊标记 加50
            for (String num: list_blue_select) {
                listfilter_nomal.add(Integer.parseInt(num)+50+"");
            }
            CommonUtil.SortCollection(listfilter_nomal);
            if(type==2){//编辑模式
                SingletonMapNomal.editDelete(fragment.getKey(),listfilter_nomal);
            }else if(type==1){//添加号码
                SingletonMapNomal.registerService(SingletonMapNomal.sign_sort+"_"+onClickFlag,listfilter_nomal);
                onClickFlag++;
                SingletonMapNomal.sign_sort++;
            }else if(type==3){//改变模式编辑号码
                SingletonMapNomal.removeMap(fragment.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
                SingletonMapNomal.registerService(CommonUtil.cutFirstKey(fragment.getKey())+"_"+onClickFlag,listfilter_nomal);
                onClickFlag++;
            }
            fragment.gotoConditionPage();

        }
    }
}
