package com.cp2y.cube.activity.recognize.keyword;

import android.text.TextUtils;

import com.cp2y.cube.custom.TipsToast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**点击完成按钮最后核对整体校验
 * Created by yangfan on 2017/4/5.
 */
public class RegexCheck {
    /**
     * 大乐透奖期格式核对
     * @param str
     * @return
     */
    public static boolean isLottoDateCorrect(String str){
        if(TextUtils.isEmpty(str))return false;
        Pattern p=Pattern.compile("[12]\\d{4}");
        Matcher m=p.matcher(str);
        return m.find();
    }
    /**
     * 大乐透奖期数据核对
     * @param str
     * @return
     */
    public static boolean isLottoDate154(String str,int Issue){
        if(str.length()==5){
            int combin=Integer.parseInt(str.substring(str.length()-3));
            if(combin>154){
                return false;
            }else if(Integer.parseInt(str)>Issue){
                return false;
            }else if(combin==0){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }
    /**
     * 双色球奖期数据核对
     * @param str
     * @return
     */
    public static boolean isDoubleDate154(String str,int Issue){
        if(str.length()==7){
            int combin=Integer.parseInt(str.substring(str.length()-3));
            if(combin>154){
                return false;
            }else if(Integer.parseInt(str)>Issue){
                return false;
            }else if(combin==0){
                return false;
            }else{
                return true;
            }
        }else{
            return false;
        }
    }

    /**
     * 是否为最新奖期
     * @param str
     * @param Issue
     * @return
     */
    public static boolean isNewDate(String str,int Issue){
        if(!TextUtils.isEmpty(str)){
            if(Integer.parseInt(str)==Issue){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }
    /**
     * 大乐透倍数核对1-99包括0开头
     * @param str
     * @return
     */
    public static boolean isLottoMultipleCorrect(String str){
        if(TextUtils.isEmpty(str))return false;
        Pattern p=Pattern.compile("[^0]\\d|[1-9]");
        Matcher m=p.matcher(str);
        return m.find();
    }

    /**
     * 大乐透号码码核对
     * @return
     */
    public static boolean isLottoNumCorrect(List<byte[]> tictks,int type){
        int k=0;//标记第几个数组
        for(byte[] singelData:tictks){
            for(int i=0;i<singelData.length;i++){
                if(singelData[i]==-1){
                    TipsToast.showTips("号码不可为空");
                    return false;
                }
                if(type==0){//单式
                    if(i==5||i==6){
                        if(singelData[i]==0||singelData[i]>12){//范围判断
                            TipsToast.showTips("请输入01-12范围的后区号码");
                            return false;
                        }
                    }else{
                        if(singelData[i]==0||singelData[i]>35){//范围判断
                            TipsToast.showTips("请输入01-35范围的前区号码");
                            return false;
                        }
                    }
                    if(!finalCheckSingle(tictks,0,5,7)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }else if(type==1){//复式
                    if(k==0){//红球
                        if(singelData[i]==0||singelData[i]>35){//范围判断
                            TipsToast.showTips("请输入01-35范围的前区号码");
                            return false;
                        }
                    }else{//蓝球
                        if(singelData[i]==0||singelData[i]>12){//范围判断
                            TipsToast.showTips("请输入01-12范围的后区号码");
                            return false;
                        }
                    }
                    if(!finalCheckMuti(tictks)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }else{//胆拖
                    if(k==0||k==1){//红球
                        if(singelData[i]==0||singelData[i]>35){//范围判断
                            TipsToast.showTips("请输入01-35范围的前区号码");
                            return false;
                        }
                    }else{//蓝球
                        if(type==3&&k==2){
                            continue;
                        }else{
                            if(singelData[i]==0||singelData[i]>12){//范围判断
                                TipsToast.showTips("请输入01-12范围的后区号码");
                                return false;
                            }
                        }
                    }
                    if(!finalCheckDantuo(tictks,4)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }
            }
            k++;
        }
        return true;
    }
    /**
     * 双色球号码码核对
     * @return
     */
    public static boolean isDoubleNumCorrect(List<byte[]> tictks,int type){
        int k=0;//标记第几个数组
        for(byte[] singelData:tictks){
            for(int i=0;i<singelData.length;i++){
                if(singelData[i]==-1){
                    TipsToast.showTips("号码不可为空");
                    return false;
                }
                if(type==0){//单式
                    if(i==6){
                        if(singelData[i]==0||singelData[i]>16){//范围判断
                            TipsToast.showTips("请输入01-16范围的蓝球号码");
                            return false;
                        }
                    }else{
                        if(singelData[i]==0||singelData[i]>33){//范围判断
                            TipsToast.showTips("请输入01-33范围的红球号码");
                            return false;
                        }
                    }
                    if(!finalCheckSingle(tictks,0,6,7)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }else if(type==1){//复式
                    if(k==0){//红球
                        if(singelData[i]==0||singelData[i]>33){//范围判断
                            TipsToast.showTips("请输入01-33范围的红球号码");
                            return false;
                        }
                    }else{//蓝球
                        if(singelData[i]==0||singelData[i]>16){//范围判断
                            TipsToast.showTips("请输入01-16范围的蓝球号码");
                            return false;
                        }
                    }
                    if(!finalCheckMuti(tictks)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }else{//胆拖
                    if(k==0||k==1){//红球
                        if(singelData[i]==0||singelData[i]>33){//范围判断
                            TipsToast.showTips("请输入01-33范围的红球号码");
                            return false;
                        }
                    }else{//蓝球
                        if(singelData[i]==0||singelData[i]>16){//范围判断
                            TipsToast.showTips("请输入01-16范围的蓝球号码");
                            return false;
                        }
                    }
                    if(!finalCheckDantuo(tictks,3)){//重复判断
                        TipsToast.showTips("号码不可重复");
                        return false;
                    }
                }
            }
            k++;
        }
        return true;
    }

    /**
     * 完成按钮单式最后重复校验
     * @return
     */
    public static boolean finalCheckSingle(List<byte[]> tictks,int start,int mid,int end){
        for(byte[] byteSingle:tictks){
            byte[] red= Arrays.copyOfRange(byteSingle,start,mid);
            byte[] blue= Arrays.copyOfRange(byteSingle,mid,end);
            Set<Byte> setRed=new HashSet<>();
            Set<Byte> setBlue=new HashSet<>();
            for(int i=0;i<red.length;i++){
                setRed.add(Byte.valueOf(red[i]));
            }
            if(setRed.size()!=red.length){
                return false;//校验红球重复
            }
            for(int i=0;i<blue.length;i++){
                setBlue.add(Byte.valueOf(blue[i]));
            }
            if(setBlue.size()!=blue.length){
                return false;//校验蓝球重复
            }
        }
        return true;
    }

    /**
     * 完成按钮复式最后重复校验
     * @param tictks
     * @return
     */
    public static boolean finalCheckMuti(List<byte[]> tictks){
        for(byte[] byteMuti:tictks){
            Set<Byte> set=new HashSet<>();
            for(byte data:byteMuti){
                set.add(Byte.valueOf(data));
            }
            if(set.size()!=byteMuti.length){
                return false;
            }
        }
        return true;
    }

    public static  boolean finalCheckDantuo(List<byte[]> tictks,int count){
        Set<Byte>setRed=new HashSet<>();
        Set<Byte>setBlue=new HashSet<>();
        for(int i=0;i<2;i++){
            for(int j=0;j<tictks.get(i).length;j++){
                setRed.add(Byte.valueOf(tictks.get(i)[j]));
            }
        }
        if(setRed.size()!=(tictks.get(0).length+tictks.get(1).length)){
            return false;//校验红球
        }
        for(int i=2;i<count;i++){
            for(int j=0;j<tictks.get(i).length;j++){
                setBlue.add(Byte.valueOf(tictks.get(i)[j]));
            }
        }
        if(count==3){//双色球
            if(setBlue.size()!=tictks.get(2).length){
                return false;//校验蓝球
            }
        }else{//大乐透
            if(setBlue.size()!=(tictks.get(2).length+tictks.get(3).length)){
                return false;//校验蓝球
            }
        }
        return true;
    }

    /**
     * 双色球奖期核对
     * @param str
     * @return
     */
    public static boolean isDoubleDateCorrect(String str){
        if(TextUtils.isEmpty(str))return false;
        Pattern p=Pattern.compile("20[12]\\d{4}");
        Matcher m=p.matcher(str);
        return m.find();
    }

    /**
     * 双色球倍数核对1-999包括0开头
     * @param str
     * @return
     */
    public static boolean isDoubleMultipleCorrect(String str){
        if(TextUtils.isEmpty(str))return false;
        Pattern p=Pattern.compile("[^0]\\d\\d|[^0]\\d|[1-9]");
        Matcher m=p.matcher(str);
        return m.find();
    }
    //大乐透单式错误数据删除
    public static boolean LottoDelete(String str){
        int count=0;
        for (int i = 0; i < str.length() / 2; i++) {
            String s = str.substring(i*2, (i+1)*2);
            if(i<5){//红球
                if(Integer.parseInt(s)>35||Integer.parseInt(s)==0){
                    count++;
                }
            }else{
                if(Integer.parseInt(s)>12||Integer.parseInt(s)==0){
                    count++;
                }
            }
            if(count>0){
                return false;
            }
        }
        return true;
    }
    //展示数据处理大乐透复式蓝球范围
    public static boolean LottoMutiBlueDelete(List<byte[]> list){
        for(int i=0;i<list.get(1).length;i++){
            if(list.get(1)[i]>12){
                return true;
            }
        }
        return false;
    }
    //展示数据处理大乐透胆拖蓝球范围
    public static boolean LottoDantuoBlueDelete(List<byte[]> list,int type){
        if(type==2){//胆拖全
            for(int i=0;i<list.get(2).length;i++){
                if(list.get(2)[i]>12){
                    return true;
                }
            }
            for(int i=0;i<list.get(3).length;i++){
                if(list.get(3)[i]>12){
                    return true;
                }
            }
        }else{
            for(int i=0;i<list.get(3).length;i++){
                if(list.get(3)[i]>12){
                    return true;
                }
            }
        }
        return false;
    }
    //双色球单式错误数据删除
    public static boolean DoubleDelete(String str){
        int count=0;
        for (int i = 0; i < str.length() / 2; i++) {
            String s = str.substring(i*2, (i+1)*2);
            if(i<6){//红球
                if(Integer.parseInt(s)>33||Integer.parseInt(s)==0){
                    count++;
                }
            }else{
                if(Integer.parseInt(s)>16||Integer.parseInt(s)==0){
                    count++;
                }
            }
            if(count>0){
                return false;
            }
        }
        return true;
    }
    //双色球复式蓝球限制
    public static boolean DoubleMutiBlueDelete(List<byte[]> list){
        for(int i=0;i<list.get(1).length;i++){
            if(list.get(1)[i]>16){
                return true;
            }
        }
        return false;
    }
    //双色球胆拖蓝球
    public static boolean DoubleDanTuoBlueDelete(List<byte[]> list){
        for(int i=0;i<list.get(2).length;i++){
            if(list.get(1)[i]>16){
                return true;
            }
        }
        return false;
    }
}
