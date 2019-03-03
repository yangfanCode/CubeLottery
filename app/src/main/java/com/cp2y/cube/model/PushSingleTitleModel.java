package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2018/1/8.
 */
public class PushSingleTitleModel {
    private List<Detail> lotteryList;


    public List<Detail> getLotteryList() {
        return lotteryList;
    }

    public void setLotteryList(List<Detail> lotteryList) {
        this.lotteryList = lotteryList;
    }

    public static class Detail{
        private String lotteryName;
        private int lotteryID;

        public String getLotteryName() {
            return lotteryName;
        }

        public void setLotteryName(String lotteryName) {
            this.lotteryName = lotteryName;
        }

        public int getLotteryID() {
            return lotteryID;
        }

        public void setLotteryID(int lotteryID) {
            this.lotteryID = lotteryID;
        }
    }
}
