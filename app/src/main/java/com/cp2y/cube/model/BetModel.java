package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/17.
 */
public class BetModel {
    private List<delail>bettingList;

    public List<delail> getBettingList() {
        return bettingList;
    }

    public void setBettingList(List<delail> bettingList) {
        this.bettingList = bettingList;
    }

    public static class delail{
        private String drawTime;
        private int lotteryID;
        private String lotteryName;

        public String getDrawTime() {
            return drawTime;
        }

        public void setDrawTime(String drawTime) {
            this.drawTime = drawTime;
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
    }
}
