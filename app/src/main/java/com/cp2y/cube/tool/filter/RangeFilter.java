package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/11/30.
 * 跨幅度过滤器
 */
public class RangeFilter extends AbstractByteFilter {

    public RangeFilter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public RangeFilter(boolean include, Integer... sums) {
        super(include, sums);
    }

    @Override
    public boolean doFilter(byte[] obj) {//和值计算校验
        if (mSums.isEmpty()) return true;
        int max = 0, min = Integer.MAX_VALUE;
        for (byte b: obj
                ) {
            max = Math.max(max, b);
            min = Math.min(min, b);
        }
        return include == mSums.contains(max - min);
    }
}
