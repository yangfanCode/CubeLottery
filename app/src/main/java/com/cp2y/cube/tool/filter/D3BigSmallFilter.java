package com.cp2y.cube.tool.filter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yangfan on 2016/11/30.
 * 大小数多次过滤器
 */
public class D3BigSmallFilter implements Filter<byte[]> {
    private int repeatCount=0;//重复次数(号码个数)
    private boolean include;//包含或者排除
    protected Set<Integer> mSums = new HashSet<>();//大数的个数
    private List<byte[]> pattern;//大小形态,0:大小;-1:小;1:大
    private boolean includePattern;//大小形态包含或者排除
    private byte baseMedian;//基础号码中值,如双色球为18
    private byte specialMedian;//特别号码中值,如双色球为9
    private byte specialIndex;//特别号码开始位置,如双色球为6
    private int patternCount = 0;//形态数量

//    /**
//     * 范围模式
//     * @param include
//     * @param bigRange
//     * @param pattern
//     * @param includePattern
//     * @param baseMedian
//     * @param specialMedian
//     * @param specialIndex
//     */
//    public D3BigSmallFilter(boolean include, Range bigRange, byte[] pattern, boolean includePattern, byte baseMedian, byte specialMedian, byte specialIndex) {
//        this.include = include;
//        for (int i = bigRange.start; i<=bigRange.end; i++) {
//            mSums.add(i);
//        }
//        this.pattern = pattern;
//        this.includePattern = includePattern;
//        this.baseMedian = baseMedian;
//        this.specialMedian = specialMedian;
//        this.specialIndex = specialIndex;
//        for (byte[] bArrays : pattern) {
//            for (byte p : bArrays) {
//                if (p != 0) patternCount++;//总形态数量
//            }
//        }
//    }

    /**
     * 独立模式
     * @param include
     * @param pattern
     * @param includePattern
     * @param baseMedian
     * @param specialMedian
     * @param specialIndex
     * @param sums
     */
    public D3BigSmallFilter(boolean include, List<byte[]> pattern, boolean includePattern,int repeatCount, byte baseMedian, byte specialMedian, byte specialIndex, Integer... sums) {
        this.include = include;
        this.pattern = pattern;
        this.includePattern = includePattern;
        this.repeatCount=repeatCount;
        this.baseMedian = baseMedian;
        this.specialMedian = specialMedian;
        this.specialIndex = specialIndex;
        if (sums != null) {
            for (Integer sum:
                    sums) {
                mSums.add(sum);
            }
        }
        for (byte[] bArrays : pattern) {
            for (byte p : bArrays) {
                if (p != 0) patternCount++;//总形态数量
            }
        }
    }

    @Override
    public boolean doFilter(byte[] obj) {
        int bigNum, smallNum = 0;//大数个数,小数个数
        int count = 0;
        if(patternCount>0){
            for(int j=0;j<pattern.size();j++){
                count=0;//每次重置次数
                byte[] bArrays = pattern.get(j);//形态数组
                for (int i = 0; i < obj.length; i++) {//大数判定
                    byte b = obj[i];
                    boolean isSmall = false;
                    if (i < specialIndex) {
                        if (b < baseMedian) {//小数
                            isSmall = true;
                        }
                    } else {//大小形态判定
                        if (b < specialMedian) {//小数
                            isSmall = true;
                        }
                    }
                    byte p = bArrays[i];
                        if (p != 0) {//形态不为0需要判断
                            if (isSmall && p == -1) {
                                count++;
                            } else if (!isSmall && p == 1) {
                                count++;
                            }
                        }
                }
                if(count==repeatCount)break;//符合条件
            }
        }
        if(mSums.size()>0){//只有大小比
            for (int i = 0; i < obj.length; i++) {//大数判定
                byte b = obj[i];
                if (i < specialIndex) {
                    if (b < baseMedian) {//小数
                        smallNum++;
                    }
                } else {//大小形态判定
                    if (b < specialMedian) {//小数
                        smallNum++;
                    }
                }
            }
        }

        if (patternCount > 0 && includePattern != (count == repeatCount)) return false;//形态判断失败
        if (mSums.isEmpty()) return true;
        bigNum = obj.length - smallNum;
        return include == mSums.contains(bigNum);
    }

}
