package com.cp2y.cube.tool.filter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by js on 2016/11/30.
 * 过滤器集合
 */
public class FilterCollection<T> {

    private List<Filter<T>> mFilters;

    private FilterCollection() {
        this(new Builder<>());
    }

    private FilterCollection(Builder<T> builder) {
        this.mFilters = builder.mFilter;
    }

    public void doFilter(List<T> list) {
        ArrayList<T> tmpList = new ArrayList<>();
        tmpList.ensureCapacity(1000000);
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext();) {
            T obj = iterator.next();
            boolean flag = true;
            for (Filter filter: mFilters
                    ) {
                if (!filter.doFilter(obj)) {//删除所有不符合条件的数据
                    flag = false;
                    break;
                }
            }
            if (flag) tmpList.add(obj);
        }
        list.clear();
        list.addAll(tmpList);
    }

    public static final class Builder<T> {

        private List<Filter<T>> mFilter = new ArrayList<>();

        public Builder<T> addFilter(Filter<T> filter) {
            if (filter != null) mFilter.add(filter);
            return this;
        }

        public FilterCollection<T> build() {
            return new FilterCollection<>(this);
        }
    }

}
