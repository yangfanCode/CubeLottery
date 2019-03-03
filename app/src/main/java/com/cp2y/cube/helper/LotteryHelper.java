package com.cp2y.cube.helper;

import com.cp2y.cube.utils.CommonUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yangfan on 2016/11/30.
 */
public class LotteryHelper {

    /**
     * 创建M选N所有组合记录
     * @param reds1 胆码
     * @param reds2 拖码
     * @param blues 蓝球
     * @param count 球总数
     * @param blueCount 蓝球总数
     * @return
     */
    public static List<byte[]> generateLotteryRecordsColorBall(byte[] reds1, byte[] reds2 , byte[] blues, int count, int blueCount) {
        ArrayList<byte[]> data = new ArrayList<>();
        data.ensureCapacity(100000);
        int n = count - blueCount - reds1.length;//托码的个数
        generateLotteryRecordsForColorBall(data, reds1, reds2, blues, 0, 0, n, new byte[n], count, blueCount);
        return data;
    }

    /**
     * 创建M选N所有组合记录
     * @param reds1 胆码
     * @param reds2 拖码
     * @param blues1 蓝胆
     * @param blues1 蓝托
     * @param count 球总数
     * @param blueCount 蓝球总数
     * @return
     */
    public static List<byte[]> generateLotteryRecordsLotoBall(byte[] reds1, byte[] reds2 , byte[] blues1, byte[] blues2, int count, int blueCount) {
        ArrayList<byte[]> data = new ArrayList<>();
        data.ensureCapacity(100000);
        int n = count - blueCount - reds1.length - blues1.length;//托码的个数
        generateLotteryRecordsForLotoBall(data, reds1, reds2,blues1, blues2, 0, 0, n, new byte[n], count, blueCount);
        return data;
    }

    /**
     * 大乐透生成算法
     * @param reds1
     * @param reds2
     * @param blues1
     * @param blues2
     * @param count
     * @param blueCount
     * @return
     */
    public static List<byte[]> generateLotteryRecordsForLotoBall(byte[] reds1, byte[] reds2 , byte[] blues1, byte[] blues2, int count, int blueCount) {
        ArrayList<byte[]> data = new ArrayList<>();
        data.ensureCapacity(100000);
        int redCount = count - blueCount;
        int nRed = redCount - reds1.length;//托码的个数
        int nBlue = blueCount - blues1.length;
        ArrayList<byte[]> reds = new ArrayList<>();
        reds.ensureCapacity(100000);
        ArrayList<byte[]> blues = new ArrayList<>();
        blues.ensureCapacity(100000);
        generateLotteryRecordsForSimpleBall(reds, reds2, 0, 0, nRed, new byte[nRed]);
        generateLotteryRecordsForSimpleBall(blues, blues2, 0, 0, nBlue, new byte[nBlue]);
        int red1Count = reds1.length;
        int blue1Count = blues1.length;
        int blue2Pos = redCount + blue1Count;
        for (byte[] red:reds) {
            for (byte[] blue:blues) {
                byte[] tmp = new byte[count];
                System.arraycopy(reds1, 0, tmp, 0, red1Count);//复制胆码
                System.arraycopy(red, 0, tmp, red1Count, nRed);//复制托码
                System.arraycopy(blues1, 0, tmp, redCount, blue1Count);//复制胆码
                System.arraycopy(blue, 0, tmp, blue2Pos, nBlue);//复制托码
                data.add(tmp);
            }
        }
        return data;
    }

    /**
     * 基本通用方法 总数里面挑几个球
     * 创建单一颜色彩票所有的组合记录
     * @param balls 所有选择的球
     */
    public static List<byte[]> generateLotteryRecordsSimpleBall(byte[] balls, int count) {
        List<byte[]> data = new ArrayList<>();
        generateLotteryRecordsForSimpleBall(data, balls, 0, 0, count, new byte[count]);
        return data;
    }

    /**
     * 单一数据源组合数
     * @param data
     * @param balls
     * @param srcIndex
     * @param iIndex
     * @param nIndex
     * @param tmp
     */
    private static void generateLotteryRecordsForSimpleBall(List<byte[]> data, byte[] balls, int srcIndex, int iIndex, int nIndex, byte[] tmp) {
        for (int i = srcIndex;i<balls.length - nIndex + 1; i++) {//托码组合
            tmp[iIndex] = balls[i];
            if (nIndex  == 1) {//索引达到上线
                byte[] arr = new byte[tmp.length];
                System.arraycopy(tmp, 0, arr, 0, tmp.length);//复制托码
                data.add(arr);
            } else {//索引未达到上线
                generateLotteryRecordsForSimpleBall(data, balls, i+1, iIndex+1, nIndex - 1, tmp);
            }
        }
    }

