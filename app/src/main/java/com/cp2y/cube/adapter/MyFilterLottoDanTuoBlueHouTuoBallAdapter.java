package com.cp2y.cube.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.selectnums.LottoSelectNumActivity;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.ColorUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.ViewUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/13.
 */
public class MyFilterLottoDanTuoBlueHouTuoBallAdapter extends MyBaseAdapter<String> {
    private float DownX=0,DownY=0;//移动距离
    private Set<String>list_select = new HashSet<>();
    private Set<String>list_select_blueDan = new HashSet<>();
    private boolean ignore = false;
    private BallSelectCall call;
    private MyInterface.setSelectNumD3PopUps popUps;
    private SparseIntArray ignores = ContextHelper.getActivity(LottoSelectNumActivity.class).getIgnoreBlues();
    private View.OnClickListener listener = view -> {
        int position = (int) view.getTag(R.id.key);
        String val = list.get(position);
        //点击之前判断
        boolean isValid = true;
        if (call != null) isValid = call.onLottoBeforeSelected(MyFilterLottoDanTuoBlueHouTuoBallAdapter.this, val);
        if (!isValid) return;
        TextView tv = (TextView) view.findViewById(R.id.item_selectfilter_NomalBlue);
        boolean selected = !list_select.contains(val);
        if(selected){
            tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
            tv.setTextColor(Color.WHITE);
            list_select.add(list.get(position));
        }else{
            tv.setBackgroundResource(R.drawable.lottery_ball_blue_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
            list_select.remove(list.get(position));
        }
        if (call != null) call.onBallSelected(MyFilterLottoDanTuoBlueHouTuoBallAdapter.this, val, selected);
    };
    private View.OnTouchListener touchListener=(v, event) -> {//滑动事件
        int position = (int) v.getTag(R.id.key);//pos位置
        //置灰不能点击判断
        String val = list.get(position);
        boolean isValid = true;
        if (call != null) isValid = call.onLottoBeforeSelected(MyFilterLottoDanTuoBlueHouTuoBallAdapter.this, val);
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
                    if(isValid){
                        popUps.setSelectNumD3PopUp(x,y,(position+1),true,false);
                    }
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
                        if(isValid) {
                            popUps.setSelectNumD3PopUp(x, y, (position + 1), false, false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("yangfan", "onTouch: "+"move");
                break;
            case MotionEvent.ACTION_UP:
                Log.e("yangfan", "onTouch: "+"up");
                try {
                    if(isValid) {
                        popUps.setSelectNumD3PopUp(x, y, (position + 1), false, false);//图片消失
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case MotionEvent.ACTION_CANCEL://快速滑动失去焦点
                Log.e("yangfan", "onTouch: "+"cancle");
                try {
                    popUps.setSelectNumD3PopUp(x,y,(position+1),false,false);//图片消失
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return false;
    };
    public MyFilterLottoDanTuoBlueHouTuoBallAdapter(Context context, int resId, boolean ignore, Set<String>list_select) {
        super(context, resId);
        this.ignore=ignore;
        this.list_select=list_select;
    }

    public void reloadData(List<String> data) {
        list_select.clear();
        list_select.addAll(data);
        notifyDataSetChanged();
    }

    public void reset() {
        list_select.clear();
        notifyDataSetChanged();
    }
    public void setDanList(Set<String>list_select_blueDan) {
        this.list_select_blueDan = list_select_blueDan;
    }

    public void setCall(BallSelectCall call) {
        this.call = call;
    }

    public void setPopUps(MyInterface.setSelectNumD3PopUps popUps){this.popUps = popUps;}

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
        notifyDataSetChanged();
    }

    @Override
    public void bindData(ViewHolder holder, int position) {
        TextView tv= (TextView) holder.findView(R.id.item_selectfilter_NomalBlue);
        tv.setTag(R.id.key, position);
        tv.setOnClickListener(listener);
        tv.setOnTouchListener(touchListener);
        TextView tv_ignore= (TextView) holder.findView(R.id.item_selectfilter_NomalBlue_ignore);
        String val = list.get(position);
        tv.setText(CommonUtil.preZeroForBall(val));
        ViewUtils.showViewsVisible(ignore, tv_ignore);
        tv_ignore.setText(String.valueOf(ignores.get(position)));
        if(list_select.contains(val)){
            tv.setBackgroundResource(R.drawable.lottery_ball_blue_big_all);
            tv.setTextColor(ColorUtils.WHITE);
        }else if(list_select_blueDan.contains(val)){
            tv.setTextColor(ColorUtils.GRAY);
            tv.setBackgroundResource(R.drawable.lottery_ball_red_big_dantuo);
        }else{
            tv.setBackgroundResource(R.drawable.lottery_ball_blue_big);
            tv.setTextColor(ColorUtils.NORMAL_GRAY);
        }
    }

}
