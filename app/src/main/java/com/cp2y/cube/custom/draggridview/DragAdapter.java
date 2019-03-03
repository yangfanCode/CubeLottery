package com.cp2y.cube.custom.draggridview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.activity.CustomProvinceActivity;
import com.cp2y.cube.custom.CustomLotteryList;
import com.cp2y.cube.custom.GuideFlag;
import com.cp2y.cube.helper.ContextHelper;
import com.cp2y.cube.utils.CommonSPUtils;
import com.cp2y.cube.utils.CommonUtil;
import com.cp2y.cube.utils.LoginSPUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yangfan on 2017/7/26.
 */
public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter{
    private DragGridView drag_gv;
    private List<HashMap<String, Object>> list=new ArrayList<>();
    private LayoutInflater mInflater;
    private int mHidePosition = -1;
    private boolean isDelete=false;//是否显示删除图标 默认隐藏

    public DragAdapter(Context context, DragGridView drag_gv){
        this.drag_gv=drag_gv;
        mInflater = LayoutInflater.from(context);
        GuideFlag.isEdited=false;//定制是否改动过,用来控制走势图是否刷新
    }
    //刷新数据
    public void reLoadData(List<HashMap<String, Object>> data){
        //数据有变化 刷新走势图
        if(!CheckList(list,data)&&list.size()>0){
            GuideFlag.isEdited=true;
        }
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //获得数据
    public List<HashMap<String, Object>> getList(){
        return list;
    }

    /**
     * 由于复用convertView导致某些item消失了，所以这里不复用item，
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.drag_item, null);
        SimpleDraweeView mImageView = (SimpleDraweeView) convertView.findViewById(R.id.drag_icon);
        TextView mTextView = (TextView) convertView.findViewById(R.id.drag_tv);
        ImageView iv_delete= (ImageView) convertView.findViewById(R.id.drag_delete);

        mImageView.setImageURI(CommonUtil.concatImgUrl((Integer) list.get(position).get("lotteryId")));
        mTextView.setText(String.valueOf(list.get(position).get("lotteryName")));
        iv_delete.setVisibility(isDelete?View.VISIBLE:View.GONE);

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.size()==1) {//至少选1个
                    ContextHelper.getActivity(CustomProvinceActivity.class).showDialog1();
                }else{
                    list.remove(position);
                    notifyDataSetChanged();
                    boolean loginState = LoginSPUtils.isLogin();//获取登录状态
                    if (!loginState) {//未登录
                        editFileData();//修改本地数据
                    }else{
                        editMapData();//修改map数据
                    }
                    GuideFlag.isEdited=true;
                }
            }
        });

        if(position == mHidePosition){
            convertView.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //获得排列后的数据
    @Override
    public void reorderItems(int oldPosition, int newPosition) {
        HashMap<String, Object> temp = list.get(oldPosition);
        if(oldPosition < newPosition){
            for(int i=oldPosition; i<newPosition; i++){
                Collections.swap(list, i, i+1);
            }
        }else if(oldPosition > newPosition){
            for(int i=oldPosition; i>newPosition; i--){
                Collections.swap(list, i, i-1);
            }
        }
        list.set(newPosition, temp);
        boolean loginState= LoginSPUtils.isLogin();//获取登录状态
        if(!loginState){//未登录
            editFileData();//修改本地数据
        }else{
            editMapData();//修改map数据
        }
        GuideFlag.isEdited=true;
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;
        notifyDataSetChanged();
    }

    //编辑模式状态
    @Override
    public void setIsDelete() {
        isDelete=true;
        drag_gv.setDragResponseMS(150);//改变拖拽时间
        drag_gv.setIsDrag(true); //设置可以拖拽
        ContextHelper.getActivity(CustomProvinceActivity.class).setFinishVisible(isDelete);//完成显示与隐藏
        notifyDataSetChanged();
    }
    //完成模式状态
    public void setFinish(){
        isDelete=false;
        drag_gv.setDragResponseMS(1000);//改变拖拽时间
        drag_gv.setIsDrag(false); //设置可以拖拽
        ContextHelper.getActivity(CustomProvinceActivity.class).setFinishVisible(isDelete);//完成显示与隐藏
        notifyDataSetChanged();
    }

    //未登录 直接修改本地
    public void editFileData(){
        StringBuilder stringBuilder=new StringBuilder();
        List<HashMap<String, Object>> list=getList();//拿排序后的数据
        for(int i=0,size=list.size();i<size;i++){
            HashMap<String, Object> map=list.get(i);
            if(i==0){//格式key_value,key_value
                stringBuilder.append(String.valueOf(map.get("lotteryId"))).append("_").append(String.valueOf(map.get("lotteryName")));
            }else{
                stringBuilder.append(",").append(String.valueOf(map.get("lotteryId"))).append("_").append(String.valueOf(map.get("lotteryName")));
            }
        }
        //存本地
        CommonSPUtils.put("customLottery",stringBuilder.toString());
    }
    //登录 修改map数据
    public void editMapData(){
        CustomLotteryList.clear();
        List<HashMap<String, Object>> list=getList();//拿排序后的数据
        for(int i=0,size=list.size();i<size;i++){
            HashMap<String, Object> map=list.get(i);
            int key= (int) map.get("lotteryId");
            String value= (String) map.get("lotteryName");
            CustomLotteryList.put(key,value);
        }
    }

    public static boolean CheckList(List<HashMap<String, Object>> list1, List<HashMap<String, Object>> list2) {
        if (list1.size() == 0 && list2.size() == 0) return false;
        if (list1.size() != list2.size()) return false;
        if (list2.containsAll(list1))
            return true;
        return false;
    }
}

