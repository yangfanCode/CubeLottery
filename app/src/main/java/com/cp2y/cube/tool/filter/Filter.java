package com.cp2y.cube.tool.filter;

/**
 * Created by js on 2016/11/30.
 */
public interface Filter<T> {
    /**过滤信息**/
    boolean doFilter(T obj);
}
