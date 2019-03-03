package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.PersonalActivity;
import com.cp2y.cube.model.ActiveModel;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.DisplayUtil;
import com.cp2y.cube.utils.ViewUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/1/5.
 */
public class ActiveAdpater extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<ActiveModel.Detail> list = new ArrayList<>();

    private View.OnClickListener onClickListener=(v -> {
        int position= (int) v.getTag(R.id.position);
        Intent intent=new Intent(context, PersonalActivity.class);
        intent.putExtra("otherUserId",list.get(position).getUserID());
        context.startActivity(intent);
    });

    public ActiveAdpater(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void loadData(List<ActiveModel.Detail> data) {
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    //上拉加载
    public void reLoadData(List<ActiveModel.Detail> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list != null && list.size() > 0 ? list.size() : 0;
    }

    @Override
    public ActiveModel.Detail getItem(int position) {
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
                convertView = inflater.inflate(R.layout.push_single_active_item, parent, false);
            } else if (type == 1) {
                convertView = inflater.inflate(R.layout.push_single_active_item1, parent, false);
            } else if (type == 2) {
                convertView = inflater.inflate(R.layout.push_single_active_item2, parent, false);
            } else if (type == 3) {
                convertView = inflater.inflate(R.layout.push_single_change_active_item, parent, false);
            } else if (type == 4) {
                convertView = inflater.inflate(R.layout.push_single_change_active_item1, parent, false);
            } else if (type == 5) {
                convertView = inflater.inflate(R.layout.push_single_change_active_item2, parent, false);
            }
        }
        SimpleDraweeView userIcon = (SimpleDraweeView) convertView.findViewById(R.id.push_single_active_icon);
        TextView tv_name = (TextView) convertView.findViewById(R.id.push_single_active_name);
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
            zjIcon.setVisibility(status==2?View.VISIBLE:View.GONE);//中奖盖章
        } else if (type == 2 || type == 5) {
            TextView tv_rateTitle = (TextView) convertView.findViewById(R.id.my_push_single_rate);
            tv_rateTitle.setText("未中");
        }
        userIcon.setImageURI(list.get(position).getPortraitUrl());
        userIcon.setOnClickListener(onClickListener);//跳转个人中心
        userIcon.setTag(R.id.position,position);
        tv_name.setText(list.get(position).getUserName());
        tv_time.setText(list.get(position).getTime());//时间
        tv_name.setOnClickListener(onClickListener);//跳转个人中心
        tv_name.setTag(R.id.position,position);
        tv_title.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(list.get(position).getTitle())) {
            tv_title.setText(list.get(position).getTitle());//标题
        } else {
            tv_title.setVisibility(View.GONE);
        }
        ViewUtils.showViewsVisible(true,convertView.findViewById(R.id.my_push_single_changeNum_icon),tv_changeNum);//显示点赞
        if (type == 3 || type == 4 || type == 5) {//转发
            TextView tv_changeName = (TextView) convertView.findViewById(R.id.push_single_change_tvName);
            TextView tv_changeTitle = (TextView) convertView.findViewById(R.id.push_single_change_tvTitle);
            tv_changeName.setText(list.get(position).getByUserName());//转发名字
            tv_changeTitle.setVisibility(View.VISIBLE);
            ViewUtils.showViewsVisible(false,convertView.findViewById(R.id.my_push_single_changeNum_icon),tv_changeNum);//隐藏点赞
            if(!TextUtils.isEmpty(list.get(position).getByTitle())){
                tv_changeTitle.setText(list.get(position).getByTitle());//转发内容
            }else{
                tv_changeTitle.setVisibility(View.GONE);
            }
        }
        tv_lotteryName.setText(list.get(position).getLotteryName());//彩种名
        tv_date.setText(list.get(position).getIssue().concat("期"));//奖期
        tv_commentsNum.setText(list.get(position).getCommentsNum());//评论
        tv_changeNum.setText(list.get(position).getChangeNum());//转发
        addFlowLayout(layout, position);//注数子项
        return convertView;
    }

    private void addFlowLayout(FlowLayout layout, int position) {
        layout.removeAllViews();//清空数据
        List<ActiveModel.Item> items = list.get(position).getNnList();//子项
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
