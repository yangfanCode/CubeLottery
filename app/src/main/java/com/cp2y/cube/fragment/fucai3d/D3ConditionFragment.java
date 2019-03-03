package com.cp2y.cube.fragment.fucai3d;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cp2y.cube.R;
import com.cp2y.cube.activity.FilterResultActivity;
import com.cp2y.cube.activity.selectnums.Select3DNumActivity;
import com.cp2y.cube.adapter.Filter3DConditionAdapter;
import com.cp2y.cube.adapter.Filter3DLotteryAdapter;
import com.cp2y.cube.custom.SingletonMap3D;
import com.cp2y.cube.custom.SingletonMapFilter;
import com.cp2y.cube.custom.SingletonMapFilterResult;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.dialog.ConditionDialog;
import com.cp2y.cube.dialog.D3BigDialog;
import com.cp2y.cube.dialog.D3OddDialog;
import com.cp2y.cube.dialog.DivideDialog;
import com.cp2y.cube.dialog.DoubleNumDialog;
import com.cp2y.cube.dialog.HistoryDialog;
import com.cp2y.cube.dialog.SumMantissaDialog;
import com.cp2y.cube.dialog.SpanDialog;
import com.cp2y.cube.dialog.SumDialog;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.helper.LotteryHelper;
import com.cp2y.cube.network.api.ApiHelper;
import com.cp2y.cube.network.subscriber.SafeOnlyNextSubscriber;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.tool.filter.D3BigSmallFilter;
import com.cp2y.cube.tool.filter.D3OddEvenFilter;
import com.cp2y.cube.tool.filter.DevideThreeFilter;
import com.cp2y.cube.tool.filter.DisOrderHistoryFilter;
import com.cp2y.cube.tool.filter.DoubleNumFilter;
import com.cp2y.cube.tool.filter.FilterCollection;
import com.cp2y.cube.tool.filter.HistoryFilter;
import com.cp2y.cube.tool.filter.MantissaFilter;
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
 * A simple {@link Fragment} subclass.
 */
