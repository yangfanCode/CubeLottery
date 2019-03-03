package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/12/1.
 * 重独数过滤器
 */
public class MultipleFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    protected Set<Integer> mSums = new HashSet<>();//重复数的个数

    public MultipleFilter(boolean include, Range mutiRange) {
        this.include = include;
        for (int i = mutiRange.start; i<=mutiRange.end; i++) {
            mSums.add(i);
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int muiNum = 0;//重复个数
        Set<Byte> mutiNums = new HashSet<>();
        Set<Byte> mutiGroup = new HashSet<>();
        for (byte b: obj
                ) {
            if (mutiGroup.contains(b)) {
                if (mutiNums.contains(b)) {
                    muiNum++;
                } else {
                    muiNum+=2;
                }
            } else {
                mutiGroup.add(b);
            }
        }
        return include == mSums.contains(muiNum);
    }

}
