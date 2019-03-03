package com.cp2y.cube.activity.pushsingle.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.pushsingle.PersonalActivity;
import com.cp2y.cube.callback.MyInterface;
import com.cp2y.cube.custom.MyListView;
import com.cp2y.cube.model.CommentModel;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2018/1/15.
 */
public class PushSingleSummaryAdapter extends BaseAdapter {
    private MyInterface.CommentReply commentReply;
    private Context context;
    private LayoutInflater inflater;
    private List<CommentModel.Detail>list=new ArrayList<>();
    private View.OnClickListener iconOnClick=v -> {//头像点击跳转个人中心
        int position= (int) v.getTag(R.id.position);
        Intent intent=new Intent(context, PersonalActivity.class);
        intent.putExtra("otherUserId",list.get(position).criticsID);
        context.startActivity(intent);
    };
    private View.OnClickListener onClickListener=v -> {//回复楼主
        int position= (int) v.getTag(R.id.position);
        commentReply.commentReply(position);
    };
    private MyListView.OnItemClickListener onItemClickListener=(parent, view, position, id) -> {
        int pos= (int) parent.getTag(R.id.position);
        commentReply.commentFloorReply(pos,position);//回复楼中楼
    };

    public PushSingleSummaryAdapter(Context context){
        this.context=context;
        inflater=LayoutInflater.from(context);
    }
    public void setCommentReply(MyInterface.CommentReply commentReply){
        this.commentReply=commentReply;
    }
    public void loadData(List<CommentModel.Detail>data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void reLoadData(List<CommentModel.Detail>data){
        list.addAll(data);
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
            convertView=inflater.inflate(R.layout.item_pushsingle_detail_comment,parent,false);
        }
        CommentModel.Detail model=list.get(position);
        RelativeLayout llComment= (RelativeLayout) convertView.findViewById(R.id.item_comment_ll);//评论点击布局
        SimpleDraweeView icIcon= (SimpleDraweeView) convertView.findViewById(R.id.item_comment_ivIcon);//评论头像
        TextView tvName= (TextView) convertView.findViewById(R.id.item_comment_name);//评论名称
        TextView tvMessage= (TextView) convertView.findViewById(R.id.item_comment_message);//评论内容
        TextView tvTime= (TextView) convertView.findViewById(R.id.item_comment_tvTime);//时间
        MyListView lvDetail= (MyListView) convertView.findViewById(R.id.item_comment_detail_lv);//楼中楼
        ImageView ivComment= (ImageView) convertView.findViewById(R.id.item_comment_iv);//点击评论
        ivComment.setOnClickListener(onClickListener);
        llComment.setOnClickListener(onClickListener);
        lvDetail.setOnItemClickListener(onItemClickListener);
        ivComment.setTag(R.id.position,position);
        llComment.setTag(R.id.position,position);
        lvDetail.setTag(R.id.position,position);
        CommentDetailAdapter adapter=new CommentDetailAdapter(context,model.replyList);//回复楼中楼
        lvDetail.setAdapter(adapter);
        icIcon.setImageURI(model.url);
        tvName.setText(model.name);
        tvMessage.setText(model.content);
        tvTime.setText(model.tiem);
        icIcon.setOnClickListener(iconOnClick);
        icIcon.setTag(R.id.position,position);
        tvName.setOnClickListener(iconOnClick);
        tvName.setTag(R.id.position,position);
        return convertView;
    }

    public List<CommentModel.Detail> getList(){
        return list;
    }
}
