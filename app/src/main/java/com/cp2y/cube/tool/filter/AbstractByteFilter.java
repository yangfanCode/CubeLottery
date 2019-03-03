package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/11/30.
 */
public abstract class AbstractByteFilter implements Filter<byte[]> {

    protected boolean include;//包含或者排除
    protected Set<Integer> mSums = new HashSet<>();

    /**
     * 范围模式初始化
     * @param range 范围
     * @param sums 最大值
     * @param include
     */
    public AbstractByteFilter(boolean include, Range range, Integer... sums) {
        this.include = include;
        for (int i = range.start; i<=range.end; i++) {
            mSums.add(i);
        }
        if (sums != null) {
            for (Integer sum:
                    sums) {
                mSums.add(sum);
            }
        }
    }

    /**
     * 独立模式初始化
     * @param include
     * @param sums
     */
    public AbstractByteFilter(boolean include, Integer... sums) {
        this.include = include;
        if (sums != null) {
            for (Integer sum:
                    sums) {
                mSums.add(sum);
            }
        }
    }

}
