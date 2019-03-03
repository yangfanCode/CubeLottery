package com.cp2y.cube.activity.multicalc.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.multicalc.MultiCalcActivity;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.MultiCalcResultModel;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.KeyBordUtils;
import com.cp2y.cube.utils.MultiCalcUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiCalcFragment extends Fragment {
    private List<CheckBox> checkBoxes;
    private CheckBox cbAll, cbBeforeAfter, cbWin;
    private int checkIndex = 0;//选择的条件 默认0
    private Button btnCalc;
    private EditText edNote,etIssue,etMulti,etPrize,etAll,etBeforeIssue,etBeforeRate,etAfterRate,etWin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_multi_calc, container, false);
        initView(rootView);
        initListener();
        return rootView;
    }

    private void initListener() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox cb = checkBoxes.get(i);
            cb.setOnClickListener(listener);//监听
        }
        btnCalc.setOnClickListener(v -> {
            KeyBordUtils.closeKeybord(edNote,getActivity());//关闭软键盘
            if(!checkInput(edNote,etIssue,etMulti,etPrize))return;
            Observable.create(subscriber -> {
                int putNotes=(Integer.valueOf(edNote.getText().toString().trim())) ;//投入注数
                int putIssues=(Integer.valueOf(etIssue.getText().toString().trim())) ;//投入期数
                int putMultis=(Integer.valueOf(etMulti.getText().toString().trim())) ;//起始倍数
                int putPrize=(Integer.valueOf(etPrize.getText().toString().trim())) ;//单注奖金
                List<MultiCalcResultModel> list=new ArrayList<>();
                if(checkIndex==0){//全程盈利率模式
                    double incomeRate=(Double.valueOf(TextUtils.isEmpty(etAll.getText().toString().trim())?"30":etAll.getText().toString().trim())) ;
                    list = MultiCalcUtils.getIncomRate(putNotes,putIssues,putMultis,putPrize,incomeRate,0,0);//展示数据源,指向list引用
                }else if(checkIndex==1){//前__期 之后模式
                    //输入期数
                    int beforeIssue=(Integer.valueOf(TextUtils.isEmpty(etBeforeIssue.getText().toString().trim())?"5":etBeforeIssue.getText().toString().trim())) ;
                    double beforeRate=(Double.valueOf(TextUtils.isEmpty(etBeforeRate.getText().toString().trim())?"50":etBeforeRate.getText().toString().trim())) ;
                    double afterRate=(Double.valueOf(TextUtils.isEmpty(etAfterRate.getText().toString().trim())?"20":etAfterRate.getText().toString().trim())) ;
                    List<MultiCalcResultModel> listBefore=MultiCalcUtils.getIncomRate(putNotes,beforeIssue,putMultis,putPrize,beforeRate,0,0);
                    List<MultiCalcResultModel> listAfter=MultiCalcUtils.getIncomRate(putNotes,putIssues,putMultis,putPrize,afterRate,listBefore.get(listBefore.size()-1).periodTotal,beforeIssue);
                    list.addAll(listBefore);
                    list.addAll(listAfter);
                }else{//全程盈利 元 模式
                    int inputMoney=(Integer.valueOf(TextUtils.isEmpty(etWin.getText().toString().trim())?"30":etWin.getText().toString().trim())) ;
                    list = MultiCalcUtils.getIncom(putNotes,putIssues,putMultis,putPrize,inputMoney);
                }
                subscriber.onNext(list);
            }).compose(ApiHelper.applySchedulers())
                    .subscribe(new SafeOnlyNextSubscriber<Object>() {
                        @Override
                        public void onNext(Object args) {
                            super.onNext(args);
                            MultiCalcActivity act = (MultiCalcActivity) getActivity();
                            List<MultiCalcResultModel> list= (List<MultiCalcResultModel>) args;
                            if(list!=null&&list.size()>0){
                                act.gotoResultPage((List<MultiCalcResultModel>) args);
                            }else{
                                TipsToast.showTips("您的方案不适合倍投。");
                                return;
                            }

                        }
                    });
        });
    }

    private void initView(View rootView) {
        checkBoxes = new ArrayList<>();
        cbAll = (CheckBox) rootView.findViewById(R.id.multi_cbAll);
        cbBeforeAfter = (CheckBox) rootView.findViewById(R.id.multi_cbBeforeAfter);
        cbWin = (CheckBox) rootView.findViewById(R.id.multi_cbWin);
        checkBoxes.add(cbAll);
        checkBoxes.add(cbBeforeAfter);
        checkBoxes.add(cbWin);
        btnCalc = (Button) rootView.findViewById(R.id.multi_calc);
        edNote = (EditText) rootView.findViewById(R.id.multi_etNote);
        etIssue = (EditText) rootView.findViewById(R.id.multi_etIssue);
        etMulti = (EditText) rootView.findViewById(R.id.multi_etMulti);
        etPrize = (EditText) rootView.findViewById(R.id.multi_etPrize);
        etAll = (EditText) rootView.findViewById(R.id.multi_etAll);
        etBeforeIssue = (EditText) rootView.findViewById(R.id.multi_etBeforeIssue);
        etBeforeRate = (EditText) rootView.findViewById(R.id.multi_etBeforeRate);
        etAfterRate = (EditText) rootView.findViewById(R.id.multi_etAfterRate);
        etWin = (EditText) rootView.findViewById(R.id.multi_etWin);
    }

    private boolean checkInput(EditText...editTexts){
        boolean isSuccess=true;
        for(int i=0;i<editTexts.length;i++){
            String input=editTexts[i].getText().toString().trim();
            if(TextUtils.isEmpty(input)){
                TipsToast.showTips("设置不可为空");
                isSuccess=false;
                break;
            }
        }
        return isSuccess;
    }

    View.OnClickListener listener = v -> {
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox cb = checkBoxes.get(i);
            if (cb == v) {
                if (checkIndex == i) break;//第二次点击同一个
                cb.setChecked(true);
                checkIndex = i;
                continue;
            }
            cb.setChecked(false);
        }
    };


}
