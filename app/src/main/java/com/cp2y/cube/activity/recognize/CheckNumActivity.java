package com.cp2y.cube.activity.recognize;

import com.cp2y.cube.activity.BaseActivity;
import com.cp2y.cube.activity.recognize.keyword.RegexCheck;
import com.cp2y.cube.custom.TipsToast;

import java.util.List;

/**
 * 修改号码时校验
 */
public class CheckNumActivity extends BaseActivity {
    //大乐透单式号码检查
    public void checkLottoSingel(int num,boolean isSingelRed){
        if(!isSingelRed){//蓝球
            if(num==0||num>12){//范围判断
                TipsToast.showTips("请输入01-12范围的后区号码");
            }
        }else{//红球
            if(num==0||num>35){//范围判断
                TipsToast.showTips("请输入01-35范围的前区号码");
            }
        }
    }
    //大乐透复式号码检查
    public void checkLottoMuti(int pos,int num){
        if(pos==0){//红球
            if(num==0||num>35){//范围判断
                TipsToast.showTips("请输入01-35范围的前区号码");
            }
        }else{//蓝球
            if(num==0||num>12){//范围判断
                TipsToast.showTips("请输入01-12范围的后区号码");
            }
        }
    }
    //大乐透胆拖号码检查
    public void checkLottoDantuo(int pos,int num){
        if(pos==0||pos==1){//红球
            if(num==0||num>35){//范围判断
                TipsToast.showTips("请输入01-35范围的前区号码");
            }
        }else{//蓝球
            if(num==0||num>12){//范围判断
                TipsToast.showTips("请输入01-12范围的后区号码");
            }
        }
    }
    //双色球单式号码检查
    public void checkDoubleSingel(int num,boolean isSingelRed){
        if(!isSingelRed){//蓝球
            if(num==0||num>16){//范围判断
                TipsToast.showTips("请输入01-16范围的蓝球号码");
            }
        }else{//红球
            if(num==0||num>33){//范围判断
                TipsToast.showTips("请输入01-33范围的红球号码");
            }
        }
    }
    //双色球复式号码检查
    public void checkDoubleMuti(int pos,int num){
        if(pos==0){//红球
            if(num==0||num>33){//范围判断
                TipsToast.showTips("请输入01-33范围的红球号码");
            }
        }else{//蓝球
            if(num==0||num>16){//范围判断
                TipsToast.showTips("请输入01-16范围的蓝球号码");
            }
        }
    }
    //双色球胆拖号码检查
    public void checkDoubleDantuo(int pos,int num){
        if(pos==0||pos==1){//红球
            if(num==0||num>33){//范围判断
                TipsToast.showTips("请输入01-33范围的红球号码");
            }
        }else{//蓝球
            if(num==0||num>16){//范围判断
                TipsToast.showTips("请输入01-16范围的蓝球号码");
            }
        }
    }
    //重复保存检查
    public boolean isSameSave(List<byte[]> list1,List<byte[]> list2){
        if(list1.size()!=list2.size())return false;
        for(int i=0;i<list1.size();i++){
            for(int j=0;j<list1.get(i).length;j++){
                if(list1.get(i)[j]==list2.get(i)[j]){
                    continue;
                }else{
                    return false;
                }
            }
        }
        return true;
    }
    //双色球号码处理结果检查 构成一注
    public boolean CheckDoubleResult(List<byte[]> list,int type){
        if(list.size()==0)return false;
        if(type==0){//单式
            for (int i=0;i<list.size();i++){
                if(list.get(i).length!=7){
                    return false;
                }
            }
        }else if(type==1){//复式
            if(list.get(0).length<6||list.get(1).length<1){
                return false;
            }
            if(list.get(0).length+list.get(1).length<7){
                return false;
            }
            if(RegexCheck.DoubleMutiBlueDelete(list)){
                return false;
            }
        }else if(type==2){
            if(list.get(0).length==0||(list.get(0).length+list.get(1).length<6)||list.get(2).length==0){
                return false;
            }
            if(list.get(0).length+list.get(1).length+list.get(2).length<7){
                return false;
            }
            if(RegexCheck.DoubleDanTuoBlueDelete(list)){
                return false;
            }
        }
        return true;
    }
    //大乐透号码处理结果检查 构成一注
    public boolean CheckLottoResult(List<byte[]> list,int type){
        if(list.size()==0)return false;
        if(type==0){//单式
            for (int i=0;i<list.size();i++){
                if(list.get(i).length!=7){
                    return false;
                }
            }
        }else if(type==1){//复式
            if(list.get(0).length<5||list.get(1).length<2){
                return false;
            }
            if(list.get(0).length+list.get(1).length<7){
                return false;
            }
            if(RegexCheck.LottoMutiBlueDelete(list)){
                return false;//蓝球范围限制
            }
        }else if(type==2){//胆拖全
            if(list.get(0).length==0||(list.get(0).length+list.get(1).length<5)||(list.get(2).length+list.get(3).length<2)){
                return false;
            }
            if(list.get(0).length+list.get(1).length+list.get(2).length+list.get(3).length<7){
                return false;
            }
            if(RegexCheck.LottoDantuoBlueDelete(list,type)){
                return false;
            }
        }else{//胆拖缺
            if(list.get(0).length==0||(list.get(0).length+list.get(1).length<5)||list.get(3).length<2){
                return false;
            }
            if(list.get(0).length+list.get(1).length+list.get(3).length<7){
                return false;
            }
            if(RegexCheck.LottoDantuoBlueDelete(list,type)){
                return false;
            }
        }
        return true;
    }
}
