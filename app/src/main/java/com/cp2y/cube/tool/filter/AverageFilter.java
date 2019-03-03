package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/11/30.
 * 均值过滤器
 */
public class AverageFilter extends AbstractByteFilter {

    public AverageFilter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public AverageFilter(boolean include, Integer... sums) {
        super(include, sums);
    }

    @Override
    public boolean doFilter(byte[] obj) {//和值计算校验
        if (mSums.isEmpty()) return true;
        int sum = 0;
        for (byte b: obj
                ) {
            sum += b;
        }
        sum = (int) Math.round(sum * 1.0d / obj.length);
        return include == mSums.contains(sum);
    }
}
