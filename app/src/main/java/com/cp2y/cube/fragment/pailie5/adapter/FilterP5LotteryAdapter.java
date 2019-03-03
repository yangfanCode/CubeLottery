package com.cp2y.cube.fragment.pailie5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.SingletonMapP5;
import com.cp2y.cube.helper.ContextHelper;
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
public class FilterP5LotteryAdapter extends BaseAdapter {
    //把map的键值对赋值到list 通过position拿到数据
    private List<Map.Entry<String, List<String>>> entries = new ArrayList<>();
    private Context context = ContextHelper.getLastActivity();
    private LayoutInflater inflater = LayoutInflater.from(context);

    public void notifyDataSetChanged() {
        entries.clear();
        entries.addAll(SingletonMapP5.getMap().entrySet());//键值对赋值
        Collections.sort(entries, new Comparator<Map.Entry<String, List<String>>>() {
            @Override
            public int compare(Map.Entry<String, List<String>> o1, Map.Entry<String, List<String>> o2) {
                String var1 = o1.getKey().substring(0, o1.getKey().indexOf("_"));
                String var2 = o2.getKey().substring(0, o2.getKey().indexOf("_"));
                return Integer.parseInt(var1) < Integer.parseInt(var2) ? 1 : -1;
            }
        });
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
        //直选
        if (entry.getValue().size() == 5) {
            return 0;//直选单式
        } else {
            //直选复式
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public List<Map.Entry<String, List<String>>> getEntries(){
        return entries;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == 0) {//直选单式
                convertView = inflater.inflate(R.layout.item_p5_single, viewGroup, false);
            } else if (type == 1) {//直选复式
                convertView = inflater.inflate(R.layout.item_p5_muti, viewGroup, false);
            }
        }
        Map.Entry<String, List<String>> entry = entries.get(position);
        List<String> list = entry.getValue();
        if (type == 0) {//单式
            TextView tv_wan = (TextView) convertView.findViewById(R.id.p5_single_wan);
            TextView tv_qian = (TextView) convertView.findViewById(R.id.p5_single_qian);
            TextView tv_bai = (TextView) convertView.findViewById(R.id.p5_single_bai);
            TextView tv_shi = (TextView) convertView.findViewById(R.id.p5_single_shi);
            TextView tv_ge = (TextView) convertView.findViewById(R.id.p5_single_ge);
            tv_wan.setText(list.get(0));//万位
            tv_qian.setText(list.get(1));//千位
            tv_bai.setText(list.get(2));//百位
            tv_shi.setText(list.get(3));//十位
            tv_ge.setText(list.get(4));//个位
        } else if (type == 1) {//复式
            int pos[] = SingletonMapP5.findMutiIndex(list);
            TouchLessGridView gv_wan = (TouchLessGridView) convertView.findViewById(R.id.item_p5_muti_wan_gv);
            TouchLessGridView gv_qian = (TouchLessGridView) convertView.findViewById(R.id.item_p5_muti_qian_gv);
            TouchLessGridView gv_bai = (TouchLessGridView) convertView.findViewById(R.id.item_p5_muti_bai_gv);
            TouchLessGridView gv_shi = (TouchLessGridView) convertView.findViewById(R.id.item_p5_muti_shi_gv);
            TouchLessGridView gv_ge = (TouchLessGridView) convertView.findViewById(R.id.item_p5_muti_ge_gv);
            MyFilterP5Adapter adapter_wan = new MyFilterP5Adapter(list.subList(0, pos[0]), context, R.layout.item_filter_double_redball);
            MyFilterP5Adapter adapter_qian = new MyFilterP5Adapter(list.subList(pos[0], pos[1]), context, R.layout.item_filter_double_redball);
            MyFilterP5Adapter adapter_bai = new MyFilterP5Adapter(list.subList(pos[1], pos[2]), context, R.layout.item_filter_double_redball);
            MyFilterP5Adapter adapter_shi = new MyFilterP5Adapter(list.subList(pos[2], pos[3]), context, R.layout.item_filter_double_redball);
            MyFilterP5Adapter adapter_ge = new MyFilterP5Adapter(list.subList(pos[3], list.size()), context, R.layout.item_filter_double_redball);
            gv_wan.setAdapter(adapter_wan);
            gv_qian.setAdapter(adapter_qian);
            gv_bai.setAdapter(adapter_bai);
            gv_shi.setAdapter(adapter_shi);
            gv_ge.setAdapter(adapter_ge);
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
            int nums = Integer.parseInt(num);
            if (nums >= 50 && nums < 100) {
                ball.setText(String.valueOf(nums - 50));
            } else if (nums >= 100) {
                ball.setText(String.valueOf(nums - 100));
            } else {
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




