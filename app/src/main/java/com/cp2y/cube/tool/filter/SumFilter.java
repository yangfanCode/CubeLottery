package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/11/30.
 * 和值过滤器
 */
public class SumFilter extends AbstractByteFilter {

    public SumFilter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public SumFilter(boolean include, Integer... sums) {
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
        return include == mSums.contains(sum);
    }

}
