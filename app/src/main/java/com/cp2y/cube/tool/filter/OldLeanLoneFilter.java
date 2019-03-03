package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/12/1.
 * 旧斜孤过滤器
 */
public class OldLeanLoneFilter implements Filter<byte[]> {

    private boolean include;//包含或者排除
    private Set<Integer> mSums1 = new HashSet<>();//旧的个数
    private Set<Integer> mSums2 = new HashSet<>();//斜的个数
    private byte[] pattern;//形态,-1:旧斜孤;0:旧;1:斜;2:孤
    private boolean includePattern;//形态包含或者排除
    private Set<Byte> mSums3 = new HashSet<>();//旧码集合
    private Set<Byte> mSums4 = new HashSet<>();//斜码集合

    /**
     * 范围模式
     * @param include
     * @param range0
     * @param range1
     * @param pattern
     * @param includePattern
     */
    public OldLeanLoneFilter(boolean include, Range range0, Range range1, byte[] pattern, boolean includePattern, List<byte[]> recentNums) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        for (int i = range0.start; i<=range0.end; i++) {
            mSums1.add(i);
        }
        for (int i = range1.start; i<=range1.end; i++) {
            mSums2.add(i);
        }
        if (recentNums!=null && recentNums.size() >=1) {
            byte[] sum3 = recentNums.get(0);
            for (Byte sum:
                    sum3) {
                mSums3.add(sum);
            }
            for (Byte sum:
                    sum3) {
                byte left = (byte)(sum - 1);
                byte right = (byte)(sum + 1);
                mSums4.add(left);
                mSums4.add(right);
            }
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
    public OldLeanLoneFilter(boolean include, byte[] pattern, boolean includePattern, int[] sum1, int[] sum2, List<byte[]> recentNums) {
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
        if (recentNums!=null && recentNums.size() >=1) {
            byte[] sum3 = recentNums.get(0);
            for (Byte sum:
                    sum3) {
                mSums3.add(sum);
            }
            for (Byte sum:
                    sum3) {
                byte left = (byte)(sum - 1);
                byte right = (byte)(sum + 1);
                mSums4.add(left);
                mSums4.add(right);
            }
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int oldNum = 0, leanNum = 0;//旧个数，斜个数，孤个数
        boolean isPatternEnable = pattern.length == obj.length;
        for (int i = 0; i < obj.length; i++) {//旧斜孤判定
            byte b = obj[i];
            int remain = 0;
            if (mSums3.contains(b)) {//旧
                oldNum++;
                remain = 1;
            } else if (mSums4.contains(b)) {//斜
                leanNum++;
                remain = 2;
            }
            if (isPatternEnable && pattern[i] > -1) {//形态判断
                if (remain == 0 && includePattern && pattern[i] != 0) {//形态判定失败(包含旧验证失败)
                    return false;
                } else if (remain == 0 && !includePattern && pattern[i] == 0) {//形态判断失败(排除旧验证失败)
                    return false;
                } else if (remain == 1 && includePattern && pattern[i] != 1) {//形态判断失败(包含斜验证失败)
                    return false;
                } else if (remain == 1 && !includePattern && pattern[i] == 1) {//形态判断失败(排除斜验证失败)
                    return false;
                } else if (remain == 2 && includePattern && pattern[i] != 2) {//形态判断失败(包含孤验证失败)
                    return false;
                } else if (remain == 2 && !includePattern && pattern[i] == 2) {//形态判断失败(排除孤验证失败)
                    return false;
                }
            }
        }
        if (mSums1.isEmpty() || mSums2.isEmpty()) return true;
        return include == (mSums1.contains(oldNum) && mSums2.contains(leanNum));
    }
}
