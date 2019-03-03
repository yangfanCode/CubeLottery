package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/12/1.
 * 同尾数过滤
 */
public class SameMantissaFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    private Range numsRange;//同尾个数
    private Range groupsRange;//同尾组数

    public SameMantissaFilter(boolean include, Range numsRange, Range groupsRange) {
        this.include = include;
        this.numsRange = numsRange;
        this.groupsRange = groupsRange;
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int mantissaNum = 0, groupNum = 0;
        Set<Integer> mantissa = new HashSet<>();
        Set<Integer> groups = new HashSet<>();
        for (int i = 0; i < obj.length; i++) {//同尾判定
            Byte b = obj[i];
            int man = b % 10;
            if (mantissa.contains(man)) {
                if (!groups.contains(man)) {
                    groups.add(man);
                    mantissaNum+=2;
                    groupNum++;
                } else {
                    mantissaNum++;
                }
            } else {
                mantissa.add(man);
            }
        }
        return include == (mantissaNum>=numsRange.start && mantissaNum<=numsRange.end && groupNum>=groupsRange.start && groupNum<=groupsRange.end);
    }
}
