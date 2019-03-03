package com.cp2y.cube.activity.calculate.fragment.p3;


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
import com.cp2y.cube.activity.calculate.fragment.d3.adapter.Calc3DZuXuanAdapter;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapP3;
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
public class CalcP3Zu6Fragment extends Fragment implements BallSelectCall {
    private int onClickFlag = 6001;//单复式
    private int onClickFlag_dantuo = 7001;//胆拖
    private boolean ignoreFlag = true;
    private Button btn_clear,btn_submit;
    private Calc3DZuXuanAdapter adapter;
    private MyGridView gv_zu3;
    private int selectCount;
    private Set<String> list_select_nomal = new HashSet<>(); //普通或拖码选中数据集合
    private Set<String> list_select_dantuo = new HashSet<>(); //胆选中数据集合
    private List<String> list = new ArrayList<>(); //总保存数据集合
    private LinearLayout tip_ll;
    private boolean isEdit = false;//是否编辑模式
    private List<String> list_check1 = new ArrayList<String>();//重复保存标记
    private List<String> list_check2 = new ArrayList<String>();//重复保存标记
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calc_d3_zu6, container, false);
        initView(rootView);//初始化
        initData();//数据
        initListener();//监听
        return rootView;
    }
    private void initListener() {
        btn_clear.setOnClickListener((v -> {
            clearMode();
        }));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
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

    private void initData() {
        List<String> list_num = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list_num.add(String.valueOf(i));
        }
        adapter = new Calc3DZuXuanAdapter(getActivity(), false, list_select_nomal, list_select_dantuo);
        gv_zu3.setAdapter(adapter);
        adapter.LoadData(list_num);
        adapter.setCall(this);
    }

    private void initView(View rootView) {
        gv_zu3 = (MyGridView) rootView.findViewById(R.id.fragment_3d_gvZu6);
        gv_zu3.setSelector(android.R.color.transparent);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_3d_zu6_tip_ll);
        btn_clear = (Button) rootView.findViewById(R.id.calc_zu6_btnClear);
        btn_submit = (Button) rootView.findViewById(R.id.calc_zu6_btnSubmit);
    }

    @Override
    public boolean onBeforeSelected(BaseAdapter adapter1, String val, boolean isLongClick) {
        //选择了一个胆码
        if (adapter1 == adapter && list_select_dantuo.size() > 1 && !list_select_dantuo.contains(val)) {
            if (!isLongClick) {//单击
                return true;
            } else {//长按
                TipsToast.showTips("组六最多2个胆码");
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
        boolean showTips = (list_select_nomal.size() + list_select_dantuo.size()) >= 3;//至少选择两个号码
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (showTips) {
            tip_ll.removeAllViews();
            if (list_select_dantuo.size() > 0) {//胆拖
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.tip_zu3_dantuo, null);
                tip_ll.addView(view);
                selectCount = CombineAlgorithm.combination(list_select_nomal.size(), (3 - list_select_dantuo.size()));
                TextView tv_dan = (TextView) view.findViewById(R.id.tip_zu3_dan);
                TextView tv_tuo = (TextView) view.findViewById(R.id.tip_zu3_tuo);
                TextView tv_count = (TextView) view.findViewById(R.id.tip_zu3_count);
                tv_dan.setText(String.valueOf(list_select_dantuo.size()));
                tv_tuo.setText(String.valueOf(list_select_nomal.size()));
                tv_count.setText(String.valueOf(selectCount));
            } else {//普通
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.tip_zu3_nomal, null);
                tip_ll.addView(view);
                selectCount = (CombineAlgorithm.combination(list_select_nomal.size(), 3));
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
    
    //编辑展示数据
    public void editData(List<String> list, int key) {
        clear();
        if (key > 6000 && key < 7000) {//组6普通
            list_select_nomal.addAll(list);
        } else {//组6胆拖
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

    //号码设置
    private void numSet(int pos1, int pos2) {
       P3CalcFragment fragment= (P3CalcFragment) CalcP3Zu6Fragment.this.getParentFragment();
        if ("".equals(fragment.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            // if(CommonUtil.parseInt(CommonUtil.cutKey(act.getKey()))>3000&&CommonUtil.parseInt(CommonUtil.cutKey(act.getKey()))<6000){
            submit_filter(3);//改变模式编辑号码
            //}else{
            //    submit_filter(pos2);// 不变模式编辑号码
            // }
        }
    }

    //过滤和确定按钮
    public void submit_filter(int type) {
        if (list_select_nomal.size() + list_select_dantuo.size() < 3) {
            TipsToast.showTips("请至少选3个号");
            return;
        }
//        if (selectCount > 100000) {
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        //号码选择10条不保存
        if (SingletonMapP3.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
            P3CalcFragment fragment= (P3CalcFragment) CalcP3Zu6Fragment.this.getParentFragment();
            fragment.gotoConditionPage();
        } else {
            ArrayList<String> listfilter_zu6 = new ArrayList<>();
            if (list_select_dantuo.size() == 0) {
                //组6单复式
                listfilter_zu6.addAll(list_select_nomal);
                saveNum(type, listfilter_zu6, onClickFlag);
            } else {
                //组3胆拖
                listfilter_zu6.addAll(list_select_dantuo);//胆
                for (String num : list_select_nomal) {
                    //添加拖
                    listfilter_zu6.add(Integer.parseInt(num) + 50 + "");
                }
                saveNum(type, listfilter_zu6, onClickFlag_dantuo);
            }

        }
    }

    private void saveNum(int type, ArrayList<String> listfilter_zu6, int click) {
        CommonUtil.SortCollection(listfilter_zu6);
        P3CalcFragment fragment= (P3CalcFragment) CalcP3Zu6Fragment.this.getParentFragment();
        if (type == 2) {//编辑模式
            SingletonMapP3.editDelete(fragment.getKey(), listfilter_zu6);
        } else if (type == 1) {//添加号码
            SingletonMapP3.registerService(SingletonMapP3.sign_sort + "_" + click, listfilter_zu6);
            click++;
            SingletonMapP3.sign_sort++;
        } else if (type == 3) {//改变模式编辑号码
            SingletonMapP3.removeMap(fragment.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
            SingletonMapP3.registerService(CommonUtil.cutFirstKey(fragment.getKey()) + "_" + click, listfilter_zu6);
            click++;
        }
        fragment.gotoConditionPage();
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
