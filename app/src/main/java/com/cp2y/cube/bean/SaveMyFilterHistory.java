package com.cp2y.cube.bean;

import java.util.List;

/**
 * Created by admin on 2016/12/22.
 */
public class SaveMyFilterHistory {
    //历史号
    private List<String>historyNum;
    //合值范围模式
    private List<String>sumnumRange;
    //合值独立模式
    private List<String>sumnumSingel;
    //跨度范围为模式
    private List<String>spanRange;
    //跨度独立模式
    private List<String>spanSingel;
    //大小比
    private List<String>daxiaoBi;
    //大小形态
    private List<String>daxiaoXingTai;
    //奇偶比
    private List<String>jiouBi;
    //奇偶形态
    private List<String>jiouXingTai;
    //除三余个数
    private List<String>chu3yuNum;
    //除三余形态
    private List<String>chu3yuXingTai;

    public SaveMyFilterHistory(List<String> historyNum, List<String> sumnumRange, List<String> sumnumSingel, List<String> spanRange, List<String> spanSingel, List<String> daxiaoBi, List<String> daxiaoXingTai, List<String> jiouBi, List<String> jiouXingTai, List<String> chu3yuNum, List<String> chu3yuXingTai) {
        this.historyNum = historyNum;
        this.sumnumRange = sumnumRange;
        this.sumnumSingel = sumnumSingel;
        this.spanRange = spanRange;
        this.spanSingel = spanSingel;
        this.daxiaoBi = daxiaoBi;
        this.daxiaoXingTai = daxiaoXingTai;
        this.jiouBi = jiouBi;
        this.jiouXingTai = jiouXingTai;
        this.chu3yuNum = chu3yuNum;
        this.chu3yuXingTai = chu3yuXingTai;
    }

    public List<String> getHistoryNum() {
        return historyNum;
    }

    public void setHistoryNum(List<String> historyNum) {
        this.historyNum = historyNum;
    }

    public List<String> getSumnumRange() {
        return sumnumRange;
    }

    public void setSumnumRange(List<String> sumnumRange) {
        this.sumnumRange = sumnumRange;
    }

    public List<String> getSumnumSingel() {
        return sumnumSingel;
    }

    public void setSumnumSingel(List<String> sumnumSingel) {
        this.sumnumSingel = sumnumSingel;
    }

    public List<String> getSpanRange() {
        return spanRange;
    }

    public void setSpanRange(List<String> spanRange) {
        this.spanRange = spanRange;
    }

    public List<String> getSpanSingel() {
        return spanSingel;
    }

    public void setSpanSingel(List<String> spanSingel) {
        this.spanSingel = spanSingel;
    }

    public List<String> getDaxiaoBi() {
        return daxiaoBi;
    }

    public void setDaxiaoBi(List<String> daxiaoBi) {
        this.daxiaoBi = daxiaoBi;
    }

    public List<String> getDaxiaoXingTai() {
        return daxiaoXingTai;
    }

    public void setDaxiaoXingTai(List<String> daxiaoXingTai) {
        this.daxiaoXingTai = daxiaoXingTai;
    }

    public List<String> getJiouBi() {
        return jiouBi;
    }

    public void setJiouBi(List<String> jiouBi) {
        this.jiouBi = jiouBi;
    }

    public List<String> getJiouXingTai() {
        return jiouXingTai;
    }

    public void setJiouXingTai(List<String> jiouXingTai) {
        this.jiouXingTai = jiouXingTai;
    }

    public List<String> getChu3yuNum() {
        return chu3yuNum;
    }

    public void setChu3yuNum(List<String> chu3yuNum) {
        this.chu3yuNum = chu3yuNum;
    }

    public List<String> getChu3yuXingTai() {
        return chu3yuXingTai;
    }

    public void setChu3yuXingTai(List<String> chu3yuXingTai) {
        this.chu3yuXingTai = chu3yuXingTai;
    }
}