public class D3ConditionFragment extends Fragment implements HistoryDialog.HistoryCall, DoubleNumDialog.DoubleNumCall, SumDialog.SumCall, SpanDialog.SpanCall, D3BigDialog.BigCall, D3OddDialog.OddCall, DivideDialog.DivideCall ,SumMantissaDialog.MantissaCall{
    private View rootView;
    private TextView histortALF_tv, doubleNumALF_tv, sumNumALF_tv, spanALF_tv, daxiaoALF_tv, jiouALF_tv, chu3yuALF_tv ,mantissaALF_tv;
    private HistoryDialog historyDialog;
    private SumDialog sumDialog;
    private SpanDialog spanDialog;
    private D3BigDialog bigDialog;
    private D3OddDialog oddDialog;
    private DivideDialog divideDialog;
    private DoubleNumDialog doubleNumDialog;
    private SumMantissaDialog sumMantissaDialog;
    private RelativeLayout myFilter_layout;
    private AVLoadingIndicatorView AV_Loading;
    private Map<Integer, List<String>> data = new HashMap<>();
    private Filter3DLotteryAdapter lotteryAdapter;
    private Filter3DConditionAdapter conditionAdapter;
    private boolean isZhiXuan = false;//直选组选区分

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_d3_condition, container, false);
        initView();//初始化
        initData();//数据
        initListener();//监听
        loadConditions();//保存的过滤条件
        return rootView;
    }

    private void loadConditions() {
        if (SingletonMapFilter.get3dMap().size() > 0) {
            myFilter_layout.setVisibility(View.VISIBLE);
            for (Iterator<Map.Entry<String, List<String>>> it = SingletonMapFilter.get3dMap().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();
                List<String> list = entry.getValue();
                if (list == null || list.size() == 0) continue;
                int key = Integer.valueOf(entry.getKey());
                if (key == 1) {//和值范围模式
                    sumDialog.initSums(list);
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
                } else if(key == 11){//和尾数
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

    private void initData() {
        historyDialog = new HistoryDialog(getContext(), this);
        historyDialog.setFlag(2);//setFlag为公用的弹窗
        doubleNumDialog = new DoubleNumDialog(getContext(), this);
        sumDialog = new SumDialog(getContext(), this);
        sumDialog.setFlag(2);
        spanDialog = new SpanDialog(getContext(), this);
        spanDialog.setFlag(2);
        bigDialog = new D3BigDialog(getContext(), this);
        bigDialog.setFlag(2);
        oddDialog = new D3OddDialog(getContext(), this);
        bigDialog.setFlag(2);
        divideDialog = new DivideDialog(getContext(), this);
        divideDialog.setFlag(2);
        sumMantissaDialog =new SumMantissaDialog(getContext(), this);
        //选中的彩票
        SwipeMenuListView listView = (SwipeMenuListView) findViewById(R.id.lottery_list);
        lotteryAdapter = new Filter3DLotteryAdapter();
        listView.setAdapter(lotteryAdapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {//跳转编辑
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(i);//获得数据
            List<String> list = entry.getValue();//截取key
            int keyNum = CommonUtil.parseInt(CommonUtil.cutKey(entry.getKey()));//截取后的key
            ContextHelper.getActivity(Select3DNumActivity.class).editData(list, entry.getKey(), keyNum);
        });
        listView.setOnMenuItemClickListener((position, menu, index) -> {//删除
            Map.Entry<String, List<String>> entry = lotteryAdapter.getItem(position);
            SingletonMap3D.removeMap(entry.getKey());
            lotteryAdapter.notifyDataSetChanged();
            return false;
        });
        listView.setMenuCreator(menu -> menu.addMenuItem(SwipeMenuHelper.generateDeleteItem()));
        //选中的条件
        SwipeMenuListView conList = (SwipeMenuListView) findViewById(R.id.condition_list);
        conditionAdapter = new Filter3DConditionAdapter();
        conList.setAdapter(conditionAdapter);//条件
        conList.setOnItemClickListener((adapterView, view1, i, l) -> conditionAdapter.getItem(i).show());
        conList.setOnMenuItemClickListener((position, menu, index) -> {//删除操作
            int type = conditionAdapter.getItemViewType(position);
            TextView[] alf_tvs = {histortALF_tv, doubleNumALF_tv, sumNumALF_tv, spanALF_tv, daxiaoALF_tv, jiouALF_tv, chu3yuALF_tv, mantissaALF_tv};
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
    }

    public void reloadData() {
        lotteryAdapter.notifyDataSetChanged();
    }
    private void initListener() {
        histortALF_tv.setOnClickListener(view -> historyDialog.show());
        doubleNumALF_tv.setOnClickListener(view -> doubleNumDialog.show());
        sumNumALF_tv.setOnClickListener(view -> sumDialog.show());
        spanALF_tv.setOnClickListener(view -> spanDialog.show());
        daxiaoALF_tv.setOnClickListener(view -> bigDialog.show());
        jiouALF_tv.setOnClickListener(view -> oddDialog.show());
        chu3yuALF_tv.setOnClickListener(view -> divideDialog.show());
        mantissaALF_tv.setOnClickListener(view -> sumMantissaDialog.show());
        findViewById(R.id.filter_condition_btn_startFilter).setOnClickListener(view -> startFilter());
        findViewById(R.id.filter_condition_ivAddNum).setOnClickListener(view -> {
            if (lotteryAdapter.getCount() < 10) {
                ContextHelper.getActivity(Select3DNumActivity.class).setAddMode();
            } else {
                TipsToast.showTips("“我的选号”已满，最多10组号码");
            }
        });
    }

    private void initView() {
        histortALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_historyNum);
        doubleNumALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_doubleNum);
        sumNumALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_sumNum);
        spanALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_span);
        daxiaoALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_daxiao);
        jiouALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_jiou);
        chu3yuALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_chu3yu);
        mantissaALF_tv = (TextView) findViewById(R.id.filter_condition_tvALF_mantissa);
    }

    private View findViewById(int id) {
        return rootView.findViewById(id);
    }

    public void showCondition(ConditionDialog dialog, boolean isEnable) {
        if (isEnable) {//如果启用过滤条件 展示刷新
            conditionAdapter.replaceData(dialog);
            myFilter_layout.setVisibility(View.VISIBLE);
        } else {
            conditionAdapter.removeData(dialog);
        }
    }

    @Override
    public void onSubmit(DivideDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        chu3yuALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        chu3yuALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(HistoryDialog dialog) {
        myFilter_layout.setVisibility(View.VISIBLE);
        conditionAdapter.replaceData(dialog);
        histortALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
        histortALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
    }

    @Override
    public void onSubmit(SpanDialog dialog, Range range, Set<Integer> sums, boolean include) {
        boolean isEnable = dialog.isConditionEnable();
        spanALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        spanALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(SumDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        sumNumALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        sumNumALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(D3BigDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        daxiaoALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        daxiaoALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(D3OddDialog dialog) {
        boolean isEnable = dialog.isConditionEnable();
        jiouALF_tv.setBackgroundResource(isEnable ? R.mipmap.gltj_bg_guolvzhibiao_pre : R.mipmap.gltj_bg_guolvzhibiao);
        jiouALF_tv.setTextColor(isEnable ? ColorUtils.NORMAL_BLUE : ColorUtils.NORMAL_GRAY);
        showCondition(dialog, isEnable);
    }

    @Override
    public void onSubmit(DoubleNumDialog dialog) {
        myFilter_layout.setVisibility(View.VISIBLE);
        conditionAdapter.replaceData(dialog);
        doubleNumALF_tv.setBackgroundResource(R.mipmap.gltj_bg_guolvzhibiao_pre);
        doubleNumALF_tv.setTextColor(ColorUtils.NORMAL_BLUE);
    }


    @Override
    public void onSubmit(SumMantissaDialog dialog, Range range, Set<Integer> sums, boolean include) {
        boolean isEnable = dialog.isConditionEnable();
        mantissaALF_tv.setBackgroundResource(isEnable?R.mipmap.gltj_bg_guolvzhibiao_pre:R.mipmap.gltj_bg_guolvzhibiao);
        mantissaALF_tv.setTextColor(isEnable?ColorUtils.NORMAL_BLUE:ColorUtils.NORMAL_GRAY);
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
        if(lotteryAdapter.getCount()>0)isZhiXuan=(lotteryAdapter.getItemViewType(0)==0||lotteryAdapter.getItemViewType(0)==1);
        if (historyDialog.isConditionEnable()) {//满足过滤条件
            if(isZhiXuan){
                builder.addFilter(new HistoryFilter(historyDialog.isChecked(), historyDialog.getList()));//判断排除包含和单式复式胆拖
            }else{
                builder.addFilter(new DisOrderHistoryFilter(historyDialog.isChecked(), historyDialog.getList()));//判断排除包含和单式复式胆拖
            }
        }
        if (doubleNumDialog.isConditionEnable()) {
            builder.addFilter(new DoubleNumFilter(doubleNumDialog.isChecked(), doubleNumDialog.getList()));
        }
        if (sumDialog.isConditionEnable()) {
            builder.addFilter(new SumFilter(sumDialog.isChecked(), sumDialog.getRange(), sumDialog.getSums()));
        }
        if (spanDialog.isConditionEnable()) {
            builder.addFilter(new RangeFilter(spanDialog.isChecked(), spanDialog.getRange(), spanDialog.getSums()));
        }
        if (bigDialog.isConditionEnable()) {
            String[] patterns = bigDialog.getPatterns();
            List<byte[]> pattern = new ArrayList<>();
            for (int i = 0; i < patterns.length; i++) {
                pattern.add(bigDialog.getPattern(patterns[i]));
            }//添加多个大小形态条件 同一个指标过滤判断 为或,有一个满足即可
            builder.addFilter(new D3BigSmallFilter(bigDialog.isChecked(), pattern, bigDialog.isChecked2(),3, (byte) 5, (byte) 0, (byte) 3, bigDialog.getNums()));
        }
        if (oddDialog.isConditionEnable()) {
            String[] patterns = oddDialog.getPatterns();
            List<byte[]> pattern = new ArrayList<>();
            for (int i = 0; i < patterns.length; i++) {
                pattern.add(oddDialog.getPattern(patterns[i]));
            }
            builder.addFilter(new D3OddEvenFilter(oddDialog.isChecked(), pattern, oddDialog.isChecked2(),2, oddDialog.getNums()));
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
            for (Iterator<Map.Entry<String, List<String>>> it = SingletonMap3D.getMap().entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, List<String>> entry = it.next();
                List<String> list = entry.getValue();
                if (list == null || list.size() == 0) continue;
                //截取key
                int key = Integer.valueOf(CommonUtil.cutKey(entry.getKey()));
                if (key < 3000) { //直选
                    isZhiXuan = true;
                    if (list.size() == 3) {//直选单式
                        byte[] single = new byte[3];
                        int i = 0;
                        for (String num : list) {
                            byte b = Byte.parseByte(num);
                            single[i++] = b;
                        }
                        data_total.add(single);//添加一注单式

                    } else {//直选定位
                        byte[] nums = new byte[list.size()];
                        int i = 0, j = 0, k = 0;
                        for (String num : list) {
                            byte b = Byte.parseByte(num);
                            nums[i++] = b < 50 ? b : (b < 100 ? (byte) (b - 50) : (byte) (b - 100));
                            j = (j == 0 && b >= 50) ? (i - 1) : j;
                            k = (k == 0 && b >= 100) ? (i - 1) : k;
                        }
                        byte[] bai = new byte[j];
                        byte[] shi = new byte[k - j];
                        byte[] ge = new byte[i - k];
                        System.arraycopy(nums, 0, bai, 0, bai.length);
                        System.arraycopy(nums, j, shi, 0, shi.length);
                        System.arraycopy(nums, k, ge, 0, ge.length);
                        List<byte[]> beforeCombine = new ArrayList<byte[]>();
                        beforeCombine.add(bai);
                        beforeCombine.add(shi);
                        beforeCombine.add(ge);
                        List<byte[]> data = LotteryHelper.generateLotteryForPermutation(beforeCombine);
                        data_total.addAll(data);
                    }
                } else if (key > 3000 && key < 4000) { //组3复式
                    isZhiXuan = false;
                    byte[] nums = new byte[list.size()];
                    int i = 0;
                    for (String num : list) {
                        byte b = Byte.parseByte(num);
                        nums[i++] = b;
                    }
                    List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(nums, 2);
                    data_total.addAll(LotteryHelper.setZuxuanSameData(data));//添加组3复式
                } else if (key > 4000 && key < 6000) {//组3胆拖
                    isZhiXuan = false;
                    byte[] nums = new byte[list.size()];
                    int i = 0, j = 0;
                    for (String num : list) {
                        byte b = Byte.parseByte(num);
                        nums[i++] = b >= 50 ? (byte) (b - 50) : b;
                        j = (j == 0 && b >= 50) ? (i - 1) : j;
                    }
                    byte[] dan = new byte[j];
                    byte[] tuo = new byte[i - j];
                    System.arraycopy(nums, 0, dan, 0, dan.length);
                    System.arraycopy(nums, j, tuo, 0, tuo.length);
                    List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(tuo, 1);//排列组合
                    List<byte[]> calcData = LotteryHelper.setZuxuanDanData(data, dan, j, i - j);//添加胆拖
                    data_total.addAll(LotteryHelper.setZuxuanSameData(calcData));//添加重复号码
                } else if (key > 6000 && key < 7000) {//组6普通
                    isZhiXuan = false;
                    byte[] nums = new byte[list.size()];
                    int i = 0;
                    for (String num : list) {
                        byte b = Byte.parseByte(num);
                        nums[i++] = b;
                    }
                    if (list.size() == 3) {//组6单式
                        data_total.add(nums);//添加一注单式
                    } else {
                        List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(nums, 3);
                        data_total.addAll(data);//添加组6复式
                    }
                } else {//组6胆拖
                    isZhiXuan = false;
                    byte[] nums = new byte[list.size()];
                    int i = 0, j = 0;
                    for (String num : list) {
                        byte b = Byte.parseByte(num);
                        nums[i++] = b >= 50 ? (byte) (b - 50) : b;
                        j = (j == 0 && b >= 50) ? (i - 1) : j;
                    }
                    byte[] dan = new byte[j];
                    byte[] tuo = new byte[i - j];
                    System.arraycopy(nums, 0, dan, 0, dan.length);
                    System.arraycopy(nums, j, tuo, 0, tuo.length);
                    //根据胆的个数 决定托的个数
                    List<byte[]> data = LotteryHelper.generateLotteryRecordsSimpleBall(tuo, 3 - j);//排列组合
                    List<byte[]> calcData = LotteryHelper.setZuxuanDanData(data, dan, j, i - j);//添加胆拖
                    data_total.addAll(calcData);//添加重复号码
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
                        intent.putExtra("flag", 2);
                        intent.putExtra("isZhixuan", isZhiXuan);
                        startActivity(intent);
                    }
                });
    }

}
