package com.cp2y.cube.fragment.doubleball;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.FilterResultActivity;
import com.cp2y.cube.activity.selectnums.SelectNumFilterActivity;
import com.cp2y.cube.adapter.FilterConditionAdapter;
import com.cp2y.cube.adapter.FilterLotteryAdapter;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.SingletonMapFilterResult;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.BigDialog;
import com.cp2y.cube.dialog.ConditionDialog;
import com.cp2y.cube.dialog.DivideDialog;
import com.cp2y.cube.dialog.HistoryDialog;
import com.cp2y.cube.dialog.SumMantissaDialog;
import com.cp2y.cube.dialog.OddDialog;
import com.cp2y.cube.dialog.SpanDialog;
import com.cp2y.cube.dialog.SumDialog;
import com.cp2y.cube.fragment.BaseFragment;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.LotteryHelper;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.tool.filter.BigSmallFilter;
import com.cp2y.cube.tool.filter.DevideThreeFilter;
import com.cp2y.cube.tool.filter.FilterCollection;
import com.cp2y.cube.tool.filter.HistoryFilter;
import com.cp2y.cube.tool.filter.MantissaFilter;
import com.cp2y.cube.tool.filter.OddEvenFilter;
import com.cp2y.cube.tool.filter.RangeFilter;
import com.cp2y.cube.tool.filter.SumFilter;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.widgets.SwipeMenuHelper;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;

/**
 * Created by js on 2017/1/13.
 */
public class ConditionFragment extends BaseFragment implements HistoryDialog.HistoryCall, SumDialog.SumCall, SpanDialog.SpanCall, BigDialog.BigCall, OddDialog.OddCall, DivideDialog.DivideCall, SumMantissaDialog.MantissaCall {

