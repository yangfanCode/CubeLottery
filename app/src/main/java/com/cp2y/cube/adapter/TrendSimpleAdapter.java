package com.cp2y.cube.adapter;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;

import com.cp2y.cube.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2017/1/5.
 */
public abstract class TrendSimpleAdapter extends BaseAdapter {

    protected int xoffset;

    public void setXoffset(int xoffset) {
        this.xoffset = xoffset;
    }

    /**显示或者隐藏分割线**/
    protected void showSeperateLine(View convertView, int position) {
        try {
            boolean showTopLine = position > 0 && (position % 5 == 0);
            boolean showBottomLine = position % 5 == 4;
            View lineTop = convertView.findViewById(R.id.line_top);
            View lineBottom = convertView.findViewById(R.id.line_bottom);
            lineTop.setVisibility(showTopLine?View.VISIBLE:View.GONE);
            lineBottom.setVisibility(showBottomLine?View.VISIBLE:View.GONE);

        } catch (Exception e) {
        }
    }

    /**修改偏移量**/
    public void offsetSeprateLine(View convertView) {
        try {
            View lineTop = convertView.findViewById(R.id.line_top);
            View lineBottom = convertView.findViewById(R.id.line_bottom);
            RelativeLayout.LayoutParams lp1 = (RelativeLayout.LayoutParams) lineTop.getLayoutParams();
            lp1.leftMargin = xoffset;
            lineTop.setLayoutParams(lp1);
            lp1 = (RelativeLayout.LayoutParams) lineBottom.getLayoutParams();
            lp1.leftMargin = xoffset;
            lineBottom.setLayoutParams(lp1);
        } catch (Exception e) {
        }
    }

    /**
     * 是否显示开奖号改变 虚线宽度
     * @param convertView
     * @param isShowBaseNum
     * @param width
     */
    public void setSeperateLineWidth(View convertView,int position,boolean isShowBaseNum,int...width){
        try {
            View lineTop = convertView.findViewById(R.id.line_top);
            View lineBottom = convertView.findViewById(R.id.line_bottom);
            boolean showTopLine = position > 0 && (position % 5 == 0);
            boolean showBottomLine = position % 5 == 4;
            lineTop.setVisibility(showTopLine?View.VISIBLE:View.GONE);
            lineBottom.setVisibility(showBottomLine?View.VISIBLE:View.GONE);
            RelativeLayout.LayoutParams lp1= (RelativeLayout.LayoutParams) lineTop.getLayoutParams();
            lp1.width=isShowBaseNum?width[0]:width[1];
            lineTop.setLayoutParams(lp1);
            lp1= (RelativeLayout.LayoutParams) lineBottom.getLayoutParams();
            lp1.width=isShowBaseNum?width[0]:width[1];
            lineBottom.setLayoutParams(lp1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*根据号码设定格式*/
    public int check3DNumTypeColor(String num){
        String nums[]=num.split(" ");
        Set<String> check=new HashSet<>();
        for(String n:nums){
            check.add(n);
        }
        if(check.size()==3){//3个都不同
            return 0;
        }else if(check.size()==2){//2个号码相同
            return 2;
        }else{//3个号码相同
            return 3;
        }
    }


}
