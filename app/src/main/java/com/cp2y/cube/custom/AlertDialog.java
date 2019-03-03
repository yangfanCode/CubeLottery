package com.cp2y.cube.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cp2y.cube.R;

/**
 * Created by admin on 2016/12/29.
 */
public class AlertDialog {
    Context context;
    android.app.AlertDialog ad;
    Button NegativeButton;
    Button PositiveButton;
    LinearLayout buttonLayout;
    android.app.AlertDialog.Builder builder;
    public AlertDialog(Context context) {
        // TODO Auto-generated constructor stub
        this.context = context;
        ad = new android.app.AlertDialog.Builder(context).create();
        builder=new android.app.AlertDialog.Builder(context);

        Window window = ad.getWindow();
        View view= LayoutInflater.from(context).inflate(R.layout.custom_alertdialog,null);
        window.setContentView(view);
        buttonLayout = (LinearLayout) window.findViewById(R.id.buttonLayout);
        NegativeButton= (Button) window.findViewById(R.id.NegativeButton);
        PositiveButton= (Button) window.findViewById(R.id.PositiveButton);
    }

    public void setView(View view) {
        builder.setView(view);
    }

    /**
     * 设置取消按钮
     *
     * @param text
     * @param listener
     */
    public void setNegativeButton(String text, final View.OnClickListener listener) {
        NegativeButton.setText(text);
        NegativeButton.setOnClickListener(listener);
    }
    public void show(){
        builder.show();
    }


    /**
     * 设置确定按钮
     *
     * @param text
     * @param listener
     */
    public void setPositiveButton(String text, final View.OnClickListener listener) {
        PositiveButton.setText(text);
        PositiveButton.setOnClickListener(listener);
    }



    /**
     * 关闭对话框
     */
    public void dismiss() {
        ad.dismiss();
    }
}
