package com.cp2y.cube.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.LotteryHistoryModel;
import com.cp2y.cube.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/8/11.
 */
public class FastHistoryAdapter extends BaseAdapter {
    private List<LotteryHistoryModel.HistoryLottery> list=new ArrayList<>();
    private LayoutInflater inflater;
    private Context context;
    private boolean isQuick;
    public FastHistoryAdapter(Context context,boolean isQuick){
        this.context=context;
        this.isQuick=isQuick;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void loadData(List<LotteryHistoryModel.HistoryLottery> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.fast_history_item,parent,false);
        }
        TextView issue= (TextView) convertView.findViewById(R.id.fast_history_issue);
        LinearLayout nums_ll= (LinearLayout) convertView.findViewById(R.id.fast_history_nums_ll);
        LinearLayout ll= (LinearLayout) convertView.findViewById(R.id.fast_history_ll);
        ll.setBackgroundColor(position%2==0?ColorUtils.BLUE_PURPLE:ColorUtils.WHITE);
        issue.setText(list.get(position).getIssue());
        nums_ll.removeAllViews();
        if(isQuick){//快开彩先蓝球再红球
            blueNum(position,nums_ll);
            redNum(position,nums_ll);
        }else{//非快开彩先红球再蓝球
            redNum(position,nums_ll);
            blueNum(position,nums_ll);
        }
        return convertView;
    }
    private void blueNum(int position,LinearLayout nums_ll){
        if(!TextUtils.isEmpty(list.get(position).getDrawBlueNumber())&&!list.get(position).getDrawBlueNumber().equals("false")){//蓝球
            String[] blues=list.get(position).getDrawBlueNumber().split(",");
            for(int i=0,size=blues.length;i<size;i++){
                TextView tv= new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.app_tvNomal_size));
                tv.setTextColor(ColorUtils.MID_BLUE);
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,15,0);
                tv.setLayoutParams(params);
                tv.setText(blues[i]);
                nums_ll.addView(tv);
            }
        }
    }
    private void redNum(int position,LinearLayout nums_ll){
        if(!TextUtils.isEmpty(list.get(position).getDrawRedNumber())&&!list.get(position).getDrawRedNumber().equals("false")){//红球
            String[] reds=list.get(position).getDrawRedNumber().split(",");
            for(int i=0,size=reds.length;i<size;i++){
                TextView tv= new TextView(context);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.app_tvNomal_size));
                tv.setTextColor(ColorUtils.NORMAL_GRAY);
                LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,10,0);
                tv.setLayoutParams(params);
                tv.setText(reds[i]);
                nums_ll.addView(tv);
            }
        }
    }
}
