package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/10/26.
 */
public class FilterHomeModel {
    private List<Detail> FilterLottery;

    public List<Detail> getFilterLottery() {
        return FilterLottery;
    }

    public void setFilterLottery(List<Detail> filterLottery) {
        FilterLottery = filterLottery;
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