    /**
     * 创建双色球所有组合记录
     * @param reds1 胆码
     * @param reds2 拖码
     * @param blues 蓝球
     * @param srcIndex 托码开始位置
     * @param nIndex 托码当前索引
     * @param tmp 临时数组
     *
     */
    private static void generateLotteryRecordsForColorBall(List<byte[]> data, byte[] reds1, byte[] reds2, byte[] blues, int srcIndex, int iIndex, int nIndex, byte[] tmp, int count, int blueCount) {
        for (int i = srcIndex;i<reds2.length - nIndex + 1; i++) {//托码组合
            tmp[iIndex] = reds2[i];
            if (nIndex  == 1) {//托码索引达到上线,遍历所有蓝球的可能情况
                List<byte[]> blueRecords = generateLotteryRecordsSimpleBall(blues, blueCount);//遍历得到所有蓝球的可能
                for (int j = 0; j < blueRecords.size(); j++) {
                    byte[] arr = new byte[count];
                    int length = reds1.length;
                    System.arraycopy(reds1, 0, arr, 0, length);//复制胆码
                    System.arraycopy(tmp, 0, arr, length, tmp.length);//复制托码
                    length += tmp.length;
                    byte[] blueRecord = blueRecords.get(j);
                    System.arraycopy(blueRecord, 0, arr, length, blueRecord.length);//复制蓝球
                    data.add(arr);
                }
            } else {//托码索引未达到上线
                generateLotteryRecordsForColorBall(data, reds1, reds2, blues, i+1, iIndex+1, nIndex - 1, tmp, count, blueCount);
            }
        }
    }

    /**
     * 创建大乐透所有组合记录
     * @param reds1 胆码
     * @param reds2 拖码
     * @param blues1 蓝胆
     * @param blues2 蓝托
     * @param srcIndex 托码开始位置
     * @param nIndex 托码当前索引
     * @param tmp 临时数组
     *
     */
    private static void generateLotteryRecordsForLotoBall(List<byte[]> data, byte[] reds1, byte[] reds2, byte[] blues1, byte[] blues2, int srcIndex, int iIndex, int nIndex, byte[] tmp, int count, int blueCount) {
        for (int i = srcIndex;i<reds2.length - nIndex + 1; i++) {//托码组合
            tmp[iIndex] = reds2[i];
            if (nIndex  == 1) {//托码索引达到上线,遍历所有蓝球的可能情况
                List<byte[]> blueRecords = generateLotteryRecordsSimpleBall(blues2, blueCount);//遍历得到所有蓝球的可能
                for (int j = 0; j < blueRecords.size(); j++) {
                    byte[] arr = new byte[count];
                    int length = reds1.length;
                    System.arraycopy(reds1, 0, arr, 0, length);//复制胆码
                    System.arraycopy(tmp, 0, arr, length, tmp.length);//复制托码
                    length += tmp.length;
                    System.arraycopy(blues1, 0 , arr, length, blues1.length);//复制蓝胆
                    length += blues1.length;
                    byte[] blueRecord = blueRecords.get(j);
                    System.arraycopy(blueRecord, 0, arr, length, blueRecord.length);//复制蓝托
                    data.add(arr);
                }
            } else {//托码索引未达到上线
                generateLotteryRecordsForLotoBall(data, reds1, reds2, blues1, blues2, i+1, iIndex+1, nIndex - 1, tmp, count, blueCount);
            }
        }
    }

    /**
     * 创建普通彩票排列排列3排列5定位模式
     * @return
     */
    public static List<byte[]> generateLotteryForPermutation(List<byte[]> src) {
        List<byte[]> data = new ArrayList<>();
        generateLotteryForPermutation(data, src, 0, new byte[src.size()]);
        return data;
    }

    /**
     * 创建普通彩排排列
     * @param data 数据源
     * @param src 原始数据
     * @param iIndex
     * @param tmp
     */
    private static void generateLotteryForPermutation(List<byte[]> data, List<byte[]> src, int iIndex, byte[] tmp) {
        if (iIndex >= src.size()) return;
        byte[] org = src.get(iIndex);//获得第i个号码的原始数据
        for (int i = 0; i < org.length; i++) {//遍历当前的原始数据
            tmp[iIndex] = org[i];
            if (iIndex == src.size() - 1) {
                byte[] arr = new byte[tmp.length];
                System.arraycopy(tmp, 0 ,arr, 0 ,tmp.length);
                data.add(arr);
            } else {
                generateLotteryForPermutation(data, src, iIndex+1, tmp);
            }
        }
    }

