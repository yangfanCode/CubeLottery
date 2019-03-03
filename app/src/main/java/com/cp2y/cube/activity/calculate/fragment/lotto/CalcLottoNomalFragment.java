package com.cp2y.cube.activity.calculate.fragment.lotto;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.fragment.lotto.adapter.CalcLottoNomalBlueBallAdapter;
import com.cp2y.cube.activity.calculate.fragment.lotto.adapter.CalcLottoNomalRedBallAdapter;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapLotto;
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
public class CalcLottoNomalFragment extends Fragment implements BallSelectCall,MyInterface.setSelectNumD3PopUps{
    private View rootView;
    private int onClickFlag = 1;
    private int editPosition = -1;
    private boolean ignoreFlag = true;
    private MyGridView gv_red, gv_blue;
    private static Set<String> list_red_select = new HashSet<>();
    private static Set<String> list_blue_select = new HashSet<>();
    private Button btn_clear, btn_save, btn_filter, btn_submit, btn_cancel, btn_back;
    private TextView tv_redNum, tv_BlueNum;
    private LinearLayout tip_ll;
    private static CalcLottoNomalRedBallAdapter adapter_red;
    private static CalcLottoNomalBlueBallAdapter adapter_blue;
    private boolean ignore = false;
    private int selectCount;
    private List<String> list = new ArrayList<>();
    private List<String> list_check1 = new ArrayList<String>();//重复保存标记
    private List<String> list_check2 = new ArrayList<String>();//重复保存标记
    private boolean isEdit = false;
    private RelativeLayout selectNum_popUp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView= inflater.inflate(R.layout.fragment_calc_lotto_nomal, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener((v1 -> clear()));
      
        btn_submit.setOnClickListener(v -> numSet(1, 2));

    }
    //号码设置
    private void numSet(int pos1, int pos2) {
        LottoCalcFragment fragment= (LottoCalcFragment) CalcLottoNomalFragment.this.getParentFragment();
        if ("".equals(fragment.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            if (CommonUtil.parseInt(CommonUtil.cutKey(fragment.getKey())) > 1000) {
                submit_filter(3);//改变模式编辑号码 或者编辑号码
            } else {
                submit_filter(pos2);
            }
        }
    }

    //编辑添加号码确定按钮
    public void submit_filter(int type) {
        if (list_red_select.size() < 5 || list_blue_select.size() < 2) {
            TipsToast.showTips("请至少选择5个前区+2个后区");
            return;
        }
//        if (selectCount > 100000) {
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }
        LottoCalcFragment fragment= (LottoCalcFragment) CalcLottoNomalFragment.this.getParentFragment();
        //号码选择10条不保存
        if (SingletonMapLotto.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
        } else {
            ArrayList<String> listfilter_nomal = new ArrayList<>();
            listfilter_nomal.addAll(list_red_select);
            //蓝球特殊标记 加50
            for (String num : list_blue_select) {
                listfilter_nomal.add(Integer.parseInt(num) + 50 + "");
            }
            CommonUtil.SortCollection(listfilter_nomal);
            if (type == 2) {//编辑号码
                SingletonMapLotto.editDelete(fragment.getKey(), listfilter_nomal);
            } else if (type == 1) {//添加号码
                Log.e("yangfan", "submit_filter: " + SingletonMapLotto.sign_sort + "_" + onClickFlag);
                SingletonMapLotto.registerService(SingletonMapLotto.sign_sort + "_" + onClickFlag, listfilter_nomal);
                onClickFlag++;
                SingletonMapLotto.sign_sort++;
            } else if (type == 3) {//改变模式编辑
                SingletonMapLotto.removeMap(fragment.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
                SingletonMapLotto.registerService(CommonUtil.cutFirstKey(fragment.getKey()) + "_" + onClickFlag, listfilter_nomal);
                onClickFlag++;
            }
            fragment.gotoConditionPage();

        }
    }
    
    private void initView() {
        gv_red = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_Nomalred_gv);
        gv_blue = (MyGridView) rootView.findViewById(R.id.selectNum_filter_layout_Nomalblue_gv);
        gv_red.setSelector(android.R.color.transparent);
        gv_blue.setSelector(android.R.color.transparent);
        btn_clear = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_clear);
        btn_save = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_save);
        btn_filter = (Button) rootView.findViewById(R.id.selectNum_filter_btnfilterNomal_filter);
        btn_submit = (Button) rootView.findViewById(R.id.selectNum_filterfilterNomal_btn_submit);
        btn_cancel = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_cancel);
        btn_back = (Button) rootView.findViewById(R.id.selectNum_filterNomal_btn_back);
        selectNum_popUp = (RelativeLayout) rootView.findViewById(R.id.selectNum_3d_layout);
        tv_redNum = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_RedNum);
        tv_BlueNum = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_BlueNum);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_filterNomal_ll);
        List<String> list_red = new ArrayList<>(), list_blue = new ArrayList<>();
        for (int i = 1; i < 36; i++) {
            list_red.add(i + "");
        }
        adapter_red = new CalcLottoNomalRedBallAdapter(getActivity(), R.layout.item_selectfilter_nomalred, ignore, list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_red.initData(list_red);
        for (int i = 1; i < 13; i++) {
            list_blue.add(i + "");
        }
        adapter_blue = new CalcLottoNomalBlueBallAdapter(getActivity(), R.layout.item_selectfilter_nomalblue, ignore, list_blue_select);
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

    public void clear() {
        list_red_select.clear();
        list_blue_select.clear();
        adapter_red.notifyDataSetChanged();
        adapter_blue.notifyDataSetChanged();
        tv_BlueNum.setText("");
        tv_redNum.setText("");
        tip_ll.setVisibility(View.GONE);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        list_red_select.clear();
        list_blue_select.clear();
    }

    //编辑展示数据
    public void editData(List<String> list) {
        List<String> list_red_edit = new ArrayList<>();
        List<String> list_blue_edit = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (Integer.parseInt(list.get(i)) < 50) {
                list_red_edit.add(list.get(i));
            } else {
                list_blue_edit.add("" + (Integer.parseInt(list.get(i)) - 50));
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

    @Override
    public void setSelectNumD3PopUp(int x, int y, int postion, boolean isShow, boolean isRed) {

    }

    public void ShowTip() {
        boolean showTips = list_blue_select.size() > 1 && list_red_select.size() > 4;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (!showTips) {
            selectCount = 0;
        } else {
            selectCount = CombineAlgorithm.combination(list_red_select.size(), 5) * CombineAlgorithm.combination(list_blue_select.size(), 2);
            TextView tv_red = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_RedNum);
            TextView tv_blue = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_BlueNum);
            TextView tv_count = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_count);
            tv_count.setText(String.valueOf(selectCount));
            tv_red.setText(String.valueOf(list_red_select.size()));
            tv_blue.setText(String.valueOf(list_blue_select.size()));
        }
    }

}
