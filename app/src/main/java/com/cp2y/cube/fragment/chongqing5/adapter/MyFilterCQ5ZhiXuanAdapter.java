package com.cp2y.cube.fragment.chongqing5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.selectnums.SelectCQ5NumActivity;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/13.
 */
public class MyFilterCQ5ZhiXuanAdapter extends BaseAdapter {
    private float DownX=0,DownY=0;//移动距离
    private int flag=0;
    private List<String> list=new ArrayList<>();
    private Set<String> list_select=new HashSet<>();
    private Context context;
    private BallSelectCall call;
    private MyInterface.setSelectNumPopUps popUps;
    private boolean ignore=false;
    private LayoutInflater inflater;
    private SparseIntArray ignores_wan = ContextHelper.getActivity(SelectCQ5NumActivity.class).getIgnoreWan();//直选万位遗漏
    private SparseIntArray ignores_qian = ContextHelper.getActivity(SelectCQ5NumActivity.class).getIgnoreQian();//直选千位遗漏
    private SparseIntArray ignores_bai = ContextHelper.getActivity(SelectCQ5NumActivity.class).getIgnoreBai();//直选百位遗漏
    private SparseIntArray ignores_shi = ContextHelper.getActivity(SelectCQ5NumActivity.class).getIgnoreShi();//直选十位遗漏
    private SparseIntArray ignores_ge = ContextHelper.getActivity(SelectCQ5NumActivity.class).getIgnoreGe();//直选个位遗漏
    public MyFilterCQ5ZhiXuanAdapter(Context context, boolean ignore, Set<String> list_select, int flag){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.list_select=list_select;
        this.ignore=ignore;
        this.flag=flag;
    }
    private View.OnClickListener listener = view -> {//点击事件
        int position = (int) view.getTag(R.id.key);
        String val = list.get(position);
        TextView tv = (TextView) view.findViewById(R.id.item_selectfilter_3dred);
        boolean selected = !list_select.contains(val);
        if(selected){
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(Color.WHITE);
            list_select.add(list.get(position));
        }else{
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
            list_select.remove(list.get(position));
        }
        if (call != null) call.onBallSelected(MyFilterCQ5ZhiXuanAdapter.this, val, selected);
    };
    private View.OnTouchListener touchListener=(v, event) -> {//滑动事件
        int position = (int) v.getTag(R.id.key);//pos位置
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        //TipsToast.showTips("x:"+x+" "+"y:"+y);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN://显示图片
                Log.e("yangfan", "onTouch: "+"DOWN");
                DownX = event.getX();//float DownX
                DownY = event.getY();//float DownY
                try {
                    popUps.setSelectNumPopUp(x,y,position,true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case MotionEvent.ACTION_MOVE://滑动超过范围图片消失
                float moveX = event.getX() - DownX;//X轴距离
                float moveY = event.getY() - DownY;//y轴距离
                Log.e("yangfan", "onTouch:moveX "+moveX);
                Log.e("yangfan", "onTouch:moveY "+moveY);
                try {
                    if(Math.abs(moveX)>34||Math.abs(moveY)>34){
                        popUps.setSelectNumPopUp(x,y,position,false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("yangfan", "onTouch: "+"move");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("yangfan", "onTouch: "+"up");
                try {
                    popUps.setSelectNumPopUp(x,y,position,false);//图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case MotionEvent.ACTION_CANCEL:
                Log.e("yangfan", "onTouch: "+"cancle");
                try {
                    popUps.setSelectNumPopUp(x,y,position,false);//图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
        return false;
    };
    public void LoadData(List<String> data){
        this.list.clear();
        this.list.addAll(data);
        notifyDataSetChanged();
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        notifyDataSetChanged();
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

    public void setCall(BallSelectCall call) {
        this.call = call;
    }

    public void setPopUps(MyInterface.setSelectNumPopUps popUps){this.popUps = popUps;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_selectfilter_3d_red,parent,false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.item_selectfilter_3dred);
        tv.setTag(R.id.key, position);
        tv.setOnClickListener(listener);
        tv.setOnTouchListener(touchListener);
        TextView tv_ignore= (TextView) convertView.findViewById(R.id.item_selectfilter_3dred_ignore);
        String val = list.get(position);
        tv.setText(val);
        ViewUtils.showViewsVisible(ignore, tv_ignore);
        tv_ignore.setText(setIgnoreText(position));//遗漏数据
        if (list_select.contains(list.get(position))) {
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(ColorUtils.WHITE);
        } else {
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
        }
        return convertView;
    }

    //遗漏数据
    public String setIgnoreText(int pos){
        String ignore="";
        switch (flag){
            case 0:
                ignore= String.valueOf(ignores_wan.get(pos));
                break;
            case 1:
                ignore=  String.valueOf(ignores_qian.get(pos));
                break;
            case 2:
                ignore=  String.valueOf(ignores_bai.get(pos));
                break;
            case 3:
                ignore=  String.valueOf(ignores_shi.get(pos));
                break;
            case 4:
                ignore=  String.valueOf(ignores_ge.get(pos));
                break;
        }
        return ignore;
    }
}
