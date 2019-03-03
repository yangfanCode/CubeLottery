package com.cp2y.cube.bean;

/**
 * Created by admin on 2016/12/2.
 */
public class WelfareLottery {
    //彩种
    private String lotteryName;
    //彩票开奖日期
    private String lotteryOpenData;
    //彩票期数
    private String lotteryYear;
    //中奖号码
    private String lotteryWinNo;
    //本地彩种图片
    private int imageLocal;
    //网络图片
    private String imageNet;
    //开奖详情按钮
    private String type;
    //截止时间
    private String endDate;

    public WelfareLottery(String lotteryName, String lotteryOpenData, String lotteryYear, String lotteryWinNo, int imageLocal, String imageNet) {
        this.lotteryName = lotteryName;
        this.lotteryOpenData = lotteryOpenData;
        this.lotteryYear = lotteryYear;
        this.lotteryWinNo = lotteryWinNo;
        this.imageLocal = imageLocal;
        this.imageNet = imageNet;
    }
    public WelfareLottery(String lotteryName, String lotteryOpenData, String lotteryYear, String lotteryWinNo, int imageLocal, String imageNet,String type) {
        this.lotteryName = lotteryName;
        this.lotteryOpenData = lotteryOpenData;
        this.lotteryYear = lotteryYear;
        this.lotteryWinNo = lotteryWinNo;
        this.imageLocal = imageLocal;
        this.imageNet = imageNet;
        this.type=type;
    }
    public WelfareLottery(String lotteryName,String endDate){
        this.lotteryName=lotteryName;
        this.endDate=endDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLotteryName() {
        return lotteryName;
    }

    public void setLotteryName(String lotteryName) {
        this.lotteryName = lotteryName;
    }

    public String getLotteryOpenData() {
        return lotteryOpenData;
    }

    public void setLotteryOpenData(String lotteryOpenData) {
        this.lotteryOpenData = lotteryOpenData;
    }

    public String getLotteryYear() {
        return lotteryYear;
    }

    public void setLotteryYear(String lotteryYear) {
        this.lotteryYear = lotteryYear;
    }

    public String getLotteryWinNo() {
        return lotteryWinNo;
    }

    public void setLotteryWinNo(String lotteryWinNo) {
        this.lotteryWinNo = lotteryWinNo;
    }

    public int getImageLocal() {
        return imageLocal;
    }

    public void setImageLocal(int imageLocal) {
        this.imageLocal = imageLocal;
    }

    public String getImageNet() {
        return imageNet;
    }

    public void setImageNet(String imageNet) {
        this.imageNet = imageNet;
    }
}
