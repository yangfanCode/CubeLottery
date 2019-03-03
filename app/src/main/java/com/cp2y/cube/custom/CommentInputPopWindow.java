package com.cp2y.cube.custom;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.percent.PercentRelativeLayout.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yangfan on 2018/2/12.
 */

public class CommentInputPopWindow extends PopupWindow{
    private View popView;


    protected Context context;


    private boolean isOpenKeyboard=false;;


    private boolean isOkClose=true;

    private EditText editText;
    private TextView tvSend,tvCount;

    public CommentInputPopWindow(Context context) {

        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popView = inflater.inflate(R.layout.comment_input_pop, null);
        editText= (EditText) popView.findViewById(R.id.comment_pop_et);
        tvSend= (TextView) popView.findViewById(R.id.comment_tvSend);
        tvCount= (TextView) popView.findViewById(R.id.comment_tvInputCount);
        View view_dismiss=popView.findViewById(R.id.pop_dismiss);
        view_dismiss.setOnClickListener((v -> dismiss()));
        // btn_take_photo.setOnClickListener(itemsOnClick);
        // 设置SelectPicPopupWindow的View
        this.setContentView(popView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
//  this.setHeight(wm.getDefaultDisplay().getHeight() / 2);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);

        this.setOutsideTouchable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        //this.setAnimationStyle(R.style.AnimBottom);
        // 实例化一个ColorDrawable颜色为半透明
        //ColorDrawable dw = new ColorDrawable(0xb0000000);
        ColorDrawable dw = new ColorDrawable();
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框


//    popView.setOnTouchListener(new OnTouchListener() {
//
//    public boolean onTouch(View v, MotionEvent event) {
//
//    int height = popView.findViewById(R.id.pop_layout).getTop(); int
//    y=(int) event.getY(); if(event.getAction()==MotionEvent.ACTION_UP){
//    if(y<height){ dismiss(); } } return true; } });


//        (popView.findViewById(R.id.btn_back)).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
//
//        (popView.findViewById(R.id.btn_right)).setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if(onHeadClickListener!=null){
//                    onHeadClickListener.okListener();
//                }
//                if(isOkClose){
//                    dismiss();
//                }
//
//            }
//        });

        if(isOpenKeyboard){
            openKeyboard();
        }
    }
    /**
     * 生成一个 透明的背景图片
     * @return
     */
    private Drawable getDrawable(){
        ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
        bgdrawable.getPaint().setColor(context.getResources().getColor(android.R.color.transparent));
        return   bgdrawable;
    }

    /**
     * 打开软键盘
     */
    private void openKeyboard() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

            }
        }, 1000);
    }
    public boolean isOpenKeyboard() {
        return isOpenKeyboard;
    }
    public void setOpenKeyboard(boolean isOpenKeyboard) {
        this.isOpenKeyboard = isOpenKeyboard;
    }
    public boolean isOkClose() {
        return isOkClose;
    }
    public void setOkClose(boolean isOkClose) {
        this.isOkClose = isOkClose;
    }
    public Context getContext() {
        return context;
    }
    public EditText getEditText(){
        return editText;
    }
    public TextView getTextViewSend(){
        return tvSend;
    }
    public TextView getTextViewCount(){
        return tvCount;
    }
}
