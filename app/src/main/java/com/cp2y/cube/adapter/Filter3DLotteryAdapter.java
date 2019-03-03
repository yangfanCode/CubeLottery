package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.SingletonMap3D;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.widgets.TouchLessGridView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by js on 2017/1/18.
 */
public class Filter3DLotteryAdapter extends BaseAdapter {
    //把map的键值对赋值到list 通过position拿到数据
    private List<Map.Entry<String, List<String>>> entries = new ArrayList<>();
    private Context context = ContextHelper.getLastActivity();
    private LayoutInflater inflater=LayoutInflater.from(context);
    public void notifyDataSetChanged() {
        entries.clear();
        //处理map排序
//        TreeMap<String, List<String>> treeMap2 =new TreeMap<>(new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                //按照key排序
//                String var1=String.valueOf(o1).substring(0,String.valueOf(o1).indexOf("_"));
//                String var2=String.valueOf(o2).substring(0,String.valueOf(o2).indexOf("_"));
//                //return var1.compareTo(var2);
//                return Integer.parseInt(var1)<Integer.parseInt(var2)?1:-1;
//            }
//        });
//        treeMap2=(TreeMap<String, List<String>>) SingletonMap3D.getMap();
        entries.addAll(SingletonMap3D.getMap().entrySet());//键值对赋值
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
        int key=Integer.valueOf(CommonUtil.cutKey(entry.getKey()));
        if ( key< 3000) {
            //直选
            if (entry.getValue().size() == 3) {
                return 0;//直选单式
            } else {
                //直选定位
                return 1;
            }
        } else if(key> 3000&&key< 4000){
            //组3 复式
            return 2;
        }else if(key > 4000&&key < 6000){
            //组3 胆拖
            return 3;
        }else if(key > 6000&&key < 7000){
            if(entry.getValue().size() == 3){
                //组6单式
                return 4;
            }else{
                //组6复式
                return 5;
            }
        }else{
            //组6胆拖
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
                if (type == 0) {//直选单式
                    convertView = inflater.inflate(R.layout.item_zhixuan_single, viewGroup, false);
                } else if (type == 1) {//直选定位
                    convertView = inflater.inflate(R.layout.item_zhixuan_location, viewGroup, false);
                } else if (type == 2) {//组3复式
                    convertView = inflater.inflate(R.layout.item_zu3_muti, viewGroup, false);
                } else if (type == 3) {//组3胆拖
                    convertView = inflater.inflate(R.layout.item_zu3_dantuo, viewGroup, false);
                } else if (type == 4) {//组6单式
                    convertView = inflater.inflate(R.layout.item_zu6_single, viewGroup, false);
                } else if (type == 5) {//组6复式
                    convertView = inflater.inflate(R.layout.item_zu6_muti, viewGroup, false);
                } else if (type == 6) {//组6胆拖
                    convertView = inflater.inflate(R.layout.item_zu6_dantuo, viewGroup, false);
                }

            }
            Map.Entry<String, List<String>> entry = entries.get(position);
            List<String> list = entry.getValue();
            if (type == 0) {//直选单式
                TextView tv_bai = (TextView) convertView.findViewById(R.id.zhixuan_single_bai);
                TextView tv_shi = (TextView) convertView.findViewById(R.id.zhixuan_single_shi);
                TextView tv_ge = (TextView) convertView.findViewById(R.id.zhixuan_single_ge);
                tv_bai.setText(list.get(0));//百位
                tv_shi.setText(list.get(1));//十位
                tv_ge.setText(list.get(2));//个位
            } else if (type == 1) {//直选定位
                int pos[]=SingletonMap3D.findZhiXuanLocationIndex(list);
                TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_bai_gv);
                TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_shi_gv);
                TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_zhixuan_location_ge_gv);
                MyFilter3DAdapter adapter_bai = new MyFilter3DAdapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
                MyFilter3DAdapter adapter_shi = new MyFilter3DAdapter(list.subList(pos[0], pos[1]), context, R.layout.item_filter_double_redball);
                MyFilter3DAdapter adapter_ge = new MyFilter3DAdapter(list.subList(pos[1], list.size()), context, R.layout.item_filter_double_redball);
                gv_bai.setAdapter(adapter_bai);
                gv_shi.setAdapter(adapter_shi);
                gv_ge.setAdapter(adapter_ge);
            } else if(type == 2){//组3复式
                TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_zu3_muti_gv);
                MyFilter3DAdapter adapter_zu3_muti = new MyFilter3DAdapter(list, context, R.layout.item_filter_double_redball);
                gv_bai.setAdapter(adapter_zu3_muti);
            }else if(type == 3){//组3胆拖
                int pos[]=SingletonMap3D.findZhiXuanLocationIndex(list);
                TouchLessGridView gv_dan = (TouchLessGridView) convertView.findViewById(R.id.item_zu3_dan_gv);
                TouchLessGridView gv_tuo = (TouchLessGridView) convertView.findViewById(R.id.item_zu3_tuo_gv);
                MyFilter3DAdapter adapter_zu3_dan = new MyFilter3DAdapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
                MyFilter3DAdapter adapter_zu3_tuo = new MyFilter3DAdapter(list.subList(pos[0], list.size()), context, R.layout.item_filter_double_redball);
                gv_dan.setAdapter(adapter_zu3_dan);
                gv_tuo.setAdapter(adapter_zu3_tuo);
            }else if(type == 4){//组6单式
                TextView tv_bai = (TextView) convertView.findViewById(R.id.zu6_single_bai);
                TextView tv_shi = (TextView) convertView.findViewById(R.id.zu6_single_shi);
                TextView tv_ge = (TextView) convertView.findViewById(R.id.zu6_single_ge);
                tv_bai.setText(list.get(0));//百位
                tv_shi.setText(list.get(1));//十位
                tv_ge.setText(list.get(2));//个位
            }else if(type == 5){//组6复式
                TouchLessGridView gv_dan = (TouchLessGridView) convertView.findViewById(R.id.item_zu6_muti_gv);
                MyFilter3DAdapter adapter_zu6_dan = new MyFilter3DAdapter(list, context, R.layout.item_filter_double_redball);
                gv_dan.setAdapter(adapter_zu6_dan);
            }else if(type == 6){//组6胆拖
                int pos[]=SingletonMap3D.findZhiXuanLocationIndex(list);
                TouchLessGridView gv_dan = (TouchLessGridView) convertView.findViewById(R.id.item_zu6_dan_gv);
                TouchLessGridView gv_tuo = (TouchLessGridView) convertView.findViewById(R.id.item_zu6_tuo_gv);
                MyFilter3DAdapter adapter_zu6_dan = new MyFilter3DAdapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
                MyFilter3DAdapter adapter_zu6_tuo = new MyFilter3DAdapter(list.subList(pos[0], list.size()), context, R.layout.item_filter_double_redball);
                gv_dan.setAdapter(adapter_zu6_dan);
                gv_tuo.setAdapter(adapter_zu6_tuo);
            }
            return convertView;
        }
    /**
     * 添加复式和胆拖布局 flowlayout 添加号码数量多会卡
     */
    private void addMutiData(FlowLayout mutiLayout, List<String> numArr) {
        mutiLayout.removeAllViews();
        for (String num : numArr) {
            TextView ball = (TextView) inflater.inflate(R.layout.red_ball_text, null);
            int nums=Integer.parseInt(num);
            if(nums>=50&&nums<100){
                ball.setText(String.valueOf(nums-50));
            }else if(nums>=100){
                ball.setText(String.valueOf(nums-100));
            }else{
                ball.setText(String.valueOf(nums));
            }
            mutiLayout.addView(ball);
            FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) ball.getLayoutParams();
           //最后一行不设置下边距
            lp.rightMargin = DisplayUtil.dip2px(4f);
            lp.bottomMargin = DisplayUtil.dip2px(10f);
            ball.setLayoutParams(lp);
        }
    }
}




