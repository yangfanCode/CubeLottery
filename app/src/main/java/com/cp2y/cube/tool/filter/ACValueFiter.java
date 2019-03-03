package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/11/30.
 * AC值过滤器
 */
public class ACValueFiter extends AbstractByteFilter {

    public ACValueFiter(boolean include, Range range, Integer... sums) {
        super(include, range, sums);
    }

    public ACValueFiter(boolean include, Integer... sums) {
        super(include, sums);
    }

    @Override
    public boolean doFilter(byte[] obj) {//和值计算校验
        if (mSums.isEmpty()) return true;
        int i1,i2;
        Set<Integer> acVals = new HashSet<>();
        for (i1=0;i1<obj.length-1;i1++)
            for (i2=i1+1;i2<obj.length;i2++)
            {
                acVals.add(obj[i2] - obj[i1]);
            }
        int ac = acVals.size() - obj.length + 1;
        return include == mSums.contains(ac);
    }

}
