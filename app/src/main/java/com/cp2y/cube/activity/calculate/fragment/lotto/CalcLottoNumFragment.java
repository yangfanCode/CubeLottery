package com.cp2y.cube.activity.calculate.fragment.lotto;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.calculate.CalculateActivity;
import com.cp2y.cube.activity.calculate.fragment.lotto.adapter.FilterLottoLotteryAdapter;
import com.cp2y.cube.activity.recognize.adapter.CashDoubleMoneyAdapter;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.custom.SingletonMapLotto;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.helper.NetHelper;
import com.cp2y.cube.model.CalcCashModel;
import com.cp2y.cube.model.ScanCashNumModel;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.utils.CalcCashUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.FileUtils;
import com.cp2y.cube.widgets.SwipeMenuHelper;
import com.google.gson.Gson;
import com.wang.avi.AVLoadingIndicatorView;
import com.yangfan.widget.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalcLottoNumFragment extends Fragment implements MyInterface.SetCalcIssue{
    private FilterLottoLotteryAdapter lotteryAdapter;
    private TextView tvAdd;
    private SwipeMenuListView listView;
    private AVLoadingIndicatorView AV_Loading;
    private TextView tvIssue;
    private Button btnCash;
    private String issue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_calc_lotto_num, container, false);
        initView(rootView);
        initData();
        initListener();
        return rootView;
    }
    private void initListener() {
        tvAdd.setOnClickListener((v -> {
            LottoCalcFragment fragment = (LottoCalcFragment) CalcLottoNumFragment.this.getParentFragment();
            fragment.setAddMode();
        }));
        btnCash.setOnClickListener((v -> {
            if (!CommonUtil.isNetworkConnected(getActivity())){
                TipsToast.showTips(getString(R.string.netOff));
                return;
            }
            if(lotteryAdapter.getCount()==0){
                TipsToast.showTips("计算奖金失败");
                return;
            }
            AV_Loading.setVisibility(View.VISIBLE);
            Observable.create(subscriber -> {
                File file=getJson();
                subscriber.onNext(file);
            }).compose(ApiHelper.applySchedulers())
                    .subscribe(new SafeOnlyNextSubscriber<Object>() {
                        @Override
                        public void onNext(Object args) {
                            super.onNext(args);
                            NetHelper.LOTTERY_API.calcLottery((File)args, 10088, issue, 1, false, true)
                                    .subscribe(new SafeOnlyNextSubscriber<ScanCashNumModel>() {
                                        @Override
                                        public void onNext(ScanCashNumModel args) {
                                            super.onNext(args);
                                            AV_Loading.setVisibility(View.GONE);
                                            if(args.getFlag()==1){
                                                CustomDialog.Builder builder=new CustomDialog.Builder(getActivity());
                                                View v=LayoutInflater.from(getActivity()).inflate(R.layout.calc_result_layout,null);
                                                builder.setContentView(v);
                                                MyListView lv= (MyListView) v.findViewById(R.id.calc_prize_lv);
                                                Button btn= (Button) v.findViewById(R.id.calc_btnKnow);
                                                TextView tvResult=(TextView)v.findViewById(R.id.calc_tvResult);
                                                TextView tvResultR=(TextView)v.findViewById(R.id.calc_tvResultR);
                                                LinearLayout ll_prize= (LinearLayout) v.findViewById(R.id.calc_prize_ll);//中奖layout
                                                tvResultR.setText("-1".equals(args.getDrawDetail().getTotal())?"--":args.getDrawDetail().getTotal());
                                                ll_prize.setVisibility("0".equals(args.getDrawDetail().getTotal())?View.GONE:View.VISIBLE);
                                                tvResult.setText("0".equals(args.getDrawDetail().getTotal())?"未中奖":"恭喜！您的中奖金额为");
                                                CashDoubleMoneyAdapter cashaDoubledapter=new CashDoubleMoneyAdapter(getActivity());//下方奖期adapter
                                                lv.setAdapter(cashaDoubledapter);
                                                cashaDoubledapter.LoadData(args.getDrawDetail().getItems());
                                                Dialog dialog=builder.create();
                                                btn.setOnClickListener(v1 -> dialog.dismiss());
                                                dialog.setCancelable(false);
                                                dialog.show();
                                            }else{
                                                TipsToast.showTips("兑奖失败");
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            super.onError(e);
                                            AV_Loading.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    });
        }));
    }
    public void reloadData() {
        lotteryAdapter.notifyDataSetChanged();
    }

    private void initData() {
        lotteryAdapter = new FilterLottoLotteryAdapter();
        lotteryAdapter.setFlag(1);
        listView.setAdapter(lotteryAdapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {//跳转编辑
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(i);
            List<String> list = entry.getValue();//截取key
            int key = CommonUtil.parseInt(CommonUtil.cutKey(entry.getKey()));
            LottoCalcFragment fragment = (LottoCalcFragment) CalcLottoNumFragment.this.getParentFragment();
            if (key < 1000) {//普通模式
                fragment.editData(list, entry.getKey(), true);
            } else {//胆拖模式
                fragment.editData(list, entry.getKey(), false);
            }
        });
        listView.setOnMenuItemClickListener((position, menu, index) -> {//删除
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(position);
            SingletonMapLotto.removeMap(entry.getKey());
            lotteryAdapter.notifyDataSetChanged();
            return false;
        });
        listView.setMenuCreator(menu -> menu.addMenuItem(SwipeMenuHelper.generateDeleteItem()));
    }

    private void initView(View rootView) {
        //选中的彩票
        listView = (SwipeMenuListView)rootView. findViewById(R.id.lottery_list);
        tvIssue = (TextView) rootView.findViewById(R.id.calc_tvIssue);
        tvAdd= (TextView) rootView.findViewById(R.id.calc_tvAdd);
        btnCash = (Button) rootView.findViewById(R.id.filter_condition_btn_startFilter);
        AV_Loading = (AVLoadingIndicatorView)rootView. findViewById(R.id.AVLoadingIndicator);
        LottoCalcFragment fragment = (LottoCalcFragment) CalcLottoNumFragment.this.getParentFragment();
        ((CalculateActivity)fragment.getActivity()).setCalcIssue(this);
    }
    //封装数据 计算奖金
    private File getJson(){
        List<CalcCashModel> data=new ArrayList<>();//封装数据
        List<Map.Entry<String, List<String>>> list=lotteryAdapter.getEntries();//数据源
        for(int i=0,size=list.size();i<size;i++){
            Map.Entry<String, List<String>> entry=list.get(i);
            CalcCashModel calcCashModel=new CalcCashModel();
            if(lotteryAdapter.getItemViewType(i)==0){//单式
                calcCashModel.number= CalcCashUtils.saveSelectNum(entry.getValue());
                calcCashModel.playType="1004";
            }else if(lotteryAdapter.getItemViewType(i)==1){//复式
                calcCashModel.number= CalcCashUtils.saveSelectDoubleNum(entry.getValue());
                calcCashModel.playType="1005";
            }else{//胆拖
                calcCashModel.number= CalcCashUtils.saveLottoDanTuoNum(entry.getValue());
                calcCashModel.playType="1006";
            }
            data.add(calcCashModel);
        }
        String json=new Gson().toJson(data);
        FileUtils.saveFile(json,FileUtils.CALC_PATH);
        return new File(FileUtils.CALC_PATH);
    }
    @Override
    public void setCalcIssue(String issue) {
        if(TextUtils.isEmpty(issue)){
            tvIssue.setText("(大乐透)".concat(issue));
        } else{
            this.issue = issue;
            tvIssue.setText("(大乐透 ".concat(issue).concat("期)"));
        }
    }
}
