package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/10.
 */
public class SelectSingleTitleModel {
    private List<Detail>pushSingleLottery;

    public List<Detail> getPushSingleLottery() {
        return pushSingleLottery;
    }

    public void setPushSingleLottery(List<Detail> pushSingleLottery) {
        this.pushSingleLottery = pushSingleLottery;
    }

    public static class Detail{
        private int lotteryID;
        private String lotteryName;

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
