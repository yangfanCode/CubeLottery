package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/11/30.
 * 和尾数过滤器
 */
public class SumMantissaFilter extends AbstractByteFilter {

    public SumMantissaFilter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public SumMantissaFilter(boolean include, Integer... sums) {
        super(include, sums);
    }

    @Override
    public boolean doFilter(byte[] obj) {
        if (mSums.isEmpty()) return true;
        int sum = 0;
        for (byte b: obj
                ) {
            sum += b;
        }
        sum = sum % 10;
        return include == mSums.contains(sum);
    }
}
