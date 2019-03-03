package com.cp2y.cube.activity.recognize.utis;

import com.cp2y.cube.services.LotteryService;
import com.cp2y.cube.services.Service;
import com.cp2y.cube.util.CombineAlgorithm;
import com.cp2y.cube.utils.CommonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan on 2017/4/10.
 */
public class ScanFormatUtils {
    /**byte to String 双色球 复式 胆拖  大乐透 复式  胆拖全
     * @return
     */
    public static String bytetoStringMutil(List<byte[]> list){
        if(list.size()==0)return "";
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).length;j++){
                if(i==list.size()-1){//最后一条
                    if(j==list.get(i).length-1){
                        stringBuilder.append(String.valueOf(list.get(i)[j]));//最后一条最后一个数据
                    }else{
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(" ");
                    }
                }else{
                    if(j==list.get(i).length-1){
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(",");
                    }else{
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(" ");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }
    /**byte to String dai双色球  大乐透 单式
     * @return
     */
    public static String bytetoStringSingle(List<byte[]> list){
        if(list.size()==0)return "";
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).length;j++){
                if(j==6){
                    stringBuilder.append(String.valueOf(list.get(i)[j])).append("</br>");
                }else{
                    stringBuilder.append(String.valueOf(list.get(i)[j])).append(" ");
                }
            }
        }
        return stringBuilder.toString();
    }
    //大乐透胆拖缺
    public static String LottoDantuoQue(List<byte[]> list){
        if(list.size()==0)return "";
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<list.size();i++){
            for(int j=0;j<list.get(i).length;j++){
                if(i==list.size()-1){//最后一条
                    if(j==list.get(i).length-1){
                        stringBuilder.append(String.valueOf(list.get(i)[j]));//最后一条最后一个数据
                    }else{
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(" ");
                    }
                }else if(i==list.size()-2){
                    stringBuilder.append(" ,");
                }else{
                    if(j==list.get(i).length-1){
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(",");
                    }else{
                        stringBuilder.append(String.valueOf(list.get(i)[j])).append(" ");
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 保存号码库 根据类型处理数据
     * @param byteData
     * @param type 号码结构
     * @param flag 0双色球,1大乐透
     * @return
     */
    public static void ListByteToString(List<byte[]> byteData,int type,int flag){
        List<String> list_total=new ArrayList<>();
        int selectCount=0;
        if(type==0){//单式
            Service.getService(LotteryService.class).saveLotteryNumber(byteData,3,flag,1);
        }else if(type==1){//复式
            list_total.addAll(CommonUtil.bytetoList(byteData.get(0)));//红球转型
            list_total.addAll(byteAddNum(byteData.get(1),50));
            if(type==0){
                selectCount = CombineAlgorithm.combination(byteData.get(0).length, 6) * byteData.get(1).length;
            }else{
                selectCount = CombineAlgorithm.combination(byteData.get(0).length, 5) * CombineAlgorithm.combination(byteData.get(1).length, 2);
            }
            Service.getService(LotteryService.class).saveLotteryNumber(list_total,1,flag,selectCount);
        }else{//胆拖
            if(flag==0){//双色球
                list_total.addAll(CommonUtil.bytetoList(byteData.get(0)));//红胆转型
                list_total.addAll(byteAddNum(byteData.get(1),50));//红拖转型
                list_total.addAll(byteAddNum(byteData.get(2),100));//蓝球转型
                selectCount = CombineAlgorithm.combination(byteData.get(1).length, 6 - byteData.get(0).length) * byteData.get(2).length;
                Service.getService(LotteryService.class).saveLotteryNumber(list_total,2,flag,selectCount);
            }else{//大乐透
                if(type==2){//胆拖全
                    list_total.addAll(CommonUtil.bytetoList(byteData.get(0)));//红胆转型
                    list_total.addAll(byteAddNum(byteData.get(1),50));//红拖转型
                    list_total.addAll(byteAddNum(byteData.get(2),100));//蓝胆转型
                    list_total.addAll(byteAddNum(byteData.get(3),150));//蓝拖转型
                    selectCount = CombineAlgorithm.combination(byteData.get(1).length, 5 - byteData.get(0).length) * CombineAlgorithm.combination(byteData.get(3).length, 2 - byteData.get(2).length);
                }else{//胆拖缺
                    list_total.addAll(CommonUtil.bytetoList(byteData.get(0)));//红胆转型
                    list_total.addAll(byteAddNum(byteData.get(1),50));//红拖转型
                    list_total.addAll(CommonUtil.bytetoList(byteData.get(2)));//蓝胆转型
                    list_total.addAll(byteAddNum(byteData.get(3),150));//蓝拖转型
                    selectCount = CombineAlgorithm.combination(byteData.get(1).length, 5 - byteData.get(0).length) * CombineAlgorithm.combination(byteData.get(3).length, 2);
                }

                Service.getService(LotteryService.class).saveLotteryNumber(list_total,2,flag,selectCount);
            }
        }
    }

    /**
     * 给byte元素加值保存
     * @param bytedata
     * @param num
     * @return
     */
    public static List<String> byteAddNum(byte[] bytedata,int num){
        List<String> list=new ArrayList<>();
        for(byte b:bytedata){
            list.add(String.valueOf(((int)b+num)));
        }
        return list;
    }
}
