package com.cp2y.cube.adapter;

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
import com.cp2y.cube.activity.selectnums.SelectP3NumActivity;
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
public class MyFilterP3ZuXuanAdapter extends BaseAdapter {
    private float DownX=0,DownY=0;//移动距离
    private List<String> list=new ArrayList<>();
    private Set<String> list_select=new HashSet<>();//普通或者拖选择号码
    private Set<String> list_select_dantuo=new HashSet<>();//胆选择号码
    private Context context;
    private BallSelectCall call;
    private boolean ignore=false;
    private LayoutInflater inflater;
    private MyInterface.setSelectNumD3PopUps popUps;
    private SparseIntArray ignores = ContextHelper.getActivity(SelectP3NumActivity.class).getIgnoreZuXuan();
    public MyFilterP3ZuXuanAdapter(Context context, boolean ignore, Set<String> list_select, Set<String> list_select_dantuo){
        this.context=context;
        inflater=LayoutInflater.from(context);
        this.list_select=list_select;
        this.list_select_dantuo=list_select_dantuo;
        this.ignore=ignore;
    }
    private View.OnClickListener listener = view -> {
        int position = (int) view.getTag(R.id.key);
        String val = list.get(position);
        //点击之前判断
        boolean isValid = true;
        if (call != null) isValid = call.onBeforeSelected(MyFilterP3ZuXuanAdapter.this, val,false);
        if (!isValid) return;
        TextView tv = (TextView) view.findViewById(R.id.item_selectfilter_3dred);
        boolean selected = !list_select.contains(val);//普通模式是否不包含
        boolean dantuoSelected=!list_select_dantuo.contains(val);//胆拖数据是否不包含
        if(selected==true&&dantuoSelected==true){//没选择的情况下单击
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(Color.WHITE);
            list_select.add(list.get(position));//普通模式添加数据
            // D3DantuoClickCount.zu3_click_count++;//组3点击增加次数
        }else{
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
            if(!selected){//普通模式下取消
                list_select.remove(list.get(position));
            }else{//胆拖模式下取消
                list_select_dantuo.remove(list.get(position));
            }
        }
        if (call != null) call.onBallSelected(MyFilterP3ZuXuanAdapter.this, val, selected);
    };
    //长按监听
    private View.OnLongClickListener longClickListener=(v -> {
        int position = (int) v.getTag(R.id.key);
        String val = list.get(position);
        //点击之前判断
        boolean isValid = true;
        if (call != null) isValid = call.onBeforeSelected(MyFilterP3ZuXuanAdapter.this, val,true);
        if (!isValid) return true;
        TextView tv = (TextView) v.findViewById(R.id.item_selectfilter_3dred);
        boolean selected = !list_select.contains(val);//普通模式
        boolean dantuoSelected=!list_select_dantuo.contains(val);//胆拖数据
        if(selected==true&&dantuoSelected==true){//没选择的情况下长按
            tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
            tv.setTextColor(Color.WHITE);
            list_select_dantuo.add(list.get(position));
        }else{
            if(!selected){//在普通选择的情况下长按
                tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
                tv.setTextColor(Color.WHITE);
                list_select_dantuo.add(list.get(position));
                list_select.remove(list.get(position));
            }
        }
        if (call != null) call.onBallSelected(MyFilterP3ZuXuanAdapter.this, val, selected);
        return true;
    });
    private View.OnTouchListener touchListener=(v, event) -> {//滑动事件
        int position = (int) v.getTag(R.id.key);//pos位置
        String val = list.get(position);
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        //TipsToast.showTips("x:"+x+" "+"y:"+y);
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN://显示图片 按下
                Log.e("yangfan", "onTouch: "+"DOWN");
                DownX = event.getX();//float DownX
                DownY = event.getY();//float DownY
                try {
                    setPopRedBlueChange(val,x,y,position,true);
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
                    if(Math.abs(moveX)>5||Math.abs(moveY)>5){
                        setPopRedBlueChange(val,x,y,position,false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("yangfan", "onTouch: "+"move");
                break;
            case MotionEvent.ACTION_UP://按下抬起
                Log.e("yangfan", "onTouch: "+"up");
                try {
                    setPopRedBlueChange(val,x,y,position,false);
                    //图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case MotionEvent.ACTION_CANCEL://快速滑动失去焦点
                Log.e("yangfan", "onTouch: "+"up");
                try {
                    setPopRedBlueChange(val,x,y,position,false);
                    //图片消失
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

    public void setPopUps(MyInterface.setSelectNumD3PopUps popUps){this.popUps = popUps;}

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_selectfilter_3d_red,parent,false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.item_selectfilter_3dred);
        tv.setTag(R.id.key, position);
        tv.setOnClickListener(listener);
        tv.setOnTouchListener(touchListener);
        tv.setOnLongClickListener(longClickListener);
        TextView tv_ignore= (TextView) convertView.findViewById(R.id.item_selectfilter_3dred_ignore);
        String val = list.get(position);
        tv.setText(val);
        ViewUtils.showViewsVisible(ignore, tv_ignore);
        tv_ignore.setText(String.valueOf(ignores.get(position)));
        if (list_select.contains(list.get(position))) {//红球
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(ColorUtils.WHITE);
        } else {
            if(list_select_dantuo.contains(list.get(position))){//蓝球
                tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
                tv.setTextColor(Color.WHITE);
            }else{//不选
                tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
                tv.setTextColor(ColorUtils.NORMAL_GRAY);
            }
        }
        return convertView;
    }
    /**
     * 控制选号pop红颜切换
     * @param val
     * @param x
     * @param y
     * @param position
     * @param isShow
     */
    public void setPopRedBlueChange(String val,int x,int y,int position,boolean isShow){
        boolean selected = !list_select.contains(val);//普通模式
        boolean dantuoSelected=!list_select_dantuo.contains(val);//胆拖数据
        if(selected==true&&dantuoSelected==true){//没选择的情况下
            popUps.setSelectNumD3PopUp(x,y,position,isShow,true);
        }else{
            if(dantuoSelected){
                popUps.setSelectNumD3PopUp(x,y,position,isShow,false);
            }else{//第三次点击取消
                popUps.setSelectNumD3PopUp(x,y,position,isShow,true);
            }
        }
    }
}