    private View rootView;
    private TextView histortALF_tv, sumNumALF_tv, spanALF_tv, daxiaoALF_tv, jiouALF_tv, chu3yuALF_tv, mantissaALF_tv;
    private HistoryDialog historyDialog;
    private SumDialog sumDialog;
    private SpanDialog spanDialog;
    private BigDialog bigDialog;
    private OddDialog oddDialog;
    private DivideDialog divideDialog;
    private SumMantissaDialog sumMantissaDialog;
    private RelativeLayout myFilter_layout;
    private AVLoadingIndicatorView AV_Loading;
    private Map<Integer, List<String>> data = new HashMap<>();
    private FilterLotteryAdapter lotteryAdapter;
    private FilterConditionAdapter conditionAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_filter_condition, container, false);
        historyDialog = new HistoryDialog(getContext(), this);
        historyDialog.setFlag(0);
        sumDialog = new SumDialog(getContext(), this);
        sumDialog.setFlag(0);
        spanDialog = new SpanDialog(getContext(), this);
        spanDialog.setFlag(0);
        bigDialog = new BigDialog(getContext(), this);
        bigDialog.setFlag(0);
        oddDialog = new OddDialog(getContext(), this);
        oddDialog.setFlag(0);
        divideDialog = new DivideDialog(getContext(), this);
        divideDialog.setFlag(0);
        sumMantissaDialog = new SumMantissaDialog(getContext(), this);
        histortALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_historyNum);
        sumNumALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_sumNum);
        spanALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_span);
        daxiaoALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_daxiao);
        jiouALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_jiou);
        chu3yuALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_chu3yu);
        mantissaALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_mantissa);
        histortALF_tv.setOnClickListener(view -> historyDialog.show());
        sumNumALF_tv.setOnClickListener(view -> sumDialog.show());
        spanALF_tv.setOnClickListener(view -> spanDialog.show());
        daxiaoALF_tv.setOnClickListener(view -> bigDialog.show());
        jiouALF_tv.setOnClickListener(view -> oddDialog.show());
        chu3yuALF_tv.setOnClickListener(view -> divideDialog.show());
        mantissaALF_tv.setOnClickListener(view -> sumMantissaDialog.show());
        //选中的彩票
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lottery_list);
        lotteryAdapter = new FilterLotteryAdapter();
        lotteryAdapter.setFlag(0);
        listView.setAdapter(lotteryAdapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {//跳转编辑
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(i);
            List<String> list = entry.getValue();//截取key
            int key = CommonUtil.parseInt(CommonUtil.cutKey(entry.getKey()));
            if (key < 1000) {//普通模式
                ContextHelper.getActivity(SelectNumFilterActivity.class).editData(list, entry.getKey(), true);
            } else {//胆拖模式
                ContextHelper.getActivity(SelectNumFilterActivity.class).editData(list, entry.getKey(), false);
            }
        });
        listView.setOnMenuItemClickListener((position, menu, index) -> {//删除
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(position);
            SingletonMapNomal.removeMap(entry.getKey());
            lotteryAdapter.notifyDataSetChanged();
            return false;
        });
        listView.setMenuCreator(menu -> menu.addMenuItem(SwipeMenuHelper.generateDeleteItem()));
        //选中的条件
        SwipeMenuListView conList = (SwipeMenuListView) findViewById(R.id.condition_list);
        conditionAdapter = new FilterConditionAdapter();
        conList.setAdapter(conditionAdapter);
        conList.setOnItemClickListener((adapterView, view1, i, l) -> conditionAdapter.getItem(i).show());
        conList.setOnMenuItemClickListener((position, menu, index) -> {//删除操作
            int type = conditionAdapter.getItemViewType(position);
            TextView[] alf_tvs = {histortALF_tv, sumNumALF_tv, spanALF_tv, daxiaoALF_tv, jiouALF_tv, chu3yuALF_tv, mantissaALF_tv};
            conditionAdapter.getItem(position).reset();
            conditionAdapter.removeData(conditionAdapter.getItem(position));
            alf_tvs[type].setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao);
            alf_tvs[type].setTextColor(ColorUtils.NORMAL_GRAY);
            myFilter_layout.setVisibility(conditionAdapter.getCount() == 0 ? View.GONE : View.VISIBLE);
            return false;
        });
        conList.setMenuCreator(menu -> menu.addMenuItem(SwipeMenuHelper.generateDeleteItem()));
        myFilter_layout = (RelativeLayout) findViewById(R.id.filter_condition_layout3);
        AV_Loading = (AVLoadingIndicatorView) findViewById(R.id.AVLoadingIndicator);
        findViewById(R.id.filter_condition_btn_startFilter).setOnClickListener(view -> startFilter());
        findViewById(R.id.filter_condition_ivAddNum).setOnClickListener(view -> {
            if (lotteryAdapter.getCount() < 10) {
                ContextHelper.getActivity(SelectNumFilterActivity.class).setAddMode();
            } else {
                TipsToast.showTips("“我的选号”已满，最多10组号码");
            }
        });
        loadConditions();
