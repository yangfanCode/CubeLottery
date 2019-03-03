package com.cp2y.cube.model;

import java.util.List;
import java.util.Map;

/**
 * Created by yangfan on 2017/11/1.
 */
public class TrendModel {
    private Map<Integer, Detail> AndroidtrendList;
    private boolean isHaveDeveloped;

    public Map<Integer, Detail> getAndroidtrendList() {
        return AndroidtrendList;
    }

    public void setAndroidtrendList(Map<Integer, Detail> androidtrendList) {
        AndroidtrendList = androidtrendList;
    }

    public boolean isHaveDeveloped() {
        return isHaveDeveloped;
    }

    public void setHaveDeveloped(boolean haveDeveloped) {
        isHaveDeveloped = haveDeveloped;
    }

    public static class Detail {
        private boolean isBranch;
        private boolean isDeveloped;
        private int lotteryID;
        private String lotteryName;
        private List<Detail> ssList;

        public boolean isDeveloped() {
            return isDeveloped;
        }

        public void setDeveloped(boolean developed) {
            isDeveloped = developed;
        }

        public boolean isBranch() {
            return isBranch;
        }

        public void setBranch(boolean branch) {
            isBranch = branch;
        }

        public int getLotteryID() {
            return lotteryID;
        }

        public void setLotteryID(int lotteryID) {
            this.lotteryID = lotteryID;
        }

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public List<Detail> getSsList() {
            return ssList;
        }

        public void setSsList(List<Detail> ssList) {
            this.ssList = ssList;
        }
    }
}
