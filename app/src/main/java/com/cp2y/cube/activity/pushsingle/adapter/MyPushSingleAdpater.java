package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.percent.PercentRelativeLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.PushSingleChangeSummaryActivity;
import com.cp2y.cube.activity.pushsingle.PushSingleSummaryActivity;
import com.cp2y.cube.model.PushSingleListModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/1/5.
 */
public class MyPushSingleAdpater extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<PushSingleListModel.Detail> list = new ArrayList<>();
    private View.OnClickListener onClickListener = v -> {
        int position = (int) v.getTag(R.id.position);
        int type = list.get(position).getType();
        Intent intent = new Intent();
        if (type == 0) {
            intent.setClass(context, PushSingleSummaryActivity.class);
        } else {
            intent.setClass(context, PushSingleChangeSummaryActivity.class);
        }
        intent.putExtra("pushSingleID", list.get(position).getiD());
        context.startActivity(intent);
    };

    public MyPushSingleAdpater(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void LoadData(List<PushSingleListModel.Detail> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    //上拉加载
    public void reLoadData(List<PushSingleListModel.Detail> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    @Override
    public PushSingleListModel.Detail getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        String rate = list.get(position).getReturnRate();//回报率
        int status = list.get(position).getStatus();//开奖状态
        int type = list.get(position).getType();//是否为转发
        if ("0.0%".equals(rate)) {
            if (status > 0) {
                return type == 0 ? 2 : 5;//未中
            } else {
                return type == 0 ? 0 : 3;//彩种icon
            }
        } else {
            return type == 0 ? 1 : 4;//回报率
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (convertView == null) {
            if (type == 0) {
                convertView = inflater.inflate(R.layout.mypush_single_item, parent, false);
            } else if (type == 1) {
                convertView = inflater.inflate(R.layout.mypush_single_item1, parent, false);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.mypush_single_item2, parent, false);
            } else if (type == 3) {
                convertView = inflater.inflate(R.layout.mypush_change_single_item, parent, false);
            } else if (type == 4) {
                convertView = inflater.inflate(R.layout.mypush_change_single_item1, parent, false);
            } else if (type == 5) {
                convertView = inflater.inflate(R.layout.mypush_change_single_item2, parent, false);
            }
        }
        PercentRelativeLayout layouts = (PercentRelativeLayout) convertView.findViewById(R.id.myPushSingle_layout);
        layouts.setOnClickListener(onClickListener);
        layouts.setTag(R.id.position, position);
        TextView tv_time = (TextView) convertView.findViewById(R.id.my_push_single_time);
        TextView tv_title = (TextView) convertView.findViewById(R.id.my_push_single_title);
        TextView tv_lotteryName = (TextView) convertView.findViewById(R.id.my_push_single_lotteryName);
        TextView tv_date = (TextView) convertView.findViewById(R.id.my_push_single_lotteryDate);
        TextView tv_commentsNum = (TextView) convertView.findViewById(R.id.my_push_single_commentsNum);
        TextView tv_changeNum = (TextView) convertView.findViewById(R.id.my_push_single_changeNum);
        ImageView zjIcon = (ImageView) convertView.findViewById(R.id.my_push_single_zjIcon);//中奖盖章
        FlowLayout layout = (FlowLayout) convertView.findViewById(R.id.my_push_single_flowLayout);
        View divider = convertView.findViewById(R.id.my_push_single_comment_divider);
        divider.setVisibility(position == getCount() - 1 ? View.GONE : View.VISIBLE);//分割线
        int status = list.get(position).getStatus();//开奖状态
        String rate = list.get(position).getReturnRate();//回报率
        zjIcon.setVisibility(View.GONE);//初始状态
        if (type == 0 || type == 3) {
            SimpleDraweeView icon = (SimpleDraweeView) convertView.findViewById(R.id.my_push_single_icon);
            icon.setImageURI(CommonUtil.concatImgUrl(list.get(position).getLotteryID()));//icon
        } else if (type == 1 || type == 4) {
            TextView tv_rate = (TextView) convertView.findViewById(R.id.my_push_single_rate);
            TextView tv_rateTitle = (TextView) convertView.findViewById(R.id.my_push_single_type);
            if (status > 0) {//开奖
                //回报率
                tv_rateTitle.setText("回报率");
            } else {//未开奖
                //预计回报率
                tv_rateTitle.setText("预计回报率");
            }
            tv_rate.setText(rate);
            zjIcon.setVisibility(status == 2 ? View.VISIBLE : View.GONE);//中奖盖章
        } else if (type == 2 || type == 5) {
            TextView tv_rateTitle = (TextView) convertView.findViewById(R.id.my_push_single_rate);
            tv_rateTitle.setText("未中");
        }
        if (type == 3 || type == 4 || type == 5) {//转发
            TextView tv_changeTitle = (TextView) convertView.findViewById(R.id.my_push_single_change_tvTitle);
            tv_changeTitle.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(list.get(position).getByTitle())) {
                tv_changeTitle.setText(list.get(position).getByTitle());//转发内容
            } else {
                tv_changeTitle.setVisibility(View.GONE);
            }
        }
        tv_time.setText(list.get(position).getTime());//时间
        tv_title.setText(list.get(position).getTitle());//标题
        tv_lotteryName.setText(list.get(position).getLotteryName());//彩种名
        tv_date.setText(list.get(position).getIssue().concat("期"));//奖期
        tv_commentsNum.setText(list.get(position).getCommentsNum());//评论
        tv_changeNum.setText(list.get(position).getChangeNum());//转发
        addFlowLayout(layout, position);//注数子项
        return convertView;
    }

    private void addFlowLayout(FlowLayout layout, int position) {
        layout.removeAllViews();//清空数据
        List<PushSingleListModel.Item> items = list.get(position).getNnList();//子项
        for (int i = 0; i < items.size(); i++) {
            TextView item = (TextView) inflater.inflate(R.layout.my_pushsingle_tv, layout, false);
            item.setText(items.get(i).getName().concat(items.get(i).getNote().concat("注")));
            setFlowLayoutMargin(item);//边距
            layout.addView(item);
        }
    }

    /**
     * 设置流式布局右下边距
     *
     * @param layout
     */
    public void setFlowLayoutMargin(View layout) {
        FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) layout.getLayoutParams();
        lp.bottomMargin = DisplayUtil.dip2px(10f);
        lp.rightMargin = DisplayUtil.dip2px(5f);
        layout.setLayoutParams(lp);//设置边距
    }
}
