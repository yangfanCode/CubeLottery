package com.cp2y.cube.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.main.MainActivity;
import com.cp2y.cube.fragment.WelcomFragmen1;
import com.cp2y.cube.fragment.WelcomFragmen2;
import com.cp2y.cube.fragment.WelcomFragmen3;
import com.cp2y.cube.fragment.WelcomFragmen4;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private ViewPager vp;
    private List<Fragment>list;
    private LinearLayout ll;
    private ImageView[] ivs;
    /**原版页面**/
    private Fragment[]fragments={new WelcomFragmen1(),new WelcomFragmen2(),new WelcomFragmen3(),new WelcomFragmen4()};
    private Fragment[]noNet_Fragments={new WelcomFragmen1(),new WelcomFragmen2(),new WelcomFragmen3()};//断网
    /**临时改版页面**/
//    private Fragment[]fragments={new WelcomFragmen4()};
//    private Fragment[]noNet_Fragments={};//断网

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        vp = (ViewPager) findViewById(R.id.app_welcome_vp);
        ll = (LinearLayout) findViewById(R.id.app_welcome_icon_ll);
        initData();
        vp.setOffscreenPageLimit(4);//加载全部页面
        vp.setAdapter(new MyAdapter(getSupportFragmentManager()));
        final int[] pos = {0};
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pos[0] =position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(!CommonUtil.isNetworkConnected(WelcomeActivity.this)) {
                    if (pos[0] == 2 && state == ViewPager.SCROLL_STATE_DRAGGING) {
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        pos[0]=0;
                    }
                }
            }
        });
        //自定义小白点
//        initIcon();
//        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                for(int i=0;i<ivs.length;i++){
//                    ivs[i].setImageResource(R.mipmap.icon02);
//                }
//                ivs[position].setImageResource(R.mipmap.icon01);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }

    private void initIcon() {
        ivs=new ImageView[list.size()];
        for (int i = 0; i < ivs.length; i++) {
            ivs[i]=new ImageView(WelcomeActivity.this);
            LayoutParams params=new LayoutParams(20,20);
            params.setMargins(0, 0, 20, 20);
            ivs[i].setTag(i);
            ivs[i].setImageResource(R.mipmap.icon02);
            ll.addView(ivs[i]);
        }
    }

    private void initData() {
        list=new ArrayList<>();
        if(!CommonUtil.isNetworkConnected(WelcomeActivity.this)) {//断网机制
            for(Fragment fragment:noNet_Fragments){//断网
                list.add(fragment);
            }
        }else{
            for(Fragment fragment:fragments){//未断网
                list.add(fragment);
            }
        }
    }
    class MyAdapter extends FragmentPagerAdapter{

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }
    }

}
