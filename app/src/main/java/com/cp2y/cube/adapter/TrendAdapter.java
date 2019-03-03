package com.cp2y.cube.adapter;

import java.util.List;

/**
 * Created by js on 2017/1/4.
 */
public interface TrendAdapter<T> {
    /**重新加载数据**/
    void reloadData(List<T> data);
    /**初始化数据,数据处理**/
    void initNums();
}
