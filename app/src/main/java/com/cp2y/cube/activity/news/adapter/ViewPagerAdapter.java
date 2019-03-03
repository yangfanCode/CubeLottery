package com.cp2y.cube.activity.news.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by yangfan on 2017/4/26.
 * 继承FragmentStatePagerAdapter每次释放内存刷新数据
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private String[] title;
    private List<Fragment>list;
    public ViewPagerAdapter(FragmentManager fm,List<Fragment>list,String[] title) {
        super(fm);
        this.list=list;
        this.title=title;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
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
