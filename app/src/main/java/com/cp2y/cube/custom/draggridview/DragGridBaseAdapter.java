package com.cp2y.cube.custom.draggridview;

/**
 * Created by yangfan on 2017/7/26.
 */
public interface DragGridBaseAdapter {
    /**
     * 重新排列数据
     * @param oldPosition
     * @param newPosition
     */
    public void reorderItems(int oldPosition, int newPosition);


    /**
     * 设置某个item隐藏
     * @param hidePosition
     */
    public void setHideItem(int hidePosition);

    /**
     * 设置编辑模式
     */
    public void setIsDelete();
}
