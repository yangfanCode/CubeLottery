package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

/**
 * Created by js on 2016/12/1.
 * 连号过滤
 */
public class ConnectNumberFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    private Range numsRange;//连号个数
    private Range groupsRange;//连号组数

    public ConnectNumberFilter(boolean include, Range numsRange, Range groupsRange) {
        this.include = include;
        this.numsRange = numsRange;
        this.groupsRange = groupsRange;
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int connectNum = 0, groupNum = 0;
        boolean connectFlag = false;
        for (int i = 0; i < obj.length-1; i++) {//连号判定
            if (obj[i+1] - obj[i] == 1) {
                if (connectFlag) {
                    connectNum++;
                } else {
                    connectNum+=2;
                    groupNum++;
                    connectFlag = true;
                }
            } else if (connectFlag){
                connectFlag = false;
            }
        }
        return include == (connectNum>=numsRange.start && connectNum<=numsRange.end && groupNum>=groupsRange.start && groupNum<=groupsRange.end);
    }
}
