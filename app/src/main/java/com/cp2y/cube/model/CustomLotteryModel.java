package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/3.
 */
public class CustomLotteryModel {
    private List<LotteryDetail> locationLottery;
    private List<LotteryDetail> wholeLottery;

    public List<LotteryDetail> getLocationLottery() {
        return locationLottery;
    }

    public void setLocationLottery(List<LotteryDetail> locationLottery) {
        this.locationLottery = locationLottery;
    }

    public List<LotteryDetail> getWholeLottery() {
        return wholeLottery;
    }

    public void setWholeLottery(List<LotteryDetail> wholeLottery) {
        this.wholeLottery = wholeLottery;
    }

    public class LotteryDetail{
        private int lotteryID;
        private String lotteryName;
        private int lotteryType;
        private int provinceID;
        private String provinceName;

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

        public int getLotteryType() {
            return lotteryType;
        }

        public void setLotteryType(int lotteryType) {
            this.lotteryType = lotteryType;
        }

        public int getProvinceID() {
            return provinceID;
        }

        public void setProvinceID(int provinceID) {
            this.provinceID = provinceID;
        }

        public String getProvinceName() {
            return provinceName;
        }

        public void setProvinceName(String provinceName) {
            this.provinceName = provinceName;
        }
    }
}
