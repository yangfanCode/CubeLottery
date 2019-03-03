package com.cp2y.cube.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.cp2y.cube.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomFragmen1 extends Fragment {

    private Button btn_Goin;
    private ImageView iv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.welcome_fragment_layout, container, false);
        btn_Goin = (Button)view. findViewById(R.id.app_welcome_btnGoin);
        iv = (ImageView)view. findViewById(R.id.app_welcome_iv);
        setImageView(iv);
        setBtn(btn_Goin);
        return view;
    }

    public void setBtn(Button btn_goin) {
    }

    public void setImageView(ImageView iv) {
    }

}
