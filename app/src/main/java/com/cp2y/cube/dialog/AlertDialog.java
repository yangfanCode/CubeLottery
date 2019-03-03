package com.cp2y.cube.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;

import com.cp2y.cube.R;

/**
 * Created by js on 2017/3/8.
 */
public class AlertDialog extends Dialog {
    private Context context;
    private int height;
    public AlertDialog(Context context) {
        super(context, R.style.BaseDialog2);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_dialog);
        setHeight();
//        findViewById(R.id.cancel).setOnClickListener(view -> {
//            findViewById(R.id.content).setVisibility(findViewById(R.id.content).getVisibility() == View.VISIBLE ? View.GONE :View.VISIBLE);
//        });
//        findViewById(R.id.confirm).setOnClickListener(view -> cancel());
    }

    public void setMaxHeight(int height){
        this.height=height;
    }
    private void setHeight(){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        if (metrics.heightPixels < 300) {
            this.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        } else {
            this.getWindow().setLayout(LayoutParams.WRAP_CONTENT, 300);

        }
    }


}
