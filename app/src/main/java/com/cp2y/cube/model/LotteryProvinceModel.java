package com.cp2y.cube.model;

import java.util.List;

/**
 * Created by yangfan on 2017/8/7.
 */
public class LotteryProvinceModel {
    private List<LotteryDetail>areaLotteryList;

    public List<LotteryDetail> getAreaLotteryList() {
        return areaLotteryList;
    }

    public void setAreaLotteryList(List<LotteryDetail> areaLotteryList) {
        this.areaLotteryList = areaLotteryList;
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
