package com.cp2y.cube.fragment.chongqing2;


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
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.selectnums.SelectCQ2XingNumActivity;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMap3D;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.FilterBaseFragment;
import com.cp2y.cube.fragment.chongqing2.adapter.MyFilterCQ2ZuXuanAdapter;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.services.CQ2LotteryService;
import com.cp2y.cube.services.D3LotteryService;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CQ2ZuXuanFragment extends FilterBaseFragment implements BallSelectCall, MyInterface.setSelectNumD3PopUps, MyInterface.isSavaNumSuccess {
    private int onClickFlag_muti = 3001;
    private int onClickFlag_dantuo = 4001;
    private boolean ignoreFlag = true;
    private Button btn_clear, btn_save, btn_filter, btn_submit, btn_cancel, btn_back;
    private MyFilterCQ2ZuXuanAdapter adapter;
    private MyGridView gv_zu3;
    private int selectCount;
    private Set<String> list_select_nomal = new HashSet<>(); //普通或拖码选中数据集合
    private Set<String> list_select_dantuo = new HashSet<>(); //胆选中数据集合
    private LinearLayout tip_ll;
    private List<String> list = new ArrayList<>(); //总保存数据集合
    private List<String> list_check1 = new ArrayList<String>();//重复保存标记
    private List<String> list_check2 = new ArrayList<String>();//重复保存标记
    private boolean isEdit = false;//是否编辑模式
    private RelativeLayout selectNum_popUp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_d3_zu3, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener((v -> {
            clearMode();
        }));
        btn_filter.setOnClickListener((v -> {
            numSet(1, 1);
        }));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
        btn_cancel.setOnClickListener((v -> ContextHelper.getActivity(SelectCQ2XingNumActivity.class).gotoConditionPage()));
        btn_back.setOnClickListener((v -> ContextHelper.getActivity(SelectCQ2XingNumActivity.class).gotoConditionPage()));
        btn_save.setOnClickListener((v -> {
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                if ((list_select_nomal.size() + list_select_dantuo.size()) > 1) {
                    if (list_select_dantuo.size() == 0) {
                        //判断相同号码
                        list_check1.clear();
                        list_check1.addAll(list_select_nomal);
                        if (CommonUtil.ListCheck(list_check1, list)) {//判断重复
                            TipsToast.showTips("已存在号码库");
                            return;
                        }
                        list.clear();
                        list.addAll(list_select_nomal);
                        CommonUtil.SortCollection(list);//排序
                        if (list.size() == 2) {//单式
                            getService(CQ2LotteryService.class).saveLotteryNumber(list, 2, 7,selectCount);
                        } else {//复式
                            getService(CQ2LotteryService.class).saveLotteryNumber(list, 3, 7,selectCount);
                        }
                    } else {
                        //判断相同号码
                        list_check2.clear();
                        list_check2.addAll(list_select_dantuo);
                        //检验蓝球特殊标记 加50
                        for (String num : list_select_nomal) {
                            list_check2.add((Integer.parseInt(num) + 50) + "");
                        }
                        if (CommonUtil.ListCheck(list_check2, list)) {
                            TipsToast.showTips("已存在号码库");
                            return;
                        }
                        list.clear();
                        list.addAll(list_select_dantuo);
                        //拖特殊标记 加50
                        for (String num : list_select_nomal) {
                            list.add(Integer.parseInt(num) + 50 + "");
                        }
                        CommonUtil.SortCollection(list);//排序
                        getService(CQ2LotteryService.class).saveLotteryNumber(list, 4, 7,selectCount);
                    }
                }else{
                    TipsToast.showTips("请至少选2个号");
                }
            } else {
                ((BaseActivity) getActivity()).intentLogin();//登录页面
            }
        }));
    }

    //清空按钮操作
    private void clearMode() {
        tip_ll.setVisibility(View.GONE);
        tip_ll.removeAllViews();
        clear();
        adapter.notifyDataSetChanged();
    }

    //清空数据
    private void clear() {
        list_select_dantuo.clear();
        list_select_nomal.clear();
    }

    //号码设置
    private void numSet(int pos1, int pos2) {
        SelectCQ2XingNumActivity act = ContextHelper.getActivity(SelectCQ2XingNumActivity.class);
        if ("".equals(act.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            //if(CommonUtil.parseInt(CommonUtil.cutKey(act.getKey()))>6000){
            submit_filter(3);//改变模式编辑号码
            //}else{
            // submit_filter(pos2);// 不变模式编辑号码
            //}
        }
    }

    private void initData() {
        List<String> list_num = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list_num.add(String.valueOf(i));
        }
        adapter = new MyFilterCQ2ZuXuanAdapter(getActivity(), false, list_select_nomal, list_select_dantuo);
        gv_zu3.setAdapter(adapter);
        adapter.LoadData(list_num);
        adapter.setCall(this);
        adapter.setPopUps(this);
    }

    private void initView(View rootView) {
        gv_zu3 = (MyGridView) rootView.findViewById(R.id.fragment_3d_gvZu3);
        gv_zu3.setSelector(android.R.color.transparent);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_3d_zu3_tip_ll);
        btn_clear = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_clear);
        btn_save = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_save);
        btn_filter = (Button) rootView.findViewById(R.id.selectNum_filter_btnfilterNomal_filter);
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_submit);
        btn_cancel = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_cancel);
        btn_back = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_back);
        selectNum_popUp = (RelativeLayout) rootView.findViewById(R.id.selectNum_3d_layout);
        getService(D3LotteryService.class).isSaveNumSuccess(this);//初始化保存接口
    }

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter1, String val, boolean isLongClick) {
        //选择了一个胆码
        if (adapter1 == adapter && list_select_dantuo.size() > 0 && !list_select_dantuo.contains(val)) {
            if (!isLongClick) {//单击
                return true;
            } else {//长按
                TipsToast.showTips("组选最多1个胆码");
                return false;
            }
        }
        return true;
    }

    @Override
    public void onBallSelected(BaseAdapter adapter, String val, boolean selected) {
        ShowTip();
    }

    //下方选号提示
    public void ShowTip() {
        boolean showTips = (list_select_nomal.size() + list_select_dantuo.size()) >= 2;//至少选择两个号码
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (showTips) {
            tip_ll.removeAllViews();
            if (list_select_dantuo.size() > 0) {//胆拖
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.tip_zu3_dantuo, null);
                tip_ll.addView(view);
                TextView tv_dan = (TextView) view.findViewById(R.id.tip_zu3_dan);
                TextView tv_tuo = (TextView) view.findViewById(R.id.tip_zu3_tuo);
                TextView tv_count = (TextView) view.findViewById(R.id.tip_zu3_count);
                tv_dan.setText(String.valueOf(list_select_dantuo.size()));
                tv_tuo.setText(String.valueOf(list_select_nomal.size()));
                tv_count.setText(String.valueOf((list_select_nomal.size())));
            } else {//普通
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.tip_zu3_nomal, null);
                tip_ll.addView(view);
                selectCount = (CombineAlgorithm.combination(list_select_nomal.size(), 2));
                TextView tv_xuan = (TextView) view.findViewById(R.id.tip_zu3_yixuan);
                TextView tv_count = (TextView) view.findViewById(R.id.tip_zu3_count);
                tv_xuan.setText(String.valueOf(list_select_nomal.size()));
                tv_count.setText(String.valueOf(selectCount));
            }
        } else {
            selectCount = 0;
        }
    }

    @Override
    public boolean onLottoBeforeSelected(BaseAdapter adapter, String val) {
        return false;
    }

    //摇一摇
    public void shark() {
        clear();
        List<Integer> list = CombineAlgorithm.sharkRandomNum(2, 10);
        for (int i = 0; i < list.size(); i++) {
            list_select_nomal.add(String.valueOf(list.get(i)));
        }
        adapter.notifyDataSetChanged();
        ShowTip();
    }

    //遗漏
    public void switchIgnore() {
        adapter.setIgnore(ignoreFlag);
        ignoreFlag = !ignoreFlag;
    }

    //编辑展示数据
    public void editData(List<String> list, int key) {
        clear();
        if (key > 3000 && key < 4000) {//组选普通
            list_select_nomal.addAll(list);
        } else {//组选胆拖
            for (int i = 0; i < list.size(); i++) {
                if (Integer.parseInt(list.get(i)) < 50) {
                    list_select_dantuo.add(list.get(i));
                } else {
                    list_select_nomal.add((Integer.parseInt(list.get(i)) - 50) + "");
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    //过滤和确定按钮
    public void submit_filter(int type) {
        if ((list_select_nomal.size() + list_select_dantuo.size()) < 2) {
            TipsToast.showTips("请至少选2个号");
            return;
        }
        if (selectCount > 100000) {
            TipsToast.showTips("选号不可超过10万注");
            return;
        }
        //号码选择10条不保存
        if (SingletonMap3D.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
            ContextHelper.getActivity(SelectCQ2XingNumActivity.class).gotoConditionPage();
        } else {
            ArrayList<String> listfilter_zu3 = new ArrayList<>();
            if (list_select_dantuo.size() == 0) {
                //组选单复式
                listfilter_zu3.addAll(list_select_nomal);
                saveNum(type, listfilter_zu3, onClickFlag_muti);
            } else {
                //组3胆拖
                listfilter_zu3.addAll(list_select_dantuo);//胆
                for (String num : list_select_nomal) {
                    //添加拖
                    listfilter_zu3.add(Integer.parseInt(num) + 50 + "");
                }
                saveNum(type, listfilter_zu3, onClickFlag_dantuo);
            }
        }
    }

    private void saveNum(int type, ArrayList<String> listfilter_zu3, int click) {
        CommonUtil.SortCollection(listfilter_zu3);
        SelectCQ2XingNumActivity act = ContextHelper.getActivity(SelectCQ2XingNumActivity.class);
        if (type == 2) {//编辑模式
            SingletonMap3D.editDelete(act.getKey(), listfilter_zu3);
        } else if (type == 1) {//添加号码
            SingletonMap3D.registerService(SingletonMap3D.sign_sort + "_" + click, listfilter_zu3);
            click++;
            SingletonMap3D.sign_sort++;
        } else if (type == 3) {//改变模式编辑号码
            SingletonMap3D.removeMap(act.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
            SingletonMap3D.registerService(CommonUtil.cutFirstKey(act.getKey()) + "_" + click, listfilter_zu3);
            click++;
        }
        act.gotoConditionPage();
    }

    //编辑模式 按钮变化
    public void setEditMode() {
        isEdit = true;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
    }

    //清除模式 按钮变化
    public void setClearMode() {
        isEdit = false;
        btn_clear.setVisibility(View.VISIBLE);
        btn_save.setVisibility(View.VISIBLE);
        btn_filter.setVisibility(View.VISIBLE);
        btn_cancel.setVisibility(View.GONE);
        btn_submit.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        clearMode();
    }

    //添加模式 按钮变化
    public void setAddMode() {
        isEdit = false;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
        clearMode();
    }

    @Override
    public void Success(boolean success) {
//        if(success){
//            if(list_select_dantuo.size()==0){
//                list_check1.clear();
//                list_check1.addAll(list_select_nomal);
//            }else{
//                list_check2.clear();
//                list_check2.addAll(list_select_dantuo);
//                //检验蓝球特殊标记 加50
//                for (String num: list_select_nomal) {
//                    list_check2.add((Integer.parseInt(num)+50)+"");
//                }
//            }
//        }
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
//                num_ball.setText(String.valueOf(postion));
//                Toolbar toolbar=ContextHelper.getActivity(SelectCQ2XingNumActivity.class).getToolBar();
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
