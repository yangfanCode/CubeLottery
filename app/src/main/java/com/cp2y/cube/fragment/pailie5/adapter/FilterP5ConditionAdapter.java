package com.cp2y.cube.fragment.pailie5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.MyFilterConditionGvAdapter;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.dialog.BigDialog;
import com.cp2y.cube.dialog.ConditionDialog;
import com.cp2y.cube.dialog.DivideDialog;
import com.cp2y.cube.dialog.HistoryDialog;
import com.cp2y.cube.dialog.SumMantissaDialog;
import com.cp2y.cube.dialog.OddDialog;
import com.cp2y.cube.dialog.SpanDialog;
import com.cp2y.cube.dialog.SumDialog;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.struct.Range;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by js on 2017/1/18.
 */
public class FilterP5ConditionAdapter extends BaseAdapter {

    private List<ConditionDialog> data = new ArrayList<>();
    private Context context = ContextHelper.getLastActivity();

    public void replaceData(ConditionDialog dialog) {
        //获得位置   编辑
        if(!data.contains(dialog)){
            data.remove(dialog);
            data.add(dialog);
        }else{
            //获得位置   编辑条件
            int pos=CommonUtil.getListPos(data,dialog);
            data.set(pos,dialog);
        }
        notifyDataSetChanged();
    }

