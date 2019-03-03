package com.cp2y.cube.dialog;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;

/**
 * Created by yangfan on 2018/2/27.
 * 取消订阅弹窗
 */

public class SubscribeCancleDialog {
    private Context context;
    private TextView tvSubmit, tvCancle;

    public SubscribeCancleDialog(Context context) {
        this.context = context;
    }


    public AlertDialog getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.subscribe_cancle_layout, null);
        tvSubmit = (TextView) v.findViewById(R.id.sub_cancle_tvSunmit);
        tvCancle = (TextView) v.findViewById(R.id.sub_cancle_tvCancle);
        builder.setView(v);
        builder.setCancelable(false);
        return builder.create();
    }

    public TextView getTvSubmit() {
        return tvSubmit;
    }

    public TextView getTvCancle() {
        return tvCancle;
    }
}
