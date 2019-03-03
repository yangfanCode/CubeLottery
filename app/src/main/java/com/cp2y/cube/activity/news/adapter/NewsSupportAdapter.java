package com.cp2y.cube.activity.news.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.custom.SingletonNewsGray;
import com.cp2y.cube.custom.TipsToast;
import com.cp2y.cube.model.NewsModel;
import com.cp2y.cube.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/4/26.
 */
public class NewsSupportAdapter extends BaseAdapter {
    private int[] supportImg={R.mipmap.icon_union_double,R.mipmap.icon_super_lotto,R.mipmap.zixun_tuijian_icon_3d,R.mipmap.zixun_icon_3_5,R.mipmap.icon_trends,R.mipmap.icon_story};
    private List<NewsModel.News>list=new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    public NewsSupportAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void LoadData(List<NewsModel.News>list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    //刷新数据
    public void ReLoadData(List<NewsModel.News>list){
        this.list.addAll(list);
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView=inflater.inflate(R.layout.news_item_support,parent,false);
        }

        try {
            ImageView icon= (ImageView) convertView.findViewById(R.id.item_support_icon);
            TextView title= (TextView) convertView.findViewById(R.id.item_support_title);
            TextView date= (TextView) convertView.findViewById(R.id.item_support_date);
            TextView zan= (TextView) convertView.findViewById(R.id.item_support_zanCount);
            TextView kan= (TextView) convertView.findViewById(R.id.item_support_kanCount);
            icon.setImageResource(supportImg[setIcon(list.get(position).getLotteryId())]);//icon
            title.setText(list.get(position).getTitle());//标题
            date.setText(list.get(position).getDate().substring(0,10));//日期
            zan.setText(list.get(position).getLikeNum());//点赞数量
            kan.setText(list.get(position).getDowns());//浏览量
            title.setTextColor(SingletonNewsGray.isContains(list.get(position).getId())?ColorUtils.GRAY:ColorUtils.NORMAL_GRAY);
        } catch (Exception e) {
            e.printStackTrace();
            TipsToast.showNewTips("系统异常，请反馈400-666-7575及时处理");
        }
        return convertView;
    }
    public int setIcon(String str){
        if(str.equals("3")){//双色球
            return 0;
        }else if(str.equals("6")){//大乐透
            return 1;
        }else if(str.equals("4")){//福彩3D
            return 2;
        }else if(str.equals("7")){//排列3/5
            return 3;
        }else if(str.equals("88")){//行业动态
            return 4;
        }else{//彩民故事
            return 5;
        }
    }
}
