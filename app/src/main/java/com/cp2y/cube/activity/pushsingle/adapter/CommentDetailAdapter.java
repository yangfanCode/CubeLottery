package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.model.CommentModel;
import com.cp2y.cube.utils.ColorUtils;

import java.util.List;

/**
 * Created by yangfan on 2018/2/11.
 * 楼中楼
 */

public class CommentDetailAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater inflater;
    private List<CommentModel.Item>list;
    public CommentDetailAdapter(Context context,List<CommentModel.Item>list){
        this.context=context;
        this.list=list;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list!=null&&list.size()>0?list.size():0;
    }

    @Override
    public CommentModel.Item getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int type=list.get(position).type;//评论类型
        return type==1?0:1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type=getItemViewType(position);//评论类型
        if(convertView==null){
            convertView=inflater.inflate(R.layout.item_comment_detail_reply,parent,false);
        }
        TextView tvMessage= (TextView) convertView.findViewById(R.id.item_reply_tv);//回复
        CommentModel.Item item=list.get(position);
        String criticsName=item.criticsName;//回复人名称
        StringBuilder stringBuilder=new StringBuilder();
        if(type==0){//回复楼主
            String message=stringBuilder.append(criticsName).append(":").append(item.content).toString();//回复人名称:内容
            SpannableString spannableString = new SpannableString(message);//颜色设置
            for(int j=0;j<message.length();j++){
                spannableString.setSpan(new ForegroundColorSpan(j<(criticsName.length()+1)? ColorUtils.MID_BLUE:ColorUtils.COLOR_6F6F6F), j, j+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            tvMessage.setText(spannableString);
        }else{//楼中楼
            String byCriticsName=item.byCriticsName;//被回复人名称
            String reply="回复";
            String message=stringBuilder.append(criticsName).append(reply).append(byCriticsName).append(":").append(item.content).toString();//回复人名称 回复 被回复人名称:内容
            SpannableString spannableString = new SpannableString(message);//颜色设置
            for(int j=0;j<message.length();j++){
                if(j<criticsName.length()||j>=(criticsName.length()+reply.length())&&j<(criticsName.length()+reply.length()+byCriticsName.length())){
                    spannableString.setSpan(new ForegroundColorSpan(ColorUtils.MID_BLUE), j, j+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//蓝色
                }else{
                    spannableString.setSpan(new ForegroundColorSpan(ColorUtils.COLOR_6F6F6F), j, j+1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//灰色
                }
            }
            tvMessage.setText(spannableString);
        }
        return convertView;
    }
}
