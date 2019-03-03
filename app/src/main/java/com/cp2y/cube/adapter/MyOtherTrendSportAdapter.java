package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;

import java.util.List;

/**
 * Created by admin on 2016/12/8.
 */
public class MyOtherTrendSportAdapter extends MyBaseAdapter<String> {
    private int img[]={R.mipmap.kjgg_icon_daletou,R.mipmap.glss_icon_zhejiang6_1,R.mipmap.glss_icon_pailie5,R.mipmap.glss_icon_pailie3,R.mipmap.glss_icon_zhejiang20xuan5,R.mipmap.glss_icon_qixingcai};
    private List<String>list;
    private Context context;
    public MyOtherTrendSportAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
        this.context=context;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        ImageView iv_lottery= (ImageView) holder.findView(R.id.item_otherthrend_iv_lotteryIcon);
        TextView tv_lottery= (TextView) holder.findView(R.id.item_otherthrend_tv_lotteryname);
        iv_lottery.setImageResource(img[postion]);
        tv_lottery.setText(list.get(postion));
        tv_lottery.setTextColor(postion==0?ColorUtils.NORMAL_GRAY:ColorUtils.Light_Gray);
    }
}
