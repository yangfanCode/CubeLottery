package com.cp2y.cube.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cp2y.cube.R;
import com.cp2y.cube.utils.ColorUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by admin on 2016/12/30.
 */
public class MyTrendDialogSupportAdapter extends MyBaseAdapter<String> {
    private List<String>list;
    private Set<Integer>selects = new HashSet<>();

    /**
     * 包含选中项
     * @param position
     * @return
     */
    public boolean containSelect(int position) {
        return selects.contains(position);
    }

    /***
     * 增加选中项
     * @param position
     */
    public void addSelect(int position) {
        selects.add(position);
        notifyDataSetChanged();
    }

    /**
     * 移除选中项
     * @param position
     */
    public void removeSelect(int position) {
        selects.remove(position);
        notifyDataSetChanged();
    }

    /**
     * 得到选择的设置个数
     * @return
     */
    public int getSelectCount(){
        return selects.size();
    }
    /**
     * 设置选中状态
     * @param position
     * @param flag
     */
    public void setSelect(int position, boolean flag) {
        if (flag) {
            addSelect(position);
        } else {
            removeSelect(position);
        }
    }

    /**
     * 替换选中项
     * @param position
     */
    public void replaceSelect(int position) {
        if (selects.contains(position)) {
            selects.remove(position);
        } else {
            selects.add(position);
        }
        notifyDataSetChanged();
    }

    public MyTrendDialogSupportAdapter(List<String> list, Context context, int resId) {
        super(list, context, resId);
        this.list=list;
    }

    @Override
    public void bindData(ViewHolder holder, int postion) {
        TextView gou= (TextView) holder.findView(R.id.item_trendsupport_tvGou);
        TextView type= (TextView) holder.findView(R.id.item_trendsupport_tv);
        boolean isSelect = selects.contains(postion);
        type.setTextColor(isSelect ? ColorUtils.MID_BLUE: ColorUtils.NORMAL_GRAY);
        gou.setVisibility(isSelect ?View.VISIBLE:View.GONE);
        holder.findView(R.id.item_trendsupport_layout).setBackgroundResource(isSelect? R.drawable.app_myfilter_ignore_selected:R.drawable.app_mytrend_dialog);
        type.setText(list.get(postion));
    }
}