    /**
     * 创建M选N全排列
     * @param balls
     * @param count
     * @return
     */
    public static List<byte[]> generateLotteryRecordsAllBall(byte[] balls, int count) {
        List<byte[]> data = new ArrayList<>();
        generateLotteryRecordsForAllBall(data, balls, 0, count, new byte[count]);
        return data;
    }

    /**
     * 创建M选N全排列
     * @param data
     * @param balls
     * @param iIndex
     * @param nIndex
     * @param tmp
     */
    private static void generateLotteryRecordsForAllBall(List<byte[]> data, byte[] balls, int iIndex, int nIndex, byte[] tmp) {
        for (int i = 0; i < balls.length ; i++) {
            tmp[iIndex] = balls[i];
            if (nIndex == 1) {//索引达到上线
                byte[] arr = new byte[tmp.length];
                System.arraycopy(tmp, 0, arr, 0, tmp.length);//复制托码
                data.add(arr);
            } else {//索引未到上限
                byte s = balls[0];
                balls[0] = balls[i];
                balls[i] = s;
                byte[] nBalls = new byte[balls.length - 1];
                System.arraycopy(balls, 1, nBalls, 0, nBalls.length);
                Arrays.sort(nBalls);
                balls[i] = balls[0];
                balls[0] = s;
                generateLotteryRecordsForAllBall(data, nBalls, iIndex + 1, nIndex - 1, tmp);
            }
        }
    }

    /**
     * 反序列化彩票记录
     * @param file
     * @param limit 限制输出条数
     * @return
     */
    public static List<String> deserializeLotteryRecords(File file, int limit) {
        List<String> data = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String recordStr;
            int line = 0;
            while ((recordStr = reader.readLine()) != null && line < limit) {
                line++;
                data.add(recordStr);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    /**
     * 序列化彩票记录
     * @param file
     */
    public static void serializeLotteryRecordsOrg(File file, List<String> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int i = 0;
            for (String record:data
                 ) {
                writer.write(record);
                writer.newLine();
                i++;
                if (i % 100 == 0) {
                    writer.flush();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 序列化彩票记录带空格
     * @param file
     */
    public static void serializeLotteryRecords(File file, List<byte[]> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int i = 0;
            for (byte[] record:data
                    ) {
                StringBuilder sb = new StringBuilder();
                for (byte b:record
                     ) {
                    sb.append(CommonUtil.preZeroForBall(b)+" ");
                }
                writer.write(sb.toString());
                writer.newLine();
                i++;
                if (i % 100 == 0) {
                    writer.flush();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 序列化3D彩票记录不带空格
     * @param file
     */
    public static void serialize3DLotteryRecords(File file, List<byte[]> data) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            int i = 0;
            for (byte[] record:data
                    ) {
                StringBuilder sb = new StringBuilder();
                for (byte b:record
                     ) {
                    sb.append(String.valueOf(b));
                }
                writer.write(sb.toString());
                writer.newLine();
                i++;
                if (i % 100 == 0) {
                    writer.flush();
                }
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**组3拼重复
     * 12设置成112和122
     * @return
     */
    public static List<byte[]> setZuxuanSameData(List<byte[]> data){
        List<byte[]> newData=new ArrayList<>();//新数据集
        for(byte[] nums:data){
            byte[] newb1=new byte[3];
            byte[] newb2=new byte[3];
            newb1[0]=newb1[1]=nums[0];
            newb1[2]=nums[1];
            newb2[0]=nums[0];
            newb2[1]=newb2[2]=nums[1];
            Arrays.sort(newb1);//横向排序
            Arrays.sort(newb2);//横向排序
            newData.add(newb1);
            newData.add(newb2);
        }
        CommonUtil.SortListByteCollection(newData);//纵向排序
        return newData;
    }

    /**
     * 组选拼胆拖
     * data 拖数据
     * dan 胆数组
     * danCount 胆数量
     * tuoCount 托数量
     * @return
     */
    public static List<byte[]> setZuxuanDanData(List<byte[]> data,byte[] dan,int danCount,int tuoCount){
        List<byte[]> newData=new ArrayList<>();//新数据集
        for(byte[] nums:data){
            byte[] newb1=new byte[danCount+nums.length];
            byte[] newb2=byteMerger(dan,nums);
            for(int i=0;i<newb2.length;i++){
                newb1[i]=newb2[i];
            }
            Arrays.sort(newb1);
            newData.add(newb1);
        }
        CommonUtil.SortListByteCollection(newData);//纵向排序
        return newData;
    }

    /**
     * 合并数组
     * @param byte_1
     * @param byte_2
     * @return
     */
    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2){
        byte[] byte_3 = new byte[byte_1.length+byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }
}
