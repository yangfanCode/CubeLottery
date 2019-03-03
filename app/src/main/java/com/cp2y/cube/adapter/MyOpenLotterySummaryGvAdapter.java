package com.cp2y.cube.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/12/7.
 */
public class MyOpenLotterySummaryGvAdapter extends MyBaseAdapter<String> {
    private List<String>list;
    private Map<String,Integer> icon=new HashMap<String,Integer>(){{
        put("号码库",R.mipmap.kjxq_icon_haomaku);
        put("走势",R.mipmap.kjxq_icon_zoushi);
        put("遗漏",R.mipmap.kjxq_icon_yilou);
        put("过滤",R.mipmap.kjxq_icon_guolv);
        put("扫一扫",R.mipmap.kjxq_icon_jijiangqi);
        put("预测",R.mipmap.icon_details_news);
        put("玩法",R.mipmap.kjxq_icon_wanfa);
    }};
    public MyOpenLotterySummaryGvAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        ImageView iv_lotteryType= (ImageView) holder.findView(R.id.item_openlottery_summary_type_iv);
        TextView tv_lotteryType= (TextView) holder.findView(R.id.item_openlottery_summary_type_tv);
        tv_lotteryType.setText("预测".equals(list.get(postion))?"资讯":list.get(postion));
        iv_lotteryType.setImageResource(icon.get(list.get(postion)));
    }
}
