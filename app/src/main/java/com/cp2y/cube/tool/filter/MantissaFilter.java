package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/11/30.
 * 和尾数过滤器
 */
public class MantissaFilter extends AbstractByteFilter {

    public MantissaFilter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public MantissaFilter(boolean include, Integer... sums) {
        super(include, sums);
    }

    @Override
    public boolean doFilter(byte[] obj) {//和值计算校验
        if (mSums.isEmpty()) return true;
        return include == mSums.contains(calcSumNum(obj));
    }

    //计算和尾数
    public int calcSumNum(byte[] obj){
        int sum=0;
        for(byte b:obj){
            sum+=b;
        }
        return sum%10;
    }
}
