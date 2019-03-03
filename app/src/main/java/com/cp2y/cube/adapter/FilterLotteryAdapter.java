package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.MyGridView;
import com.cp2y.cube.custom.SingletonMapNomal;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by js on 2017/1/18.
 */
public class FilterLotteryAdapter extends BaseAdapter {
    /*0大乐透,1双色球*/
    private int flag=-1;
    private List<Map.Entry<String, List<String>>> entries = new ArrayList<>();
    private Context context = ContextHelper.getLastActivity();

    public void notifyDataSetChanged() {
        entries.clear();
        //处理map排序
//        TreeMap<String, List<String>> treeMap2 =new TreeMap<>(new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                //按照key排序
//                return String.valueOf(o1).compareTo(String.valueOf(o2));
//            }
//        });
//        treeMap2=(TreeMap<String, List<String>>) SingletonMapNomal.getMap();
        entries.addAll(SingletonMapNomal.getMap().entrySet());
        Collections.sort(entries, new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                String var1=o1.getKey().substring(0,o1.getKey().indexOf("_"));
                String var2=o2.getKey().substring(0,o2.getKey().indexOf("_"));
                return Integer.parseInt(var1)<Integer.parseInt(var2)?1:-1;
            }
        });
        //倒序反转
        //Collections.reverse(entries);
        super.notifyDataSetChanged();
    }
    public void setFlag(int flag){
        this.flag=flag;
    }
    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Map.Entry<String, List<String>> getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Map.Entry<String, List<String>> entry = entries.get(position);
        if (Integer.valueOf(CommonUtil.cutKey(entry.getKey())) < 1000) {
            if (entry.getValue().size() == 7) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;//包含单式，复式，胆拖
    }

    public List<Map.Entry<String, List<String>>> getEntries(){
        return entries;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (convertView == null){
            //双色球
            if(flag==0){
                if (type == 0) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_single_mode, null);
                } else if (type == 1){
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_double_mode, null);
                } else {
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_dantuo_mode, null);
                }
            }else if(flag==1){//大乐透
                if (type == 0) {
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_lotto_single_mode, null);
                } else if (type == 1){
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_double_mode, null);
                } else {
                    convertView = LayoutInflater.from(context).inflate(R.layout.filter_lotto_dantuo_mode, null);
                }
            }

        }
        Map.Entry<String, List<String>> entry = entries.get(position);
        List<String> list = entry.getValue();
        if (type == 0) {
            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.filter_single_ball_layout);
            for (int i = 0; i < layout.getChildCount(); i++) {
                TextView tv = (TextView) layout.getChildAt(i);
                int val = Integer.valueOf(list.get(i));
                val = val > 50 ? val - 50:val;
                tv.setText(CommonUtil.preZeroForBall(val));
            }
        } else if (type == 1) {
            int pos = SingletonMapNomal.findMutiBlueIndex(list);
            MyGridView gv_red = (MyGridView) convertView.findViewById(R.id.filter_double_gv_red);
            MyGridView gv_blue = (MyGridView) convertView.findViewById(R.id.filter_double_gv_blue);
            MyFilterDoubleModeRedAdapter adapter_red = new MyFilterDoubleModeRedAdapter(list.subList(0, pos), context, R.layout.item_filter_double_redball);
            MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(list.subList(pos, list.size()), context, R.layout.item_filter_double_blueball,0);
            gv_red.setAdapter(adapter_red);
            gv_blue.setAdapter(adapter_blue);
        } else {
            MyGridView gv_dan= (MyGridView) convertView.findViewById(R.id.filter_dantuo_dan_gv);
            MyGridView gv_tuo= (MyGridView) convertView.findViewById(R.id.filter_dantuo_tuo_gv);
            MyGridView gv_lan= (MyGridView) convertView.findViewById(R.id.filter_dantuo_lan_gv);
            if(flag==0){//双色球
                int[] pos = SingletonMapNomal.findDanTuoIndex(list);
                MyFilterDoubleModeRedAdapter adapter_red_dan = new MyFilterDoubleModeRedAdapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
                MyFilterDoubleModeRedAdapter adapter_red_tuo = new MyFilterDoubleModeRedAdapter(list.subList(pos[0], pos[1]), context, R.layout.item_filter_double_redball);
                MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(list.subList(pos[1], list.size()), context, R.layout.item_filter_double_blueball,1);
                gv_dan.setAdapter(adapter_red_dan);
                gv_tuo.setAdapter(adapter_red_tuo);
                gv_lan.setAdapter(adapter_blue);
            }else if(flag==1){//大乐透
                int[] pos = SingletonMapNomal.findLottoDanTuoIndex(list);
                MyGridView gv_lan_houTuo= (MyGridView) convertView.findViewById(R.id.filter_dantuo_lan_houTuo_gv);
                MyFilterDoubleModeRedAdapter adapter_red_dan = new MyFilterDoubleModeRedAdapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
                MyFilterDoubleModeRedAdapter adapter_red_tuo = new MyFilterDoubleModeRedAdapter(list.subList(pos[0], pos[1]), context, R.layout.item_filter_double_redball);
                MyFilterDoubleModeBlueAdapter adapter_blue = new MyFilterDoubleModeBlueAdapter(list.subList(pos[1], pos[2]), context, R.layout.item_filter_double_blueball,1);
                MyFilterDoubleModeBlueAdapter adapter_blue_houTuo = new MyFilterDoubleModeBlueAdapter(list.subList(pos[2], list.size()), context, R.layout.item_filter_double_blueball,2);
                gv_dan.setAdapter(adapter_red_dan);
                gv_tuo.setAdapter(adapter_red_tuo);
                gv_lan.setAdapter(adapter_blue);
                gv_lan_houTuo.setAdapter(adapter_blue_houTuo);
            }

        }
        return convertView;
    }

}
