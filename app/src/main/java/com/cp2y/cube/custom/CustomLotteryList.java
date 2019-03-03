package com.cp2y.cube.custom;

import android.text.TextUtils;

import com.cp2y.cube.model.CustomModel;
import com.cp2y.cube.model.TrendModel;
import com.cp2y.cube.utils.CommonSPUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 彩种定制保存list判断点击个数
 * 未登录时在baseactivity里同步,功能计算定制个数
 * 登陆后每次请求接口都同步数据,功能计算定制个数,上传定制结果
 * Created by yangfan on 2017/8/4.
 */
public class CustomLotteryList {
    private static final int[] DEFAULT_ID={10002,10088,10001,10003};//默认四个
    private static final String[] DEFAULT_NAME={"双色球","超级大乐透","福彩3D","排列3"};//默认四个
    private CustomLotteryList(){}
    private static Map<Integer,String> objMap=new LinkedHashMap<>();
    //存
    public static void put(int lotteryId,String lotteryName){
        objMap.put(lotteryId,lotteryName);
    }
    //删
    public static void delete(int lotteryId){
        objMap.remove(lotteryId);
    }
    //是否存在
    public static boolean contains(int lotteryId){
        return objMap.containsKey(lotteryId);
    }
    //list对象
    public static Map<Integer,String> getMap(){
        return objMap;
    }
    //大小
    public static int getSize(){
        return objMap.size();
    }
    //清除
    public static void clear(){objMap.clear();}
    //保存到本地 格式 key_value,key_value
    public static void saveFiles(){
        StringBuilder stringBuilder=new StringBuilder();
        if(objMap.size()==0)return;
        int i=0;
        for(Iterator<Map.Entry<Integer,String>>iterator=objMap.entrySet().iterator();iterator.hasNext();i++){
            Map.Entry<Integer,String> entry=iterator.next();
            int key=entry.getKey();
            String value=entry.getValue();
            if(i==0){
                stringBuilder.append(String.valueOf(key)).append("_").append(value);
            }else{
                stringBuilder.append(",").append(String.valueOf(key)).append("_").append(value);
            }
        }
        //存本地
        CommonSPUtils.put("customLottery",stringBuilder.toString());
    }
    //同步本地数据到list
    public static void synchronizedData(){
        String customLottery=CommonSPUtils.getString("customLottery");
        objMap.clear();
        if(!TextUtils.isEmpty(customLottery)){
            String[] custom=customLottery.split(",");
            for(String str:custom){
                int key=Integer.valueOf(str.substring(0,str.indexOf("_")));
                String value=str.substring(str.indexOf("_")+1,str.length());
                objMap.put(key,value);
            }
        }else{//默认4个数据
            StringBuilder stringBuilder=new StringBuilder();
            for (int i = 0; i < DEFAULT_ID.length; i++) {
                if(i==0){//格式key_value,key_value
                    stringBuilder.append(String.valueOf(DEFAULT_ID[i])).append("_").append(String.valueOf(DEFAULT_NAME[i]));
                }else{
                    stringBuilder.append(",").append(String.valueOf(DEFAULT_ID[i])).append("_").append(String.valueOf(DEFAULT_NAME[i]));
                }
                objMap.put(DEFAULT_ID[i],DEFAULT_NAME[i]);
                //存本地
            }
            CommonSPUtils.put("customLottery",stringBuilder.toString());
        }
    }
    //同步登录数据到list
    public static void synchronizedLoginData(List<CustomModel.Detail> list){
        objMap.clear();
        if(list!=null&&list.size()>0){
            for(int i=0,size=list.size();i<size;i++){
                int key=list.get(i).getLotteryID();
                String value=list.get(i).getLotteryName();
                objMap.put(key,value);
            }
        }
    }
    //同步走势登录数据到list
    public static void synchronizedTrendLoginData(Map<Integer,TrendModel.Detail> map){
        objMap.clear();
        if(map!=null&&map.size()>0){
            for(Iterator<Map.Entry<Integer,TrendModel.Detail>> it=map.entrySet().iterator();it.hasNext();){
                Map.Entry<Integer,TrendModel.Detail> entry=it.next();
                int key=entry.getKey();
                String value=entry.getValue().getLotteryName();
                objMap.put(key,value);
            }
        }
    }
}
