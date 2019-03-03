package com.cp2y.cube.tool.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/12/5.
 * 无序历史号码过滤器
 */
public class DisOrderHistoryFilter implements Filter<byte[]> {

    private boolean include;
    private Set<String> historys = new HashSet<>();


    public DisOrderHistoryFilter(boolean include, List<byte[]> historys) {
        this.include = include;
        for (byte[] history:historys
             ) {
            byte[] sort=new byte[history.length];
            System.arraycopy(history, 0, sort, 0, history.length);
            Arrays.sort(sort);
            this.historys.add(new String(sort));
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        Arrays.sort(obj);
        return include == historys.contains(new String(obj));
    }
}
