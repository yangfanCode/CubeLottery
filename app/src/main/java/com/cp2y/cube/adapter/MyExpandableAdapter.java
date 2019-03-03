package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.bean.TrendIgnore;
import com.cp2y.cube.callback.MyInterface;

import java.util.List;

/**
 * Created by admin on 2017/3/13.
 */
public class MyExpandableAdapter extends BaseAdapter{
    private int flag=-1;//0,双色球,1,大乐透
    private MyInterface.TrendItemClick click;
    private List<TrendIgnore>list;
    private Context context;
    public void setClick(MyInterface.TrendItemClick click){
        this.click=click;
    }
    public MyExpandableAdapter(Context context,List<TrendIgnore> list,int flag){
        this.context=context;
        this.list=list;
        this.flag=flag;
    }
    //遗漏按钮点击事件
    public View.OnClickListener listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            click.click(v,flag);
        }
    };
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(context).inflate(R.layout.expandable_item,parent,false);
        TextView tv_trend= (TextView) view.findViewById(R.id.expandable_lv_item_tvTrend);
        TextView tv_ignore= (TextView) view.findViewById(R.id.expandable_lv_item_tvIgnore);
        if("".equals(list.get(position).getIgnore())){
            tv_ignore.setBackground(null);
        }else{
            tv_ignore.setBackgroundResource(R.drawable.app_expandable);
        }
        tv_trend.setText(list.get(position).getTrend());
        tv_ignore.setText(list.get(position).getIgnore());
        tv_ignore.setOnClickListener(listener);//点击监听
        tv_ignore.setTag(position);//添加标记
        return view;
    }
}
