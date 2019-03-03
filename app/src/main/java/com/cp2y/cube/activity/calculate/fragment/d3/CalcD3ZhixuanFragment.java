package com.cp2y.cube.activity.calculate.fragment.d3;


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
import com.cp2y.cube.activity.calculate.fragment.d3.adapter.Calc3DZhiXuanAdapter;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMap3D;
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
public class CalcD3ZhixuanFragment extends Fragment  implements BallSelectCall{
    private int onClickFlag = 1;
    private Button btn_clear, btn_submit;
    private MyGridView gv_bai, gv_shi, gv_ge;
    private LinearLayout tip_ll;
    private int selectCount;
    private Calc3DZhiXuanAdapter adapter_bai, adapter_shi, adapter_ge;
    private Set<String> list_select_bai = new HashSet<>(); //百位选中数据集合
    private Set<String> list_select_shi = new HashSet<>(); //十位选中数据集合
    private Set<String> list_select_ge = new HashSet<>(); //个位选中数据集合
    private List<String> list = new ArrayList<>(); //总保存数据集合
    private TextView tv_bai, tv_shi, tv_ge, tv_count;
    private RelativeLayout selectNum_popUp;
    private List<String> list_check1 = new ArrayList<String>();//重复保存标记
    private List<String> list_check2 = new ArrayList<String>();//重复保存标记
    private boolean isEdit = false;//是否编辑模式
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calc_d3_zhixuan, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }
    private void initView(View rootView) {
        gv_bai = (MyGridView) rootView.findViewById(R.id.fragment_3d_gvBai);
        gv_shi = (MyGridView) rootView.findViewById(R.id.fragment_3d_gvShi);
        gv_ge = (MyGridView) rootView.findViewById(R.id.fragment_3d_gvGe);
        gv_bai.setSelector(android.R.color.transparent);
        gv_shi.setSelector(android.R.color.transparent);
        gv_ge.setSelector(android.R.color.transparent);//设置无点击效果
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_3d_tip_ll);
        btn_clear = (Button) rootView.findViewById(R.id.calc_zhixuan_btnClear);
        btn_submit = (Button) rootView.findViewById(R.id.calc_zhixuan_btnSubmit);
        tv_bai = (TextView) rootView.findViewById(R.id.selectNum_3d_tip_bai);
        tv_shi = (TextView) rootView.findViewById(R.id.selectNum_3d_tip_shi);
        tv_ge = (TextView) rootView.findViewById(R.id.selectNum_3d_tip_ge);
        selectNum_popUp = (RelativeLayout) rootView.findViewById(R.id.selectNum_3d_layout);
        tv_count = (TextView) rootView.findViewById(R.id.selectNum_3d_tip_count);
    }

    private void initData() {
        List<String> list_num = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list_num.add(String.valueOf(i));
        }
        adapter_bai = new Calc3DZhiXuanAdapter(getActivity(), false, list_select_bai, 0);
        gv_bai.setAdapter(adapter_bai);
        adapter_bai.LoadData(list_num);
        adapter_shi = new Calc3DZhiXuanAdapter(getActivity(), false, list_select_shi, 1);
        gv_shi.setAdapter(adapter_shi);
        adapter_shi.LoadData(list_num);
        adapter_ge = new Calc3DZhiXuanAdapter(getActivity(), false, list_select_ge, 2);
        gv_ge.setAdapter(adapter_ge);
        adapter_ge.LoadData(list_num);
        adapter_bai.setCall(this);
        adapter_shi.setCall(this);
        adapter_ge.setCall(this);
    }
    private void initListener() {
        btn_clear.setOnClickListener((v -> {//清空
            clearMode();
        }));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
    }
    //清空按钮操作
    private void clearMode() {
        tip_ll.setVisibility(View.GONE);
        tv_count.setText("");
        tv_bai.setText("");//百位提示框
        tv_shi.setText("");//十位提示框
        tv_ge.setText("");//个位提示框
        clear();
        adapterNotify();
    }

    //清空选择号码
    private void clear() {
        list_select_bai.clear();
        list_select_ge.clear();
        list_select_shi.clear();
    }

    //刷新方法
    private void adapterNotify() {
        adapter_bai.notifyDataSetChanged();
        adapter_ge.notifyDataSetChanged();
        adapter_shi.notifyDataSetChanged();
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
        boolean showTips = list_select_bai.size() > 0 && list_select_shi.size() > 0 && list_select_ge.size() > 0;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (!showTips) {
            selectCount = 0;
        } else {
            selectCount = (list_select_bai.size() * list_select_shi.size() * list_select_ge.size());
            tv_count.setText(String.valueOf(selectCount));
            tv_bai.setText(String.valueOf(list_select_bai.size()));//百位提示框
            tv_shi.setText(String.valueOf(list_select_shi.size()));//十位提示框
            tv_ge.setText(String.valueOf(list_select_ge.size()));//个位提示框
        }
    }

    //编辑展示数据
    public void editData(List<String> list) {
        clear();//清除数据
        if (list.size() == 3) {//直选单式
            list_select_bai.add(list.get(0));
            list_select_shi.add(list.get(1));
            list_select_ge.add(list.get(2));
        } else {//直选定位
            for (int i = 0; i < list.size(); i++) {
                if (Integer.parseInt(list.get(i)) < 50) {
                    list_select_bai.add(list.get(i));
                } else if (Integer.parseInt(list.get(i)) >= 50 && Integer.parseInt(list.get(i)) < 100) {
                    list_select_shi.add((Integer.parseInt(list.get(i)) - 50) + "");
                } else {
                    list_select_ge.add((Integer.parseInt(list.get(i)) - 100) + "");
                }
            }
        }
        adapterNotify();//刷新
    }
    //号码设置
    private void numSet(int pos1, int pos2) {
        D3CalcFragment fragment= (D3CalcFragment) CalcD3ZhixuanFragment.this.getParentFragment();
        if ("".equals(fragment.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            submit_filter(pos2);// 编辑号码

        }
    }

    //过滤和确定按钮
    public void submit_filter(int type) {
        if (list_select_bai.size() == 0 || list_select_shi.size() == 0 || list_select_ge.size() == 0) {
            TipsToast.showTips("请每位至少选1个号");
            return;
        }
//        if (selectCount > 100000) {
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        D3CalcFragment fragment= (D3CalcFragment) CalcD3ZhixuanFragment.this.getParentFragment();
        //号码选择10条不保存
        if (SingletonMap3D.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
        } else {
            ArrayList<String> listfilter_zhixuan = new ArrayList<>();
            if (list_select_bai.size() == 1 && list_select_shi.size() == 1 && list_select_ge.size() == 1) {
                //直选单式 不加标记
                listfilter_zhixuan.addAll(list_select_bai);//添加百位
                listfilter_zhixuan.addAll(list_select_shi);//添加十位
                listfilter_zhixuan.addAll(list_select_ge);//添加个位
            } else {
                //直选定位
                listfilter_zhixuan.addAll(list_select_bai);//添加百位
                //十位特殊标记 加50
                for (String num : list_select_shi) {
                    //添加十位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 50 + "");
                }
                //个位特殊标记 加100
                for (String num : list_select_ge) {
                    //添加个位
                    listfilter_zhixuan.add(Integer.parseInt(num) + 100 + "");
                }
                CommonUtil.SortCollection(listfilter_zhixuan);
            }
            if (type == 2) {//编辑模式
                SingletonMap3D.editDelete(fragment.getKey(), listfilter_zhixuan);
            } else if (type == 1) {//添加号码
                SingletonMap3D.registerService(SingletonMap3D.sign_sort + "_" + onClickFlag, listfilter_zhixuan);
                onClickFlag++;
                SingletonMap3D.sign_sort++;
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
