package com.cp2y.cube.fragment.lotto;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
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
import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.adapter.MyFilterLottoNomaiBlueBallAdapter;
import com.cp2y.cube.adapter.MyFilterLottoNomaiRedBallAdapter;
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
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class LottoNomalFragment extends FilterBaseFragment implements BallSelectCall, MyInterface.setSelectNumD3PopUps, MyInterface.isSavaNumSuccess {
    private static final int REQUEST_CODE = 100;
    private View rootView;
    private int onClickFlag = 1;
    private int editPosition = -1;
    private boolean ignoreFlag = true;
    private MyGridView gv_red, gv_blue;
    private static Set<String> list_red_select = new HashSet<>();
    private static Set<String> list_blue_select = new HashSet<>();
    private Button btn_clear, btn_save, btn_filter, btn_submit, btn_cancel, btn_back;
    private TextView tv_redNum, tv_BlueNum, tv_redNum_text, tv_redNum_sign, tv_blueNum_text, tv_blueNum_sign;
    private LinearLayout tip_ll;
    private static MyFilterLottoNomaiRedBallAdapter adapter_red;
    private static MyFilterLottoNomaiBlueBallAdapter adapter_blue;
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
        rootView = inflater.inflate(R.layout.fragment_nomal, container, false);
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
        tv_redNum_text = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_tv1);
        tv_redNum_sign = (TextView) rootView.findViewById(R.id.nomal_red_textSign);
        tv_blueNum_text = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_tv2);
        tv_blueNum_sign = (TextView) rootView.findViewById(R.id.nomal_blue_textSign);
        tv_BlueNum = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_BlueNum);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_filterNomal_ll);
        tv_redNum_text.setText("前区");
        tv_blueNum_text.setText("后区");
        tv_redNum_sign.setText("至少选择5个");
        tv_blueNum_sign.setText("至少选择2个");
        getService(LotteryService.class).isSaveNumSuccess(this);//初始化保存接口
        List<String> list_red = new ArrayList<>(), list_blue = new ArrayList<>();
        for (int i = 1; i < 36; i++) {
            list_red.add(i + "");
        }
        adapter_red = new MyFilterLottoNomaiRedBallAdapter(getActivity(), R.layout.item_selectfilter_nomalred, ignore, list_red_select);
        gv_red.setAdapter(adapter_red);
        adapter_red.initData(list_red);
        for (int i = 1; i < 13; i++) {
            list_blue.add(i + "");
        }
        adapter_blue = new MyFilterLottoNomaiBlueBallAdapter(getActivity(), R.layout.item_selectfilter_nomalblue, ignore, list_blue_select);
        gv_blue.setAdapter(adapter_blue);
        adapter_blue.initData(list_blue);
        adapter_red.setCall(this);
        adapter_blue.setCall(this);
        adapter_red.setPopUps(this);
        adapter_blue.setPopUps(this);
        btn_clear.setOnClickListener((v1 -> clear()));
        btn_save.setOnClickListener(view1 -> {
            boolean loginState = LoginSPUtils.isLogin();
            if (loginState) {
                if (list_red_select.size() >= 5 && list_blue_select.size() > 1) {
                    if (list_red_select.size() + list_blue_select.size() == 7) {
                        //判断相同号码
                        list_check1.clear();
                        list_check1.addAll(list_red_select);
                        list_check1.addAll(list_blue_select);
                        if (CommonUtil.ListCheck(list_check1, list)) {
                            TipsToast.showTips("已存在号码库");
                            return;
                        }
                        list.clear();
                        //排序红球 蓝球
                        List<String> list_sort = new ArrayList<String>();
                        List<String> list_sort_blue = new ArrayList<String>();
                        list_sort.addAll(list_red_select);
                        list_sort_blue.addAll(list_blue_select);
                        CommonUtil.SortCollection(list_sort);
                        CommonUtil.SortCollection(list_sort_blue);
                        list.addAll(list_sort);
                        list.addAll(list_sort_blue);
                        getService(LotteryService.class).saveLotteryNumber(list, 0, 1,selectCount);
                    } else if (list_red_select.size() + list_blue_select.size() > 7) {
                        list_check2.clear();
                        //判断相同号码
                        list_check2.addAll(list_red_select);
                        //检验蓝球特殊标记 加50
                        for (String num : list_blue_select) {
                            list_check2.add(Integer.parseInt(num) + 50 + "");
                        }
                        if (CommonUtil.ListCheck(list_check2, list)) {
                            TipsToast.showTips("已存在号码库");
                            return;
                        }
                        list.clear();
                        list.addAll(list_red_select);
                        //蓝球特殊标记 加50
                        for (String num : list_blue_select) {
                            list.add(Integer.parseInt(num) + 50 + "");
                        }
                        CommonUtil.SortCollection(list);
                        getService(LotteryService.class).saveLotteryNumber(list, 1, 1,selectCount);
                    }
                } else {
                    TipsToast.showTips("请至少选择5个前区+2个后区");
                }
            } else {
                ((BaseActivity)getActivity()).intentLogin();//登录页面
            }
        });
        //过滤
        btn_filter.setOnClickListener(v -> numSet(1, 1));
        //编辑提交
        btn_submit.setOnClickListener(v -> numSet(1, 2));
        btn_cancel.setOnClickListener(view -> ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage());
        btn_back.setOnClickListener(view -> ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage());

        //编辑数据
        return rootView;
    }

    //号码设置
    private void numSet(int pos1, int pos2) {
        LottoSelectNumActivity act = ContextHelper.getActivity(LottoSelectNumActivity.class);
        if ("".equals(act.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            if (CommonUtil.parseInt(CommonUtil.cutKey(act.getKey())) > 1000) {
                submit_filter(3);//改变模式编辑号码 或者编辑号码
            } else {
                submit_filter(pos2);
            }
        }
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

    //摇一摇
    public void shark() {
        List<String> list_red_edit = new ArrayList<>();
        List<String> list_blue_edit = new ArrayList<>();
        List<Integer> list = CombineAlgorithm.LottoRedRandomNum();
        List<Integer> list_blue = CombineAlgorithm.LottoBlueRandomNum();
        for (int red : list) {
            list_red_edit.add(String.valueOf(red));
        }
        for (int blue : list_blue) {
            list_blue_edit.add(String.valueOf(blue));
        }
        //排序红球
        TreeSet ts = new TreeSet(list_red_edit);
        //清除数据重新刷新
        list_red_select.clear();
        list_red_select.addAll(list_red_edit);
        adapter_red.notifyDataSetChanged();
        list_blue_select.clear();
        list_blue_select.addAll(list_blue_edit);
        adapter_blue.notifyDataSetChanged();
        ShowTip();
    }

    //编辑添加号码确定按钮
    public void submit_filter(int type) {
        if (list_red_select.size() < 5 || list_blue_select.size() < 2) {
            TipsToast.showTips("请至少选择5个前区+2个后区");
            return;
        }
        if (selectCount > 100000) {
            TipsToast.showTips("选号不可超过10万注");
            return;
        }
        //号码选择10条不保存
        if (SingletonMapNomal.getMapSize() >= 10 && isEdit == false) {
            TipsToast.showTips("“我的选号”已满，最多10组号码");
            ContextHelper.getActivity(LottoSelectNumActivity.class).gotoConditionPage();
        } else {
            ArrayList<String> listfilter_nomal = new ArrayList<>();
            listfilter_nomal.addAll(list_red_select);
            //蓝球特殊标记 加50
            for (String num : list_blue_select) {
                listfilter_nomal.add(Integer.parseInt(num) + 50 + "");
            }
            CommonUtil.SortCollection(listfilter_nomal);
            LottoSelectNumActivity act = ContextHelper.getActivity(LottoSelectNumActivity.class);
            if (type == 2) {//编辑号码
                SingletonMapNomal.editDelete(act.getKey(), listfilter_nomal);
            } else if (type == 1) {//添加号码
                Log.e("yangfan", "submit_filter: " + SingletonMapNomal.sign_sort + "_" + onClickFlag);
                SingletonMapNomal.registerService(SingletonMapNomal.sign_sort + "_" + onClickFlag, listfilter_nomal);
                onClickFlag++;
                SingletonMapNomal.sign_sort++;
            } else if (type == 3) {//改变模式编辑
                SingletonMapNomal.removeMap(act.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
                SingletonMapNomal.registerService(CommonUtil.cutFirstKey(act.getKey()) + "_" + onClickFlag, listfilter_nomal);
                onClickFlag++;
            }
            act.gotoConditionPage();

        }
    }

    public void setEditMode() {
        isEdit = true;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
    }

    public void setAddMode() {
        isEdit = false;
        btn_clear.setVisibility(View.GONE);
        btn_save.setVisibility(View.GONE);
        btn_filter.setVisibility(View.GONE);
        btn_back.setVisibility(View.GONE);
        btn_cancel.setVisibility(View.VISIBLE);
        btn_submit.setVisibility(View.VISIBLE);
        clear();
    }

    public void setClearMode() {
        isEdit = false;
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
            if (requestCode == REQUEST_CODE) {
                if (resultCode == 100 && bundle != null) {
                    //进入编辑形态
                    setEditMode();
                    List<String> list = (List<String>) bundle.getSerializable("edit_list");
                    editPosition = bundle.getInt("pos");
                    editData(list);
                    //加号清空数据
                } else if (resultCode == 300) {
                    //除编辑形态清空数据
                    setAddMode();
                } else if (resultCode == 400) {
                    //除编辑形态清空数据
                    setClearMode();
                }
            }
        }
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

    public void switchIgnore() {
        adapter_red.setIgnore(ignoreFlag);
        adapter_blue.setIgnore(ignoreFlag);
        ignoreFlag = !ignoreFlag;
    }

    @Override
    public void Success(boolean success) {
        if (success) {
//            if(list_red_select.size()+list_blue_select.size()==7){
//                list_check1.clear();
//                list_check1.addAll(list_red_select);
//                list_check1.addAll(list_blue_select);
//            }else{
//                list_check2.addAll(list_red_select);
//                //检验蓝球特殊标记 加50
//                for (String num: list_blue_select) {
//                    list_check2.add(Integer.parseInt(num)+50+"");
//                }
//            }
        }
    }

    @Override
    public void setSelectNumD3PopUp(int x, int y, int postion, boolean isShow, boolean isRed) {
        if (isShow) {
            if (selectNum_popUp.getChildCount() == 0) {
                View popUp = null;
                if (isRed) {
                    popUp = LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_redlayout, selectNum_popUp, false);
                } else {
                    popUp = LayoutInflater.from(getActivity()).inflate(R.layout.selectnum_popup_bluelayout, selectNum_popUp, false);
                }
                TextView num_ball = (TextView) popUp.findViewById(R.id.selectNum_pop_ball);
                num_ball.setText(CommonUtil.preZeroForBall(String.valueOf(postion)));
                Toolbar toolbar = ContextHelper.getActivity(LottoSelectNumActivity.class).getToolBar();
                toolbar.measure(0, 0);
                int toolBarHeight = toolbar.getMeasuredHeight();//toolbar高度
                int systemBarHeight = CommonUtil.getStatusBarHeight();//系统状态栏高度
                selectNum_popUp.addView(popUp);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) popUp.getLayoutParams();
                params.setMargins((x - DisplayUtil.dip2px(13.5f)), (y - toolBarHeight - systemBarHeight - DisplayUtil.dip2px(54.5f)), 0, 0);
                popUp.setLayoutParams(params);
            }
        } else {
            if (selectNum_popUp.getChildCount() > 0) {
                selectNum_popUp.removeAllViews();
            }
        }
    }
}
