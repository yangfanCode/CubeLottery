package com.cp2y.cube.activity.news.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.cp2y.cube.activity.news.NewsSummaryActivity;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

/**
 * Created by yangfan on 2017/4/27.
 */
public class ViewPagerPlayAdapter extends PagerAdapter {
    private List<SimpleDraweeView>list;
    private List<String>ImageIntent;
    private Context context;
    public ViewPagerPlayAdapter(List<SimpleDraweeView>list,List<String>ImageIntent,Context context){
        this.list=list;
        this.ImageIntent=ImageIntent;
        this.context=context;
    }
    @Override
    public int getCount() {
        if(list.size()==1){
            return 1;
        }else{
            return Integer.MAX_VALUE;
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleDraweeView simpleDraweeView=list.get(position%list.size());
        simpleDraweeView.setOnClickListener((v -> {//跳转详情
            Intent intent=new Intent(context, NewsSummaryActivity.class);
            intent.putExtra("url",ImageIntent.get(position%ImageIntent.size()));
            context.startActivity(intent);
        }));
        if (simpleDraweeView.getParent() != null) {//添加前移除数据
            ((ViewPager)simpleDraweeView.getParent()).removeView(simpleDraweeView);

        }
        container.addView(simpleDraweeView);//轮播数据
        return simpleDraweeView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //container.removeView(list.get(position%list.size()));
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

}