//        BaseActivity activity= (BaseActivity) getActivity();
//        activity.
        return rootView;
    }

    private void loadConditions() {
        if (SingletonMapFilter.getMap().size() > 0) {
            myFilter_layout.setVisibility(View.VISIBLE);
            for (Iterator<Map.Entry<String, List<String>>> it = SingletonMapFilter.getMap().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();
                List<String> list = entry.getValue();
                if (list == null || list.size() == 0) continue;
                int key = Integer.valueOf(entry.getKey());
                if (key == 1) {//和值范围模式
                    int start = CommonUtil.parseInt(list.get(0)), end = CommonUtil.parseInt(list.get(1));
                    sumDialog.setRange(start, end);
                    sumNumALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    sumNumALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(sumDialog, true);
                } else if (key == 4) {//跨度独立模式
                    spanDialog.initSums(list);
                    spanALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    spanALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(spanDialog, true);
                } else if (key == 7) {//奇偶形态
                    oddDialog.initPattern(list);
                    jiouALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    jiouALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(oddDialog, true);
                } else if (key == 8) {//奇偶比
                    oddDialog.initNums(list);
                    jiouALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    jiouALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(oddDialog, true);
                } else if (key == 9) {//除3形态
                    divideDialog.initPattern(list);
                    chu3yuALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    chu3yuALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(divideDialog, true);
                } else if (key == 10) {
                    divideDialog.initNum(list);
                    chu3yuALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    chu3yuALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(divideDialog, true);
                } else if (key == 11) {//和尾数
                    sumMantissaDialog.initSums(list);
                    mantissaALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    mantissaALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(sumMantissaDialog, true);
                } else if (key == 12) {//大小形态
                    bigDialog.initPattern(list);
                    daxiaoALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    daxiaoALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(bigDialog, true);
                } else if (key == 13) {//大小比
                    bigDialog.initNums(list);
                    daxiaoALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
                    daxiaoALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
                    showCondition(bigDialog, true);
                }
            }
        } else {
            myFilter_layout.setVisibility(View.GONE);
        }
    }

    public void reloadData() {
        lotteryAdapter.notifyDataSetChanged();
    }

    public void showCondition(ConditionDialog dialog, boolean isEnable) {
        if (isEnable) {
            conditionAdapter.replaceData(dialog);
            myFilter_layout.setVisibility(View.VISIBLE);
        } else {
            conditionAdapter.removeData(dialog);
        }
    }

    @Override
    public void onSubmit(HistoryDialog dialog) {
        myFilter_layout.setVisibility(View.VISIBLE);
        conditionAdapter.replaceData(dialog);
        histortALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
        histortALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
    }

    @Override
    public void onSubmit(SumDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        sumNumALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        sumNumALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(SpanDialog dialog, Range range, Set<Integer> sums, boolean include) {
        boolean isEnable = dialog.isConditionEnable();
        spanALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        spanALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(BigDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        daxiaoALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        daxiaoALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(OddDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        jiouALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        jiouALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(DivideDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        chu3yuALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        chu3yuALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(SumMantissaDialog dialog, Range range, Set<Integer> sums, boolean include) {
        boolean isEnable = dialog.isConditionEnable();
        mantissaALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        mantissaALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    /**
     * 开始过滤
     */
    private void startFilter() {
        //保存筛选过滤结果,保存之前清空
        SingletonMapFilterResult.getMap().clear();
        AV_Loading.setVisibility(View.VISIBLE);
        FilterCollection.Builder<byte[]> builder = new FilterCollection.Builder<>();
        if (historyDialog.isConditionEnable()) {//满足过滤条件
            builder.addFilter(new HistoryFilter(historyDialog.isChecked(), historyDialog.getList()));//判断排除包含和单式复式胆拖
        }
        if (sumDialog.isConditionEnable()) {
            builder.addFilter(new SumFilter(sumDialog.isChecked(), sumDialog.getRange(), sumDialog.getSums()));
        }
        if (spanDialog.isConditionEnable()) {
            builder.addFilter(new RangeFilter(spanDialog.isChecked(), spanDialog.getRange(), spanDialog.getSums()));
        }
        if (bigDialog.isConditionEnable()) {
            builder.addFilter(new BigSmallFilter(bigDialog.isChecked(), bigDialog.getPattern(), bigDialog.isChecked2(), (byte) 18, (byte) 9, (byte) 6, bigDialog.getNums()));
        }
        if (oddDialog.isConditionEnable()) {
            builder.addFilter(new OddEvenFilter(oddDialog.isChecked(), oddDialog.getPattern(), oddDialog.isChecked2(), oddDialog.getNums()));
        }
        if (divideDialog.isConditionEnable()) {
            builder.addFilter(new DevideThreeFilter(divideDialog.isChecked(), divideDialog.getPattern(), divideDialog.isChecked2(), divideDialog.getNums1(), divideDialog.getNums2()));
        }
        if (sumMantissaDialog.isConditionEnable()) {
            builder.addFilter(new MantissaFilter(sumMantissaDialog.isChecked(), sumMantissaDialog.getRange(), sumMantissaDialog.getSums()));
        }
        ArrayList<byte[]> data_total = new ArrayList<>();
        data_total.ensureCapacity(1000000);
        Observable.create(subscriber -> {
            for (Iterator<Map.Entry<String, List<String>>> it = SingletonMapNomal.getMap().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();
                List<String> list = entry.getValue();
                if (list == null || list.size() == 0) continue;
                //截取key
                int key = Integer.valueOf(CommonUtil.cutKey(entry.getKey()));
                if (key < 1000) { //单式
                    if (list.size() == 7) {
                        byte[] single = new byte[7];
                        int i = 0;
                        for (String num : list) {
                            byte b = Byte.parseByte(num);
                            single[i++] = b > 50 ? (byte) (b - 50) : b;
                        }
                        data_total.add(single);

                    } else if (list.size() > 7) {//复式
                        byte[] nums = new byte[list.size()];
                        int i = 0, j = 0;
                        for (String num : list) {
                            byte b = Byte.parseByte(num);
                            nums[i++] = b > 50 ? (byte) (b - 50) : b;
                            j = (j == 0 && b > 50) ? (i - 1) : j;
                        }
                        byte[] reds = new byte[j];
                        byte[] blues = new byte[i - j];
                        System.arraycopy(nums, 0, reds, 0, reds.length);
                        System.arraycopy(nums, j, blues, 0, blues.length);
                        List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(new byte[]{}, reds, blues, 7, 1);//双色球复式
                        data_total.addAll(data);
                    }
                } else { //胆拖
                    byte[] nums = new byte[list.size()];
                    int i = 0, j = 0, k = 0;
                    for (String num : list) {
                        byte b = Byte.parseByte(num);
                        nums[i++] = b < 50 ? b : (b < 100 ? (byte) (b - 50) : (byte) (b - 100));
                        j = (j == 0 && b > 50) ? (i - 1) : j;
                        k = (k == 0 && b > 100) ? (i - 1) : k;
                    }
                    byte[] reds1 = new byte[j];
                    byte[] reds2 = new byte[k - j];
                    byte[] blues = new byte[i - k];
                    System.arraycopy(nums, 0, reds1, 0, reds1.length);
                    System.arraycopy(nums, j, reds2, 0, reds2.length);
                    System.arraycopy(nums, k, blues, 0, blues.length);
                    List<byte[]> data = LotteryHelper.generateLotteryRecordsColorBall(reds1, reds2, blues, 7, 1);
                    data_total.addAll(data);
                }
            }
            //过滤
            FilterCollection<byte[]> filterCollection = builder.build();
            int totalCount = data_total.size();
            filterCollection.doFilter(data_total);
            int afterCount = data_total.size();
            subscriber.onNext(totalCount + "_" + afterCount);
        })
                .compose(ApiHelper.applySchedulers())
                .subscribe(new SafeOnlyNextSubscriber<Object>() {
                    @Override
                    public void onNext(Object args) {
                        super.onNext(args);
                        String[] count = String.valueOf(args).split("_");
                        int totalCount = CommonUtil.parseInt(count[0]);
                        int afterCount = CommonUtil.parseInt(count[1]);
                        AV_Loading.setVisibility(View.GONE);
                        if (afterCount > 100000) {
                            TipsToast.showTips("无法显示，过滤结果不可超10万注");
                            data_total.clear();
                            return;
                        }
                        SingletonMapFilterResult.registerService("filterResult", data_total);
                        //过滤结束跳转
                        Intent intent = new Intent(getActivity(), FilterResultActivity.class);
                        intent.putExtra("totalCount", totalCount);
                        intent.putExtra("afterCount", afterCount);
                        intent.putExtra("flag", 0);
                        startActivity(intent);
                    }
                });
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

}
