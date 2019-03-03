package com.cp2y.cube.fragment;


import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.ImageView;

import com.cp2y.cube.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomFragmen3 extends WelcomFragmen1 {
    @Override
    public void setBtn(Button btn_goin) {
        super.setBtn(btn_goin);
//        btn_goin.setVisibility(View.VISIBLE);
//        btn_goin.setOnClickListener((v -> {
//            //存是否是第一次
//            FirstAppSPUtils.put(getActivity(), "isFirstApp", false);
//            Intent intent = new Intent(getActivity(), MainActivity.class);
//            startActivity(intent);
//            getActivity().overridePendingTransition(0,0);
//            getActivity().finish();
//        }));
    }

    @Override
    public void setImageView(ImageView iv) {
        super.setImageView(iv);
        iv.setImageResource(R.mipmap.welcome3);
    }
}
