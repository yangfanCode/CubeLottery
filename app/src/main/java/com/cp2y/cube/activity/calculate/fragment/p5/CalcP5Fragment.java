package com.cp2y.cube.activity.calculate.fragment.p5;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.fragment.p5.adapter.CalcP5ZhiXuanAdapter;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapP5;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalcP5Fragment extends Fragment implements BallSelectCall {
    private boolean isEdit = false;
    private boolean ignoreFlag = true;
    private int onClickFlag = 1;
    private Button btn_clear, btn_submit;
    private MyGridView gv_wan,gv_qian,gv_bai, gv_shi, gv_ge;
    private LinearLayout tip_ll;
    private int selectCount;
    private CalcP5ZhiXuanAdapter adapter_wan,adapter_qian,adapter_bai, adapter_shi, adapter_ge;
    private Set<String> list_select_wan = new HashSet<>(); //万位选中数据集合
    private Set<String> list_select_qian = new HashSet<>(); //千位选中数据集合
    private Set<String> list_select_bai = new HashSet<>(); //百位选中数据集合
    private Set<String> list_select_shi = new HashSet<>(); //十位选中数据集合
    private Set<String> list_select_ge = new HashSet<>(); //个位选中数据集合
    private List<String> list = new ArrayList<>(); //总保存数据集合
    private TextView tv_count;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calc_p5, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }
    private void initListener() {
        btn_clear.setOnClickListener((v -> clearMode()));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
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
        adapter_wan = new CalcP5ZhiXuanAdapter(getActivity(), false, list_select_wan, 0);
        gv_wan.setAdapter(adapter_wan);
        adapter_wan.LoadData(list_num);
        adapter_qian = new CalcP5ZhiXuanAdapter(getActivity(), false, list_select_qian, 1);
        gv_qian.setAdapter(adapter_qian);
        adapter_qian.LoadData(list_num);
        adapter_bai = new CalcP5ZhiXuanAdapter(getActivity(), false, list_select_bai, 2);
        gv_bai.setAdapter(adapter_bai);
        adapter_bai.LoadData(list_num);
        adapter_shi = new CalcP5ZhiXuanAdapter(getActivity(), false, list_select_shi, 3);
        gv_shi.setAdapter(adapter_shi);
        adapter_shi.LoadData(list_num);
        adapter_ge = new CalcP5ZhiXuanAdapter(getActivity(), false, list_select_ge, 4);
        gv_ge.setAdapter(adapter_ge);
        adapter_ge.LoadData(list_num);
        adapter_wan.setCall(this);
        adapter_qian.setCall(this);
        adapter_bai.setCall(this);
        adapter_shi.setCall(this);
        adapter_ge.setCall(this);
    }

    private void initView(View rootView) {
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
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_submit);
        tv_count = (TextView) rootView.findViewById(R.id.selectNum_p5_tip_count);
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
    public boolean onBeforeSelected(BaseAdapter adapter, String val, boolean isLongClick) {
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
        P5CalcFragment fragment= (P5CalcFragment) CalcP5Fragment.this.getParentFragment();
        if ("".equals(fragment.getKey())) {
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
//        if (selectCount > 100000) {
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        P5CalcFragment fragment= (P5CalcFragment) CalcP5Fragment.this.getParentFragment();
        //号码选择10条不保存
        if (SingletonMapP5.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
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
            if (type == 2) {//编辑模式
                SingletonMapP5.editDelete(fragment.getKey(), listfilter_zhixuan);
            } else if (type == 1) {//添加号码
                SingletonMapP5.registerService(SingletonMapP5.sign_sort + "_" + onClickFlag, listfilter_zhixuan);
                onClickFlag++;
                SingletonMapP5.sign_sort++;
            }
            fragment.gotoConditionPage();
        }
    }

    //编辑模式 按钮变化
    public void setEditMode() {
        isEdit = true;
    }

    //清除模式 按钮变化
    public void setClearMode() {
        isEdit = false;
        clearMode();
    }

    //添加模式 按钮变化
    public void setAddMode() {
        isEdit = false;
        clearMode();
    }
}
