package com.cp2y.cube.model;

/**
 * Created by js on 2017/1/5.
 */
public class ConditionModel extends BaseModel {

    /**期数**/
    private int period = 100;
    /**H5默认期数**/
    private int periodH5 = 30;
    /**当前加载期数**/
    private int currentPeriod = 0;
    /**星期**/
    private int weekDay = 0;
    /**当前加载的星期**/
    private int currentWeekDay = 0;
    /**是否显示开奖号**/
    private boolean showBaseNum = true;
    /**是否显示遗漏**/
    private boolean showMiss = true;
    /**含蓝球计算**/
    private boolean calcBlue = true;
    /**重号显示**/
    private boolean showMutiNum;
    /**边号显示**/
    private boolean showEdgeNum;
    /**连号显示**/
    private boolean showConnectNum;
    /**串号显示**/
    private boolean showSerialNum;
    /**折叠线显示**/
    private boolean showFoldLine;
    /**分区线显示**/
    private boolean showPartLine;
    /**平均线显示**/
    private boolean showAvgLine;
    /**分断线显示**/
    private boolean showBurstLine;
    /**柱图显示**/
    private boolean showBarChart;
    /**包含特别号码显示**/
    private boolean showSpecialNum=true;


    public int getPeriodH5() {
        return periodH5;
    }

    public void setPeriodH5(int periodH5) {
        this.periodH5 = periodH5;
    }

    public boolean isShowSpecialNum() {
        return showSpecialNum;
    }

    public void setShowSpecialNum(boolean showSpecialNum) {
        this.showSpecialNum = showSpecialNum;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(int currentPeriod) {
        this.currentPeriod = currentPeriod;
    }

    public int getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(int weekDay) {
        this.weekDay = weekDay;
    }

    public int getCurrentWeekDay() {
        return currentWeekDay;
    }

    public void setCurrentWeekDay(int currentWeekDay) {
        this.currentWeekDay = currentWeekDay;
    }

    public boolean isShowBaseNum() {
        return showBaseNum;
    }

    public void setShowBaseNum(boolean showBaseNum) {
        this.showBaseNum = showBaseNum;
    }

    public boolean isShowMiss() {
        return showMiss;
    }

    public void setShowMiss(boolean showMiss) {
        this.showMiss = showMiss;
    }

    public boolean isCalcBlue() {
        return calcBlue;
    }

    public void setCalcBlue(boolean calcBlue) {
        this.calcBlue = calcBlue;
    }

    public boolean isShowMutiNum() {
        return showMutiNum;
    }

    public void setShowMutiNum(boolean showMutiNum) {
        this.showMutiNum = showMutiNum;
    }

    public boolean isShowEdgeNum() {
        return showEdgeNum;
    }

    public void setShowEdgeNum(boolean showEdgeNum) {
        this.showEdgeNum = showEdgeNum;
    }

    public boolean isShowConnectNum() {
        return showConnectNum;
    }

    public void setShowConnectNum(boolean showConnectNum) {
        this.showConnectNum = showConnectNum;
    }

    public boolean isShowSerialNum() {
        return showSerialNum;
    }

    public void setShowSerialNum(boolean showSerialNum) {
        this.showSerialNum = showSerialNum;
    }

    public boolean isShowPartLine() {
        return showPartLine;
    }

    public void setShowPartLine(boolean showPartLine) {
        this.showPartLine = showPartLine;
    }

    public boolean isShowBurstLine() {
        return showBurstLine;
    }

    public void setShowBurstLine(boolean showBurstLine) {
        this.showBurstLine = showBurstLine;
    }

    public boolean isShowBarChart() {
        return showBarChart;
    }

    public void setShowBarChart(boolean showBarChart) {
        this.showBarChart = showBarChart;
    }

    public boolean isShowFoldLine() {
        return showFoldLine;
    }

    public void setShowFoldLine(boolean showFoldLine) {
        this.showFoldLine = showFoldLine;
    }

    public boolean isShowAvgLine() {
        return showAvgLine;
    }

    public void setShowAvgLine(boolean showAvgLine) {
        this.showAvgLine = showAvgLine;
    }
}
