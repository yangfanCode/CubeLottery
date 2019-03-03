package com.cp2y.cube.fragment.pailie5;


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
import com.cp2y.cube.activity.selectnums.SelectP5NumActivity;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.MyScrollView;
import com.cp2y.cube.custom.SingletonMapP5;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.fragment.FilterBaseFragment;
import com.cp2y.cube.fragment.pailie5.adapter.MyFilterP5ZhiXuanAdapter;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.services.D3LotteryService;
import com.cp2y.cube.services.P5LotteryService;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.cp2y.cube.utils.ViewUtils;
import com.cp2y.cube.widgets.listener.ScrollViewListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class P5ZhiXuanFragment extends FilterBaseFragment implements BallSelectCall, MyInterface.setSelectNumPopUps, MyInterface.isSavaNumSuccess {
    private boolean isEdit = false;
    private boolean ignoreFlag = true;
    private int onClickFlag = 1;
    private Button btn_clear, btn_save, btn_filter, btn_submit, btn_cancel, btn_back;
    private MyGridView gv_wan,gv_qian,gv_bai, gv_shi, gv_ge;
    private LinearLayout tip_ll;
    private int selectCount;
    private MyFilterP5ZhiXuanAdapter adapter_wan,adapter_qian,adapter_bai, adapter_shi, adapter_ge;
    private Set<String> list_select_wan = new HashSet<>(); //万位选中数据集合
    private Set<String> list_select_qian = new HashSet<>(); //千位选中数据集合
    private Set<String> list_select_bai = new HashSet<>(); //百位选中数据集合
    private Set<String> list_select_shi = new HashSet<>(); //十位选中数据集合
    private Set<String> list_select_ge = new HashSet<>(); //个位选中数据集合
    private List<String> list = new ArrayList<>(); //总保存数据集合
    private TextView tv_count;
    private MyScrollView scrollView;
    private RelativeLayout selectNum_popUp;
    private List<String> list_check1 = new ArrayList<String>();//重复保存标记
    private List<String> list_check2 = new ArrayList<String>();//重复保存标记

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_p5_zhi_xuan, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener((v -> {//清空
            clearMode();
        }));
        btn_filter.setOnClickListener((v -> {
            numSet(1, 1);
        }));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
        btn_cancel.setOnClickListener((v -> ContextHelper.getActivity(SelectP5NumActivity.class).gotoConditionPage()));
        btn_back.setOnClickListener((v -> ContextHelper.getActivity(SelectP5NumActivity.class).gotoConditionPage()));
        btn_save.setOnClickListener((v -> {
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                if (list_select_wan.size() > 0 && list_select_qian.size() > 0 &&list_select_bai.size() > 0 && list_select_shi.size() > 0 && list_select_ge.size() > 0) {
                    if (list_select_wan.size() == 1 &&list_select_qian.size() == 1 &&list_select_bai.size() == 1 && list_select_shi.size() == 1 && list_select_ge.size() == 1) {//单式
                        //判断相同号码
                        list_check1.clear();
                        list_check1.addAll(list_select_wan);
                        list_check1.addAll(list_select_qian);
                        list_check1.addAll(list_select_bai);
                        list_check1.addAll(list_select_shi);
                        list_check1.addAll(list_select_ge);
                        if (list.size() > 0) {//保存过
                            if (list_check1.get(0).equals(list.get(0)) && list_check1.get(1).equals(list.get(1)) && list_check1.get(2).equals(list.get(2))
                                    && list_check1.get(3).equals(list.get(3))&& list_check1.get(4).equals(list.get(4))) {//判断重复
                                TipsToast.showTips("已存在号码库");
                                return;
                            }
                        }
                        list.clear();
                        list.addAll(list_select_wan);
                        list.addAll(list_select_qian);
                        list.addAll(list_select_bai);
                        list.addAll(list_select_shi);
                        list.addAll(list_select_ge);
                        getService(P5LotteryService.class).saveLotteryNumber(list,0,4,selectCount);
                    } else {//定位
                        //判断相同号码
                        list_check2.clear();
                        list_check2.addAll(list_select_wan);
                        for (String num : list_select_qian) {//千位加50
                            list_check2.add((Integer.parseInt(num) + 50) + "");
                        }
                        for (String num : list_select_bai) {//百位加100
                            list_check2.add((Integer.parseInt(num) + 100) + "");
                        }
                        for (String num : list_select_shi) {//十位加150
                            list_check2.add((Integer.parseInt(num) + 150) + "");
                        }
                        for (String num : list_select_ge) {//个位加200
                            list_check2.add((Integer.parseInt(num) + 200) + "");
                        }
                        if (CommonUtil.ListCheck(list_check2, list)) {
                            TipsToast.showTips("已存在号码库");
                            return;
                        }
                        list.clear();
                        list.addAll(list_select_wan);
                        for (String num : list_select_qian) {
                            list.add(Integer.parseInt(num) + 50 + "");
                        }
                        for (String num : list_select_bai) {
                            list.add(Integer.parseInt(num) + 100 + "");
                        }
                        for (String num : list_select_shi) {
                            list.add(Integer.parseInt(num) + 150 + "");
                        }
                        for (String num : list_select_ge) {
                            list.add((Integer.parseInt(num) + 200) + "");
                        }
                        CommonUtil.SortCollection(list);//排序
                        getService(P5LotteryService.class).saveLotteryNumber(list, 1,4,selectCount);
                    }
                } else {
                    TipsToast.showTips("请每位至少选1个号");
                }
            } else {
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        }));
        scrollView.setScrollViewListener(new ScrollViewListener() {
            @Override
            public void onScrollChanged(View scrollView, int x, int y, int oldx, int oldy) {
                if (oldy > 5) {
                    setSelectNumPopUp(0, 0, 0, false);
                }
            }
        });
    }

    //清空按钮操作
    private void clearMode() {
        tip_ll.setVisibility(View.GONE);
        tv_count.setText("");
        clear();
        adapterNotify();
    }

    //清空选择号码
    private void clear() {
        list_select_wan.clear();
        list_select_qian.clear();
        list_select_bai.clear();
        list_select_ge.clear();
        list_select_shi.clear();
    }

    //刷新方法
    private void adapterNotify() {
        adapter_wan.notifyDataSetChanged();
        adapter_qian.notifyDataSetChanged();
        adapter_bai.notifyDataSetChanged();
        adapter_ge.notifyDataSetChanged();
        adapter_shi.notifyDataSetChanged();
    }

    private void initData() {
        List<String> list_num = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list_num.add(String.valueOf(i));
        }
        adapter_wan = new MyFilterP5ZhiXuanAdapter(getActivity(), false, list_select_wan, 0);
        gv_wan.setAdapter(adapter_wan);
        adapter_wan.LoadData(list_num);
        adapter_qian = new MyFilterP5ZhiXuanAdapter(getActivity(), false, list_select_qian, 1);
        gv_qian.setAdapter(adapter_qian);
        adapter_qian.LoadData(list_num);
        adapter_bai = new MyFilterP5ZhiXuanAdapter(getActivity(), false, list_select_bai, 2);
        gv_bai.setAdapter(adapter_bai);
        adapter_bai.LoadData(list_num);
        adapter_shi = new MyFilterP5ZhiXuanAdapter(getActivity(), false, list_select_shi, 3);
        gv_shi.setAdapter(adapter_shi);
        adapter_shi.LoadData(list_num);
        adapter_ge = new MyFilterP5ZhiXuanAdapter(getActivity(), false, list_select_ge, 4);
        gv_ge.setAdapter(adapter_ge);
        adapter_ge.LoadData(list_num);
        adapter_wan.setCall(this);
        adapter_qian.setCall(this);
        adapter_bai.setCall(this);
        adapter_shi.setCall(this);
        adapter_ge.setCall(this);
        adapter_wan.setPopUps(this);
        adapter_qian.setPopUps(this);
        adapter_bai.setPopUps(this);
        adapter_shi.setPopUps(this);
        adapter_ge.setPopUps(this);
    }

    private void initView(View rootView) {
        scrollView = (MyScrollView) rootView.findViewById(R.id.ScrollView);
        gv_wan = (MyGridView) rootView.findViewById(R.id.fragment_p5_gvWan);
        gv_qian = (MyGridView) rootView.findViewById(R.id.fragment_p5_gvQian);
        gv_bai = (MyGridView) rootView.findViewById(R.id.fragment_p5_gvBai);
        gv_shi = (MyGridView) rootView.findViewById(R.id.fragment_p5_gvShi);
        gv_ge = (MyGridView) rootView.findViewById(R.id.fragment_p5_gvGe);
        gv_bai.setSelector(android.R.color.transparent);
        gv_shi.setSelector(android.R.color.transparent);
        gv_ge.setSelector(android.R.color.transparent);//设置无点击效果
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_p5_tip_ll);
        btn_clear = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_clear);
        btn_save = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_save);
        btn_filter = (Button) rootView.findViewById(R.id.selectNum_filter_btnfilterNomal_filter);
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_submit);
        btn_cancel = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_cancel);
        btn_back = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_back);
        selectNum_popUp = (RelativeLayout) rootView.findViewById(R.id.selectNum_p5_layout);
        tv_count = (TextView) rootView.findViewById(R.id.selectNum_p5_tip_count);
        getService(D3LotteryService.class).isSaveNumSuccess(this);//初始化保存接口
    }

    //摇一摇
    public void shark() {
        clear();
        int wan = (int) (Math.random() * 10);
        int qian = (int) (Math.random() * 10);
        int bai = (int) (Math.random() * 10);
        int shi = (int) (Math.random() * 10);
        int ge = (int) (Math.random() * 10);
        list_select_wan.add(String.valueOf(wan));
        list_select_qian.add(String.valueOf(qian));
        list_select_bai.add(String.valueOf(bai));
        list_select_shi.add(String.valueOf(shi));
        list_select_ge.add(String.valueOf(ge));
        adapterNotify();
        ShowTip();
    }

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter, String val,boolean isLongClick) {
        return true;
    }

    @Override
    public void onBallSelected(BaseAdapter adapter, String val, boolean selected) {
        ShowTip();
    }

    @Override
    public boolean onLottoBeforeSelected(BaseAdapter adapter, String val) {
        return false;
    }

    //下方选号提示
    public void ShowTip() {
        boolean showTips = list_select_wan.size() > 0 &&list_select_qian.size() > 0 &&list_select_bai.size() > 0 && list_select_shi.size() > 0 && list_select_ge.size() > 0;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (!showTips) {
            selectCount = 0;
        } else {
            selectCount = (list_select_wan.size()*list_select_qian.size()*list_select_bai.size() * list_select_shi.size() * list_select_ge.size());
            tv_count.setText(String.valueOf(selectCount));
        }
    }

    //遗漏
    public void switchIgnore() {
        adapter_wan.setIgnore(ignoreFlag);
        adapter_qian.setIgnore(ignoreFlag);
        adapter_bai.setIgnore(ignoreFlag);
        adapter_shi.setIgnore(ignoreFlag);
        adapter_ge.setIgnore(ignoreFlag);
        ignoreFlag = !ignoreFlag;
    }

    //编辑展示数据
    public void editData(List<String> list) {
        clear();//清除数据
        if (list.size() == 5) {//单式
            list_select_wan.add(list.get(0));
            list_select_qian.add(list.get(1));
            list_select_bai.add(list.get(2));
            list_select_shi.add(list.get(3));
            list_select_ge.add(list.get(4));
        } else {//复式
            for (int i = 0; i < list.size(); i++) {
                if (Integer.parseInt(list.get(i)) < 50) {
                    list_select_wan.add(list.get(i));
                } else if(Integer.parseInt(list.get(i)) >= 50 && Integer.parseInt(list.get(i)) < 100){
                    list_select_qian.add((Integer.parseInt(list.get(i)) - 50) + "");
                } else if(Integer.parseInt(list.get(i)) >= 100 && Integer.parseInt(list.get(i)) < 150){
                    list_select_bai.add((Integer.parseInt(list.get(i)) - 100) + "");
                }else if (Integer.parseInt(list.get(i)) >= 150 && Integer.parseInt(list.get(i)) < 200) {
                    list_select_shi.add((Integer.parseInt(list.get(i)) - 150) + "");
                } else {
                    list_select_ge.add((Integer.parseInt(list.get(i)) - 200) + "");
                }
            }
        }
        adapterNotify();//刷新
    }

    //号码设置
    private void numSet(int pos1, int pos2) {
        SelectP5NumActivity act = ContextHelper.getActivity(SelectP5NumActivity.class);
        if ("".equals(act.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            submit_filter(pos2);// 编辑号码

        }
    }

    //过滤和确定按钮
    public void submit_filter(int type) {
        if (list_select_wan.size() == 0||list_select_qian.size() == 0||list_select_bai.size() == 0 || list_select_shi.size() == 0 || list_select_ge.size() == 0) {
            TipsToast.showTips("请每位至少选1个号");
            return;
        }
        if (selectCount > 100000) {
            TipsToast.showTips("选号不可超过10万注");
            return;
        }
        //号码选择10条不保存
        if (SingletonMapP5.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
            ContextHelper.getActivity(SelectP5NumActivity.class).gotoConditionPage();
        } else {
            ArrayList<String> listfilter_zhixuan = new ArrayList<>();
            if (list_select_wan.size() == 1 && list_select_qian.size() == 1 && list_select_bai.size() == 1 && list_select_shi.size() == 1 && list_select_ge.size() == 1) {
                //直选单式 不加标记
                listfilter_zhixuan.addAll(list_select_wan);//添加万位
                listfilter_zhixuan.addAll(list_select_qian);//添加千位
                listfilter_zhixuan.addAll(list_select_bai);//添加百位
                listfilter_zhixuan.addAll(list_select_shi);//添加十位
                listfilter_zhixuan.addAll(list_select_ge);//添加个位
            } else {
                //直选复式
                listfilter_zhixuan.addAll(list_select_wan);//添加万位
                //千位特殊标记 加50
                for (String num : list_select_qian) {
                    //添加十位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 50 + "");
                }
                //百位特殊标记 加100
                for (String num : list_select_bai) {
                    //添加个位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 100 + "");
                }
                //十位特殊标记 加150
                for (String num : list_select_shi) {
                    //添加个位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 150 + "");
                }
                //个位特殊标记 加200
                for (String num : list_select_ge) {
                    //添加个位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 200 + "");
                }
                CommonUtil.SortCollection(listfilter_zhixuan);
            }
            SelectP5NumActivity act = ContextHelper.getActivity(SelectP5NumActivity.class);
            if (type == 2) {//编辑模式
                SingletonMapP5.editDelete(act.getKey(), listfilter_zhixuan);
            } else if (type == 1) {//添加号码
                SingletonMapP5.registerService(SingletonMapP5.sign_sort + "_" + onClickFlag, listfilter_zhixuan);
                onClickFlag++;
                SingletonMapP5.sign_sort++;
            }
            act.gotoConditionPage();
        }
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
    public void setSelectNumPopUp(int x, int y, int position, boolean isShow) {
        if(isShow){
            if(selectNum_popUp.getChildCount()==0){
                View popUp=LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_redlayout,selectNum_popUp,false);
                TextView num_ball= (TextView) popUp.findViewById(R.id.selectNum_pop_ball);
                num_ball.setText(String.valueOf(position));
                Toolbar toolbar=ContextHelper.getActivity(SelectP5NumActivity.class).getToolBar();
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

    @Override
    public void Success(boolean success) {
//        if(success){
//            if(list_select_bai.size()==1&&list_select_shi.size()==1&&list_select_ge.size()==1){
//                list_check1.clear();
//                list_check1.addAll(list_select_bai);
//                list_check1.addAll(list_select_shi);
//                list_check1.addAll(list_select_ge);
//            }else{
//                list_check2.clear();
//                list_check2.addAll(list_select_bai);
//                //检验蓝球特殊标记 加50
//                for (String num: list_select_shi) {
//                    list_check2.add((Integer.parseInt(num)+50)+"");
//                }
//                for (String num: list_select_ge) {
//                    list_check2.add((Integer.parseInt(num)+100)+"");
//                }
//            }
//        }
    }
}
