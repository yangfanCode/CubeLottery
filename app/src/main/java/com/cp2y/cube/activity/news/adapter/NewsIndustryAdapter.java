package com.cp2y.cube.activity.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.SingletonNewsGray;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.NewsIndustryModel;
import com.cp2y.cube.utils.ColorUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/5/2.
 */
public class NewsIndustryAdapter extends BaseAdapter {
    private boolean isShowImportNews =false;//是否有要闻
    private Context context;
    private int YaoWenCount=0;//要闻条数
    private List<NewsIndustryModel.News> list=new ArrayList<>();
    private LayoutInflater inflater;
    public NewsIndustryAdapter(Context context){
        this.context=context;
        Fresco.initialize(context);//fresco初始化
        inflater=LayoutInflater.from(context);

    }
    //第一次展示数据
    public void LoadData(List<NewsIndustryModel.News> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }
    //上拉加载
    public void ReLoadData(List<NewsIndustryModel.News> data){
        list.addAll(data);
        notifyDataSetChanged();
    }
    public void setYWCount(int YaoWenCount){
        this.YaoWenCount=YaoWenCount;

    }
    public void setIsShowImportNews(boolean showImportNews){
        this.isShowImportNews=showImportNews;
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;

    }

    @Override
    public NewsIndustryModel.News getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position)==null){//标题数据
            if(isShowImportNews){//有要闻
                if(position==0){
                    return 0;//要闻标题布局
                }else if(position==(YaoWenCount+1)){
                    return 1;
                }else{
                   return DataView(position);
                }
            }else{//无要闻
                if(position==0){//动态标题布局
                    return 1;
                }else{
                    return DataView(position);
                }
            }
        }else{//内容数据
            return DataView(position);
        }
    }
    //内容布局
    private int DataView(int position) {
        if("-1".equals(list.get(position).getTitlePicture())){
            return 2;//无图布局
        }else{
            return 3;//有图布局
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);
        if(convertView==null){
            if(type==0){
                convertView =inflater.inflate(R.layout.news_item_industry_yaowen,parent,false);
            }else if(type==1){
                convertView =inflater.inflate(R.layout.news_item_industry_dongtai,parent,false);
            }else if(type==2){
                convertView =inflater.inflate(R.layout.news_item_lottery,parent,false);
            }else{
                convertView =inflater.inflate(R.layout.news_item_industry_pic,parent,false);
            }
        }
        try {
            if(type==2){//无图
                TextView title= (TextView) convertView.findViewById(R.id.item_support_title);
                TextView date= (TextView) convertView.findViewById(R.id.item_support_date);
                TextView zan= (TextView) convertView.findViewById(R.id.item_support_zanCount);
                TextView kan= (TextView) convertView.findViewById(R.id.item_support_kanCount);
                title.setText(list.get(position).getTitle());//标题
                date.setText(list.get(position).getDate().substring(0,10));//日期
                zan.setText(list.get(position).getLikeNum());//点赞数量
                kan.setText(list.get(position).getDowns());//浏览量
                title.setTextColor(SingletonNewsGray.isContains(list.get(position).getId())? ColorUtils.GRAY:ColorUtils.NORMAL_GRAY);
            }else if(type==3){//有图
                TextView title= (TextView) convertView.findViewById(R.id.news_item_industry_title);
                TextView date= (TextView) convertView.findViewById(R.id.news_item_industry_date);
                TextView zan= (TextView) convertView.findViewById(R.id.news_item_industry_zanCount);
                TextView kan= (TextView) convertView.findViewById(R.id.news_item_industry_kanCount);
                SimpleDraweeView icon= (SimpleDraweeView) convertView.findViewById(R.id.news_item_industry_icon);
                title.setText(list.get(position).getTitle());//标题
                date.setText(list.get(position).getDate().substring(0,10));//日期
                zan.setText(list.get(position).getLikeNum());//点赞数量
                kan.setText(list.get(position).getDowns());//浏览量
                icon.setImageURI(list.get(position).getTitlePicture());
                title.setTextColor(SingletonNewsGray.isContains(list.get(position).getId())?ColorUtils.GRAY:ColorUtils.NORMAL_GRAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showNewTips("系统异常，请反馈400-666-7575及时处理");
        }
        return convertView;
    }
}