    public void removeData(ConditionDialog dialog) {
        data.remove(dialog);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ConditionDialog getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ConditionDialog dialog = data.get(position);
        if (dialog instanceof HistoryDialog) {
            return 0;
        } else if (dialog instanceof SumDialog) {
            return 1;
        } else if (dialog instanceof SpanDialog) {
            return 2;
        } else if (dialog instanceof BigDialog) {
            return 3;
        } else if (dialog instanceof OddDialog) {
            return 4;
        } else if (dialog instanceof DivideDialog){
            return 5;
        } else {
            return 6;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == 0) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_history, null);
            } else if (type == 1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_sum, null);
            } else if (type == 2) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_span, null);
            } else if (type == 3) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_big, null);
            } else if (type == 4) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_odd, null);
            } else if (type == 5){
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_divide, null);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_condition_mantissa, null);
            }
        }
        if (type == 0) {//历史
            HistoryDialog dialog = (HistoryDialog) data.get(position);
            TextView tv_history_switch = (TextView) convertView.findViewById(R.id.filter_condition_paichu_tv);
            tv_history_switch.setText(dialog.isChecked()?"包含":"排除");
            TextView historyNum_tv_count = (TextView) convertView.findViewById(R.id.filter_condition_historyNum_tv);
            historyNum_tv_count.setText("近" + dialog.getPeriod() + "期开奖号码");
        } else if (type == 1) {//和值
            SumDialog dialog = (SumDialog) data.get(position);
            TextView sumnum_range_tv = (TextView) convertView.findViewById(R.id.filter_condition_sumnum_tv_range);
            Range range = dialog.getRange();
            sumnum_range_tv.setVisibility(range.start !=-1?View.VISIBLE:View.GONE);
            sumnum_range_tv.setText(range.start +"-"+range.end);
            TextView tv_sum_switch = (TextView) convertView.findViewById(R.id.filter_condition_include1_tv);
            tv_sum_switch.setText(dialog.isChecked()?"包含":"排除");
            MyGridView sumnum_click_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_sumnum_gv_click);
            sumnum_click_gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
            MyFilterConditionGvAdapter adapter_click = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_num_smallgv);
            sumnum_click_gv.setAdapter(adapter_click);
            String sums = dialog.getSumStr();
            adapter_click.reloadData(TextUtils.isEmpty(sums)?new ArrayList<>(): Arrays.asList(sums.replace("，",",").split(",")));
        } else if (type == 2) {
            SpanDialog dialog = (SpanDialog) data.get(position);
            TextView span_range_tv = (TextView) convertView.findViewById(R.id.filter_condition_span_tv_range);
            Range range = dialog.getRange();
            span_range_tv.setVisibility(range.start !=-1?View.VISIBLE:View.GONE);
            span_range_tv.setText(range.start +"-"+range.end);
            MyGridView span_singel_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_span_gv_single);
            MyFilterConditionGvAdapter adapter_span_singel = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_num_smallgv);
            span_singel_gv.setAdapter(adapter_span_singel);
            //排序
            List<String>list_collection=new ArrayList<>();
            for (Integer sum:  dialog.getSums()) {
                list_collection.add(String.valueOf(sum));
            }
            CommonUtil.SortCollection(list_collection);
            Set<String> list = new LinkedHashSet<>();
            list.addAll(list_collection);
            adapter_span_singel.reloadData(list);
            TextView tv_span_switch = (TextView) convertView.findViewById(R.id.filter_condition_include2_tv);
            tv_span_switch.setText(dialog.isChecked()?"包含":"排除");
        } else if (type == 3) {
            BigDialog dialog = (BigDialog) data.get(position);
            RelativeLayout daxiaoBi_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_daxiaobi_layout);
            Set<String> sums1 = dialog.getSums1();
            daxiaoBi_layout.setVisibility(sums1.size() > 0 ?View.VISIBLE:View.GONE);
            MyGridView daxiaobi_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_daxiaobi_gv);
            MyFilterConditionGvAdapter adapter_daxiaobi = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_gv);
            daxiaobi_gv.setAdapter(adapter_daxiaobi);
            adapter_daxiaobi.reloadData(sums1);
            RelativeLayout daxiaoXingTai_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_daxiaoXingTai_layout);
            Map<Integer, String> sums2 = dialog.getSums2();
            daxiaoXingTai_layout.setVisibility(sums2.size() > 0 ? View.VISIBLE:View.GONE);
            MyFilterConditionGvAdapter adapter_daxiao = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_gv);
            MyGridView daxiao_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_daxiao_gv);
            daxiao_gv.setAdapter(adapter_daxiao);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String val = sums2.get(i);
                list.add(val != null?val:"大小");
            }
            adapter_daxiao.reloadData(list);
            TextView tv_daxiaobi_switch = (TextView) convertView.findViewById(R.id.filter_condition_include3_tv);
            TextView tv_daxiaoxingtai_switch = (TextView) convertView.findViewById(R.id.filter_condition_include4_tv);
            tv_daxiaobi_switch.setText(dialog.isChecked()?"包含":"排除");
            tv_daxiaoxingtai_switch.setText(dialog.isChecked2()?"包含":"排除");
        } else if (type == 4) {
            OddDialog dialog = (OddDialog) data.get(position);
            RelativeLayout jioubi_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_jioubi_layout);
            Set<String> sums1 = dialog.getSums1();
            jioubi_layout.setVisibility(sums1.size() > 0 ?View.VISIBLE:View.GONE);
            MyFilterConditionGvAdapter adapter_jioubi = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_gv);
            MyGridView jioubi_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_jioubi_gv);
            jioubi_gv.setAdapter(adapter_jioubi);
            adapter_jioubi.reloadData(sums1);
            RelativeLayout jiouXingTai_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_jiouXingTai_layout);
            Map<Integer, String> sums2 = dialog.getSums2();
            jiouXingTai_layout.setVisibility(sums2.size() > 0 ? View.VISIBLE:View.GONE);
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String val = sums2.get(i);
                list.add(val != null?val:"奇偶");
            }
            MyGridView jiouXingtai_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_jiouXingTai_gv);
            MyFilterConditionGvAdapter adapter_jiouXingTai = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_gv);
            jiouXingtai_gv.setAdapter(adapter_jiouXingTai);
            adapter_jiouXingTai.reloadData(list);
            TextView tv_jioubi_switch = (TextView) convertView.findViewById(R.id.filter_condition_include5_tv);
            TextView tv_jiouxingtai_switch = (TextView) convertView.findViewById(R.id.filter_condition_include6_tv);
            tv_jioubi_switch.setText(dialog.isChecked()?"包含":"排除");
            tv_jiouxingtai_switch.setText(dialog.isChecked2()?"包含":"排除");
        } else if (type==5){
            DivideDialog dialog = (DivideDialog) data.get(position);
            MyGridView chu3yu_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_chu3yu_gv);
            MyFilterConditionGvAdapter adapter_chu3 = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_gv);
            chu3yu_gv.setAdapter(adapter_chu3);
            Map<Integer, String> sums = dialog.getSums();
            List<String> list = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                String val = sums.get(i);
                list.add(val != null?val:"012");
            }
            adapter_chu3.reloadData(list);
            RelativeLayout chu3yuNum_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_chu3yuNum_layout);
            Range range0 = dialog.getRange0();
            Range range1 = dialog.getRange1();
            chu3yuNum_layout.setVisibility(range0==null&&range1==null?View.GONE:View.VISIBLE);
            TextView tv_chu3Yu0 = (TextView) convertView.findViewById(R.id.filter_condition_chu3yuNum_tvyu0);
            TextView tv_chu3Yu1 = (TextView) convertView.findViewById(R.id.filter_condition_chu3yuNum_tvyu1);
            //除3余余0显示隐藏
            if (range0 != null) {
                tv_chu3Yu0.setText("余(0)" + range0.start + "-" + range0.end+"个");
                tv_chu3Yu0.setVisibility(View.VISIBLE);
            } else {
                tv_chu3Yu0.setVisibility(View.GONE);
            }
            //除3余余1显示隐藏
            if (range1 != null) {
                tv_chu3Yu1.setText("余(1)" + range1.start + "-" + range1.end+"个");
                tv_chu3Yu1.setVisibility(View.VISIBLE);
            } else {
                tv_chu3Yu1.setVisibility(View.GONE);
            }
            RelativeLayout chu3yuXingTai_layout = (RelativeLayout) convertView.findViewById(R.id.filter_condition_chu3yuXingTai_layout);
            chu3yuXingTai_layout.setVisibility(sums.size()>0?View.VISIBLE:View.GONE);
            TextView tv_chu3yuNum_switch = (TextView) convertView.findViewById(R.id.filter_condition_include8_tv);
            TextView tv_chu3yuxingtai_switch = (TextView) convertView.findViewById(R.id.filter_condition_include7_tv);
            tv_chu3yuNum_switch.setText(dialog.isChecked()?"包含":"排除");
            tv_chu3yuxingtai_switch.setText(dialog.isChecked2()?"包含":"排除");
        } else {//和尾数
            SumMantissaDialog dialog = (SumMantissaDialog) data.get(position);
            TextView mantissa_range_tv = (TextView) convertView.findViewById(R.id.filter_condition_mantissa_tv_range);
            Range range = dialog.getRange();
            mantissa_range_tv.setVisibility(range.start !=-1?View.VISIBLE:View.GONE);
            mantissa_range_tv.setText(range.start +"-"+range.end);
            MyGridView mantissa_singel_gv = (MyGridView) convertView.findViewById(R.id.filter_condition_mantissa_gv_single);
            MyFilterConditionGvAdapter adapter_mantissa_singel = new MyFilterConditionGvAdapter(context, R.layout.item_filter_condition_num_smallgv);
            mantissa_singel_gv.setAdapter(adapter_mantissa_singel);
            //排序
            List<String>list_collection=new ArrayList<>();
            for (Integer sum:  dialog.getSums()) {
                list_collection.add(String.valueOf(sum));
            }
            CommonUtil.SortCollection(list_collection);
            Set<String> list = new LinkedHashSet<>();
            list.addAll(list_collection);
            adapter_mantissa_singel.reloadData(list);
            TextView tv_span_switch = (TextView) convertView.findViewById(R.id.filter_condition_include2_tv);
            tv_span_switch.setText(dialog.isChecked()?"包含":"排除");
        }
        return convertView;
    }

}
