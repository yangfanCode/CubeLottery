package com.cp2y.cube.model;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class MultiCalcResultModel {
    public int multi;//倍数
    public int periodNow;//本期投入
    public int periodTotal;//累计投入
    public int income;//累计收益
    public double incomeRate;//收益率

    public MultiCalcResultModel(int multi, int periodNow, int periodTotal, int income, double incomeRate) {
        this.multi = multi;
        this.periodNow = periodNow;
        this.periodTotal = periodTotal;
        this.income = income;
        this.incomeRate = incomeRate;
    }
}
