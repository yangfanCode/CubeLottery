package com.cp2y.cube.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Created by js on 2016/12/1.
 * 组合数算法,C(m,n)
 */
public class CombineAlgorithm<T> {
    /**初始数据**/
    private T[] org;
    /**原始数据**/
    private T[] src;
    /**原始数据长度**/
    private int m;
    /**需要的数据个数**/
    private int n;
    /**结果数组**/
    private List<T[]> obj;

    public CombineAlgorithm(T[] src, int selectNum) {
        this(null, src, selectNum);
    }

    public CombineAlgorithm(T[] org, T[] src, int selectNum) {
        if (src == null || src.length < selectNum) {
            return;
        }
        this.org = org;
        this.src = src;
        m = src.length;
        n = selectNum;
    }

    public static int combination(int m, int n) {
        if (m < n)
        return 0; // 如果总数小于取出的数，直接返回0
        int k = 1;
        int j = 1;
        for (int i = n; i >= 1; i--) {
            k = k * m;
            j = j * n;
            m--;
            n--;
        }
        return k / j;
    }

    private void combine(Object src[], int srcIndex, int i, int n, Object[] tmp) {
        int j;
        for (j = srcIndex; j < src.length - (n - 1); j++ ) {
            tmp[i] = src[j];
            if (n == 1) {
                Object[] arr;
                if (org  == null) {
                    arr = new Object[tmp.length];
                    System.arraycopy(tmp, 0, arr, 0, tmp.length);
                } else {
                    arr = new Object[tmp.length + org.length];
                    System.arraycopy(org, 0, arr, 0, org.length);
                    System.arraycopy(tmp, 0, arr, org.length, tmp.length);
                }
                obj.add((T[])arr);
            } else {
                combine(src, j+1, i+1, n-1, tmp);
            }
        }
    }

    /**
     * 生成结果
     * @return
     */
    public List<T[]> getResult() {
        obj = new ArrayList<>();
        Object[] tmp = new Object[n];
        combine(src, 0, 0, n, tmp);
        return obj;
    }

    /**
     * 双色球随机数
     * @return
     */
    public static List<Integer> RandomNum(){
        Random ran = new Random();
        HashSet<Integer> set = new HashSet<Integer>();
        while (set.size() < 6) {
            set.add(ran.nextInt(33)+1);
        }
        List<Integer> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    /**
     * 乐透红球随机数
     * @return
     */
    public static List<Integer> LottoRedRandomNum(){
        Random ran = new Random();
        HashSet<Integer> set = new HashSet<Integer>();
        while (set.size() < 5) {
            set.add(ran.nextInt(35)+1);
        }
        List<Integer> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }
    /**
     * 乐透蓝球随机数
     * @return
     */
    public static List<Integer> LottoBlueRandomNum(){
        Random ran = new Random();
        HashSet<Integer> set = new HashSet<Integer>();
        while (set.size() < 2) {
            set.add(ran.nextInt(12)+1);
        }
        List<Integer> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }

    /**
     * 随机球号摇一摇
     * @return
     */
    public static List<Integer> sharkRandomNum(int size,int count){
        Random ran = new Random();
        HashSet<Integer> set = new HashSet<Integer>();
        while (set.size() < size) {
            set.add(ran.nextInt(count));
        }
        List<Integer> list = new ArrayList<>();
        list.addAll(set);
        return list;
    }
}
