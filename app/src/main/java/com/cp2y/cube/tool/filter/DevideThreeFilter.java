package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/12/1.
 * 除3余过滤器
 */
public class DevideThreeFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    private Set<Integer> mSums1 = new HashSet<>();//余0的个数
    private Set<Integer> mSums2 = new HashSet<>();//余1的个数
    private byte[] pattern;//形态,-1:012;0:0;1:1;2:2
    private boolean includePattern;//形态包含或者排除
    private int patternCount = 0;//形态数量

    /**
     * 范围模式
     * @param include
     * @param range0
     * @param range1
     * @param pattern
     * @param includePattern
     */
    public DevideThreeFilter(boolean include, Range range0, Range range1, byte[] pattern, boolean includePattern) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        for (int i = range0.start; i<=range0.end; i++) {
            mSums1.add(i);
        }
        for (int i = range1.start; i<=range1.end; i++) {
            mSums2.add(i);
        }
        for (byte p : pattern) {
            if (p != -1) patternCount++;
        }
    }

    /**
     * 独立模式
     * @param include
     * @param pattern
     * @param includePattern
     * @param sum1
     * @param sum2
     */
    public DevideThreeFilter(boolean include, byte[] pattern, boolean includePattern, int[] sum1, int[] sum2) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        if (sum1 != null) {
            for (Integer sum:
                    sum1) {
                mSums1.add(sum);
            }
        }
        if (sum2 != null) {
            for (Integer sum:
                    sum2) {
                mSums2.add(sum);
            }
        }
        for (byte p : pattern) {
            if (p != -1) patternCount++;
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int zeroNum = 0, oneNum = 0;//余0个数，余1个数
        int count = 0;
        for (int i = 0; i < obj.length; i++) {//除3余判定
            byte b = obj[i];
            int remain = b % 3;//除3余
            if (remain == 0) {
                zeroNum++;
            } else if (remain == 1) {
                oneNum++;
            }
            if (patternCount > -1) {//形态数量大于0才需要判定
                byte p = pattern[i];
                if (remain == p) {//形态判定成功
                    count ++;
                }
            }
        }
        if (patternCount > 0 && includePattern != (count == patternCount)) return false;//形态判断失败
        if (mSums1.isEmpty() && mSums2.isEmpty()) return true;
        return include == ((mSums1.contains(zeroNum)||mSums1.isEmpty()) && (mSums2.contains(oneNum)||mSums2.isEmpty()));
    }
}
