package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.CommonUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/23.
 */
public class MyFilterResultAdapter extends BaseAdapter {
    private List<byte[]>list;
    private LayoutInflater inflater;
    private Context context;
    private Set<Integer>sevenSet=new HashSet<Integer>(){{add(0);add(1);}};//七个球的彩种
    private Set<Integer>threeSet=new HashSet<Integer>(){{add(2);add(3);add(5);}};//三个球的彩种
    private Set<Integer>fiveSet=new HashSet<Integer>(){{add(4);}};//五个球的彩种
    private Set<Integer>twoSet=new HashSet<Integer>(){{add(7);}};//两个球的彩种
    private Set<Integer>containsZero=new HashSet<Integer>(){{add(0);add(1);}};//是否需要拼0的彩种

    /*0双色球,1大乐透*2,福彩3D,3排列3,4排列5,5重庆时时彩3星,6重庆时时彩5星,7重庆时时彩2星*/
    private int flag=-1;
    public MyFilterResultAdapter(List<byte[]> list, Context context,int flag) {
        this.list=list;
        this.flag=flag;
        this.context=context;
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

    @Override
    public int getItemViewType(int position) {
        if(sevenSet.contains(flag)){
            return 0;
        }else if(threeSet.contains(flag)){
            return 1;
        }else if(twoSet.contains(flag)){
            return 3;
        }else{
            return 2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);
        if(convertView==null){
            if(type==0){//双色球大乐透
                convertView=inflater.inflate(R.layout.item_filterresult_lv,parent,false);
            }else if(type==1){//福彩3D 排列3
                convertView=inflater.inflate(R.layout.item_d3filterresult_lv,parent,false);
            }else if(type==2){//排列5
                convertView=inflater.inflate(R.layout.item_p5filterresult_lv,parent,false);
            }else{//2星
                convertView=inflater.inflate(R.layout.item_cq2filterresult_lv,parent,false);
            }
        }
        //最少2个球
        TextView tv_result_1= (TextView) convertView.findViewById(R.id.filter_result_tv1);
        TextView tv_result_2= (TextView) convertView.findViewById(R.id.filter_result_tv2);
        tv_result_1.setText(containsZero.contains(flag)?CommonUtil.preZeroForBall(list.get(position)[0]):String.valueOf(list.get(position)[0]));
        tv_result_2.setText(containsZero.contains(flag)?CommonUtil.preZeroForBall(list.get(position)[1]):String.valueOf(list.get(position)[1]));
        if(type==0){//双色球大乐透
            TextView tv_result_3= (TextView) convertView.findViewById(R.id.filter_result_tv3);
            TextView tv_result_4= (TextView) convertView.findViewById(R.id.filter_result_tv4);
            TextView tv_result_5= (TextView) convertView.findViewById(R.id.filter_result_tv5);
            TextView tv_result_6= (TextView) convertView.findViewById(R.id.filter_result_tv6);
            TextView tv_result_7= (TextView) convertView.findViewById(R.id.filter_result_tv7);
            TextView tv_result_DoubleBall= (TextView) convertView.findViewById(R.id.filter_result_tvDoubleBall);
            TextView tv_result_BigLotto= (TextView) convertView.findViewById(R.id.filter_result_tvBigLotto);
            tv_result_3.setText(CommonUtil.preZeroForBall(list.get(position)[2]));
            tv_result_4.setText(CommonUtil.preZeroForBall(list.get(position)[3]));
            tv_result_5.setText(CommonUtil.preZeroForBall(list.get(position)[4]));
            tv_result_6.setText(CommonUtil.preZeroForBall(list.get(position)[5]));
            tv_result_7.setText(CommonUtil.preZeroForBall(list.get(position)[6]));
            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(3,0,3,0);
            if(flag==0){
                tv_result_BigLotto.setText("");
                tv_result_DoubleBall.setText("+");
                tv_result_DoubleBall.setLayoutParams(params);
            }else if(flag==1){
                tv_result_DoubleBall.setText("");
                tv_result_BigLotto.setText("+");
                tv_result_BigLotto.setLayoutParams(params);
            }
        }else if(type==2){//排列5
            TextView tv_result_3= (TextView) convertView.findViewById(R.id.filter_result_tv3);
            TextView tv_result_4= (TextView) convertView.findViewById(R.id.filter_result_tv4);
            TextView tv_result_5= (TextView) convertView.findViewById(R.id.filter_result_tv5);
            tv_result_3.setText(String.valueOf(list.get(position)[3]));
            tv_result_4.setText(String.valueOf(list.get(position)[3]));
            tv_result_5.setText(String.valueOf(list.get(position)[4]));
        }else if(type==1){
            TextView tv_result_3= (TextView) convertView.findViewById(R.id.filter_result_tv3);
            tv_result_3.setText(containsZero.contains(flag)?CommonUtil.preZeroForBall(list.get(position)[2]):String.valueOf(list.get(position)[2]));
        }
        return convertView;
    }
}
