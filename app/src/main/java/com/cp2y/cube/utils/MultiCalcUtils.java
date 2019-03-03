package com.cp2y.cube.utils;

import com.cp2y.cube.model.MultiCalcResultModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class MultiCalcUtils {
    /**
     * 全程盈利元
     * @param putNotes 投入注数
     * @param putIssues 投入期数
     * @param putMultis 起始倍数
     * @param putPrize 单注奖金
     * @param inputMoney 输入金额
     */
    public static List<MultiCalcResultModel> getIncom(int putNotes, int putIssues,int putMultis, int putPrize,double inputMoney){
        List<MultiCalcResultModel> list=new ArrayList<>();
        int cal = (putPrize*putMultis-putNotes*2*putMultis);
        if (!(inputMoney>0)) {
            return list;
        }
        if (cal<=0) {
            return list;
        }
        double putTotal=0;//累计投入
        for (int i = 0; i<putIssues; i++) {

            double bs = 0;//每期计算倍数
            if (i==0) {
                bs=putMultis;
            }else{
                bs = ((inputMoney+putTotal)/cal)*putMultis;
                bs=Math.ceil(bs);//向上取整
                if (bs<putMultis) {
                    bs=putMultis;
                }
            }
            if (bs>100000&&i==0) {
                break;
            }
            if (bs>100000) {
                break;
            }
            int  putNowPeriod = (int)bs*2*putNotes;//本期投入
            putTotal = putNowPeriod+putTotal;//累计投入
            double incomeNowPeriod = putPrize*(int)bs-putNowPeriod;//本期收益
            double incomeTotal = putNowPeriod+incomeNowPeriod-putTotal;//累计收益
            String syl = CommonUtil.changeDoubleTwo(Double.valueOf((incomeTotal/putTotal)*100));//收益率
            MultiCalcResultModel model=new MultiCalcResultModel((int)bs,putNowPeriod,(int)putTotal,(int)incomeTotal,Double.valueOf(syl));
            list.add(model);
        }
        return list;
    }

    /**
     * 盈利率
     * @param putNotes 投入注数
     * @param putIssues 投入期数
     * @param putMultis 起始倍数
     * @param putPrize 单注奖金
     * @param putIncomeRate 投入盈利率
     * @param putTotal 累计投入 前几期传0 结果为后面入参
     * @param putBeforeIssue 前几期投入期数
     * @return
     */
    public static List<MultiCalcResultModel> getIncomRate(int putNotes, int putIssues,int putMultis, int putPrize,double putIncomeRate,double putTotal,int putBeforeIssue){
        List<MultiCalcResultModel> list=new ArrayList<>();
        //盈利率
//        float putTotal=[paramArr[5] floatValue];//累计投入  0
        float bascMoney = putNotes*putMultis*2;
//        int s = [paramArr[6] intValue];//前期 投入期数  0
        float sumYm = putPrize*putMultis-bascMoney;
        putIncomeRate=putIncomeRate/100;//转成0.几
        for (int i = putBeforeIssue; i<putIssues; i++) {
            double bn =(sumYm-putIncomeRate*bascMoney);
            double tempC =((1+putIncomeRate)*putTotal/bn)*putMultis;

            double bs= i==0?putMultis:Math.ceil(tempC);
            if (bs<putMultis) {
                bs=putMultis;
            }
            double putNowPeriod = bs*2*putNotes;
            putTotal = putNowPeriod+putTotal;
            double bqsy = putPrize*bs-putNowPeriod;
            double incomeTotal = putNowPeriod+bqsy-putTotal;
            String syl = CommonUtil.changeDoubleTwo((incomeTotal/putTotal)*100);
            if ((Double.valueOf(syl)<putIncomeRate&&i==0)||(!(bn>=0)&&i==0)) {
                break;
            }
            if (bs>100000||tempC<0) {
                break;
            }
            MultiCalcResultModel model=new MultiCalcResultModel((int)bs,(int)putNowPeriod,(int)putTotal,(int)incomeTotal,Double.valueOf(syl));
            list.add(model);
        }
        return list;
        
    }
}
