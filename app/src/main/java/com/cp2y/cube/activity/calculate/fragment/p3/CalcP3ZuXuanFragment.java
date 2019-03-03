package com.cp2y.cube.activity.calculate.fragment.p3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.SingletonMapP3;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.KeyBordUtils;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalcP3ZuXuanFragment extends Fragment {
    private String textTemp;
    private List<List<String>> list_select = new ArrayList<>(); //组选单式
    private int selectCount;
    private int onClickFlag = 9001;//组选单式
    private EditText editText;
    private Button btn_clear, btn_submit;
    private View rootView;
    private boolean isEdit = false;//是否编辑模式
    private LinearLayout tip_ll;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calc_d3_zu_xuan, container, false);
        initView();
        initListener();
        return rootView;
    }

    private void initListener() {
        btn_clear.setOnClickListener((v -> {
            clear();
        }));
        btn_submit.setOnClickListener(v -> numSet(1, 2));//1添加 2编辑
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (!TextUtils.isEmpty(textTemp) && textTemp.length() > text.length()) {//删除操作
                    //删除处理
                    textTemp = text;
                } else {//添加  中间添加处理
                    int index = editText.getSelectionStart();
                    if (index > 0 && index < text.length()) {
                        //中间插入 加换行符
                        int lastNull = text.substring(0, index).lastIndexOf("\n") + 1;
                        if (index - lastNull < 4) {

                        } else if (index - lastNull == 4) {//判断距离上一个换行符是不是4位,4位换行加数字
                            Editable editable = editText.getText();
                            editable.insert(index - 1, "\n");
                        }
                    } else {//从尾部插入
                        if (CommonUtil.D3P3ZuXuanInputValue(text)) {
                            Editable editable = editText.getText();
                            editable.insert(index, "\n");
                            textTemp = text.concat("\n");
//                            editText.setText(textTemp);
                            editText.setSelection(editText.getText().length());
                        } else {
                            textTemp = text;
                        }
                    }
                }
                addSelectData(text);
                //for(int i=0,size=text.length();)
            }
        });
    }

    private void initView() {
        editText = (EditText) rootView.findViewById(R.id.calc_zuxuan_edInput);
        btn_clear = (Button) rootView.findViewById(R.id.calc_zuxuan_btnClear);
        btn_submit = (Button) rootView.findViewById(R.id.calc_zuxuan_btnSubmit);
        tip_ll = (LinearLayout) rootView.findViewById(R.id.selectNum_3d_zu3_tip_ll);
    }

    //清空按钮操作
    private void clear() {
        tip_ll.setVisibility(View.GONE);
        editText.setText("");
    }

    //添加模式 按钮变化
    public void setAddMode() {
        isEdit = false;
        editText.setText("");
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});
    }

    //清除模式 按钮变化
    public void setClearMode() {
        isEdit = false;
        clear();
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.MAX_VALUE)});
    }

    //编辑模式 按钮变化
    public void setEditMode() {
        isEdit = true;
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
    }

    //编辑展示数据
    public void editData(List<String> list, int key) {
        clear();
        //组选单式
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
        }
        stringBuilder.append("\n");
        editText.setText(stringBuilder.toString());
        editText.setSelection(editText.getText().length());
    }

    private void addSelectData(String text) {
        if (TextUtils.isEmpty(text)) return;
        if (text.length() < 3) return;
        list_select.clear();
        if (text.indexOf("\n") > 0) {
            String[] data = text.split("\n");
            for (int i = 0, size = data.length; i < size; i++) {
                String num = data[i];
                if (num.length() == 3) {
                    List<String> nums = new ArrayList<>();
                    for (int j = 0; j < 3; j++) {
                        nums.add(num.substring(j, j + 1));
                    }
                    list_select.add(nums);
                }
            }
        } else {
            List<String> nums = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                nums.add(text.substring(j, j + 1));
            }
            list_select.add(nums);
        }

        ShowTip();
    }

    public void ShowTip() {
        boolean showTips = list_select.size() > 0 && list_select.get(0).size() > 2;
        ViewUtils.showViewsVisible(showTips, tip_ll);
        if (!showTips) {
            selectCount = 0;
        } else {
            selectCount = list_select.size();
            TextView tv_count = (TextView) rootView.findViewById(R.id.selectNum_filterNomal_count);
            tv_count.setText(String.valueOf(selectCount));
        }
    }

    //号码设置
    private void numSet(int pos1, int pos2) {
        P3CalcFragment fragment = (P3CalcFragment) CalcP3ZuXuanFragment.this.getParentFragment();
        if ("".equals(fragment.getKey())) {
            submit_filter(pos1);//添加号码
        } else {
            if (CommonUtil.parseInt(CommonUtil.cutKey(fragment.getKey())) > 3000 && CommonUtil.parseInt(CommonUtil.cutKey(fragment.getKey())) < 6000) {
                submit_filter(3);//改变模式编辑号码
            } else {
                submit_filter(pos2);// 不变模式编辑号码
            }
        }
    }

    //过滤和确定按钮
    public void submit_filter(int type) {
        if (list_select == null || (list_select.size() == 1 && list_select.get(0).size() < 3)) {
            TipsToast.showTips("请至少选3个号");
            return;
        }
//        if (selectCount > 100000) {
//            TipsToast.showTips("选号不可超过10万注");
//            return;
//        }

        //CommonUtil.SortCollection(listfilter_zu6);
        P3CalcFragment fragment = (P3CalcFragment) CalcP3ZuXuanFragment.this.getParentFragment();
        if (type == 2) {//编辑模式 可能切换过来编辑很多 所以重新put
            for (int i = 0, size = list_select.size(); i < size; i++) {
                SingletonMapP3.registerService(fragment.getKey(), list_select.get(i));
            }
        } else if (type == 1) {//添加号码
            for (int i = 0, size = list_select.size(); i < size; i++) {
                SingletonMapP3.registerService(SingletonMapP3.sign_sort + "_" + onClickFlag, list_select.get(i));
                onClickFlag++;
                SingletonMapP3.sign_sort++;
            }
        } else if (type == 3) {//改变模式编辑号码
//            SingletonMapP3.removeMap(fragment.getKey());//删除原数据 增加新数据用原数据的标识key CommonUtil.cutFirstKey(act.getKey()
//            SingletonMapP3.registerService(CommonUtil.cutFirstKey(fragment.getKey()) + "_" + onClickFlag, listfilter_zu6);
//            onClickFlag++;
        }
        KeyBordUtils.closeKeybord(editText, getActivity());//关闭软键盘
        fragment.gotoConditionPage();


    }
}
