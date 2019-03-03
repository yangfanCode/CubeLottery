package com.cp2y.cube.utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by js on 2016/12/29.
 */
public class ViewUtils {

    /**生成中等大小的文字**/
    public static TextView generateMidTextView(Context context, String text, int color, int gravity) {
        if (context == null) return null;
        TextView textView = new TextView(context);
        textView.setGravity(gravity);
        textView.setText(text);
        textView.setTextSize(14f);
        textView.setTextColor(color);
        return textView;
    }

    /**
     * 显示或隐藏控件
     */
    public static void showViewsVisible(boolean visible, View... views) {
        if (views == null) return;
        for (View v:views) {
            try {
                v.setVisibility(visible?View.VISIBLE:View.GONE);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 显示或隐藏控件
     */
    public static void showViewsInvisible(boolean visible, View... views) {
        if (views == null) return;
        for (View v:views) {
            try {
                v.setVisibility(visible?View.VISIBLE:View.INVISIBLE);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 设置视图显示或隐藏
     * @param showView
     * @param hideViews
     */
    public static void showViewsVisible(View showView, View[] hideViews) {
        try {
            showView.setVisibility(View.VISIBLE);
            for (View view: hideViews
                 ) {
                view.setVisibility(View.GONE);
            }
        } catch (Exception e) {

        }
    }

    /**
     * 取消选中
     */
    public static void uncheckViews(ViewGroup group) {
        if (group == null) return;
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof CheckBox) {
                CheckBox box = (CheckBox) v;
                box.setChecked(false);
            }
        }
    }

    /**
     * 从父节点中移除
     * @param view
     */
    public static void removeFromParent(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        parent.removeView(view);
    }

    /**
     * 设置是否可点击
     * @param isEnable
     * @param views
     */
    public static void setViewEnable(boolean isEnable,View...views){
        if (views == null) return;
        for (View v:views) {
            try {
                v.setEnabled(isEnable);
            } catch (Exception e) {
            }
        }
    }
}
