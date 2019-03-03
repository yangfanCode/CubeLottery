package com.cp2y.cube.tool.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/12/5.
 * 历史号码过滤器
 */
public class HistoryFilter implements Filter<byte[]> {

    private boolean include;
    private Set<String> historys = new HashSet<>();


    public HistoryFilter(boolean include, List<byte[]> historys) {
        this.include = include;
        for (byte[] history:historys
             ) {
            this.historys.add(new String(history));
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        return include == historys.contains(new String(obj));
    }
}
