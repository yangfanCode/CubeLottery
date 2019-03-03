package com.cp2y.cube.activity.pushsingle.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.SelectSingleActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.KeyBordUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class PushSingleFragment extends Fragment {
    private boolean islMaxCount=false;
    private EditText et_input;
    private TextView lottery_type;
    private TextView textCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_push_single, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initListener() {
        lottery_type.setOnClickListener((v -> ContextHelper.getActivity(SelectSingleActivity.class).editData()));
        et_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int detailLength = s.length();
                textCount.setText(detailLength + "/150");
                if (detailLength == 149) {
                    islMaxCount = true;
                }
                if (detailLength == 150 && islMaxCount) {
                    TipsToast.showTips("达到输入上限");
                    islMaxCount = false;
                }
            }
        });
    }

    private void initView(View rootView) {
        et_input = (EditText)rootView. findViewById(R.id.push_single_et);
        textCount = (TextView)rootView. findViewById(R.id.pushsingle_textCount);
        lottery_type = (TextView)rootView. findViewById(R.id.pushsingle_lotteryName_tv);
        String lotteryName=ContextHelper.getActivity(SelectSingleActivity.class).getLotteryName();
        lottery_type.setText(lotteryName);//类型
    }
    public void setLotteryName(){
        String lotteryName=ContextHelper.getActivity(SelectSingleActivity.class).getLotteryName();
        lottery_type.setText(lotteryName);//类型
    }
    public void openKeyBoard(){
        KeyBordUtils.openKeybord(et_input,getActivity());//打开软键盘
    }
    public void closeKeyBoard(){
        KeyBordUtils.closeKeybord(et_input,getActivity());//关闭软键盘
    }
    //输入内容
    public String getTitle(){
        return et_input.getText().toString();
    }
}
