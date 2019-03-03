package com.cp2y.cube.tool.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by js on 2016/12/5.
 * 双码过滤器
 */
public class DoubleNumFilter implements Filter<byte[]> {

    private boolean include;
    private List<String[]> doubleNumSet = new ArrayList<>();


    public DoubleNumFilter(boolean include, List<String[]> doubleNums) {
        this.include = include;
        doubleNumSet.addAll(doubleNums);
    }

    @Override
    public boolean doFilter(byte[] obj) {
        //处理数据
            for(String[] strs:doubleNumSet){
                String byteStr=String.valueOf(strs[0]);
                String var1=byteStr.substring(0,1);
                String var2=byteStr.substring(1,byteStr.length());
                boolean isContains= disposeNum(obj,var1,var2);
                if(include){//包含时符合条件就返回true
                    if(include==isContains){//符合条件
                        return true;
                    }else{
                        continue;
                    }
                }else{//排除时全部不含双马返回true
                    if(include==isContains){//符合条件
                        continue;
                    }else{//有一个含双码就返回false
                        return false;
                    }
                }
            }//包含和排除
            return !include;//包含返回false排除返回true
    }

    public boolean disposeNum(byte[] obj,String var1,String var2){
        int count=0;
        List<String>num=new ArrayList<>();
        for(byte b:obj){
            num.add(String.valueOf((int)b));
        }
        if(num.contains(var1)){
            count++;
            num.remove(var1);
        }
        if(num.contains(var2)){
            count++;
            num.remove(var2);
        }
        return (count>=2);
    }
}
