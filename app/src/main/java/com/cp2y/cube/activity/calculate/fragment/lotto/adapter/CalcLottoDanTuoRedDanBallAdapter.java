package com.cp2y.cube.activity.calculate.fragment.lotto.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.adapter.BallSelectCall;
import com.cp2y.cube.adapter.MyBaseAdapter;
import com.cp2y.cube.adapter.ViewHolder;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.ViewUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/13.
 */
public class CalcLottoDanTuoRedDanBallAdapter extends MyBaseAdapter<String> {
    private float DownX=0,DownY=0;//移动距离
    private Set<String> list_select_red = new HashSet<>();
    private boolean ignore = false;
    private BallSelectCall call;
    private MyInterface.setSelectNumD3PopUps popUps;
    private View.OnClickListener listener = view -> {
        int position = (int) view.getTag(R.id.key);
        String val = list.get(position);
        boolean isValid = true;
        if (call != null) isValid = call.onBeforeSelected(CalcLottoDanTuoRedDanBallAdapter.this, val,false);
        if (!isValid) return;
        TextView tv = (TextView) view.findViewById(R.id.item_selectfilter_NomalRed);
        boolean selected = !list_select_red.contains(val);
        if(selected){
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(Color.WHITE);
            list_select_red.add(list.get(position));
        }else{
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
            list_select_red.remove(list.get(position));
        }
        if (call != null) call.onBallSelected(CalcLottoDanTuoRedDanBallAdapter.this, val, selected);
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
                    popUps.setSelectNumD3PopUp(x,y,(position+1),true,true);
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
                        popUps.setSelectNumD3PopUp(x,y,(position+1),false,true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("yangfan", "onTouch: "+"move");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("yangfan", "onTouch: "+"up");
                try {
                    popUps.setSelectNumD3PopUp(x,y,(position+1),false,true);//图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case MotionEvent.ACTION_CANCEL://快速滑动失去焦点
                Log.e("yangfan", "onTouch: "+"cancle");
                try {
                    popUps.setSelectNumD3PopUp(x,y,(position+1),false,true);//图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    };
    public CalcLottoDanTuoRedDanBallAdapter(Context context, int resId, boolean ignore, Set<String> list_select_red) {
        super(context, resId);
        this.list_select_red = list_select_red;
        this.ignore = ignore;
    }

    public void reloadData(List<String> data) {
        list_select_red.clear();
        list_select_red.addAll(data);
        notifyDataSetChanged();
    }

    public void reset() {
        list_select_red.clear();
        notifyDataSetChanged();
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        notifyDataSetChanged();
    }

    public void setCall(BallSelectCall call) {
        this.call = call;
    }

    public void setPopUps(MyInterface.setSelectNumD3PopUps popUps){this.popUps = popUps;}

    @Override
    public void bindData(ViewHolder holder, int position) {
        TextView tv = (TextView) holder.findView(R.id.item_selectfilter_NomalRed);
        tv.setTag(R.id.key, position);
        tv.setOnClickListener(listener);
        tv.setOnTouchListener(touchListener);
        TextView tv_ignore = (TextView) holder.findView(R.id.item_selectfilter_NomalRed_ignore);
        String val = list.get(position);
        tv.setText(CommonUtil.preZeroForBall(val));
        ViewUtils.showViewsVisible(ignore, tv_ignore);
        if (list_select_red.contains(list.get(position))) {
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_all);
            tv.setTextColor(ColorUtils.WHITE);
        } else {
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
        }
    }

}
