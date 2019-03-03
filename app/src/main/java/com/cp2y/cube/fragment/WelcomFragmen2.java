package com.cp2y.cube.fragment;


import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;

import com.cp2y.cube.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomFragmen2 extends WelcomFragmen1 {
    @Override
    public void setBtn(Button btn_goin) {
        super.setBtn(btn_goin);
    }

    @Override
    public void setImageView(ImageView iv) {
        super.setImageView(iv);
        iv.setImageResource(R.mipmap.welcome2);
    }
}
