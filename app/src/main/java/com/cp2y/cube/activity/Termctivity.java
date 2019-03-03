package com.cp2y.cube.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.cp2y.cube.R;

/**
 * 服务条款
 */

public class Termctivity extends BaseActivity {

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termctivity);
        tv = (TextView) findViewById(R.id.tv);
        setMainTitle("用户协议");
        setNavigationIcon(R.mipmap.back_chevron);
        setNavigationOnClickListener((v -> finish()));
        tv.setText(getResources().getString(R.string.term));
    }
}
