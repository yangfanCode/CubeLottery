package com.cp2y.cube.tool.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by js on 2016/11/30.
 * 奇偶数多次过滤器
 */
public class D3OddEvenFilter implements Filter<byte[]> {
    private int repeatCount=0;//重复次数(号码个数)
    private boolean include;//包含或者排除
    protected Set<Integer> mSums = new HashSet<>();//奇数的个数
    private List<byte[]> pattern;//奇偶形态,0:奇偶;-1:奇;1:偶
    private boolean includePattern;//奇偶形态包含或者排除
    private int patternCount = 0;//形态数量

//    /**
//     * 范围模式
//     * @param include
//     * @param oddRange
//     * @param pattern
//     * @param includePattern
//     */
//    public D3OddEvenFilter(boolean include, Range oddRange, byte[] pattern, boolean includePattern) {
//        this.include = include;
//        this.pattern = pattern;
//        this.includePattern = includePattern;
//        for (int i = oddRange.start; i<=oddRange.end; i++) {
//            mSums.add(i);
//        }
//        for (byte p : pattern) {
//            if (p != 0) patternCount++;
//        }
//    }

    /**
     * 独立模式
     *
     * @param include
     * @param pattern
     * @param includePattern
     * @param sums
     */
    public D3OddEvenFilter(boolean include, List<byte[]> pattern, boolean includePattern,int repeatCount, Integer... sums) {
        this.include = include;
        this.repeatCount=repeatCount;
        this.pattern = pattern;
        this.includePattern = includePattern;
        if (sums != null) {
            for (Integer sum :
                    sums) {
                mSums.add(sum);
            }
        }
//        for (byte p : pattern) {
//            if (p != 0) patternCount++;
//        }
        for (byte[] bArrays : pattern) {
            for (byte p : bArrays) {
                if (p != 0) patternCount++;//总形态数量
            }
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int oddNum = 0;//奇数个数
        int count = 0;
        if (patternCount > 0) {//形态数量大于0才需要判定
            for (int j = 0; j < pattern.size(); j++) {
                count=0;//每次重置次数
                byte[] bArrays = pattern.get(j);//形态数组
                for (int i = 0; i < obj.length; i++) {//奇数判定
                    byte b = obj[i];
                    boolean isOdd = false;
                    if (b % 2 == 1) {//奇数
                        isOdd = true;
                    }
                    byte p = bArrays[i];
                    if (p != 0) {
                        if (isOdd && p == -1) {
                            count++;
                        } else if (!isOdd && p == 1) {
                            count++;
                        }
                    }
                }
                if(count==repeatCount)break;//符合条件
            }
        }
        if(mSums.size()>0){//有奇偶比
            for (int i = 0; i < obj.length; i++) {//奇数判定
                byte b = obj[i];
                if (b % 2 == 1) {//奇数
                    oddNum++;
                }
            }
        }
        if (patternCount > 0 && includePattern != (count == repeatCount)) return false;//形态判断失败
        if (mSums.isEmpty()) return true;
        return include == mSums.contains(oddNum);
    }

}
