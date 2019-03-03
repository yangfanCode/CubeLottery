package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/12/1.
 * 间隔号过滤
 */
public class SeparateNumberFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    private Range numsRange;//间隔号个数
    private Range groupsRange;//间隔号组数

    public SeparateNumberFilter(boolean include, Range numsRange, Range groupsRange) {
        this.include = include;
        this.numsRange = numsRange;
        this.groupsRange = groupsRange;
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int separateNum = 0, groupNum = 0;
        boolean separateFlag = false;
        for (int i = 0; i < obj.length-1; i++) {//间隔号判定
            if (obj[i+1] - obj[i] == 2) {
                if (separateFlag) {
                    separateNum++;
                } else {
                    separateNum+=2;
                    groupNum++;
                    separateFlag = true;
                }
            } else if (separateFlag){
                separateFlag = false;
            }
        }
        return include == (separateNum>=numsRange.start && separateNum<=numsRange.end && groupNum>=groupsRange.start && groupNum<=groupsRange.end);
    }
}
