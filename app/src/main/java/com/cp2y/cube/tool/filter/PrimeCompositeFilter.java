package com.cp2y.cube.tool.filter;

import com.cp2y.cube.struct.Range;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by js on 2016/11/30.
 * 质合数过滤器
 */
public class PrimeCompositeFilter implements Filter<byte[]> {

    private static final Set<Integer> oddNums = new HashSet<>();
    static {
        int[] data = new int[] {1,2,3,5,7,11,13,17,19,23,29,31,37,41,43,47,53,59,61,67,71,73,79,83,89,97};
        for (int num : data
             ) {
            oddNums.add(num);
        }
    }

    private boolean include;//包含或者排除
    protected Set<Integer> mSums = new HashSet<>();//质数的个数
    private byte[] pattern;//质合形态,0:质合;-1:质;1:合
    private boolean includePattern;//质合形态包含或者排除

    /**
     * 范围模式
     * @param include
     * @param oddRange
     * @param pattern
     * @param includePattern
     */
    public PrimeCompositeFilter(boolean include, Range oddRange, byte[] pattern, boolean includePattern) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        for (int i = oddRange.start; i<=oddRange.end; i++) {
            mSums.add(i);
        }
    }

    /**
     * 独立模式
     * @param include
     * @param pattern
     * @param includePattern
     * @param sums
     */
    public PrimeCompositeFilter(boolean include, byte[] pattern, boolean includePattern, Integer... sums) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        if (sums != null) {
            for (Integer sum:
                    sums) {
                mSums.add(sum);
            }
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int primeNum = 0;//质数个数
        boolean isPatternEnable = pattern.length == obj.length;
        for (int i = 0; i < obj.length; i++) {//质合判定
            byte b = obj[i];
            boolean isPrime = false;
            if (oddNums.contains((int)b)) {//质数
                primeNum++;
                isPrime = true;
            }
            if (isPatternEnable && pattern[i] != 0) {//质合形态判断
                if (isPrime && includePattern && pattern[i] == 1) {//质合形态包含判定失败(包含合数)
                    return false;
                } else if (isPrime && !includePattern && pattern[i] == -1) {//质合形态判断失败(排除质数)
                    return false;
                } else if (!isPrime && includePattern && pattern[i] == -1) {//质合形态判断失败(包含质数)
                    return false;
                } else if (!isPrime && !includePattern && pattern[i] == 1) {//质合形态判断失败(排除合数)
                    return false;
                }
            }
        }
        if (mSums.isEmpty()) return true;
        return include == mSums.contains(primeNum);
    }

}
